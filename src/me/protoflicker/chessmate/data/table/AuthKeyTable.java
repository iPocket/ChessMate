package me.protoflicker.chessmate.data.table;

import me.protoflicker.chessmate.Server;
import me.protoflicker.chessmate.data.Database;
import me.protoflicker.chessmate.util.GeneralUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

public class AuthKeyTable {

	public static byte[] getUserIdByAuthKey(String authKey){
		String statement =
				"""
				SELECT userId
				FROM `AuthKeys`
				WHERE authKey = ? AND expiry < NOW()
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setString(1, authKey);
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

	public static String createAndAddAuthKey(byte[] userId){
		String newAuthKey = GeneralUtils.getSecureRandomString(64);
		String statement =
				"""
				INSERT INTO `AuthKeys` (userId, authKey, expiry)
				VALUES (?, ?, ?);
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, userId);
			s.setString(2, newAuthKey);
			s.setTimestamp(3, new Timestamp(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3)));
			s.executeUpdate();

			return newAuthKey;
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static void createTable(Database database){
		{
			String statement =
					"""
						CREATE TABLE IF NOT EXISTS `AuthKeys` (
						userId BINARY(16) NOT NULL,
						authKey CHAR(64) NOT NULL,
						expiry TIMESTAMP NOT NULL,
						PRIMARY KEY (userId, authKey),
						FOREIGN KEY (userId) REFERENCES Users(userId),
						KEY (authKey)
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
					CREATE EVENT IF NOT EXISTS `AuthKeyRemover`
						ON SCHEDULE EVERY 1 DAY
						STARTS CURRENT_TIMESTAMP
							DO
								DELETE FROM chess.AuthKeys WHERE expiry < NOW();
					""";

			try (PreparedStatement s = database.getConnection().prepareStatement(statement)) {
				s.executeUpdate();
			} catch(SQLException e){
				throw new RuntimeException(e);
			}
		}
	}
}
