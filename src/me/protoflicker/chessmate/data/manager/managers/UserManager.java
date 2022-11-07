package me.protoflicker.chessmate.data.manager.managers;

import me.protoflicker.chessmate.data.Database;
import me.protoflicker.chessmate.data.manager.TableManager;
import me.protoflicker.chessmate.data.record.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//remember LoadingCache and whatnot, CompletableFuture, etc
public class UserManager extends TableManager<User> {

	public UserManager(Database database){
		super(database);
	}

	@Override
	public void createTable(){
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

		try (PreparedStatement s = this.database.getConnection().prepareStatement(statement)){
			s.executeUpdate();
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getId(User record){
		return record.userId();
	}

	@Override
	public User loadRecord(String id){
		String statement =
    			"""
				SELECT *
				FROM `Users`
				WHERE userId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = this.database.getConnection().prepareStatement(statement)){
			s.setString(0, id);
			ResultSet r = s.executeQuery();
			return new User(id, r.getString(0), r.getString(1), r.getDate(2), r.getTimestamp(3));
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public void insertRecord(User record){
		String statement =
				"""
				INSERT INTO `Users` (username, hashedPassword, birthday, lastLogin)
				VALUES (?, ?, ?, ?);
				""";

		try (PreparedStatement s = this.database.getConnection().prepareStatement(statement)){
			s.setString(0, record.userId());
			s.setString(1, record.username());
			s.setString(2, record.hashedPassword());
			s.setDate(3, record.birthday());
			s.setTimestamp(4, record.lastLogin());
			s.executeUpdate();
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public void updateRecord(User record){
		String statement =
    			"""
				UPDATE `Users`
				WHERE userId = ?
				SET (username, hashedPassword, birthday, lastLogin)
				= (?, ?, ?, ?);
				""";

		try (PreparedStatement s = this.database.getConnection().prepareStatement(statement)){
			s.setString(0, record.userId());
			s.setString(1, record.username());
			s.setString(2, record.hashedPassword());
			s.setDate(3, record.birthday());
			s.setTimestamp(4, record.lastLogin());
			s.executeUpdate();
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}
}
