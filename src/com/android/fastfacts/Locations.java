package com.android.fastfacts;

import android.content.Context;

public class Locations {

	private int id;
	private String address, name;
	private float lat, lon;
	
	private Context ctx;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}
	
	public void setAdress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public float getLat() {
		return lat;
	}
	public void setLat(float float1) {
		this.lat = float1;
	}

	public float getLon() {
		return lon;
	}
	public void setLon(float float1) {
		this.lon = float1;
	}
	
	public Context getContext() {
		return ctx;
	}
	
	public void setContext(Context ctx) {
		this.ctx = ctx;
	}

}
