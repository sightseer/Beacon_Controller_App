package com.mrwu.demo;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;


import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Notification.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.MediaStore.Audio;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mrwu.demo.DemoAdapter.ViewHolder;

public class MainActivity extends Activity implements OnClickListener,
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
	 * SelectAll
	 */
	private Button btnSelectAll = null;

	/**
	 * Apply Button
	 */
	private Button btnApply = null;

	/**
	 * ListView列表
	 */
	private ListView lvListView = null;

	/**
	 * 适配对象
	 */
	private DemoAdapter adpAdapter = null;

	protected static final String TAG = "ScanActivity";
    private final String MODE_SCANNING = "Stop Scanning";
    private final String MODE_STOPPED = "Apply";    
    String SensorTag =
//    		 b4 99 4c 64 c8 68
    		"B4:99:4C:64:C8:68";
    String HereBeacon= 
//    		 20 c3 8f 8d 7e 0c
    		"20:C3:8F:8D:7E:0C";
    String Koala = 
//    		 d5 56 9e 24 f9 63
    		"D5:56:9E:24:F9:63";
    String ChargeHR = 
//    		 c0 95 6f 2e 99 89
    		"F9:3E:08:56:3B:CA";
	List<String> application = new ArrayList<String>();
	
	List<DemoBean> actionDatas = new ArrayList<DemoBean>();
	Map<Integer,List<DemoBean>> actionmap = new HashMap<Integer, List<DemoBean>>();
    
	List<ConditionBean> conditionDatas = new ArrayList<ConditionBean>();
	List<ConditionBean> exceptionDatas = new ArrayList<ConditionBean>();;
	
	
	//private FileHelper fileHelper; 
	List<DemoBean> demoDatas;
    private int eventNum = 1;
    private int i = 1;
    volatile Boolean beacon1,beacon2,beacon3,beaconblack;
    BluetoothAdapter mBluetoothAdapter ;

	public static final String DB_NAME="conditiondata.db";
	SQLiteDatabase db;

	private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    private CharSequence tickerText = null;
    private Uri soundURI = Uri.withAppendedPath(
			Audio.Media.INTERNAL_CONTENT_URI, "6");
    
    public File Outputdir;
    public File Outputfile;
    public FileOutputStream f;
    
    boolean conditionvalue = true,exceptionvalue = true;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Firebase.setAndroidContext(this);
		
		OpenCreateDB();
		// 初始化视图
		initView();
		

		// 初始化控件
		initData();
		
		initNotification();

	}

	private void initNotification() {
		final Firebase iToyRef = new Firebase("https://itoyserver.firebaseio.com/");
		final Firebase notifRef = iToyRef.child("notification");
       
		Outputdir = new File(Environment.getExternalStorageDirectory().getPath(), "/itoy");
		Outputdir.mkdirs();

		notifRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.i("Test", "SnapShot Are = " + snapshot);
                //Log.i("Test", "SnapShot Are = " + snapshot.child(Integer.toString(1)).child("Status").getValue());

                for (int jk = 1; jk<=snapshot.getChildrenCount() ;jk++){
                    if(snapshot.child(Integer.toString(jk)).child("Status").getValue().toString()=="1"){
                        rulesendNotification("Rule Number " +Integer.toString(jk)+" Trigerred" ,jk);

                        //Check if image Data Exist
                        if(snapshot.child(Integer.toString(jk)).child("Data").getValue().toString()=="1"){
                            //ToDo
                            //Get Image From snapshot.child(Integer.toString(jk)).child("Image").getValue()
                            Log.i("Test", "Image Are: " + snapshot.child(Integer.toString(jk)).child("Image").getValue());

                            byte[] data = Base64.decode(snapshot.child(Integer.toString(jk)).child("Image").getValue().toString(), Base64.DEFAULT);
                            Outputfile = new File(Outputdir, "pic.jpg");
                            try (OutputStream stream = new FileOutputStream(Outputfile)) {
                                stream.write(data);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            sendNotificationImage("Image From iToy Delivered", jk);
                        }
                        notifRef.child(Integer.toString(jk)).child("Status").setValue(0);
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

                Log.i("Test", "Error Are = " + firebaseError.getMessage());
            }
        });

	}

	public void OpenCreateDB(){
		db = openOrCreateDatabase(DB_NAME, this.MODE_PRIVATE, null);
//        db.execSQL("DROP TABLE IF EXISTS applications ");  
        db.execSQL("CREATE TABLE IF NOT EXISTS applications (_id INTEGER PRIMARY KEY AUTOINCREMENT, application VARCHAR)");
    }

	/**
	 * 初始化控件
	 */
	private void initView() {

	//	btnDelete = (ViewGroup) findViewById(R.id.btnDelete);
	//	btnDelete.setOnClickListener(this);

		btnAdd = (ViewGroup) findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(this);

		btnApply = (Button) findViewById(R.id.btnApply);
		btnApply.setOnClickListener(this);

	//	btnSelectAll = (Button) findViewById(R.id.btnSelectAll);
	//	btnSelectAll.setOnClickListener(this);

		lvListView = (ListView) findViewById(R.id.lvListView);
		lvListView.setOnItemClickListener(this);

	}

	/**
	 * 初始化视图
	 */
	private void initData() {

		// 模拟假数据
		demoDatas = new ArrayList<DemoBean>();

		demoDatas.add(new DemoBean("Life Bring", true));
		demoDatas.add(new DemoBean("Diary", true));

		adpAdapter = new DemoAdapter(this,this, demoDatas);

		lvListView.setAdapter(adpAdapter);

	}

	public void refresh()
	{
		adpAdapter.notifyDataSetChanged();
	}

	/**
	 * 按钮点击事件
	 */
	@Override
	public void onClick(View v) {

		/*
		 * 当点击返回的时候
		 */
		if (v == btnApply) {
			BluetoothManager bluetoothManager =
			        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			mBluetoothAdapter = bluetoothManager.getAdapter();
			mBluetoothAdapter.enable();
//			mBluetoothAdapter.startLeScan(mLeScanCallback);
			Map<Integer, Boolean> map = adpAdapter.getCheckMap();
			// 获取当前的数据数量
			int count = adpAdapter.getCount();
			application.clear();
			// 进行遍历
			for (int i = 0; i < count; i++) {
				// 因为List的特性,删除了2个item,则3变成2,所以这里要进行这样的换算,才能拿到删除后真正的position
				int position = i - (count - adpAdapter.getCount());
				if (map.get(i) != null && map.get(i)) {
					DemoBean bean = (DemoBean) adpAdapter.getItem(position);
					application.add(bean.getTitle());
				}
			}
			
			JSONObject rule = new JSONObject();//创建rule的JSONObject
			JSONObject rulephone = new JSONObject();	
			 
			int rulenumber = 1; //记录生成JSONObject的rule数量
			int rulephonenumber = 1;

			String toy = null ;
			 
			for(int i = 0; i < application.size(); i++) //遍历被选中的application
			{
				//查找exceptions table, 将rule 存入exceptionDatas List里面
				exceptionDatas.clear();
				Cursor c = db.rawQuery("SELECT * FROM exceptions WHERE application = ?", new String[]{application.get(i)});
		        while (c.moveToNext()) {
		        	ConditonDataBase person = new ConditonDataBase();
		        	person.rssivalue = c.getInt(c.getColumnIndex("rssivalue"));
		        	person.rssicondition=c.getString(c.getColumnIndex("rssicondition"));
		        	String string = null;
		        	if( person.rssicondition!= null && person.rssicondition.length() != 0)
		        	{
		        		string = ""+"RSSI "+person.rssicondition+" "+person.rssivalue;
		        	}
		        	person.gxvalue = c.getInt(c.getColumnIndex("gxvalue"));
		        	person.gxcondition=c.getString(c.getColumnIndex("gxcondition"));
		        	if( person.gxcondition!= null && person.gxcondition.length() != 0)
		        	{
		        		string = ""+"Gyro_x "+person.gxcondition+" "+person.gxvalue;
		        	}
		        	person.gyvalue = c.getInt(c.getColumnIndex("gyvalue"));
		        	person.gycondition=c.getString(c.getColumnIndex("gycondition"));
		        	if( person.gycondition!= null && person.gycondition.length() != 0)
		        	{
		        		string = ""+"Gyro_y "+person.gycondition+" "+person.gyvalue;
		        	}
		        	person.gzvalue = c.getInt(c.getColumnIndex("gzvalue"));
		        	person.gzcondition=c.getString(c.getColumnIndex("gzcondition"));
		        	if( person.gzcondition!= null && person.gzcondition.length() != 0)
		        	{
		        		string = ""+"Gyro_z "+person.gzcondition+" "+person.gzvalue;
		        	}
		        	
		        	int logic = c.getInt(c.getColumnIndex("conditionlogic"));
		        	exceptionDatas.add(new ConditionBean(c.getString(c.getColumnIndex("sensor")),string,logic ,true));
		        }
		        c.close();
				
				
				 //查找conditions table, 将rule 存入conditionDatas List里面
				 conditionDatas.clear();
				 Cursor d = db.rawQuery("SELECT * FROM conditions WHERE application = ?", new String[]{application.get(i)});  
				 while (d.moveToNext()) {
					 ConditonDataBase person = new ConditonDataBase();
					 person.rssivalue = d.getInt(d.getColumnIndex("rssivalue"));
					 person.rssicondition=d.getString(d.getColumnIndex("rssicondition"));
					 String string = null;
					 if( person.rssicondition!= null && person.rssicondition.length() != 0){
						 string = ""+"RSSI "+person.rssicondition+" "+person.rssivalue;
					 }
					 person.gxvalue = d.getInt(d.getColumnIndex("gxvalue"));
					 person.gxcondition=d.getString(d.getColumnIndex("gxcondition"));
					 if( person.gxcondition!= null && person.gxcondition.length() != 0){
						 string = ""+"Gyro_x "+person.gxcondition+" "+person.gxvalue;
					 }
					 person.gyvalue = d.getInt(d.getColumnIndex("gyvalue"));
					 person.gycondition=d.getString(d.getColumnIndex("gycondition"));
					 if( person.gycondition!= null && person.gycondition.length() != 0){
						 string = ""+"Gyro_y "+person.gycondition+" "+person.gyvalue;	
					 }
					 person.gzvalue = d.getInt(d.getColumnIndex("gzvalue"));
					 person.gzcondition=d.getString(d.getColumnIndex("gzcondition"));
					 if( person.gzcondition!= null && person.gzcondition.length() != 0){	
						 string = ""+"Gyro_z "+person.gzcondition+" "+person.gzvalue;
					 }
					 person.button = d.getInt(d.getColumnIndex("button"));
					 if( person.button!= 0){
						 string = ""+"Button Pressed";	
					 }
					 int logic = d.getInt(d.getColumnIndex("conditionlogic"));
					 conditionDatas.add(new ConditionBean(d.getString(d.getColumnIndex("sensor")),string,logic,false));   
				 }   
				 d.close();
			        
				 //查找actions table, 将rule 存入actionDatas List里面
				 actionDatas.clear();
				 Cursor b = db.rawQuery("SELECT * FROM actions WHERE application = ? ", new String[]{application.get(i)});
				 while (b.moveToNext()) {
					 actionDatas.add(new DemoBean(b.getString(b.getColumnIndex("action")), false));
					 toy = b.getString(b.getColumnIndex("Intelligent_Toy"));  
				 }
				 b.close();
				 
				// string比较相等     apply rule on phone
				 //rule to JSON
				 if(toy != null && toy.equals("Phone") && conditionvalue && exceptionvalue)
				 {
					 SaveRuleApplyOnPhoneInJSON(rulephone,rulephonenumber,application.get(i));
					 rulephonenumber++;
					 Toast.makeText(getBaseContext(),"Apply "+application.get(i)+" on Phone",Toast.LENGTH_SHORT).show();

					 
				 }else if(toy != null){
					 JSONObject rule1 = new JSONObject();
					//Create Action JSON
					 JSONObject action = new JSONObject();//1:SendNotify 2:Alarm 3:Take Photo
					 for(int j= 0; j < actionDatas.size(); j++ ){
						 switch(actionDatas.get(j).getTitle()){
						 case "Alarm":
							 try {
								 action.put(Integer.toString(j+1), 2);
							 } catch (JSONException e) {
								 e.printStackTrace();
							 }
							 break;
						 case "Take Photo":
							 try {
								 action.put(Integer.toString(j+1), 3);
							 } catch (JSONException e) {
								 e.printStackTrace();
							 }
							 break;
						 case "Send Notify":
							 try {
								 action.put(Integer.toString(j+1), 1);
							 } catch (JSONException e) {
								 e.printStackTrace();
							 }
							 break;
						 default:
							 System.out.println("default");
						 }
					 }
					 try {
						 action.put("Number", actionDatas.size());
						 rule1.put("Action", action);
					 } catch (JSONException e) {
						 e.printStackTrace();
					 }
					 //Create Exception JSON
					 JSONObject excep = new JSONObject();
					 int ExceptnNumber;
					 for(ExceptnNumber = 0; ExceptnNumber < exceptionDatas.size(); ExceptnNumber++ ){
						 System.out.println("-----------------");
						 JSONObject excepunit = new JSONObject();
						 if(exceptionDatas.get(ExceptnNumber).getValue() != null)
						 {
							 exceptionvalue = true;
						 String[] sourceStrArray = exceptionDatas.get(ExceptnNumber).getValue().split(" ");
						 String character = null;
						 switch(sourceStrArray[0]){
						 case "RSSI":
							 character = "ri"; 
							 break;
						 case "Gyro_x":
							 character = "gx"; 
							 break;
						 case "Gyro_y":
							 character = "gy"; 
							 break;
						 case "Gyro_z":
							 character = "gz"; 
							 break;	 
						 default:
							 System.out.println("default");
						 }
						 int operator = 0;
						 switch(sourceStrArray[1]){
						 case "=":
							 operator = 1; 
							 break;
						 case ">":
							 operator = 2; 
							 break;
						 case "<":
							 operator = 3; 
							 break; 
						 default:
							 System.out.println("default");
						 }
						 String device = null;
						 switch(exceptionDatas.get(ExceptnNumber).getTitle()){
						 case "Sensor Tag":
							 device = "b4994c64c868"; 
							 break;
						 case "Here Beacon":
							 device = "20c38f8d7e0c"; 
							 break;
						 case "Koala":
							 device = "d5569e24f963"; 
							 break;
						 case "Charge HR":
							 device = "c0956f2e9989"; 
							 break;	 
						 default:
							 System.out.println("default");
						 }
						 try {
							 excepunit.put("characteristic", character);
							 excepunit.put("device", device);
							 excepunit.put("operator", operator);
							 excepunit.put("treshold", Integer.parseInt(sourceStrArray[2]));
							 excep.put(Integer.toString(ExceptnNumber+1), excepunit);
						 } catch (JSONException e) {
							 e.printStackTrace();
						 }
						 }else{
							 Toast.makeText(getBaseContext(),"Exception Value Null ",Toast.LENGTH_SHORT).show();
							 exceptionvalue = false;
						 }
					 }
					 JSONObject exceplogic = new JSONObject();
					 for(int j = 1; j < exceptionDatas.size(); j++){
						 System.out.println("+++++++++++++");
						 System.out.println(exceptionDatas.get(j).getLogic()+1);
						 try {
							 exceplogic.put(Integer.toString(j), exceptionDatas.get(j).getLogic()+1);
						 } catch (JSONException e) {
							 e.printStackTrace();
						 }
					 }
					 try {
						excep.put(Integer.toString(ExceptnNumber+1), exceplogic);
						excep.put("Number",ExceptnNumber);
						rule1.put("Exception", excep);
						System.out.println("+++++++++++++"+ExceptnNumber);	
					 } catch (JSONException e) {
						 e.printStackTrace();
					 }
					 
					//Create Condition JSON
					 JSONObject condition = new JSONObject();
					 int ConditionNumber;
					 for(ConditionNumber = 0; ConditionNumber < conditionDatas.size(); ConditionNumber++ ){
						 System.out.println("-----------------");
						 JSONObject conditionunit = new JSONObject();
						 if(conditionDatas.get(ConditionNumber).getValue() != null)
						 {
							 conditionvalue = true;
						 String[] sourceStrArray = conditionDatas.get(ConditionNumber).getValue().split(" ");
						 String character = null;
						 switch(sourceStrArray[0]){
						 case "RSSI":
							 character = "ri"; 
							 break;
						 case "Gyro_x":
							 character = "gx"; 
							 break;
						 case "Gyro_y":
							 character = "gy"; 
							 break;
						 case "Gyro_z":
							 character = "gz"; 
							 break;	 
						 default:
							 System.out.println("default");
						 }
						 int operator = 0;
						 switch(sourceStrArray[1]){
						 case "=":
							 operator = 1; 
							 break;
						 case ">":
							 operator = 2; 
							 break;
						 case "<":
							 operator = 3; 
							 break; 
						 default:
							 System.out.println("default");
						 }
						 String device = null;
						 switch(conditionDatas.get(ConditionNumber).getTitle()){
						 case "Sensor Tag":
							 device = "b4994c64c868"; 
							 break;
						 case "Here Beacon":
							 device = "20c38f8d7e0c"; 
							 break;
						 case "Koala":
							 device = "d5569e24f963"; 
							 break;
						 case "Charge HR":
							 device = "c0956f2e9989"; 
							 break;	 
						 default:
							 System.out.println("default");
						 }
						 try {
							 conditionunit.put("characteristic", character);
							 conditionunit.put("device", device);
							 conditionunit.put("operator", operator);
							 conditionunit.put("treshold", Integer.parseInt(sourceStrArray[2]));
							 condition.put(Integer.toString(ConditionNumber+1), conditionunit);
						 } catch (JSONException e) {
							 e.printStackTrace();
						 }
						 }else{
							 Toast.makeText(getBaseContext(),"Condition Value Null ",Toast.LENGTH_SHORT).show();
							 conditionvalue = false;
						 }
						 
					 }
					 JSONObject conditionlogic = new JSONObject();
					 for(int j = 1; j < conditionDatas.size(); j++){
						 System.out.println("+++++++++++++");
						 System.out.println(conditionDatas.get(j).getLogic()+1);
						 try {
							 conditionlogic.put(Integer.toString(j), conditionDatas.get(j).getLogic()+1);
						 } catch (JSONException e) {
							 e.printStackTrace();
						 }
					 }
					 try {
						condition.put(Integer.toString(ConditionNumber+1), conditionlogic);
						condition.put("Number",ConditionNumber);
						rule1.put("LogicUnit", condition);
						rule1.put("Status", 1);
						rule.put(Integer.toString(rulenumber), rule1);
						System.out.println("+++++++++++++"+ConditionNumber);	
					 } catch (JSONException e) {
						 e.printStackTrace();
					 }
					
					 rulenumber++;	 
				 }
			 }
			try {
				rule.put("Number", rulenumber-1);
				rulephone.put("Number", rulephonenumber-1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if(toy != null && toy.equals("Phone") && conditionvalue && exceptionvalue)
			{
				 toggleScanState(rulephone);//BLE scan
				 for(Entry<String, Object> entry:RSSIvalue.entrySet()){    
				        System.out.println(entry.getKey()+"--->"+entry.getValue());    
				   }

				 System.out.println(RSSIvalue.get(Koala));   
				 Firebase mrefphone=new Firebase("https://itoynctu.firebaseio.com/rulephone");
				 String jsonphoneString = rulephone.toString(); 
				 Map<String,Object> mapphone = new Gson().fromJson(jsonphoneString, new TypeToken<HashMap<String, Object>>(){}.getType());				
				 mrefphone.setValue(mapphone);

			}else if(toy != null && conditionvalue && exceptionvalue){
				//send JSON to FireBase Cloud
				Firebase mref=new Firebase("https://itoyserver.firebaseio.com/rule");
				String jsonString = rule.toString(); 
				Map<String,Object> map2 = new Gson().fromJson(jsonString, new TypeToken<HashMap<String, Object>>(){}.getType());
				mref.setValue(map2);				 
				Toast.makeText(getBaseContext(),"Send rule to Intelligent Toy",Toast.LENGTH_SHORT).show();
				for(int i = 1; i <= rulenumber-1; i++){
					
				}
				
			}

			for(Entry<String, Object> entry:RSSIvalue.entrySet()){    
		        System.out.println(entry.getKey()+"--->"+entry.getValue());    
		   }
			
			
			

			 
		}

		/*
		 * 当点击增加的时候
		 */
		if (v == btnAdd) {
			LayoutInflater inflater=getLayoutInflater();
		      final View layout=inflater.inflate(R.layout.edittext_dialog,(ViewGroup)findViewById(R.id.request_dialog));
		      new AlertDialog.Builder(MainActivity.this)
		      .setTitle("Application Name").setView(layout).
		      setPositiveButton("OK",new DialogInterface.OnClickListener(){

		       @Override
		       public void onClick(DialogInterface dialog, int which) {
		        // TODO Auto-generated method stub
		    	   EditText et_search = (EditText)layout.findViewById(R.id.request_content);
		    	   String text = et_search.getText().toString();
		    	   if(null == text || "".equals(text.trim())) {
		    		   Toast.makeText(getBaseContext(),"application name null",Toast.LENGTH_SHORT).show();
		                 
		            }else {
		            	if(!demoDatas.contains(new  DemoBean(text, true)))
		            	{
		            		adpAdapter.add(new DemoBean(text, true));
//		            		db.execSQL("INSERT INTO applications VALUES (NULL,'text')");
		            		db.execSQL("INSERT INTO applications VALUES (NULL,?)", new Object[]{text});
		                    
		            		
		            	}else{
		            		Toast.makeText(getBaseContext(),"application already exist",Toast.LENGTH_SHORT).show();
		            	}
		            }
        		adpAdapter.notifyDataSetChanged();
		       }

		      })
		      .setNegativeButton("Cancel",new DialogInterface.OnClickListener(){

		       @Override
		       public void onClick(DialogInterface dialog, int which) {
		        // TODO Auto-generated method stub
		        dialog.dismiss();
			   }
		       
		      }).show();



		}

		//  当点击删除的时候
/*		if (v == btnDelete) {
			
			// 删除算法最复杂,拿到checkBox选择寄存map
			Map<Integer, Boolean> map = adpAdapter.getCheckMap();

			// 获取当前的数据数量
			int count = adpAdapter.getCount();

			// 进行遍历
			for (int i = 0; i < count; i++) {

				// 因为List的特性,删除了2个item,则3变成2,所以这里要进行这样的换算,才能拿到删除后真正的position
				int position = i - (count - adpAdapter.getCount());

				if (map.get(i) != null && map.get(i)) {

					DemoBean bean = (DemoBean) adpAdapter.getItem(position);

					if (bean.isCanRemove()) {
						db.delete("applications", "application= ?", new String[]{bean.getTitle()});
						db.delete("conditions", "application= ?", new String[]{bean.getTitle()});
						db.delete("actions", "application= ?", new String[]{bean.getTitle()});
						db.delete("exceptions", "application= ?", new String[]{bean.getTitle()});
						adpAdapter.getCheckMap().remove(i);
						adpAdapter.remove(position);
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
	/*	if (v == btnSelectAll) {

			if (btnSelectAll.getText().toString().trim().equals("Select All")) {

				// 所有项目全部选中
				adpAdapter.configCheckMap(true);

				adpAdapter.notifyDataSetChanged();

				btnSelectAll.setText("Clear All");
			} else {

				// 所有项目全部不选中
				adpAdapter.configCheckMap(false);

				adpAdapter.notifyDataSetChanged();

				btnSelectAll.setText("Select All");
			}
		}*/
	}
	private void SaveRuleApplyOnPhoneInJSON(JSONObject rulephone, int rulephonenumber, String applicationname) {
		 JSONObject rule1 = new JSONObject();
		//Create Action JSON
		 JSONObject action = new JSONObject();//1:SendNotify 2:Alarm 3:Take Photo
		 for(int j= 0; j < actionDatas.size(); j++ ){
			 switch(actionDatas.get(j).getTitle()){
			 case "Alarm":
				 try {
					 action.put(Integer.toString(j+1), 2);
				 } catch (JSONException e) {
					 e.printStackTrace();
				 }
				 break;
			 case "Take Photo":
				 try {
					 action.put(Integer.toString(j+1), 3);
				 } catch (JSONException e) {
					 e.printStackTrace();
				 }
				 break;
			 case "Send Notify":
				 try {
					 action.put(Integer.toString(j+1), 1);
				 } catch (JSONException e) {
					 e.printStackTrace();
				 }
				 break;
			 default:
				 System.out.println("default");
			 }
		 }
		 try {
			 action.put("Number", actionDatas.size());
			 rule1.put("Action", action);
		 } catch (JSONException e) {
			 e.printStackTrace();
		 }
		 //Create Exception JSON
		 JSONObject excep = new JSONObject();
		 int ExceptnNumber;
		 for(ExceptnNumber = 0; ExceptnNumber < exceptionDatas.size(); ExceptnNumber++ ){
			 System.out.println("-----------------");
			 JSONObject excepunit = new JSONObject();
			 if(exceptionDatas.get(ExceptnNumber).getValue() != null)
			 {
				 exceptionvalue = true;
			 String[] sourceStrArray = exceptionDatas.get(ExceptnNumber).getValue().split(" ");
			 String character = null;
			 switch(sourceStrArray[0]){
			 case "RSSI":
				 character = "ri"; 
				 break;
			 case "Gyro_x":
				 character = "gx"; 
				 break;
			 case "Gyro_y":
				 character = "gy"; 
				 break;
			 case "Gyro_z":
				 character = "gz"; 
				 break;	 
			 default:
				 System.out.println("default");
			 }
			 int operator = 0;
			 switch(sourceStrArray[1]){
			 case "=":
				 operator = 1; 
				 break;
			 case ">":
				 operator = 2; 
				 break;
			 case "<":
				 operator = 3; 
				 break; 
			 default:
				 System.out.println("default");
			 }
			 String device = null;
			 switch(exceptionDatas.get(ExceptnNumber).getTitle()){
			 case "Sensor Tag":
				 device = SensorTag; 
				 break;
			 case "Here Beacon":
				 device = HereBeacon; 
				 break;
			 case "Koala":
				 device = Koala; 
				 break;
			 case "Charge HR":
				 device = ChargeHR; 
				 break;	 
			 default:
				 System.out.println("default");
			 }
			 try {
				 excepunit.put("characteristic", character);
				 excepunit.put("device", device);
				 excepunit.put("operator", operator);
				 excepunit.put("treshold", Integer.parseInt(sourceStrArray[2]));
				 excep.put(Integer.toString(ExceptnNumber+1), excepunit);
			 } catch (JSONException e) {
				 e.printStackTrace();
			 }
			 }else{
				 Toast.makeText(getBaseContext(),"Exception Value Null ",Toast.LENGTH_SHORT).show();
				 exceptionvalue = false;
			 }
		 }
		 JSONObject exceplogic = new JSONObject();
		 for(int j = 1; j < exceptionDatas.size(); j++){
			 System.out.println("+++++++++++++");
			 System.out.println(exceptionDatas.get(j).getLogic()+1);
			 try {
				 exceplogic.put(Integer.toString(j), exceptionDatas.get(j).getLogic()+1);
			 } catch (JSONException e) {
				 e.printStackTrace();
			 }
		 }
		 try {
			excep.put(Integer.toString(ExceptnNumber+1), exceplogic);
			excep.put("Number",ExceptnNumber);
			rule1.put("Exception", excep);
			System.out.println("+++++++++++++"+ExceptnNumber);	
		 } catch (JSONException e) {
			 e.printStackTrace();
		 }
		 
		//Create Condition JSON
		 JSONObject condition = new JSONObject();
		 int ConditionNumber;
		 for(ConditionNumber = 0; ConditionNumber < conditionDatas.size(); ConditionNumber++ ){
			 System.out.println("-----------------");
			 JSONObject conditionunit = new JSONObject();
			 if(conditionDatas.get(ConditionNumber).getValue() != null)
			 {
				 conditionvalue = true;
			 String[] sourceStrArray = conditionDatas.get(ConditionNumber).getValue().split(" ");
			 String character = null;
			 switch(sourceStrArray[0]){
			 case "RSSI":
				 character = "ri"; 
				 break;
			 case "Gyro_x":
				 character = "gx"; 
				 break;
			 case "Gyro_y":
				 character = "gy"; 
				 break;
			 case "Gyro_z":
				 character = "gz"; 
				 break;	 
			 default:
				 System.out.println("default");
			 }
			 int operator = 0;
			 switch(sourceStrArray[1]){
			 case "=":
				 operator = 1; 
				 break;
			 case ">":
				 operator = 2; 
				 break;
			 case "<":
				 operator = 3; 
				 break; 
			 default:
				 System.out.println("default");
			 }
			 String device = null;
			 switch(conditionDatas.get(ConditionNumber).getTitle()){
			 case "Sensor Tag":
				 device = SensorTag; 
				 break;
			 case "Here Beacon":
				 device = HereBeacon; 
				 break;
			 case "Koala":
				 device = Koala; 
				 break;
			 case "Charge HR":
				 device = ChargeHR; 
				 break;	 
			 default:
				 System.out.println("default");
			 }
			 try {
				 conditionunit.put("characteristic", character);
				 conditionunit.put("device", device);
				 conditionunit.put("operator", operator);
				 conditionunit.put("treshold", Integer.parseInt(sourceStrArray[2]));
				 condition.put(Integer.toString(ConditionNumber+1), conditionunit);
			 } catch (JSONException e) {
				 e.printStackTrace();
			 }
			 }else{
				 Toast.makeText(getBaseContext(),"Condition Value Null ",Toast.LENGTH_SHORT).show();
				 conditionvalue = false;
			 }
		 }
		 JSONObject conditionlogic = new JSONObject();
		 for(int j = 1; j < conditionDatas.size(); j++){
			 System.out.println("+++++++++++++");
			 System.out.println(conditionDatas.get(j).getLogic()+1);
			 try {
				 conditionlogic.put(Integer.toString(j), conditionDatas.get(j).getLogic()+1);
			 } catch (JSONException e) {
				 e.printStackTrace();
			 }
		 }
		 try {
			condition.put(Integer.toString(ConditionNumber+1), conditionlogic);
			condition.put("Number",ConditionNumber);
			rule1.put("LogicUnit", condition);
			rule1.put("Status", 1);
			rule1.put("Rulename", applicationname);
			rulephone.put(Integer.toString(rulephonenumber), rule1);
			System.out.println("+++++++++++++"+ConditionNumber);	
		 } catch (JSONException e) {
			 e.printStackTrace();
		 }
		 
	}

	/**
	 * ListView item Click
	 */
	@Override
	public void onItemClick(AdapterView<?> listView, View itemLayout,int position, long id) {
		Intent intent = new Intent(MainActivity.this, SettingConditionActivity.class);
        startActivity(intent);

       ConditionValue.setApplicationName(((DemoBean) adpAdapter.getItem(position)).getTitle());
//        if (itemLayout.getTag() instanceof ViewHolder) {
//			ViewHolder holder = (ViewHolder) itemLayout.getTag();
//			// 会自动触发CheckBox的checked事件
//			holder.cbCheck.toggle();
//		}

	}
	
	private void toggleScanState(JSONObject rulephone) {
		Button scanButton = getScanButton();
		String currentState = scanButton.getText().toString();
		if (currentState.equals(MODE_SCANNING)) {
			stopScanning(scanButton);
		} else {
			startScanning(scanButton,rulephone);
		}
	}
	private Button getScanButton() {
		return (Button)findViewById(R.id.btnApply);
	}

	Map<String, Object> RSSIvalue = new HashMap<String, Object>();
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                	if (rssi > -70) {
//	        			if(device.getAddress().equals(BEACON1)){
//	        				beacon1=true;
//	        			}
//	        			if(device.getAddress().equals(BEACON2)){
//	        				beacon2=true;
//	        			}
//	        			if(device.getAddress().equals(BEACON7)){
//	        				beacon3=true;	
//	        			}
//	        			if(device.getAddress().equals(BEACONBLACK)){
//	        				beaconblack=true;
//	        			}
//	        			
//	        		}
                	RSSIvalue.put(device.getAddress(), -rssi);
                	
                }
            });
        }
    };

	private void startScanning( final Button scanButton, final JSONObject rulephone) {
		// Set UI elements to the correct state.
		scanButton.setText(MODE_SCANNING);
//		((EditText)findViewById(R.id.scanText)).setText("");
//		logToDisplay("*** New Scan ***");
		//Start scanning again.
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(scanButton.getText().equals(MODE_SCANNING)){
//					beacon1=false;
//					beacon2=false;
//					beacon3=false;
//					beaconblack=false;
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
//					if((beacon1||beacon2||beacon3)&&!(beacon1&&beacon2&&beacon3)){
//						if(beacon1==false&&beacon2==false&&beacon3==true)
//							sendNotification("You forget beacon1 and beacon2");
//						if(beacon1==false&&beacon2==true&&beacon3==false)
//							sendNotification("You forget beacon1 and beacon3");
//						if(beacon1==true&&beacon2==false&&beacon3==false)
//							sendNotification("You forget beacon2 and beacon3");
//						if(beacon1==false&&beacon2==true&&beacon3==true)
//							sendNotification("You forget beacon1");
//						if(beacon1==true&&beacon2==false&&beacon3==true)
//							sendNotification("You forget beacon2");
//						if(beacon1==true&&beacon2==true&&beacon3==false)
//							sendNotification("You forget beacon3");
//					}
//					if(!beaconblack)
//					{
//						sendNotification("You lost your key");
//					}
					RuleJSONParse(rulephone);
						
				}
			}
		}).start();
		mBluetoothAdapter.startLeScan(mLeScanCallback);
		
		 
//		iBeaconManager.setRangeNotifier(new RangeNotifier() {
//        	@Override 
//        	public void didRangeBeaconsInRegion(final Collection<IBeacon> iBeacons, Region region) {
//        		sendNotification("You forget beacon4");
//        		
//        		if (iBeacons.size() > 0) {
//        			Iterator <IBeacon> beaconIterator = iBeacons.iterator();
//        			while (beaconIterator.hasNext()) {
//        				IBeacon iBeacon = beaconIterator.next();
// 		        		int rssi = iBeacon.getRssi();
// 		        		int thresh = -100;
// 		        		sendNotification("You forget beacon4");
//		        		if (rssi > thresh) {
//		        			if(iBeacon.getMajor() ==13){
//		        			beacon13=true;
//		        			}
//		        			if(iBeacon.getMajor() ==2){
//			        			beacon2=true;
//			        			}
//		        			if(iBeacon.getMajor() ==4){
//			        			beacon4=true;
//			        			}
//		        		}		
//        				
//        			}
//        		}
//        	}
//        });
//
//        try {
//            iBeaconManager.startRangingBeaconsInRegion(region);
//        } catch (RemoteException e) {   
//        	// TODO - OK, what now then?	
//        }
		//fileHelper.prepareExternalFile();
	}
	protected void RuleJSONParse(JSONObject rulephone) {
		// TODO Auto-generated method stub
		
		try {
			for (int IDNumber = 1; IDNumber <= rulephone.getInt("Number"); IDNumber++) {	
				
				//Check if Rule Status == 1
				if( rulephone.getJSONObject(Integer.toString(IDNumber)).getInt("Status") == 1){
			
					boolean isExceptionHappend = false;
					
					//Check For Exception First
					if(rulephone.getJSONObject(Integer.toString(IDNumber)).getJSONObject("Exception").getInt("Number") > 1){
					
						isExceptionHappend = UnitCoreException(rulephone,IDNumber,1);
						
						for(int ite = 2; ite<= rulephone.getJSONObject(Integer.toString(IDNumber)).getJSONObject("Exception").getInt("Number"); ite++){
							
							if(rulephone.getJSONObject(Integer.toString(IDNumber)).getJSONObject("Exception").getJSONObject(Integer.toString(rulephone.getJSONObject(Integer.toString(IDNumber)).getJSONObject("Exception").getInt("Number")+1)).getInt(Integer.toString(ite-1)) == 1){ // 1 = 'and'
								isExceptionHappend = isExceptionHappend && UnitCoreException(rulephone,IDNumber,ite);
							} else {
								isExceptionHappend = isExceptionHappend || UnitCoreException(rulephone,IDNumber,ite);
							}
						}
												
					} else if(rulephone.getJSONObject(Integer.toString(IDNumber)).getJSONObject("Exception").getInt("Number") == 1){
					
						isExceptionHappend = UnitCoreException(rulephone,IDNumber,1);
					}
					System.out.println("__________except_______________"+isExceptionHappend);
					

					//If it turns false go to Core Logic
					boolean endResult = false;
				if (isExceptionHappend == false){
					
					if(rulephone.getJSONObject(Integer.toString(IDNumber)).getJSONObject("LogicUnit").getInt("Number") > 1){
					
						endResult = UnitCoreLogic(rulephone,IDNumber,1);
						
						for(int ite = 2; ite<= rulephone.getJSONObject(Integer.toString(IDNumber)).getJSONObject("LogicUnit").getInt("Number"); ite++){
							
							if(rulephone.getJSONObject(Integer.toString(IDNumber)).getJSONObject("LogicUnit").getJSONObject(Integer.toString(rulephone.getJSONObject(Integer.toString(IDNumber)).getJSONObject("LogicUnit").getInt("Number")+1)).getInt(Integer.toString(ite-1))== 1){ // 1 = 'and'
								endResult = endResult && UnitCoreLogic(rulephone,IDNumber,ite);
							} else {
								endResult = endResult || UnitCoreLogic(rulephone,IDNumber,ite);
							}
						}
						//ActionLogicCore(IDNumber,endResult);
						
					} else if(rulephone.getJSONObject(Integer.toString(IDNumber)).getJSONObject("LogicUnit").getInt("Number") == 1){
					
						endResult = UnitCoreLogic(rulephone,IDNumber,1);
						//ActionLogicCore(IDNumber,endResult);
					}
				}
				System.out.println("___________end______________"+endResult);
				
				//If End Result == True Then Lets Move To Action Handling
				if (endResult == true){
					for(int ite = 1; ite<= rulephone.getJSONObject(Integer.toString(IDNumber)).getJSONObject("Action").getInt("Number"); ite++){							
						ActionLogicCore(rulephone,IDNumber,rulephone.getJSONObject(Integer.toString(IDNumber)).getJSONObject("Action").getInt(Integer.toString(ite)));
					}
				}
				
				}
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

	private void ActionLogicCore(JSONObject rulephone, int RuleID, int ActionID) {
		switch(ActionID) {
		case 1: //Notification
			try {
				sendNotification(rulephone.getJSONObject(Integer.toString(RuleID)).getString("Rulename"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        break;
	    case 2: //Buzzer Alarm
//			Buzzer_ms_long = 200;
//	        SendAlarm(Buzzer_ms_long);
	        break;
	       
		}
		
	}

	private boolean UnitCoreLogic(JSONObject rulephone, int RuleID, int UnitID) {
		try {
			int	operator = rulephone.getJSONObject(Integer.toString(RuleID)).getJSONObject("LogicUnit").getJSONObject(Integer.toString(UnitID)).getInt("operator");
		    int treshold = rulephone.getJSONObject(Integer.toString(RuleID)).getJSONObject("LogicUnit").getJSONObject(Integer.toString(UnitID)).getInt("treshold");
		    if(rulephone.getJSONObject(Integer.toString(RuleID)).getJSONObject("LogicUnit").getJSONObject(Integer.toString(UnitID)).getString("characteristic").equals("ri"))
		    {	
		    	int data = 200;
		    	if(RSSIvalue.get(rulephone.getJSONObject(Integer.toString(RuleID)).getJSONObject("LogicUnit").getJSONObject(Integer.toString(UnitID)).getString("device"))!=null){
		    		 data = (int)RSSIvalue.get(rulephone.getJSONObject(Integer.toString(RuleID)).getJSONObject("LogicUnit").getJSONObject(Integer.toString(UnitID)).getString("device"));
		    	}
		    	if(operator == 2){
	    			return (data>treshold);
	    		} else if (operator == 3){
	    			return (data<treshold);
	    		} else if(operator == 1){
	    			return (data==treshold);
	    		}
		    }		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean UnitCoreException(JSONObject rulephone, int RuleID, int UnitID) {
		try {
			int	operator = rulephone.getJSONObject(Integer.toString(RuleID)).getJSONObject("Exception").getJSONObject(Integer.toString(UnitID)).getInt("operator");
		    int treshold = rulephone.getJSONObject(Integer.toString(RuleID)).getJSONObject("Exception").getJSONObject(Integer.toString(UnitID)).getInt("treshold");
		    if(rulephone.getJSONObject(Integer.toString(RuleID)).getJSONObject("Exception").getJSONObject(Integer.toString(UnitID)).getString("characteristic").equals("ri"))
		    {
		    	int data = 200;
		    	if(RSSIvalue.get(rulephone.getJSONObject(Integer.toString(RuleID)).getJSONObject("Exception").getJSONObject(Integer.toString(UnitID)).getString("device"))!=null){
		    		data = (int)RSSIvalue.get(rulephone.getJSONObject(Integer.toString(RuleID)).getJSONObject("Exception").getJSONObject(Integer.toString(UnitID)).getString("device"));
		    	}
		    	if(operator == 2){
	    			return (data>treshold);
	    		} else if (operator == 3){
	    			return (data<treshold);
	    		} else if(operator == 1){
	    			return (data==treshold);
	    		}
		    }		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void stopScanning(Button scanButton) {
//		try {
//			iBeaconManager.stopRangingBeaconsInRegion(region);
//			} catch (RemoteException e) {
//				// TODO - OK, what now then?
//			}
		// Flush location details to a file.
		//fileHelper.closeExternalFile();
		// Display file created message.
		mBluetoothAdapter.stopLeScan(mLeScanCallback);
		Toast.makeText(getBaseContext(),
				"Stop scaning",
				Toast.LENGTH_SHORT).show();
		scanButton.setText(MODE_STOPPED);
	}
	private void rulesendNotification(String msg, int Not_ID) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(
							BitmapFactory.decodeResource(this.getResources(),
									R.drawable.itoy_logo))
					    .setTicker(tickerText)
                        .setContentTitle("IToy: Rule " + Integer.toString(Not_ID) + " Triggered")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentIntent(contentIntent).setSound(soundURI)
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(true)
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(Not_ID, mBuilder.build());
    }

	private void sendNotificationImage(String s, int jk) {

        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        //File file = new File("YOUR_SONG_URI"); // set your audio path
        //File file = new File("pic.jpg");
        intent.setDataAndType(Uri.fromFile(Outputfile), "image/*");

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(
							BitmapFactory.decodeResource(this.getResources(),
									R.drawable.itoy_logo))
					    .setTicker(tickerText)
                        .setContentTitle("Click To View Image")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(s))
                        .setContentIntent(contentIntent).setSound(soundURI)
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(true)
                        .setContentText(s);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(jk, mBuilder.build());
    }


	public void sendNotification(String message) {

		try {

			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			Builder builder = new Notification.Builder(MainActivity.this);
			PendingIntent contentIndent = PendingIntent.getActivity(
					MainActivity.this, 0, new Intent(MainActivity.this,
							MainActivity.class),
					PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(contentIndent)
					.setSmallIcon(R.drawable.ic_launcher)
					// 砞竚篈柑瓜ボ瓜ボ
					.setLargeIcon(
							BitmapFactory.decodeResource(this.getResources(),
									R.drawable.itoy_logo))
					.setTicker(message) 
					.setWhen(System.currentTimeMillis())
					.setAutoCancel(false)
					.setContentTitle("Alarm " )
					.setContentText(message);

			Notification notification = builder.getNotification();

			// notification.defaults |= Notification.DEFAULT_SOUND;
//			notification.sound = Uri
//					.parse("file:///sdcard/Notifications/hangout_ringtone.m4a");
			notification.sound = Uri.withAppendedPath(
					Audio.Media.INTERNAL_CONTENT_URI, "6");
//			notification.sound = Uri.parse("android.resource://"
//					+ getPackageName() + "/" + R.raw.koko);
		


//			notification.defaults |= Notification.DEFAULT_VIBRATE; // 琘ㄇも诀ぃや穿 叫
															
			// long[] vibrate = {0,100,200,900,100};
			// notification.vibrate = vibrate;

	
			notification.defaults |= Notification.DEFAULT_LIGHTS; 
			// notification.ledARGB = 0xff00ff00;
			// notification.ledOnMS = 300;
			// notification.ledOffMS = 1000;
			// notification.flags |= Notification.FLAG_SHOW_LIGHTS;

			// i琌陪ボ兵Notification
			notificationManager.notify(1, notification);
			i++;
			
		} catch (Exception e) {

		}
	}

	protected void onResume() {
		// TODO Auto-generated method stub
		int count = adpAdapter.getCount();
	
		// 进行遍历,显示listview
		for (int i = 0; i < count; i++) {
			// 因为List的特性,删除了2个item,则3变成2,所以这里要进行这样的换算,才能拿到删除后真正的position
			int position = i - (count - adpAdapter.getCount());
			adpAdapter.remove(position);			
		}
		Cursor c = db.rawQuery("SELECT * FROM applications", null);
        while (c.moveToNext()) {
        	adpAdapter.add(new DemoBean(c.getString(c.getColumnIndex("application")), true));
        }
        c.close();
        adpAdapter.notifyDataSetChanged();
        
		super.onResume();
	}


}