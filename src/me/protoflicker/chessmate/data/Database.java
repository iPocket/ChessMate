package me.protoflicker.chessmate.data;

import java.sql.Connection;
import java.sql.SQLException;

public interface Database {
	Connection getConnection();

	void connect() throws SQLException;

	void disconnect();

	boolean isConnected();

	void refresh() throws SQLException;
}
