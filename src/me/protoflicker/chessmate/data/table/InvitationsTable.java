package me.protoflicker.chessmate.data.table;

import me.protoflicker.chessmate.Server;
import me.protoflicker.chessmate.data.Database;
import me.protoflicker.chessmate.protocol.chess.ChessUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InvitationsTable {


	public static void createTable(Database database){
		{
			String statement =
					"""
						CREATE TABLE IF NOT EXISTS `Invitations` (
						invitationId BINARY(16) NOT NULL UNIQUE,
						inviterId BINARY(16) NOT NULL,
						inviteeId BINARY(16) NOT NULL,
						expiry TIMESTAMP NOT NULL,
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
