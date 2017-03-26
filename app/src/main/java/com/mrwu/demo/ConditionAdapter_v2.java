package com.mrwu.demo;

import helper.MySQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class ConditionAdapter_v2 extends BaseAdapter {

    // 上下文对象

    private Context context = null;
    private SettingConditionActivity activity;
 //   SQLiteDatabase db;
//	public static final String DB_NAME="conditiondata.db";


    // 数据集合

    private List<ConditionBean> datas = null;
    private List<Integer> logicdatas = null;

    /**
     * CheckBox 是否选择的存储集合,key 是 position , value 是该position是否选中
     */
    private Map<Integer, Boolean> isCheckMap = new HashMap<Integer, Boolean>();

    public ConditionAdapter_v2(Context context,SettingConditionActivity activity, List<ConditionBean> datas,List<Integer> logicdatas) {
 //       this.context = context;
  //      super(context, activity, datas, logicdatas);
        this.datas = datas;
        this.logicdatas = logicdatas;
        this.activity=activity;
        this.context=context;
//		for(int i=0;i<datas.size();i++)
//		{
//			logicdatas.add(0);
//		}
       // 初始化,默认都没有选中

        configCheckMap(false);
//        SettingConditionActivity tempSetConditionActivity=new SettingConditionActivity();

    }

   /* public void OpenCreateDB()
    {
        db = SQLiteDatabase.openOrCreateDatabase("conditiondata.db", null);
    }*/
    /**
     * 首先,默认情况下,所有项目都是没有选中的.这里进行初始化
     */
    public void configCheckMap(boolean bool) {

        for (int i = 0; i < datas.size(); i++) {
            isCheckMap.put(i, bool);
        }

    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

  //      ViewGroup layout = null;
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

         // 进行ListView 的优化

        if (convertView == null) {
        //    layout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.listview_item_value_v2, parent, false);
            convertView = inflater.inflate(R.layout.listview_item_value_v2, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
  //          layout = (ViewGroup) convertView;
            holder = (ViewHolder) convertView.getTag();
        }

 //       ImageView btnDel=(ImageView)layout.findViewById(R.id.img_del);
        holder.btnDel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                                       showAlertDialog(position);
                        //   Toast.makeText(context,"delteCatched",Toast.LENGTH_SHORT).show();
                    }

                }

        );

        final ConditionBean bean = datas.get(position);
		// 获得该item 是否允许删除
        boolean canRemove = bean.isCanRemove();

		  //设置每一个item的文本
   //     TextView tvTitle = (TextView) layout.findViewById(R.id.tvTitle);
        holder.tvTitle.setText(bean.getTitle());
   //     TextView tvValue = (TextView) layout.findViewById(R.id.tvValue);
        holder.tvValue.setText(bean.getValue());

		//  获得Spinner

        final int sp;
        sp=position;

        holder.mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                SQLiteDatabase tempDB = MySQLiteOpenHelper.getInstance(context).getReadableDatabase();
//		        db.execSQL("DROP TABLE IF EXISTS conditions");  
                ContentValues cv = new ContentValues();
                cv.put("conditionlogic",position );
                tempDB.update("conditions", cv, "sensor = ? and application= ?", new String[]{datas.get(sp).getTitle(), ConditionValue.getApplicationName()});
//		        Toast.makeText(context,datas.get(sp).getTitle(), 20).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        if (position==0) {
            // 隐藏单选按钮,因为是不可删除的
            holder.mSpinner.setVisibility(View.INVISIBLE);
        }

//		mSpinner.setSelection((Integer) logicdatas.get(position));
        holder.mSpinner.setSelection(bean.getLogic());

        if (canRemove) {
          //  ViewHolder holder = new ViewHolder();
            holder = new ViewHolder(convertView);
 //           holder.tvTitle = tvTitle;
        }
        return convertView;
    }

    /**
     * 增加一项的时候
     */
    public void add(ConditionBean bean) {
        this.datas.add(bean);
//		this.logicdatas.add(1);
        // 让所有项目都为不选择
        configCheckMap(false);
    }

    public void showAlertDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String strSensor=((ConditionBean) getItem(position)).getTitle();//从资料库中抓出item的sensor名称
      //  String tempStr=Integer.toString(position+1);
        builder.setTitle("Delete item "+strSensor+" ?");
        builder.setCancelable(true);
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("OK",null);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //deleting a record in database table based on "name"
                ConditionBean bean = (ConditionBean) getItem(position);
                String sensor=((ConditionBean) getItem(position)).getTitle();
                if (bean.isCanRemove()) {

                    remove(position);
                    activity.db.delete("conditions", "sensor = ? and application= ?", new String[]{sensor, ConditionValue.getApplicationName()});
                    Toast.makeText(activity, "Deleted!", Toast.LENGTH_SHORT).show();
                    activity.refresh(); //调用activity里的refrensh方法更新list数据
                }


            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.show();
    }


    // 移除一个项目的时候
    public void remove(int position) {
        this.datas.remove(position);
    }

    public Map<Integer, Boolean> getCheckMap() {
        return this.isCheckMap;
    }

    public static class ViewHolder {

        public TextView tvTitle = null;

   //     public CheckBox cbCheck = null;
        public Object data = null;
        public ImageView btnDel=null;
        public TextView tvValue ;
        public Spinner mSpinner ;
        public ViewHolder(View v) {
            tvTitle = (TextView) v.findViewById(R.id.tvTitle);
 //           job = (TextView) v.findViewById(R.id.job);
            btnDel = (ImageView) v.findViewById(R.id.img_del);
            tvValue = (TextView) v.findViewById(R.id.tvValue);
            mSpinner = (Spinner) v.findViewById(R.id.spinner1);
        }
    }

    public List<ConditionBean> getDatas() {
        return datas;
    }

}