package com.mrwu.demo;

import helper.MySQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class ConditionAdapter extends BaseAdapter {

	/**
	 * 上下文对象
	 */
	private Context context = null;
//	public static final String DB_NAME="conditiondata.db";
	

	/**
	 * 数据集合
	 */
	private List<ConditionBean> datas = null;
	private List<Integer> logicdatas = null;

	/**
	 * CheckBox 是否选择的存储集合,key 是 position , value 是该position是否选中
	 */
	private Map<Integer, Boolean> isCheckMap = new HashMap<Integer, Boolean>();

	public ConditionAdapter(Context context, List<ConditionBean> datas,List<Integer> logicdatas) {
		this.context = context;
		this.datas = datas;
		this.logicdatas = logicdatas;
//		for(int i=0;i<datas.size();i++)
//		{
//			logicdatas.add(0);
//		}


		
		// 初始化,默认都没有选中
		configCheckMap(false);
	}

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

		ViewGroup layout = null;

		/**
		 * 进行ListView 的优化
		 */
		if (convertView == null) {
			layout = (ViewGroup) LayoutInflater.from(context).inflate(
					R.layout.listview_item_value, parent, false);
		} else {
			layout = (ViewGroup) convertView;
		}

		 final ConditionBean bean = datas.get(position);

		/*
		 * 获得该item 是否允许删除
		 */
		boolean canRemove = bean.isCanRemove();

		/*
		 * 设置每一个item的文本
		 */
		TextView tvTitle = (TextView) layout.findViewById(R.id.tvTitle);
		tvTitle.setText(bean.getTitle());
		TextView tvValue = (TextView) layout.findViewById(R.id.tvValue);
		tvValue.setText(bean.getValue());

		/*
		 * 获得Spinner
		 */
		final int sp;
		sp=position;
		Spinner mSpinner = (Spinner) layout.findViewById(R.id.spinner1);
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
		    public void onItemSelected(AdapterView<?> parent, View view,
		            int position, long id) {

				SQLiteDatabase db = MySQLiteOpenHelper.getInstance(context).getReadableDatabase();
//		        db.execSQL("DROP TABLE IF EXISTS conditions");  
		        ContentValues cv = new ContentValues();  
		    	cv.put("conditionlogic",position );  		    	
				db.update("conditions", cv, "sensor = ? and application= ?", new String[]{datas.get(sp).getTitle(),ConditionValue.getApplicationName()});  
//		        Toast.makeText(context,datas.get(sp).getTitle(), 20).show();
		    }
		    @Override
		    public void onNothingSelected(AdapterView<?> parent) {
		        // TODO Auto-generated method stub
		    }
		});
		if (position==0) {
			// 隐藏单选按钮,因为是不可删除的
			mSpinner.setVisibility(View.INVISIBLE);
		}
		
//		mSpinner.setSelection((Integer) logicdatas.get(position));
		mSpinner.setSelection(bean.getLogic());
		
		/*
		 * 获得单选按钮
		 */
		CheckBox cbCheck = (CheckBox) layout.findViewById(R.id.cbCheckBox);

		/*
		 * 设置单选按钮的选中
		 */
		cbCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				/*
				 * 将选择项加载到map里面寄存
				 */
				isCheckMap.put(position, isChecked);
			}
		});

		if (!canRemove) {
			// 隐藏单选按钮,因为是不可删除的
			cbCheck.setVisibility(View.GONE);
			cbCheck.setChecked(false);
		} else {
			cbCheck.setVisibility(View.VISIBLE);

			if (isCheckMap.get(position) == null) {
				isCheckMap.put(position, false);
			}

			cbCheck.setChecked(isCheckMap.get(position));

			ViewHolder holder = new ViewHolder();

			holder.cbCheck = cbCheck;

			holder.tvTitle = tvTitle;

			/**
			 * 将数据保存到tag
			 */
			layout.setTag(holder);
		}

		return layout;
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

	// 移除一个项目的时候
	public void remove(int position) {
		this.datas.remove(position);
	}

	public Map<Integer, Boolean> getCheckMap() {
		return this.isCheckMap;
	}

	public static class ViewHolder {

		public TextView tvTitle = null;

		public CheckBox cbCheck = null;
		public Object data = null;

	}

	public List<ConditionBean> getDatas() {
		return datas;
	}

}