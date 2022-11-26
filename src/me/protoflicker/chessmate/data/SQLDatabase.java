package me.protoflicker.chessmate.data;

import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//TODO connection pooling
public final class SQLDatabase implements Database {

	@Getter
	private final String ip;

	@Getter
	private final String port;

	private final String database;

	private final String username;

	private final String password;

	private Connection connection;

	public SQLDatabase(String ip, String port, String database, String username, String password){
		this.ip = ip;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
	}

	@Override
	public synchronized Connection getConnection(){
		return this.connection;
	}

	@Override
	public synchronized void connect() throws SQLException {
		if(!this.isConnected()){
			try {
				Class.forName("org.mariadb.jdbc.Driver");
				this.connection = DriverManager.getConnection(
						"jdbc:mariadb://" + this.ip + ":" + this.port + '/' + this.database +
								"?allowMultiQueries=true&autoReconnect=true", this.username, this.password);
				return;
			} catch (ClassNotFoundException e){
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public synchronized void disconnect(){
		if(this.connection != null){
			try {
				this.connection.close();
			} catch (SQLException ignored){

			}
		}
	}

	@Override
	public synchronized boolean isConnected(){
		try {
			return this.connection != null && !this.connection.isClosed();
		} catch (SQLException e){
			return false;
		}
	}

	@Override
	public synchronized void refresh() throws SQLException {
		try {
			PreparedStatement s = this.connection.prepareStatement("SELECT 1");
			s.executeQuery();
		} catch (SQLException e){
			this.connect();
		}
	}
}
