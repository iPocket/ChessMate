package me.protoflicker.chessmate.console;

import me.protoflicker.chessmate.chess.RunningGame;
import me.protoflicker.chessmate.connection.ClientThread;
import me.protoflicker.chessmate.data.DataManager;
import me.protoflicker.chessmate.data.table.UserTable;
import me.protoflicker.chessmate.manager.GameManager;
import me.protoflicker.chessmate.manager.LoginManager;
import me.protoflicker.chessmate.protocol.chess.enums.SimpleGameInfo;
import org.mariadb.jdbc.plugin.authentication.standard.ed25519.Utils;

import java.util.Scanner;

public class ConsoleThread extends Thread {

	public ConsoleThread(){
		super("ConsoleThread");
	}

	//This method was used for testing the chess module
//	@Override
//	public void run(){
//		Scanner scan = new Scanner(System.in).useDelimiter("\n");
//		String text;
//		ChessBoard board = ChessUtils.getStartingBoard();
//		while(!isInterrupted()){
//			try {
//				text = scan.next();
//				if(!text.isEmpty()){
//					if(text.equals("undo")){
//						board.undoLastMove();
//					} else {
//						String[] p = text.split(" ");
//						ChessMove move = board.tryGetValidMove(ChessPosition.fromChessNotation(p[0]), ChessPosition.fromChessNotation(p[1]));
//						if(move != null && move.getGameSide().equals(board.getNumberOfPerformedMoves() % 2 == 0 ? GameSide.WHITE : GameSide.BLACK)){
//							board.performMove(move, new Timestamp(System.currentTimeMillis()));
//						} else {
//							Logger.getInstance().log("Move illegal", Logger.LogLevel.WARNING);
//						}
//					}
//				}
//				Logger.getInstance().log(board.getGameStatus().getName());
//				Logger.getInstance().log(board.getAsciiDisplay(true));
//			} catch (Exception e){
//				continue;
//			}
//		}
//	}

	@Override
	public void run(){
		Scanner scan = new Scanner(System.in).useDelimiter("\n");
		String text;
		while(!isInterrupted()){
			try {
				text = scan.next();
				if(!text.isEmpty()){
					String[] args = text.split(" ");
					switch(args[0].toLowerCase()){
						case "user" -> {
							if(args.length > 1){
								switch(args[1].toLowerCase()){
									case "delete" -> {
										if(args.length > 2){
											byte[] userId = UserTable.getUserIdByUsername(args[2]);
											if(userId != null){
												for(RunningGame game : GameManager.getRunningGames().values()){
													if(game.getInfo().isParticipant(userId)){
														GameManager.unloadGameAndKick(game);
													}
												}

												for(SimpleGameInfo info : DataManager.getGamesByUser(userId)){
													DataManager.deleteGame(info.getGameId());
												}

												new Thread(() -> {
													for(ClientThread client : LoginManager.getClientsById(userId)){
														client.tryClose();
													}
												}).start();

												UserTable.deleteUser(userId);
												Logger.getInstance().log("Successfully deleted user " + Utils.bytesToHex(userId));
											} else {
												Logger.getInstance().log("Error while deleting user: No such user exists.");
											}
										} else {
											Logger.getInstance().log("Usage: user delete <target>");
										}
									}

									case "rename" -> {
										if(args.length > 3){
											byte[] userId = UserTable.getUserIdByUsername(args[2]);
											String newUsername = args[3];
											if(userId != null){
												if(LoginManager.isUsernameValid(newUsername)){
													UserTable.setUsername(userId, newUsername);
													Logger.getInstance().log("Successfully renamed user " + Utils.bytesToHex(userId) + " to " + args[3]);
												} else {
													Logger.getInstance().log("Error while deleting user: Bad new username. Must be alphanumeric and <= 32");
												}
											} else {
												Logger.getInstance().log("Error while deleting user: No such user exists.");
											}
										} else {
											Logger.getInstance().log("Usage: user rename <target> <new>");
										}
									}

									case "setpass" -> {
										if(args.length > 3){
											byte[] userId = UserTable.getUserIdByUsername(args[2]);
											String newPassword = args[3];
											if(userId != null){
												if(LoginManager.isPasswordValid(newPassword)){
													UserTable.setHashedPassword(userId, LoginManager.hashPassword(newPassword));
													Logger.getInstance().log("Successfully reset password of user " + Utils.bytesToHex(userId));
												} else {
													Logger.getInstance().log("Error while deleting user: Bad new password. Must be >= 8");
												}
											} else {
												Logger.getInstance().log("Error while deleting user: No such user exists.");
											}
										} else {
											Logger.getInstance().log("Usage: user setpass <target> <new>");
										}
									}
									default -> {
										Logger.getInstance().log("Usage: user <delete/rename/setpass> <target> [...]");
									}
								}
							} else {
								Logger.getInstance().log("Usage: user <delete/rename/setpass> <target> [...]");
							}
						}

						default -> {
							Logger.getInstance().log("Available commands: user <delete/rename/setpass> <target> [...]");
						}
					}
				}
			} catch (Exception e){
				continue;
			}
		}
	}
}
