package me.protoflicker.chessmate.console;

import me.protoflicker.chessmate.protocol.chess.ChessBoard;
import me.protoflicker.chessmate.protocol.chess.ChessMove;
import me.protoflicker.chessmate.protocol.chess.ChessPosition;
import me.protoflicker.chessmate.protocol.chess.ChessUtils;
import me.protoflicker.chessmate.protocol.enums.GameSide;

import java.sql.Timestamp;
import java.util.Scanner;

public class ConsoleThread extends Thread {

	public ConsoleThread(){
		super("ConsoleThread");
	}

	@Override
	public void run(){
		Scanner scan = new Scanner(System.in).useDelimiter("\n");
		String text;
		ChessBoard board = ChessUtils.getDemoBoard();
		while(!isInterrupted()){
			try {
				text = scan.next();
				if(!text.isEmpty()){
					if(text.equals("undo")){
						board.undoLastMove();
					} else {
						String[] p = text.split(" ");
						ChessMove move = board.tryGetValidMove(ChessPosition.fromChessNotation(p[0]), ChessPosition.fromChessNotation(p[1]));
						if(move != null && move.getGameSide().equals(board.getNumberOfPerformedMoves() % 2 == 0 ? GameSide.WHITE : GameSide.BLACK)){
							board.performMove(move, new Timestamp(System.currentTimeMillis()));
						} else {
							Logger.getInstance().log("Move illegal", Logger.LogLevel.WARNING);
						}
					}
				}
				Logger.getInstance().log(board.getTakenPieces().size() + "");
				Logger.getInstance().log(board.getAsciiDisplay(true));
			} catch (Exception e){
				continue;
			}
		}
	}
}
