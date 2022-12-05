package me.protoflicker.chessmate.data;

import lombok.Getter;
import me.protoflicker.chessmate.data.record.ParticipationManager;
import me.protoflicker.chessmate.data.record.UserManager;

import java.sql.Connection;

public class DataManager {

	@Getter
	private final String ip;

	@Getter
	private final String port;

	private final String database;

	private final String username;

	private final String password;

	private Connection connection;

	public DataManager(String ip, String port, String database, String username, String password){
		this.ip = ip;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
	}

	public Database getNewDatabase(){
		return new SQLDatabase(ip, port, database, username, password);
	}

	public static void createTables(Database database){
		UserManager.createTable(database);
		ParticipationManager.createTable(database);
	}
}
