package me.protoflicker.chessmate.data;

import lombok.Getter;
import me.protoflicker.chessmate.data.manager.managers.UserManager;

public class DataManager {

	@Getter
	private final Database database;

	@Getter
	private final UserManager userManager;

	public DataManager(Database database){
		this.database = database;
		this.userManager = new UserManager(database);
	}

	public synchronized boolean connect(){
		return this.database.connect();
	}

	public synchronized void disconnect(){
		this.database.disconnect();
	}

	public void createTables(){
		userManager.createTable();
	}
}
