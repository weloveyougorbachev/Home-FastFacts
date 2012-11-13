package com.android.fastfacts;

import android.content.Context;

public class Fact {
	private int id, locId;
	private String fact;
	private Context ctx;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getLocationId() {
		return locId;
	}
	
	public void setLocationId(int id) {
		this.locId = id;
	}

	public String getFact() {
		return fact;
	}
	
	public void setFact(String fact) {
		this.fact = fact;
	}

	public Context getContext() {
		return ctx;
	}
	
	public void setContext(Context ctx) {
		this.ctx = ctx;
	}
}
