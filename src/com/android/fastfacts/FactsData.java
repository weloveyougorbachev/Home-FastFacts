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

	private String[] allColumns = {DatabaseHelper.colId,DatabaseHelper.colName, DatabaseHelper.colReference, DatabaseHelper.colFact};

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
	 *
	 * @return List<Facts>
	 */
	public List<Facts> getLocations(String name) {
		List<Facts> listOfFacts = new ArrayList<Facts>();
		Cursor cursor = database.query(DatabaseHelper.factsTable, allColumns, DatabaseHelper.colName + " = " + name, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			listOfFacts.add(cursorToFact(cursor));
			cursor.moveToNext();
		}
		cursor.close();
		return listOfFacts;
	}
	
	/**
	 * This method returns the fact object that a passed cursor object points to.
	 * 
	 * @author Vitaliy Zheltov
	 * @param cursor
	 * @return The fact object that the cursor points to.
	 */
	private Facts cursorToFact(Cursor cursor) {
		Facts fact = new Facts();
		
		fact.setId(cursor.getInt(0));
		fact.setName(cursor.getString(1));
		fact.setReference(cursor.getString(2));
		fact.setFact(cursor.getString(3));

		return fact;
	}
}
