package com.mrwu.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.AlertDialog;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DemoAdapter extends BaseAdapter {


	 // 上下文对象

	private Context context = null;
	private MainActivity activity;

	//  数据集合
	private List<DemoBean> datas = null;


	//  CheckBox 是否选择的存储集合,key 是 position , value 是该position是否选中

	private Map<Integer, Boolean> isCheckMap = new HashMap<Integer, Boolean>();

	public DemoAdapter(Context context,MainActivity activity , List<DemoBean> datas) {
		this.activity=activity;
		this.datas = datas;
		this.context = context;

		// 初始化,默认都没有选中
		configCheckMap(false);
	}


	//  首先,默认情况下,所有项目都是没有选中的.这里进行初始化

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

		ViewGroup layout = null;
		ViewHolder holder;
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		//  进行ListView 的优化

		if (convertView == null) {
//			layout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.listview_item_layout, parent, false);
			convertView = inflater.inflate(R.layout.listview_item_layout, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
		//	layout = (ViewGroup) convertView;
			holder = (ViewHolder) convertView.getTag();
		}

		holder.btnDel.setOnClickListener(
				new View.OnClickListener(){
					public void onClick(View v)
					{
		//				Toast.makeText(context,"delteCatched", Toast.LENGTH_SHORT).show();
						showAlertDialog(position);
					}
				}
		);

		DemoBean bean = datas.get(position);


		//  获得该item 是否允许删除

		boolean canRemove = bean.isCanRemove();


		//  设置每一个item的文本

	//	TextView tvTitle = (TextView) layout.findViewById(R.id.tvTitle);
		holder.tvTitle.setText(bean.getTitle());

		// 获得单选按钮
	//	CheckBox cbCheck = (CheckBox) layout.findViewById(R.id.cbCheckBox);


		// * 设置单选按钮的选中

		/*cbCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				*//*
				 * 将选择项加载到map里面寄存
				 *//*
				isCheckMap.put(position, isChecked);
			}
		});*/

		if (!canRemove) {
			// 隐藏单选按钮,因为是不可删除的
//			cbCheck.setVisibility(View.GONE);
//			cbCheck.setChecked(false);
		} else {
//			cbCheck.setVisibility(View.VISIBLE);

			if (isCheckMap.get(position) == null) {
				isCheckMap.put(position, false);
			}

//			cbCheck.setChecked(isCheckMap.get(position));

//			ViewHolder holder = new ViewHolder();

//			holder.cbCheck = cbCheck;

//			holder.tvTitle = tvTitle;

			/**
			 * 将数据保存到tag
			 */
//			layout.setTag(holder);
		}

		return convertView;
	}

	public void showAlertDialog(final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//		String tempStr=Integer.toString(position+1);
		DemoBean tempBean=(DemoBean)getItem(position);
		String strTitle=tempBean.getTitle();           //从资料库中抓出application的title
		builder.setTitle("Delete item "+strTitle+" ?");
		builder.setCancelable(true);
		builder.setMessage("Are you sure?");
		builder.setPositiveButton("OK",null);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//deleting a record in database table based on "name"
				DemoBean bean = (DemoBean) getItem(position);
				if (bean.isCanRemove()) {

					remove(position);
					activity.db.delete("applications", "application= ?", new String[]{bean.getTitle()});
					activity.db.delete("conditions", "application= ?", new String[]{bean.getTitle()});
					activity.db.delete("actions", "application= ?", new String[]{bean.getTitle()});
					activity.db.delete("exceptions", "application= ?", new String[]{bean.getTitle()});
					Toast.makeText(activity, "Deleted!", Toast.LENGTH_SHORT).show();
					activity.refresh(); //调用activity里的refrensh方法更新list数据
				}


			}
		});
		builder.setNegativeButton("Cancel", null);

		builder.show();
	}

	 // 增加一项的时候
	public void add(DemoBean bean) {
		this.datas.add(0, bean);

		// 让所有项目都为不选择
		configCheckMap(false);
	}

	// 移除一个项目的时候
	public void remove(int position) {
		this.datas.remove(position);
	}

	public Map<Integer, Boolean> getCheckMap() {
		return this.isCheckMap;
	}

	public static class ViewHolder {
		public ImageView btnDel=null;
		public TextView tvTitle = null;
		public CheckBox cbCheck = null;
		public Object data = null;

			public ViewHolder(View v)
			{
				btnDel=(ImageView)v.findViewById(R.id.img_del);
				tvTitle=(TextView)v.findViewById(R.id.tvTitle);
			}

	}

	public List<DemoBean> getDatas() {
		return datas;
	}

}