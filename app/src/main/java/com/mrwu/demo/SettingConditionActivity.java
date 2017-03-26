package com.mrwu.demo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.mrwu.demo.DemoAdapter.ViewHolder;

public class SettingConditionActivity extends Activity implements OnClickListener,
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
	private Button btnLast = null; //返回上一页
	/**
	 * ListView列表
	 */
	private ListView lvListView = null;

	private ImageView btnDel=null;

	/**
	 * 适配对象
	 */
//	private DemoAdapter adpAdapter = null;
	private ConditionAdapter_v2 adpAdapter ;
	private BluetoothAdapter mBluetoothAdapter;
	String devicename;
//	ArrayList<String> devicename;
	public static final String DB_NAME="conditiondata.db";
	SQLiteDatabase db;
	List<ConditionBean> demoDatas ;
	List<Integer> logicdatas;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setcondition);
		final BluetoothManager bluetoothManager =
		        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//		mBluetoothAdapter = bluetoothManager.getAdapter();
//		mBluetoothAdapter.enable();
//		mBluetoothAdapter.startLeScan(mLeScanCallback);
		
		// 初始化视图
		initView();
		// 初始化控件
		initData();
		//initial data base
		OpenCreateDB();

	}
	
	public void OpenCreateDB(){
		db = openOrCreateDatabase(DB_NAME, this.MODE_PRIVATE, null);
//        db.execSQL("DROP TABLE IF EXISTS conditions");  
//        db.execSQL("DROP TABLE IF EXISTS logicconditions");  
        db.execSQL("CREATE TABLE IF NOT EXISTS conditions (_id INTEGER PRIMARY KEY AUTOINCREMENT, application VARCHAR, sensor VARCHAR, rssivalue INTEGER, rssicondition VARCHAR, gxvalue INTEGER, gxcondition VARCHAR, gyvalue INTEGER, gycondition VARCHAR, gzvalue INTEGER, gzcondition VARCHAR, button INTEGER,conditionlogic INTEGER)");
//        db.execSQL("CREATE TABLE IF NOT EXISTS logicconditions (_id INTEGER PRIMARY KEY AUTOINCREMENT, application VARCHAR, logic INTEGER)");

	}
	
//	private BluetoothAdapter.LeScanCallback mLeScanCallback =
//	        new BluetoothAdapter.LeScanCallback() {
//	    @Override
//	    public void onLeScan(final BluetoothDevice device, int rssi,
//	            byte[] scanRecord) {
//	        runOnUiThread(new Runnable() {
//	           @Override
//	           public void run() {
//	        	   devicename=device.getName();
////	               devicename.add(device.getName());
////	        	   mLeDeviceListAdapter.addDevice(device);
////	               mLeDeviceListAdapter.notifyDataSetChanged();
//	           }
//	       });
//	   }
//	};

	/**
	 * 初始化控件
	 */
	private void initView() {

//		btnDelete = (ViewGroup) findViewById(R.id.btnDelete);
//		btnDelete.setOnClickListener(this);

		btnAdd = (ViewGroup) findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(this);

		btnNext = (Button) findViewById(R.id.btnNext);
		btnNext.setOnClickListener(this);

		btnLast = (Button) findViewById(R.id.btnLast);
		btnLast.setOnClickListener(this);
//
//		btnSelectAll = (Button) findViewById(R.id.btnSelectAll);
//		btnSelectAll.setOnClickListener(this);

		lvListView = (ListView) findViewById(R.id.lvListView);
		lvListView.setOnItemClickListener(this);

/*		btnDel=(ImageView)findViewById(R.id.btn_del);
		btnDel.setOnClickListener(this);*/

	}

	/**
	 * 初始化视图
	 */
	private void initData() {

		// 模拟假数据
//		List<ConditionBean> demoDatas = new ArrayList<ConditionBean>();
		demoDatas = new ArrayList<ConditionBean>();
		logicdatas = new ArrayList<Integer>();

//		demoDatas.add(new ConditionBean("张三", true));
//		demoDatas.add(new ConditionBean("李四", true));
//		demoDatas.add(new ConditionBean("王五", false));
//		demoDatas.add(new ConditionBean("赵六", true));

		adpAdapter = new ConditionAdapter_v2(this, this,demoDatas,logicdatas );

		lvListView.setAdapter(adpAdapter);

	}

	/**
	 * 按钮点击事件
	 */
	@Override
	public void onClick(View v) {

		/*
		 * 当点击返回的时候
		 */
		if (v == btnNext) {
//			int b=0;
//			db.execSQL("DROP TABLE IF EXISTS logicconditions");  
//	        db.execSQL("CREATE TABLE IF NOT EXISTS logicconditions (_id INTEGER PRIMARY KEY AUTOINCREMENT, application VARCHAR, logic INTEGER)");
			
//			for(Iterator i = logicdatas.iterator();i.hasNext();)
//			{
//				b++;
//				int a = (Integer) i.next();
//				db.execSQL("INSERT INTO logicconditions VALUES (NULL,?,?)", new Object[]{ConditionValue.getApplicationName(),a});
//				Toast.makeText(this,"logic"+a, 10).show();
//			}
//			Toast.makeText(this,""+b, 10).show();
			Intent intent = new Intent(SettingConditionActivity.this, SettingActionActivity.class);
	        startActivity(intent);
			finish();

		}

		//当点击返回上一页
		if (v == btnLast) {
			Intent intent = new Intent(SettingConditionActivity.this, MainActivity.class);
			startActivity(intent);
			finish();

		}

		/*
		 * 当点击增加的时候
		 */
		if (v == btnAdd) {
//			mBluetoothAdapter.startLeScan(mLeScanCallback);
			final boolean[] checkedItems= new boolean[] { false,false,false,false, false,false };	//记录各列表项的状态
			final String[] items = new String[] { "Phone", "Sensor Tag", "Here Beacon", "KoalaTEST",
					"Charge HR"};	//各列表项要显示的内容
//			final String[] items = (String[])devicename.toArray(new String[devicename.size()]);
//			final boolean[] checkedItems= new boolean[devicename.size()] ;
			// 显示带单选列表项的对话框
			Builder builder = new AlertDialog.Builder(SettingConditionActivity.this);
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
//							adpAdapter.add(new ConditionBean(items[i],null, true));
							if (!demoDatas.contains(new ConditionBean(items[i],null,0,true))) {
								adpAdapter.add(new ConditionBean(items[i],null,0,true));
//								demoDatas.add(new ConditionBean(items[i],null, true));
								ConditonDataBase a =new ConditonDataBase(ConditionValue.getApplicationName(), items[i], 0, null, 0, null, 0, null, 0, null,0,0);
								db.execSQL("INSERT INTO conditions VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{a.application,a.sensor,a.rssivalue,a.rssicondition,a.gxvalue,a.gxcondition,a.gyvalue,a.gycondition,a.gzvalue,a.gzcondition,a.button,a.conditionlogic});
							}else{
								Toast.makeText(getBaseContext(),"sensor already exist", 10).show();
							}
							
//							result+=items[i]+"、";	//将选项的内容添加到result中
						}
					}
					
					adpAdapter.notifyDataSetChanged();
				}
			});
			builder.create().show(); // 创建对话框并显示
			
//		      adpAdapter.add(new ConditionBean(text, true));
//			adpAdapter.notifyDataSetChanged();
		}

		// 当点击删除的时候

		/*if (v == btnDelete) {

			
			*//*
			 * 删除算法最复杂,拿到checkBox选择寄存map
			 *//*
			Map<Integer, Boolean> map = adpAdapter.getCheckMap();

			// 获取当前的数据数量
			int count = adpAdapter.getCount();

			// 进行遍历
			for (int i = 0; i < count; i++) {

				// 因为List的特性,删除了2个item,则3变成2,所以这里要进行这样的换算,才能拿到删除后真正的position
				int position = i - (count - adpAdapter.getCount());

				if (map.get(i) != null && map.get(i)) {

					ConditionBean bean = (ConditionBean) adpAdapter.getItem(position);
					String sensor=((ConditionBean) adpAdapter.getItem(position)).getTitle();
					if (bean.isCanRemove()) {
						adpAdapter.getCheckMap().remove(i);
						adpAdapter.remove(position);
						db.delete("conditions", "sensor = ? and application= ?", new String[]{sensor,ConditionValue.getApplicationName()});  
					} else {
						map.put(position, false);
					}

				}
			}

			adpAdapter.notifyDataSetChanged();

		}*/

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
/*		if(v==btnDel)
		{
			Toast.makeText(getBaseContext(),"Deleted catched", 10).show();
		}*/
	}
	/**
	 * 当ListView 子项点击的时候
	 */
	@Override
	public void onItemClick(AdapterView<?> listView, View itemLayout,int position, long id) {
		Intent intent = new Intent(SettingConditionActivity.this, SettingsActivity.class);
        startActivity(intent);
        ConditionValue.setSensor(((ConditionBean) adpAdapter.getItem(position)).getTitle());

	}

	public void refresh()
	{
		adpAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		int count = adpAdapter.getCount();

		// 进行遍历
		for (int i = 0; i < count; i++) {
			// 因为List的特性,删除了2个item,则3变成2,所以这里要进行这样的换算,才能拿到删除后真正的position
			int position = i - (count - adpAdapter.getCount());
			adpAdapter.remove(position);			
		}
    	Cursor c = db.rawQuery("SELECT * FROM conditions WHERE application = ?", new String[]{ConditionValue.getApplicationName()});
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
        	
        	person.button = c.getInt(c.getColumnIndex("button"));
        	if( person.button!= 0)
        	{
        		string = ""+"Button Pressed";
        	}
        	
        	int logic = c.getInt(c.getColumnIndex("conditionlogic"));
//        	list.add(string);
//        	Log.d("msg",string);
//        	if(person.rssicondition!=null)
//        	{
        		adpAdapter.add(new ConditionBean(c.getString(c.getColumnIndex("sensor")),string,logic,true));
//        		demoDatas.add(new ConditionBean(c.getString(c.getColumnIndex("sensor")),string, true));
//        	}
        }
        c.close();
        adpAdapter.notifyDataSetChanged();
		super.onResume();
	}
	
}