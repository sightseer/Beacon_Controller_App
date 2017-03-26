package com.mrwu.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mrwu.demo.DemoAdapter.ViewHolder;

public class SettingActionActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	/**
	 * 返回按钮
	 */
	private ViewGroup  btnDelete= null;

	/**
	 * 确定按钮
	 */
	private ViewGroup btnAdd = null;

	/**
	 * 选择所有
	 */
	private Button btnSelectAll = null;

	/**
	 * 清除所有
	 */
	private Button btnNext = null;
	private TextView text = null;

	private Button btnLast = null; //返回上一页按钮
	/**
	 * ListView列表
	 */
	private ListView lvListView = null;
	private ListView actionListView = null;

	/**
	 * 适配对象
	 */
	private ActionAdapter actionAdapter ;
	private ConditionAdapter adpAdapter ;
	
	String Intelligent_Toy;
	public static final String DB_NAME="conditiondata.db";
	SQLiteDatabase db;
	List<DemoBean> actionDatas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setaction);

		//initial data base
		OpenCreateDB();

		// 初始化视图
		initView();

		// 初始化控件
		initData();
		

	}

	public void OpenCreateDB(){
		db = openOrCreateDatabase(DB_NAME, this.MODE_PRIVATE, null);
//        db.execSQL("DROP TABLE IF EXISTS actions");  
        db.execSQL("CREATE TABLE IF NOT EXISTS actions (_id INTEGER PRIMARY KEY AUTOINCREMENT, application VARCHAR, Intelligent_Toy VARCHAR, action VARCHAR)");
    }
	/**
	 * 初始化控件
	 */
	private void initView() {

		btnDelete = (ViewGroup) findViewById(R.id.btnDelete);
		btnDelete.setOnClickListener(this);

		btnAdd = (ViewGroup) findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(this);

		btnNext = (Button) findViewById(R.id.btnNext);
		btnNext.setOnClickListener(this);

		btnLast = (Button) findViewById(R.id.btnLast);
		btnLast.setOnClickListener(this);
		
		text = (TextView) findViewById(R.id.intelligenttoyname);
		text.setOnClickListener(this);
		
//		btnSelectAll = (Button) findViewById(R.id.btnSelectAll);
//		btnSelectAll.setOnClickListener(this);

		lvListView = (ListView) findViewById(R.id.lvListView);
		lvListView.setOnItemClickListener(this);
		
		actionListView = (ListView) findViewById(R.id.actionListView);
		actionListView.setOnItemClickListener(this);
		
	}

	/**
	 * 初始化视图
	 */
	String string;
	private void initData() {

		// 模拟假数据
		List<ConditionBean> demoDatas = new ArrayList<ConditionBean>();
		List<Integer> logicdatas = new ArrayList<Integer>();
		
		actionDatas = new ArrayList<DemoBean>();

//		demoDatas.add(new ConditionBean("Beacon1",null, false));
//		demoDatas.add(new ConditionBean("Beacon2",null, false));
//		demoDatas.add(new ConditionBean("Beacon3",null, false));
		
    	Cursor c = db.rawQuery("SELECT * FROM conditions WHERE application = ?", new String[]{ConditionValue.getApplicationName()});
//        Cursor b = db.rawQuery("SELECT * FROM logicconditions WHERE application = ?", new String[]{ConditionValue.getApplicationName()});
    	while (c.moveToNext()) {
        	ConditonDataBase person = new ConditonDataBase();
        	person.rssivalue = c.getInt(c.getColumnIndex("rssivalue"));
        	person.rssicondition=c.getString(c.getColumnIndex("rssicondition"));
        	String string = null;
        	if( person.rssicondition!= null && person.rssicondition.length() != 0)
        	{
        		string = ""+"RSSI "+person.rssicondition+person.rssivalue;
        	}
        	person.gxvalue = c.getInt(c.getColumnIndex("gxvalue"));
        	person.gxcondition=c.getString(c.getColumnIndex("gxcondition"));
        	if( person.gxcondition!= null && person.gxcondition.length() != 0)
        	{
        		string = ""+"Gyro_x "+person.gxcondition+person.gxvalue;
        	}
        	person.gyvalue = c.getInt(c.getColumnIndex("gyvalue"));
        	person.gycondition=c.getString(c.getColumnIndex("gycondition"));
        	if( person.gycondition!= null && person.gycondition.length() != 0)
        	{
        		string = ""+"Gyro_y "+person.gycondition+person.gyvalue;
        	}
        	person.gzvalue = c.getInt(c.getColumnIndex("gzvalue"));
        	person.gzcondition=c.getString(c.getColumnIndex("gzcondition"));
        	if( person.gzcondition!= null && person.gzcondition.length() != 0)
        	{
        		string = ""+"Gyro_z "+person.gzcondition+person.gzvalue;
        	}
        	person.button = c.getInt(c.getColumnIndex("button"));
        	if( person.button!= 0)
        	{
        		string = ""+"Button Pressed";
        	}
        	
        	int logic = c.getInt(c.getColumnIndex("conditionlogic"));
//        	int logic = b.getInt(b.getColumnIndex("logic"));
        	demoDatas.add(new ConditionBean(c.getString(c.getColumnIndex("sensor")),string,logic ,false));
        }
        c.close();

//        Cursor b = db.rawQuery("SELECT * FROM logicconditions WHERE application = ?", new String[]{ConditionValue.getApplicationName()});
//        while (b.moveToNext()) {
//        	int logic = b.getInt(b.getColumnIndex("logic"));
//        	logicdatas.add(logic);
//			Toast.makeText(this,"logic"+20+logic, 10).show();
//        }
//        b.close();

		adpAdapter = new ConditionAdapter(this,demoDatas,logicdatas);
		actionAdapter = new ActionAdapter(this, actionDatas);

		lvListView.setAdapter(adpAdapter);
		actionListView.setAdapter(actionAdapter);

//		Spinner conditionSpinner=(Spinner)findViewById(R.id.spinner1);
//		conditionSpinner.setEnabled(false);
	}

	/**
	 * 按钮点击事件
	 */
	@Override
	public void onClick(View v) {

		/*
		 * 当点击下一页的时候
		 */
		if (v == btnNext) {
			Intent intent = new Intent(SettingActionActivity.this, SettingExceptionActivity.class);
	        startActivity(intent);
			finish();

		}

		//当点击返回上一页的时候
		if (v == btnLast) {
			Intent intent = new Intent(SettingActionActivity.this, SettingConditionActivity.class);
			startActivity(intent);
			finish();

		}
		if (v == text) {
			final boolean[] checkedItems= new boolean[] { false,false,false };	//记录各列表项的状态
			final String[] items = new String[] { "Alarm", "Take Photo", "Send Notify"};	//各列表项要显示的内容
			// 显示带单选列表项的对话框
			Builder builder = new AlertDialog.Builder(SettingActionActivity.this);
			builder.setTitle("Action：");	//设置对话框标题
			builder.setMultiChoiceItems(items, checkedItems,
					new OnMultiChoiceClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which, boolean isChecked) {
							checkedItems[which]=isChecked;	//改变被操作列表项的状态	
						}
					});
			//为对话框添加“确定”按钮
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					for(int i=0;i<checkedItems.length;i++){
						if(checkedItems[i]){	//当选项被选择时
							if (!actionDatas.contains(new DemoBean(items[i], true))) {
								actionDatas.add(new DemoBean(items[i], true));
								db.execSQL("INSERT INTO actions VALUES (NULL,?,?,?)", new Object[]{ConditionValue.getApplicationName(),Intelligent_Toy,items[i]});    
							}else{
								Toast.makeText(getBaseContext(),"action already exist", 10).show();
							}	
						}
					}
					
					actionAdapter.notifyDataSetChanged();
				}
			});
			builder.create().show(); // 创建对话框并显示

		}


		/*
		 * 当点击增加的时候
		 */
		if (v == btnAdd) {
			final boolean[] checkedItems= new boolean[] { false,false,false };	//记录各列表项的状态
			final String[] items = new String[] { "Phone", "Intel Edison", "Raspberry Pi" };	//各列表项要显示的内容
			// 显示带单选列表项的对话框
			Builder builder = new AlertDialog.Builder(SettingActionActivity.this);
//			builder.setIcon(R.drawable.advise2);	//设置对话框的图标
			builder.setTitle("Intelligent Toy");	//设置对话框标题
			
			builder.setMultiChoiceItems(items, checkedItems,
					new OnMultiChoiceClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which, boolean isChecked) {
							checkedItems[which]=isChecked;	//改变被操作列表项的状态	
						}
					});
			//为对话框添加“确定”按钮
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
//					String result="";	//用于保存选择结果
					for(int i=0;i<checkedItems.length;i++){
						if(checkedItems[i]){	//当选项被选择时
//							TextView a= (TextView)findViewById(R.id.intelligenttoyname);
							text.setVisibility(View.VISIBLE );
			                text.setText(""+items[i]+" will do");
			                Intelligent_Toy=items[i];
			                ContentValues cv = new ContentValues();  
			            	cv.put("Intelligent_Toy", items[i]);  
			            	db.update("actions", cv, "application = ?", new String[]{ConditionValue.getApplicationName()});  
						}
					}
					
				}
			});
			builder.create().show(); // 创建对话框并显示
			
//		      adpAdapter.add(new ConditionBean(text, true));
//			adpAdapter.notifyDataSetChanged();
		}

		/*
		 * 当点击删除的时候
		 */
		if (v == btnDelete) {

			
			/*
			 * 删除算法最复杂,拿到checkBox选择寄存map
			 */
			Map<Integer, Boolean> map = actionAdapter.getCheckMap();

			// 获取当前的数据数量
			int count = actionAdapter.getCount();

			// 进行遍历
			for (int i = 0; i < count; i++) {

				// 因为List的特性,删除了2个item,则3变成2,所以这里要进行这样的换算,才能拿到删除后真正的position
				int position = i - (count - actionAdapter.getCount());

				if (map.get(i) != null && map.get(i)) {

					DemoBean bean = (DemoBean) actionAdapter.getItem(position);
					String action=((DemoBean) actionAdapter.getItem(position)).getTitle();
					if (bean.isCanRemove()) {
						actionAdapter.getCheckMap().remove(i);
						actionAdapter.remove(position);
						db.delete("actions", "action = ? and application= ?", new String[]{action,ConditionValue.getApplicationName()});  	
					} else {
						map.put(position, false);
					}

				}
			}

			actionAdapter.notifyDataSetChanged();

		}

		/*
		 * 当点击全选的时候
		 */
		if (v == btnSelectAll) {

			if (btnSelectAll.getText().toString().trim().equals("全选")) {

				// 所有项目全部选中
				adpAdapter.configCheckMap(true);

				adpAdapter.notifyDataSetChanged();

				btnSelectAll.setText("全不选");
			} else {

				// 所有项目全部不选中
				adpAdapter.configCheckMap(false);

				adpAdapter.notifyDataSetChanged();

				btnSelectAll.setText("全选");
			}
		}
	}
	/**
	 * 当ListView 子项点击的时候
	 */
	@Override
	public void onItemClick(AdapterView<?> listView, View itemLayout,int position, long id) {

	}
	String toy;

	protected void onResume() {
		// TODO Auto-generated method stub
		int count = actionAdapter.getCount();
	
		// 进行遍历,显示listview
		for (int i = 0; i < count; i++) {
			// 因为List的特性,删除了2个item,则3变成2,所以这里要进行这样的换算,才能拿到删除后真正的position
			int position = i - (count - actionAdapter.getCount());
			actionAdapter.remove(position);			
		}
    	Cursor c = db.rawQuery("SELECT * FROM actions WHERE application = ? ", new String[]{ConditionValue.getApplicationName()});
        while (c.moveToNext()) {
        	actionDatas.add(new DemoBean(c.getString(c.getColumnIndex("action")), true));
        	toy = c.getString(c.getColumnIndex("Intelligent_Toy"));
        }
        c.close();
        actionAdapter.notifyDataSetChanged();
        
        //显示textview 
        if(toy != null){
        	text.setVisibility(View.VISIBLE );
        	text.setText(""+toy+" will do");
        }
		super.onResume();
	}
	
}