package com.mrwu.demo;

public class ConditonDataBase {
	int _id;//与所建立的表对应
	String application;
	String sensor;
    int rssivalue;
    String rssicondition ;
    int gxvalue;
    String gxcondition ;
    int gyvalue;
    String gycondition ;
    int gzvalue;
    String gzcondition ;
    int button;
    
    int conditionlogic;
	public ConditonDataBase(String application,String sensor,int rssivalue,String rssicondition,int gxvalue,String gxcondition,int gyvalue,String gycondition,int gzvalue,String gzcondition,int button,int conditionlogic){
		this.application=application;
		this.sensor=sensor;
		this.rssivalue=rssivalue;
		this.rssicondition=rssicondition;
		this.gxvalue=gxvalue;
		this.gxcondition=gxcondition;
		this.gyvalue=gyvalue;
		this.gycondition=gycondition;
		this.gzvalue=gzvalue;
		this.gzcondition=gzcondition;
		this.button=button;
		
		this.conditionlogic=conditionlogic;
	}
	public ConditonDataBase(){
		
	}
}