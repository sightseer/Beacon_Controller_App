package com.mrwu.demo;




import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	boolean rssi_able,gx_able,gy_able,gz_able,bt_able;
	String rssi_sp,gx_sp,gy_sp,gz_sp;
	int rssi_value,gx_value,gy_value,gz_value,button_check;
	public static final String DB_NAME="conditiondata.db";
	SQLiteDatabase db;
	SeekBar seekbarRSSI,seekbarGyro_X,seekbarGyro_Y,seekbarGyro_Z;
	Spinner spinnerRSSI,spinnerGyro_X,spinnerGyro_Y,spinnerGyro_Z;
	TextView textRSSIValue,textGxValue,textGyValue,textGzValue;
   int Gx_unchecked=0,Gy_unchecked=0,Gz_unchecked=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		OpenCreateDB();
		setContentView(R.layout.aa);
		//checkbox
		checkboxselect();

		textGzValue=(TextView)findViewById(R.id.textGzValue);
		textGyValue=(TextView)findViewById(R.id.textGyValue);
		textGxValue=(TextView)findViewById(R.id.textGxValue);
		textRSSIValue=(TextView)findViewById(R.id.textRSSIValue);
		
		//RSSI setting
		spinnerRSSI = (Spinner) findViewById(R.id.spinner1);

		// 建立数据源
		String[] mItems = getResources().getStringArray(R.array.ctype);
		// 建立Adapter并且绑定数据源
		ArrayAdapter<String> _Adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
		//绑定 Adapter到控件
		spinnerRSSI.setAdapter(_Adapter);
		spinnerRSSI.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int position, long id) {
				rssi_sp = parent.getItemAtPosition(position).toString();
				ConditionValue.setRssisp(position);
//		        ConditionValue.setRssisp(position);
//		        Toast.makeText(SettingsActivity.this, "你点击的是:"+str, 2000).show();
//		        ConditionValue.setConditionValue(rssi_sp);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});
		spinnerRSSI.setSelection(ConditionValue.getRssisp());
//		spinnerRSSI.getSelectedView().setEnabled(false);
		spinnerRSSI.setEnabled(false);
//		SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar1);	//获取拖动条
		seekbarRSSI=(SeekBar) findViewById(R.id.seekBar1);
//		seekbarRSSI.setProgress(ConditionValue.getRssiValue());
		seekbarRSSI.setProgress(0);
		textRSSIValue.setText("0");

		seekbarRSSI.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			//	String strRSSIValue=String.valueOf(rssi_value);
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
//				Toast.makeText(MainActivity.this, "结束滑动", Toast.LENGTH_SHORT).show();
				Toast.makeText(SettingsActivity.this, "RSSI:  " + rssi_value, 20).show();

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
//				Toast.makeText(MainActivity.this, "开始滑动", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
										  boolean fromUser) {
//				result.setText("当前值："+progress);				//修改文本视图的值
				rssi_value = progress;
				ConditionValue.setRssiValue(progress);
//				Toast.makeText(SettingsActivity.this, "RSSI:  "+progress, 20).show();
//				ConditionValue.setConditionValue(""+rssi_value);
				String strRSSIValue = String.valueOf(rssi_value);
				textRSSIValue.setText("-"+strRSSIValue);
			}
		});
//		seekbarRSSI.setProgress(ConditionValue.getRssiValue());

		seekbarRSSI.setEnabled(false);
//		SeekBar seekbarGyro_Z = (SeekBar) findViewById(R.id.seekBar4);
//		seekbarGyro_Z.setEnabled(false);




		G_xsetting();
		G_ysetting();
		G_zsetting();
//		seekbarGyro_Z.setEnabled(false);

		Button btnOk = (Button) findViewById(R.id.btnok);
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!rssi_able)
				{
					rssi_value = 0;
					rssi_sp = null;
				}
				if(!gx_able)
				{
					gx_value = 0;
					gx_sp = null;
				}
				if(!gy_able)
				{
					gy_value = 0;
					gy_sp = null;
				}
				if(!gz_able)
				{
					gz_value = 0;
					gz_sp = null;
				}
				if(!bt_able)
				{
					button_check = 0;
				}
				if(bt_able)
				{
					button_check = 1;
				}
				
				ContentValues cv = new ContentValues();  
		    	cv.put("rssivalue", rssi_value);  
		    	cv.put("rssicondition", rssi_sp);
		    	cv.put("gxvalue", gx_value);  
		    	cv.put("gxcondition", gx_sp); 
		    	cv.put("gyvalue", gy_value);  
		    	cv.put("gycondition", gy_sp);  
		    	cv.put("gzvalue", gz_value);  
		    	cv.put("gzcondition", gz_sp);  
		    	cv.put("button", button_check);  
		    	
		    	db.update("conditions", cv, "sensor = ? and application= ?", new String[]{ConditionValue.getSensor(),ConditionValue.getApplicationName()});  
//				ConditionValue.setConditionValue("RSSI"+rssi_sp+rssi_value);
		    	finish();
			}
		});
	}

	private void G_zsetting() {
		spinnerGyro_Z = (Spinner) findViewById(R.id.spinner4);
		String[] mItems = getResources().getStringArray(R.array.ctype);
		ArrayAdapter<String> _Adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
		spinnerGyro_Z.setAdapter(_Adapter);
		spinnerGyro_Z.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int position, long id) {
				gz_sp = parent.getItemAtPosition(position).toString();
				ConditionValue.setGzsp(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});
		spinnerGyro_Z.setSelection(ConditionValue.getGzsp());
		seekbarGyro_Z = (SeekBar) findViewById(R.id.seekBar4);	//获取拖动条

		seekbarGyro_Z.setProgress(50);
		textGzValue.setText("0");
		seekbarGyro_Z.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Toast.makeText(SettingsActivity.this, "Gyro_z:  " + gz_value, 20).show();
				//			String strGzValue=String.valueOf(gz_value);
				//			textGzValue.setText(strGzValue);

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
										  boolean fromUser) {
				gz_value = progress * 5 - 250;
				ConditionValue.setGzValue(progress);
				String strGzValue = String.valueOf(gz_value);
				textGzValue.setText(strGzValue);
			}
		});
//		seekbarGyro_Z.setProgress(ConditionValue.getGzValue());
		seekbarGyro_Z.setEnabled(false);
		spinnerGyro_Z.setEnabled(false);
	}

	private void G_ysetting() {
		spinnerGyro_Y = (Spinner) findViewById(R.id.spinner3);
		String[] mItems = getResources().getStringArray(R.array.ctype);
		ArrayAdapter<String> _Adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
		spinnerGyro_Y.setAdapter(_Adapter);
		spinnerGyro_Y.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int position, long id) {
				gy_sp = parent.getItemAtPosition(position).toString();
				ConditionValue.setGysp(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});
		spinnerGyro_Y.setSelection(ConditionValue.getGysp());
		seekbarGyro_Y = (SeekBar) findViewById(R.id.seekBar3);    //获取拖动条

		seekbarGyro_Y.setProgress(50);
		textGyValue.setText("0");
		seekbarGyro_Y.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Toast.makeText(SettingsActivity.this, "Gyro_y:  " + gy_value, 20).show();


			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
										  boolean fromUser) {
				gy_value = progress * 5 - 250;
				ConditionValue.setGyValue(progress);
				String strGyValue = String.valueOf(gy_value);
				textGyValue.setText(strGyValue);
			}
		});
//		seekbarGyro_Y.setProgress(ConditionValue.getGyValue());
		seekbarGyro_Y.setEnabled(false);
		spinnerGyro_Y.setEnabled(false);
	}

	private void G_xsetting() {
		spinnerGyro_X = (Spinner) findViewById(R.id.spinner2);
		String[] mItems = getResources().getStringArray(R.array.ctype);
		ArrayAdapter<String> _Adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
		spinnerGyro_X.setAdapter(_Adapter);
		spinnerGyro_X.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int position, long id) {
				gx_sp = parent.getItemAtPosition(position).toString();
				ConditionValue.setGxsp(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});
		spinnerGyro_X.setSelection(ConditionValue.getGxsp());
		seekbarGyro_X = (SeekBar) findViewById(R.id.seekBar2);	//获取拖动条

		seekbarGyro_X.setProgress(50);
		textGxValue.setText("0");
		seekbarGyro_X.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Toast.makeText(SettingsActivity.this, "Gyro_x:  " + gx_value, 20).show();

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
										  boolean fromUser) {
				gx_value = progress * 5 - 250;
				ConditionValue.setGxValue(progress);
				String strGxValue = String.valueOf(gx_value);
				textGxValue.setText(strGxValue);
			}
		});
//		seekbarGyro_X.setProgress(ConditionValue.getGxValue());
	//S	if(Gx_unchecked==0)
		seekbarGyro_X.setEnabled(false);
		spinnerGyro_X.setEnabled(false);
	}

	private void checkboxselect() {
		CheckBox check1 = (CheckBox)findViewById(R.id.checkBox1);  
		CheckBox check2 = (CheckBox)findViewById(R.id.checkBox2);  
		CheckBox check3 = (CheckBox)findViewById(R.id.checkBox3);  
		CheckBox check4 = (CheckBox)findViewById(R.id.checkBox4);  
		CheckBox check5 = (CheckBox)findViewById(R.id.checkBox5);  
		check1.setOnCheckedChangeListener(listener);  
        check2.setOnCheckedChangeListener(listener);  
        check3.setOnCheckedChangeListener(listener);  
        check4.setOnCheckedChangeListener(listener);  
        check5.setOnCheckedChangeListener(listener);

		
	}
	 private OnCheckedChangeListener listener = new OnCheckedChangeListener()  
	    {  
	    @Override  
	    public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)  
	    {  
	        switch(buttonView.getId())  
	        {  
	        case R.id.checkBox1:  
	            if(isChecked) {
					rssi_able = true;
					seekbarRSSI.setEnabled(true);
					spinnerRSSI.setEnabled(true);
				}
	            else {
					rssi_able = false;
					seekbarRSSI.setEnabled(false);
					spinnerRSSI.setEnabled(false);
				}
	            break;  
	        case R.id.checkBox2:  
	            if(isChecked) {
					gx_able = true;
					seekbarGyro_X.setEnabled(true);
					spinnerGyro_X.setEnabled(true);
		//			Gx_unchecked=1;
				}
	            else {
					gx_able = false;
					seekbarGyro_X.setEnabled(false);
					spinnerGyro_X.setEnabled(false);
				}
	            break;  
	        case R.id.checkBox3:  
	            if(isChecked) {
					gy_able = true;
					seekbarGyro_Y.setEnabled(true);
					spinnerGyro_Y.setEnabled(true);
				}
	            else {
					gy_able = false;
					seekbarGyro_Y.setEnabled(false);
					spinnerGyro_Y.setEnabled(false);
				}
	            break;  
	        case R.id.checkBox4:  
	            if(isChecked) {
					gz_able = true;
					seekbarGyro_Z.setEnabled(true);
					spinnerGyro_Z.setEnabled(true);
				}
	            else {
					gz_able = false;
					seekbarGyro_Z.setEnabled(false);
					spinnerGyro_Z.setEnabled(false);
				}
	            break;
	        case R.id.checkBox5:  
	            if(isChecked)  
	            	bt_able = true;
	            else
	            	bt_able = false;
	            break;
	        
	            }  
	        }

		
	    };
	public void OpenCreateDB(){
		db = openOrCreateDatabase(DB_NAME, this.MODE_PRIVATE, null);
//        db.execSQL("DROP TABLE IF EXISTS conditions");  
        db.execSQL("CREATE TABLE IF NOT EXISTS conditions (_id INTEGER PRIMARY KEY AUTOINCREMENT, application VARCHAR, sensor VARCHAR, rssivalue INTEGER, rssicondition VARCHAR, conditionlogic VARCHAR)");
    }
	
}