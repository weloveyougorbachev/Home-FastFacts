package com.android.fastfacts;

import android.content.Context;

public class Facts {
	private int id;
	private String reference;
	private String fact, name;
	private Context ctx;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getReference() {
		return reference;
	}
	
	public void setReference(String i) {
		this.reference = i;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String i) {
		this.name = i;
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
