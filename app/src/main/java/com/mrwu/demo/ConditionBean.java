package com.mrwu.demo;

public class ConditionBean {
private String title;
private String value;
private int logic;

	/**
	 * 标识是否可以删除
	 */
	private boolean canRemove = true;

	public int getLogic() {
		return logic;
	}

	public void setLogic(int logic) {
		this.logic = logic;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isCanRemove() {
		return canRemove;
	}

	public void setCanRemove(boolean canRemove) {
		this.canRemove = canRemove;
	}

	public ConditionBean(String title, String value, int logic, boolean canRemove) {
		this.title = title;
		this.value = value;
		this.logic = logic;
		this.canRemove = canRemove;
	}
	@Override
	 public boolean equals(Object obj) {
	  if (obj instanceof ConditionBean) {
		  ConditionBean t = (ConditionBean) obj;
	   return this.getTitle().equals(t.getTitle());
	  }
	  return super.equals(obj);
	 }

	public ConditionBean() {
	}



}