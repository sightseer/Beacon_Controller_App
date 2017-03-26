package com.mrwu.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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
import android.widget.TextView;
import android.widget.Toast;

import com.mrwu.demo.DemoAdapter.ViewHolder;

public class SettingExceptionActivity extends Activity implements OnClickListener,
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
	TextView except;

	/**
	 * 清除所有
	 */
	private Button btnNext = null;

	private  Button btnLast = null;

	/**
	 * ListView列表
	 */
	private ListView lvListView = null;
	private ListView actionListView = null;
	private ListView exceptionListView = null;
	
	/**
	 * 适配对象
	 */
//	private DemoAdapter adpAdapter = null;
	private ConditionAdapter adpAdapter ;
	private ActionAdapter actionAdapter ;
	private ExceptionAdapter exceptionAdapter ;

	List<ConditionBean> demoDatas;
	List<Integer> logicdatas;

	public static final String DB_NAME="conditiondata.db";
	SQLiteDatabase db;
	List<ConditionBean> exceptionDatas;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setexception);

		//initial data base
		OpenCreateDB();
		// 初始化视图
		initView();

		// 初始化控件
		initData();

	}

	public void OpenCreateDB(){
		db = openOrCreateDatabase(DB_NAME, this.MODE_PRIVATE, null);
//        db.execSQL("DROP TABLE IF EXISTS exceptions ");  
        db.execSQL("CREATE TABLE IF NOT EXISTS exceptions (_id INTEGER PRIMARY KEY AUTOINCREMENT, application VARCHAR, sensor VARCHAR, rssivalue INTEGER, rssicondition VARCHAR, gxvalue INTEGER, gxcondition VARCHAR, gyvalue INTEGER, gycondition VARCHAR, gzvalue INTEGER, gzcondition VARCHAR, conditionlogic INTEGER)");
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

		except = (TextView)findViewById(R.id.exception);
//
//		btnSelectAll = (Button) findViewById(R.id.btnSelectAll);
//		btnSelectAll.setOnClickListener(this);

		lvListView = (ListView) findViewById(R.id.lvListView);
//		lvListView.setOnItemClickListener(this);

		actionListView = (ListView) findViewById(R.id.actionListView);
//		actionListView.setOnItemClickListener(this);
		
		exceptionListView = (ListView) findViewById(R.id.exceptionListView);
		exceptionListView.setOnItemClickListener(this);

	}

	/**
	 * 初始化视图
	 */
	private void initData() {

		// 模拟假数据
		demoDatas = new ArrayList<ConditionBean>();
		logicdatas = new ArrayList<Integer>();
		List<DemoBean> actionDatas = new ArrayList<DemoBean>();
		exceptionDatas = new ArrayList<ConditionBean>();


//		demoDatas.add(new ConditionBean("Phone","G_X >3", false));
//		demoDatas.add(new ConditionBean("Beacon2",null, false));
//		demoDatas.add(new ConditionBean("Beacon3",null, false));
//		actionDatas.add(new DemoBean("Alarm", false));
//		actionDatas.add(new DemoBean("Send SMS", false));

		Cursor c = db.rawQuery("SELECT * FROM conditions WHERE application = ?", new String[]{ConditionValue.getApplicationName()});
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
        	demoDatas.add(new ConditionBean(c.getString(c.getColumnIndex("sensor")),string,logic,false));
        }
        c.close();
        String toy = null;
        Cursor b = db.rawQuery("SELECT * FROM actions WHERE application = ? ", new String[]{ConditionValue.getApplicationName()});
        while (b.moveToNext()) {
        	actionDatas.add(new DemoBean(b.getString(b.getColumnIndex("action")), false));
        	toy = b.getString(b.getColumnIndex("Intelligent_Toy"));
        }
        b.close();
      //显示textview 
		TextView a= (TextView)findViewById(R.id.intelligenttoyname);
        if(toy == null){
        	a.setVisibility(View.INVISIBLE );
        }
		a.setText(""+toy+" will do");
		adpAdapter = new ConditionAdapter(this, demoDatas,logicdatas);
		actionAdapter = new ActionAdapter(this, actionDatas);
		exceptionAdapter = new ExceptionAdapter(this,exceptionDatas);


		lvListView.setAdapter(adpAdapter);
		actionListView.setAdapter(actionAdapter);
		exceptionListView.setAdapter(exceptionAdapter);


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
//			Intent intent = new Intent(SettingExceptionActivity.this, MainActivity.class);
//	        startActivity(intent);
			finish();

		}

		//当点击返回上一页的时候
		if (v == btnLast) {
			Intent intent = new Intent(SettingExceptionActivity.this, SettingActionActivity.class);
	        startActivity(intent);
			finish();

		}
		/*
		 * 当点击增加的时候
		 */
		if (v == btnAdd) {
			final boolean[] checkedItems= new boolean[] { false,false,false,false, false };	//记录各列表项的状态
			final String[] items = new String[] { "Phone", "Sensor Tag", "Here Beacon", "KoalaTest",
					"Charge HR"};	//各列表项要显示的内容
			// 显示带单选列表项的对话框
			Builder builder = new AlertDialog.Builder(SettingExceptionActivity.this);
//			builder.setIcon(R.drawable.advise2);	//设置对话框的图标
			builder.setTitle("Sensor：");	//设置对话框标题
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
							except.setVisibility(View.VISIBLE );
							if (!exceptionDatas.contains(new ConditionBean(items[i],null,0,true))) {
								exceptionDatas.add(new ConditionBean(items[i],null,0,true));
								ConditonDataBase a =new ConditonDataBase(ConditionValue.getApplicationName(), items[i], 0, null, 0, null, 0, null, 0, null, 0,0);
								db.execSQL("INSERT INTO exceptions VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{a.application,a.sensor,a.rssivalue,a.rssicondition,a.gxvalue,a.gxcondition,a.gyvalue,a.gycondition,a.gzvalue,a.gzcondition,a.conditionlogic});
							}else{
								Toast.makeText(getBaseContext(),"sensor already exist", Toast.LENGTH_SHORT).show();
							}
							
//							result+=items[i]+"、";	//将选项的内容添加到result中
						}
					}
					
					exceptionAdapter.notifyDataSetChanged();
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
			Map<Integer, Boolean> map = exceptionAdapter.getCheckMap();

			// 获取当前的数据数量
			int count = exceptionAdapter.getCount();

			// 进行遍历
			for (int i = 0; i < count; i++) {

				// 因为List的特性,删除了2个item,则3变成2,所以这里要进行这样的换算,才能拿到删除后真正的position
				int position = i - (count - exceptionAdapter.getCount());

				if (map.get(i) != null && map.get(i)) {

					ConditionBean bean = (ConditionBean) exceptionAdapter.getItem(position);
					String sensor=((ConditionBean) exceptionAdapter.getItem(position)).getTitle();
					

					if (bean.isCanRemove()) {
						exceptionAdapter.getCheckMap().remove(i);
						exceptionAdapter.remove(position);
						db.delete("exceptions", "sensor = ? and application= ?", new String[]{sensor,ConditionValue.getApplicationName()});  
					} else {
						map.put(position, false);
					}

				}
			}

			exceptionAdapter.notifyDataSetChanged();

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
		Intent intent = new Intent(SettingExceptionActivity.this, ExceptionValueActivity.class);
        startActivity(intent);
        ConditionValue.setSensor(((ConditionBean) exceptionAdapter.getItem(position)).getTitle());


	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		int count = exceptionAdapter.getCount();

		// 进行遍历
		for (int i = 0; i < count; i++) {
			// 因为List的特性,删除了2个item,则3变成2,所以这里要进行这样的换算,才能拿到删除后真正的position
			int position = i - (count - exceptionAdapter.getCount());
			exceptionAdapter.remove(position);			
		}
    	Cursor c = db.rawQuery("SELECT * FROM exceptions WHERE application = ?", new String[]{ConditionValue.getApplicationName()});
        while (c.moveToNext()) {
        	ConditonDataBase person = new ConditonDataBase();
        	person.rssivalue = c.getInt(c.getColumnIndex("rssivalue"));
        	person.rssicondition=c.getString(c.getColumnIndex("rssicondition"));
//        	person.age = c.getInt(c.getColumnIndex("age"));
//        	person.info = c.getString(c.getColumnIndex("info"));
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
        	
        	int logic = c.getInt(c.getColumnIndex("conditionlogic"));
//        	list.add(string);
//        	Log.d("msg",string);
//        	if(person.rssicondition!=null)
//        	{
        	except.setVisibility(View.VISIBLE );
        	exceptionDatas.add(new ConditionBean(c.getString(c.getColumnIndex("sensor")),string,logic ,true));
//        	}
        }
        c.close();
		exceptionAdapter.notifyDataSetChanged();
		super.onResume();
	}
}