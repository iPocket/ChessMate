package me.protoflicker.chessmate.data.record;

import me.protoflicker.chessmate.Server;
import me.protoflicker.chessmate.data.Database;

import java.sql.*;

public abstract class UserManager {

	public static String getUserIdByUsername(String username){
		String statement =
				"""
				SELECT userId
				FROM `Users`
				WHERE username = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setString(0, username);
			ResultSet r = s.executeQuery();
			return r.getString(0);
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static String getUsername(String userId){
		String statement =
				"""
				SELECT username
				FROM `Users`
				WHERE userId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setString(0, userId);
			ResultSet r = s.executeQuery();
			return r.getString(0);
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static String getHashedPassword(String userId){
		String statement =
				"""
				SELECT hashedPassword
				FROM `Users`
				WHERE userId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setString(0, userId);
			ResultSet r = s.executeQuery();
			return r.getString(0);
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static Date getBirthday(String userId){
		String statement =
				"""
				SELECT birthday
				FROM `Users`
				WHERE userId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setString(0, userId);
			ResultSet r = s.executeQuery();
			return r.getDate(0);
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static Timestamp getLastLogin(String userId){
		String statement =
				"""
				SELECT lastLogin
				FROM `Users`
				WHERE userId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setString(0, userId);
			ResultSet r = s.executeQuery();
			return r.getTimestamp(0);
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static void createTable(Database database){
		String statement =
				"""
				CREATE TABLE IF NOT EXISTS `Users` (
				userId BINARY(16) DEFAULT (UNHEX(REPLACE(UUID(), "-",""))) NOT NULL UNIQUE PRIMARY KEY,
				username VARCHAR(20) NOT NULL,
				hashedPassword BINARY(32) NOT NULL,
				birthday DATE NOT NULL,
				lastLogin TIMESTAMP NOT NULL
				);
				""";

		try (PreparedStatement s = database.getConnection().prepareStatement(statement)){
			s.executeUpdate();
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}
}
