package me.protoflicker.chessmate.data.table;

import me.protoflicker.chessmate.Server;
import me.protoflicker.chessmate.data.Database;
import me.protoflicker.chessmate.protocol.enums.AccountType;

import java.sql.*;

public abstract class UserTable {

	public static byte[] getUserIdByUsername(String username){
		String statement =
				"""
				SELECT userId
				FROM `Users`
				WHERE username = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setString(1, username);
			ResultSet r = s.executeQuery();
			if(r.next()){
				return r.getBytes(1);
			} else {
				return null;
			}
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static String getUsername(byte[] userId){
		String statement =
				"""
				SELECT username
				FROM `Users`
				WHERE userId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, userId);
			ResultSet r = s.executeQuery();
			if(r.next()){
				return r.getString(1);
			} else {
				return null;
			}
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static String getHashedPassword(byte[] userId){
		String statement =
				"""
				SELECT password
				FROM `Users`
				WHERE userId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, userId);
			ResultSet r = s.executeQuery();
			if(r.next()){
				return r.getString(1);
			} else {
				return null;
			}
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static AccountType getAccountType(byte[] userId){
		String statement =
				"""
				SELECT accountType
				FROM `Users`
				WHERE userId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, userId);
			ResultSet r = s.executeQuery();
			if(r.next()){
				return AccountType.getByCode(r.getInt(1));
			} else {
				return null;
			}
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static Date getBirthday(byte[] userId){
		String statement =
				"""
				SELECT birthday
				FROM `Users`
				WHERE userId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, userId);
			ResultSet r = s.executeQuery();
			if(r.next()){
				return r.getDate(1);
			} else {
				return null;
			}
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static Timestamp getLastLogin(byte[] userId){
		String statement =
				"""
				SELECT lastLogin
				FROM `Users`
				WHERE userId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, userId);
			ResultSet r = s.executeQuery();
			if(r.next()){
				return r.getTimestamp(1);
			} else {
				return null;
			}
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static void createTable(Database database){
		String statement =
				"""
				CREATE TABLE IF NOT EXISTS `Users` (
				userId BINARY(16) DEFAULT (UNHEX(REPLACE(UUID(), "-",""))) NOT NULL UNIQUE,
				username VARCHAR(20) NOT NULL,
				password BINARY(32) NOT NULL,
				accountType TINYINT(1) UNSIGNED DEFAULT 0 NOT NULL,
				birthday DATE NOT NULL,
				lastLogin TIMESTAMP DEFAULT (NOW()) NOT NULL,
				PRIMARY KEY (userId)
				);
				""";

		try (PreparedStatement s = database.getConnection().prepareStatement(statement)){
			s.executeUpdate();
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}
}
