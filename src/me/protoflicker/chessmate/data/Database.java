package me.protoflicker.chessmate.data;

import java.sql.Connection;

public interface Database {

	Connection getConnection();

	boolean connect();

	void disconnect();

	boolean isConnected();

	void refresh();

	String getIp();

	String getPort();
}
