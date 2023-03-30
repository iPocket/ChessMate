package me.protoflicker.chessmate.data.table;

import me.protoflicker.chessmate.Server;
import me.protoflicker.chessmate.data.Database;
import me.protoflicker.chessmate.protocol.chess.enums.AccountType;
import me.protoflicker.chessmate.protocol.packet.user.UserInfo;

import java.sql.*;

public abstract class UserTable {

	public static byte[] getUserIdByUsername(String username){
		String statement =
				"""
				SELECT userId
				FROM `Users`
				WHERE LOWER(username) = LOWER(?)
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

	public static void setUsername(byte[] userId, String username){
		String statement =
				"""
				UPDATE `Users`
				SET username = ?
				WHERE userId = ?;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setString(1, username);
			s.setBytes(2, userId);
			s.executeUpdate();

		} catch (SQLException e){
			return; //no exception needed
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

	public static void setHashedPassword(byte[] userId, String password){
		String statement =
				"""
				UPDATE `Users`
				SET password = ?
				WHERE userId = ?;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setString(1, password);
			s.setBytes(2, userId);
			s.executeUpdate();

		} catch (SQLException e){
			return; //no exception needed
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

	public static UserInfo getUserInfo(byte[] userId){
		String statement =
				"""
				SELECT username,birthday,accountType,lastLogin
				FROM `Users`
				WHERE userId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, userId);
			ResultSet r = s.executeQuery();
			if(r.next()){
				return new UserInfo(userId, r.getString("username"), r.getDate("birthday"),
						AccountType.getByCode(r.getInt("accountType")), r.getTimestamp("lastLogin"));
			} else {
				return null;
			}
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static void updateLastLogin(byte[] userId){
		String statement =
				"""
				UPDATE `Users`
				SET lastLogin = ?
				WHERE userId = ?;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			s.setBytes(2, userId);
			s.executeUpdate();

		} catch (SQLException e){
			return; //no exception needed
		}
	}

	public static void createUser(String username, String hashedPassword, Date birthday, AccountType type){
		String statement =
				"""
				INSERT INTO `Users` (username, password, birthday, accountType)
				VALUES (?, ?, ?, ?);
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setString(1, username);
			s.setString(2, hashedPassword);
			s.setDate(3, birthday);
			s.setInt(4, type.getCode());
			s.executeUpdate();
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static void deleteUser(byte[] userId){
		String statement =
				"""
				DELETE FROM `Users`
				WHERE userId = ?;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, userId);
			s.executeUpdate();
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static void createTable(Database database){
		String statement =
				"""
				CREATE TABLE IF NOT EXISTS `Users` (
				userId BINARY(16) DEFAULT (UNHEX(REPLACE(UUID(), "-",""))) NOT NULL UNIQUE,
				username VARCHAR(32) NOT NULL,
				password CHAR(64) NOT NULL,
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
