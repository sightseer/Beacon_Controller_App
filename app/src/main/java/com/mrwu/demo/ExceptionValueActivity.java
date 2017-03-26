package com.mrwu.demo;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ExceptionValueActivity extends Activity {

	boolean rssi_able,gx_able,gy_able,gz_able;
	String rssi_sp,gx_sp,gy_sp,gz_sp;
	int rssi_value,gx_value,gy_value,gz_value;
	public static final String DB_NAME="conditiondata.db";
	SQLiteDatabase db;
	SeekBar seekbarRSSI,seekbarGx,seekbarGy,seekbarGz;
	Spinner spinnerRSSI,spinnerGx,spinnerGy,spinnerGz;
	TextView textRSSIValue,textGxValue,textGyValue,textGzValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		OpenCreateDB();
		setContentView(R.layout.aa);
		//checkbox		
		checkboxselect();

		textRSSIValue=(TextView)findViewById(R.id.textRSSIValue);
		textGzValue=(TextView)findViewById(R.id.textGzValue);
		textGyValue=(TextView)findViewById(R.id.textGyValue);
		textGxValue=(TextView)findViewById(R.id.textGxValue);

	//	Spinner mSpinner = (Spinner) findViewById(R.id.spinner1);
		spinnerRSSI=(Spinner) findViewById(R.id.spinner1);
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
//		        Toast.makeText(SettingsActivity.this, "你点击的是:"+str, 2000).show();
//		        ConditionValue.setConditionValue(rssi_sp);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});
		spinnerRSSI.setSelection(ConditionValue.getRssisp());
		//SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar1);	//获取拖动条
		seekbarRSSI= (SeekBar) findViewById(R.id.seekBar1);
		seekbarRSSI.setProgress(0);
		textRSSIValue.setText("0");

		seekbarRSSI.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
//				Toast.makeText(MainActivity.this, "结束滑动", Toast.LENGTH_SHORT).show();
				Toast.makeText(ExceptionValueActivity.this, "RSSI:  "+rssi_value, 20).show();
//				
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

				String strRSSIValue=String.valueOf(rssi_value);
				textRSSIValue.setText("-"+strRSSIValue);
//				Toast.makeText(SettingsActivity.this, "RSSI:  "+progress, 20).show();
//				ConditionValue.setConditionValue(""+rssi_value);
			}
		});
		seekbarRSSI.setProgress(ConditionValue.getRssiValue());
		seekbarRSSI.setEnabled(false);
		spinnerRSSI.setEnabled(false);

		G_xsetting();
		G_ysetting();
		G_zsetting();
		
		
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
				ContentValues cv = new ContentValues();  
		    	cv.put("rssivalue", rssi_value);  
		    	cv.put("rssicondition", rssi_sp);  
		    	cv.put("gxvalue", gx_value);  
		    	cv.put("gxcondition", gx_sp); 
		    	cv.put("gyvalue", gy_value);  
		    	cv.put("gycondition", gy_sp);  
		    	cv.put("gzvalue", gz_value);  
		    	cv.put("gzcondition", gz_sp);  
		    	
		    	db.update("exceptions", cv, "sensor = ? and application= ?", new String[]{ConditionValue.getSensor(),ConditionValue.getApplicationName()});  
//				ConditionValue.setConditionValue("RSSI"+rssi_sp+rssi_value);
		    	finish();
			}
		});
	}

	private void G_zsetting() {
	//	Spinner mSpinner = (Spinner) findViewById(R.id.spinner4);
		spinnerGz= (Spinner) findViewById(R.id.spinner4);
		String[] mItems = getResources().getStringArray(R.array.ctype);
		ArrayAdapter<String> _Adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
		spinnerGz.setAdapter(_Adapter);
		spinnerGz.setOnItemSelectedListener(new OnItemSelectedListener() {
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
		spinnerGz.setSelection(ConditionValue.getGzsp());
	//	SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar4);	//获取拖动条
		seekbarGz = (SeekBar) findViewById(R.id.seekBar4);

		seekbarGz.setProgress(50);
		textGzValue.setText("0");
		seekbarGz.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Toast.makeText(ExceptionValueActivity.this, "Gyro_z:  "+gz_value, 20).show();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				gz_value = progress*5-250;
				ConditionValue.setGzValue(progress);
				String strGz_value=String.valueOf(gz_value);
				textGzValue.setText(strGz_value);
			}
		});
		seekbarGz.setProgress(ConditionValue.getGzValue());
		seekbarGz.setEnabled(false);
		spinnerGz.setEnabled(false);
	}

	private void G_ysetting() {
		spinnerGy = (Spinner) findViewById(R.id.spinner3);
		String[] mItems = getResources().getStringArray(R.array.ctype);
		ArrayAdapter<String> _Adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
		spinnerGy.setAdapter(_Adapter);
		spinnerGy.setOnItemSelectedListener(new OnItemSelectedListener() {
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
		spinnerGy.setSelection(ConditionValue.getGysp());
		seekbarGy = (SeekBar) findViewById(R.id.seekBar3);	//获取拖动条

		seekbarGy.setProgress(50);
		textGyValue.setText("0");
		seekbarGy.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Toast.makeText(ExceptionValueActivity.this, "Gyro_y:  " + gy_value, 20).show();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
										  boolean fromUser) {
				gy_value = progress * 5 - 250;
				ConditionValue.setGyValue(progress);
				String strGy_value=String.valueOf(gy_value);
				textGyValue.setText(strGy_value);
			}
		});
		seekbarGy.setProgress(ConditionValue.getGyValue());
		seekbarGy.setEnabled(false);
		spinnerGy.setEnabled(false);
	}

	private void G_xsetting() {
		spinnerGx = (Spinner) findViewById(R.id.spinner2);
		String[] mItems = getResources().getStringArray(R.array.ctype);
		ArrayAdapter<String> _Adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
		spinnerGx.setAdapter(_Adapter);
		spinnerGx.setOnItemSelectedListener(new OnItemSelectedListener() {
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
		spinnerGx.setSelection(ConditionValue.getGxsp());
		seekbarGx = (SeekBar) findViewById(R.id.seekBar2);	//获取拖动条

		seekbarGx.setProgress(50);
		textGxValue.setText("0");
		seekbarGx.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Toast.makeText(ExceptionValueActivity.this, "Gyro_x:  "+gx_value, 20).show();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				gx_value = progress*5-250;
				ConditionValue.setGxValue(progress);
				String strGx_value=String.valueOf(gx_value);
				textGxValue.setText(strGx_value);
			}
		});
		seekbarGx.setProgress(ConditionValue.getGxValue());
		seekbarGx.setEnabled(false);
		spinnerGx.setEnabled(false);
	}

	private void checkboxselect() {
		CheckBox check1 = (CheckBox)findViewById(R.id.checkBox1);  
		CheckBox check2 = (CheckBox)findViewById(R.id.checkBox2);  
		CheckBox check3 = (CheckBox)findViewById(R.id.checkBox3);  
		CheckBox check4 = (CheckBox)findViewById(R.id.checkBox4);  
		check1.setOnCheckedChangeListener(listener);  
        check2.setOnCheckedChangeListener(listener);  
        check3.setOnCheckedChangeListener(listener);  
        check4.setOnCheckedChangeListener(listener);  
        
		
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
					seekbarGx.setEnabled(true);
					spinnerGx.setEnabled(true);
				}
	            else {
					gx_able = false;
					seekbarGx.setEnabled(false);
					spinnerGx.setEnabled(false);
				}
	            break;
	        case R.id.checkBox3:
	            if(isChecked) {
					gy_able = true;
					seekbarGy.setEnabled(true);
					spinnerGy.setEnabled(true);
				}
	            else {
					gy_able = false;
					seekbarGy.setEnabled(false);
					spinnerGy.setEnabled(false);
				}
	            break;  
	        case R.id.checkBox4:  
	            if(isChecked) {
					gz_able = true;
					seekbarGz.setEnabled(true);
					spinnerGz.setEnabled(true);
				}
	            else {
					gz_able = false;
					seekbarGz.setEnabled(false);
					spinnerGz.setEnabled(false);
					}
	            break;
	            }  
	        }

		
	    };

	public void OpenCreateDB(){
		db = openOrCreateDatabase(DB_NAME, this.MODE_PRIVATE, null);
//        db.execSQL("DROP TABLE IF EXISTS conditions");  
        db.execSQL("CREATE TABLE IF NOT EXISTS exceptions (_id INTEGER PRIMARY KEY AUTOINCREMENT, application VARCHAR, sensor VARCHAR, rssivalue INTEGER, rssicondition VARCHAR, conditionlogic VARCHAR)");
    }
	
}