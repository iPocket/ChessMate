package me.protoflicker.chessmate.data.table;

import me.protoflicker.chessmate.Server;
import me.protoflicker.chessmate.data.Database;
import me.protoflicker.chessmate.util.GeneralUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

public class TokenTable {

	public static byte[] getUserIdByToken(String token){
		String statement =
				"""
				SELECT userId
				FROM `Tokens`
				WHERE token = ? AND expiry > NOW()
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setString(1, token);
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

	public static String createAndAddToken(byte[] userId){
		String newToken = GeneralUtils.getSecureRandomString(128);
		String statement =
				"""
				INSERT INTO `Tokens` (userId, token, expiry)
				VALUES (?, ?, ?);
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, userId);
			s.setString(2, newToken);
			s.setTimestamp(3, new Timestamp(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(14)));
			s.executeUpdate();

			return newToken;
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static void removeTokenIfAuthorised(byte[] userId, String token){
		String statement =
				"""
				DELETE FROM `Tokens`
				WHERE token = ? AND userId = ?;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setString(1, token);
			s.setBytes(2, userId);
			s.executeUpdate();
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static void createTable(Database database){
		{
			String statement =
					"""
						CREATE TABLE IF NOT EXISTS `Tokens` (
						token CHAR(128) NOT NULL UNIQUE,
						userId BINARY(16) NOT NULL,
						expiry TIMESTAMP NOT NULL,
						PRIMARY KEY (token),
						FOREIGN KEY (userId) REFERENCES Users(userId) ON DELETE CASCADE
						);
							""";

			try (PreparedStatement s = database.getConnection().prepareStatement(statement)) {
				s.executeUpdate();
			} catch(SQLException e){
				throw new RuntimeException(e);
			}
		}

		{
			String statement =
				"""
					CREATE EVENT IF NOT EXISTS `TokenRemover`
						ON SCHEDULE EVERY 1 DAY
						STARTS CURRENT_TIMESTAMP
							DO
								DELETE FROM %MedicBag%.Tokens WHERE expiry < NOW();
					""".replace("%MedicBag%", Server.getInstance().getDataManager().getDatabaseName());

			try (PreparedStatement s = database.getConnection().prepareStatement(statement)) {
				s.executeUpdate();
			} catch(SQLException e){
				throw new RuntimeException(e);
			}
		}
	}
}
