package me.protoflicker.chessmate.data.manager;

public interface TableManagerInterface<T> {

	void createTable();

	String getId(T record);

	T getRecord(String id); //get record from cache
	T loadRecord(String id); //load record from database (sync)

	void addRecord(T record); //insert new record and add to cache
	void insertRecord(T record); //insert record to database
	void updateRecord(T record); //update record in database
}
