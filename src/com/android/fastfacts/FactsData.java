package com.android.fastfacts;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class FactsData {
	// Database fields
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;

	private String[] allColumns = {DatabaseHelper.colLocationID, DatabaseHelper.colFact};

	public FactsData(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	
	/**
	 * This method returns the Location object that a passed cursor object points to.
	 * 
	 * @author Vitaliy Zheltov
	 * @param cursor
	 * @return The Location object that the cursor points to.
	 */
	private Facts cursorToFact(Cursor cursor) {
		Facts fact = new Facts();
		
		fact.setId(cursor.getInt(0));
		fact.setLocationId(cursor.getInt(1));
		fact.setFact(cursor.getString(2));

		return fact;
	}
}
