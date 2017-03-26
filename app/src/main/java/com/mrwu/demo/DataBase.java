package com.mrwu.demo;

import android.database.sqlite.SQLiteDatabase;

public class DataBase {
	private static SQLiteDatabase conditiondb;
	private static String application_name;
	private static int rssi_sp;
	private static int rssi_value;


	public static void setApplicationName(String a) { application_name = a; }
//	public static void setConditionValue(String a) { value = a; }
	public static void setRssisp(int a) { rssi_sp = a; }
	public static void setRssiValue(int a) { rssi_value = a; }

	public static String getApplicationName() { return application_name; }
	public static SQLiteDatabase getConditionDataBase() { return conditiondb; }
	public static int getRssisp() { return rssi_sp; }
	public static int getRssiValue() { return rssi_value; }

}