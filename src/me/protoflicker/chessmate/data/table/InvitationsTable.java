package me.protoflicker.chessmate.data.table;

import me.protoflicker.chessmate.Server;
import me.protoflicker.chessmate.data.Database;
import me.protoflicker.chessmate.protocol.chess.ChessUtils;
import me.protoflicker.chessmate.protocol.chess.enums.GameSide;
import me.protoflicker.chessmate.protocol.chess.enums.SimpleGameInfo;
import me.protoflicker.chessmate.protocol.packet.game.invitation.GameInvitation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InvitationsTable {

	public static GameInvitation getInvitationById(byte[] invitationId){
		String statement =
				"""
						SELECT inviterId,inviteeId,gameName,startingBoard,timeConstraint,timeIncrement,inviterSide
						FROM `Invitations`
						WHERE invitationId = ? AND expiry > NOW()
						LIMIT 1;
						""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)) {
			s.setBytes(1, invitationId);
			ResultSet r = s.executeQuery();
			if(r.next()){
				return getGameInvitation(
						invitationId,
						r.getBytes("inviterId"),
						r.getBytes("inviteeId"),
						r.getString("gameName"),
						r.getString("startingBoard"),
						r.getInt("timeConstraint"),
						r.getInt("timeIncrement"),
						GameSide.getByCode(r.getInt("inviterSide")));
			} else {
				return null;
			}
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}


	public static List<GameInvitation> getInvitationsByInviteeId(byte[] inviteeId){
		List<GameInvitation> invitations = new ArrayList<>();

		String statement =
				"""
				SELECT invitationId,inviterId,inviteeId,gameName,startingBoard,timeConstraint,timeIncrement,inviterSide
				FROM `Invitations`
				WHERE inviteeId = ? AND expiry > NOW()
				ORDER BY expiry DESC;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, inviteeId);
			ResultSet r = s.executeQuery();
			while(r.next()){
				invitations.add(getGameInvitation(
						r.getBytes("invitationId"),
						r.getBytes("inviterId"),
						inviteeId,
						r.getString("gameName"),
						r.getString("startingBoard"),
						r.getInt("timeConstraint"),
						r.getInt("timeIncrement"),
						GameSide.getByCode(r.getInt("inviterSide"))
				));
			}
			r.close();

			return invitations;
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	private static GameInvitation getGameInvitation(byte[] invitationId, byte[] inviterId,
													byte[] inviteeId, String gameName, String startingBoard, int timeConstraint, int timeIncrement,
													GameSide inviterSide){
		return new GameInvitation(
				invitationId,
				inviterSide,
				new SimpleGameInfo(
						null,
						gameName,
						inviterSide == GameSide.WHITE ? inviterId : inviteeId,
						inviterSide == GameSide.BLACK ? inviterId : inviteeId,
						startingBoard,
						timeConstraint,
						timeIncrement
				)
		);
	}


	public static byte[] createInvitationAndGetId(GameInvitation inv){
		String statement =
				"""
				INSERT INTO `Invitations` (inviterId, inviteeId, expiry, gameName, startingBoard, timeConstraint, timeIncrement, inviterSide)
				VALUES (?, ?, ?, ?, ?, ?, ?);
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)){
			s.setBytes(1, inv.getInviterId());
			s.setBytes(2, inv.getInviteeId());
			s.setTimestamp(3, new Timestamp(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(2)));
			s.setString(4, inv.getInfo().getGameName());
			s.setString(5, inv.getInfo().getStartingBoard());
			s.setInt(6, inv.getInfo().getTimeConstraint());
			s.setInt(7, inv.getInfo().getTimeIncrement());
			s.setInt(8, inv.getInviterSide().getCode());
			s.executeUpdate();

			return s.getGeneratedKeys().getBytes("invitationId");
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static void removeReceivedInvitation(byte[] invitationId, byte[] inviteeId){
		String statement =
				"""
				DELETE FROM `Invitations`
				WHERE invitationId = ? AND inviteeId = ?;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, invitationId);
			s.setBytes(2, inviteeId);
			s.executeUpdate();
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}


	public static void createTable(Database database){
		{
			String statement =
					"""
						CREATE TABLE IF NOT EXISTS `Invitations` (
						invitationId BINARY(16) NOT NULL UNIQUE,
						inviterId BINARY(16) NOT NULL,
						inviteeId BINARY(16) NOT NULL,
						expiry TIMESTAMP NOT NULL,
						gameName VARCHAR(64) DEFAULT "Unnamed Game" NOT NULL,
						startingBoard CHAR(191) DEFAULT ("%MedicBag%") NOT NULL,
						timeConstraint INT(16) UNSIGNED NOT NULL,
						timeIncrement INT(16) UNSIGNED NOT NULL,
						inviterSide TINYINT(1) UNSIGNED NOT NULL,
						PRIMARY KEY (invitationId),
						FOREIGN KEY (inviterId) REFERENCES Users(userId) ON DELETE CASCADE,
						FOREIGN KEY (inviteeId) REFERENCES Users(userId) ON DELETE CASCADE
						);
							""".replace("%MedicBag%", ChessUtils.getStartingBoardText());

			try (PreparedStatement s = database.getConnection().prepareStatement(statement)) {
				s.executeUpdate();
			} catch(SQLException e){
				throw new RuntimeException(e);
			}
		}

		{
			String statement =
				"""
					CREATE EVENT IF NOT EXISTS `InvitationRemover`
						ON SCHEDULE EVERY 1 DAY
						STARTS CURRENT_TIMESTAMP
							DO
								DELETE FROM %MedicBag%.Invitations WHERE expiry < NOW();
					""".replace("%MedicBag%", Server.getInstance().getDataManager().getDatabaseName());

			try (PreparedStatement s = database.getConnection().prepareStatement(statement)) {
				s.executeUpdate();
			} catch(SQLException e){
				throw new RuntimeException(e);
			}
		}
	}
}
