package com.mrwu.demo;

public class DemoBean {

	private String title;

	
	/**
	 * 标识是否可以删除
	 */
	private boolean canRemove = true;

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

	public DemoBean(String title, boolean canRemove) {
		this.title = title;
		this.canRemove = canRemove;
	}

	@Override
	 public boolean equals(Object obj) {
	  if (obj instanceof DemoBean) {
		  DemoBean t = (DemoBean) obj;
	   return this.getTitle().equals(t.getTitle());
	  }
	  return super.equals(obj);
	 }

	public DemoBean() {
	}

}