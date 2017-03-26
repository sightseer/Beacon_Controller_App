package com.mrwu.demo;

import java.util.ArrayList;
import java.util.List;


public class ConditionValue {
	private static String value;
	private static String application_name;
	private static String sensor;
	private static int rssi_sp;
	private static int rssi_value;
	private static int gx_sp;
	private static int gx_value;
	private static int gy_sp;
	private static int gy_value;
	private static int gz_sp;
	private static int gz_value;
//	private static List<Integer> logicdatas = new ArrayList() ;


	public static void setApplicationName(String a) { application_name = a; }
	public static void setSensor(String a) { sensor = a; }
//	public static void setConditionValue(String a) { value = a; }
	public static void setRssisp(int a) { rssi_sp = a; }
	public static void setRssiValue(int a) { rssi_value = a; }
	public static void setGxsp(int a) { gx_sp = a; }
	public static void setGxValue(int a) { gx_value = a; }
	public static void setGysp(int a) { gy_sp = a; }
	public static void setGyValue(int a) { gy_value = a; }
	public static void setGzsp(int a) { gz_sp = a; }
	public static void setGzValue(int a) { gz_value = a; }

	
	public static String getApplicationName() { return application_name; }
	public static String getSensor() { return sensor; }
//	public static String getConditionValue() { return value; }
	public static int getRssisp() { return rssi_sp; }
	public static int getRssiValue() { return rssi_value; }
	public static int getGxsp() { return gx_sp; }
	public static int getGxValue() { return gx_value; }
	public static int getGysp() { return gy_sp; }
	public static int getGyValue() { return gy_value; }
	public static int getGzsp() { return gz_sp; }
	public static int getGzValue() { return gz_value; }
	
}
