package me.protoflicker.chessmate.protocol.chess;

import me.protoflicker.chessmate.protocol.enums.GameSide;
import me.protoflicker.chessmate.protocol.enums.PieceType;

public abstract class ChessUtils {

	public static String getStartingBoardText(){ //team+piece, Z for nothing
		return "Wr,Wn,Wb,Wq,Wk,Wb,Wn,Wr;" +
				"Wp,Wp,Wp,Wp,Wp,Wp,Wp,Wp;" +
				"Zz,Zz,Zz,Zz,Zz,Zz,Zz,Zz;" +
				"Zz,Zz,Zz,Zz,Zz,Zz,Zz,Zz;" +
				"Zz,Zz,Zz,Zz,Zz,Zz,Zz,Zz;" +
				"Zz,Zz,Zz,Zz,Zz,Zz,Zz,Zz;" +
				"Bp,Bp,Bp,Bp,Bp,Bp,Bp,Bp;" +
				"Br,Bn,Bb,Bq,Bk,Bb,Bn,Br";
	}

	public static ChessBoard getStartingBoard(){
		return stringToBoard(getStartingBoardText());
	}

	public static ChessBoard getDemoBoard(){
		return stringToBoard("Wr,Wn,Wb,Wq,Wk,Wb,Wn,Wr;" +
				"Wp,Wp,Wp,Wp,Wp,Wp,Wp,Wp;" +
				"Zz,Zz,Zz,Zz,Zz,Zz,Zz,Zz;" +
				"Zz,Zz,Zz,Zz,Zz,Zz,Zz,Zz;" +
				"Zz,Zz,Zz,Zz,Zz,Zz,Zz,Zz;" +
				"Zz,Zz,Zz,Zz,Zz,Zz,Zz,Zz;" +
				"Bp,Bp,Bp,Bp,Bp,Bp,Bp,Bp;" +
				"Br,Zz,Zz,Br,Bk,Zz,Zz,Br");
	}


	public static ChessBoard stringToBoard(String boardString){
		ChessPiece[][] board = new ChessPiece[8][8];
		String[] ranks = boardString.split(";");
		for(int i = 0; i < 8; i++){
			String[] rank = ranks[i].split(",");
			for(int j = 0; j < 8; j++){
				board[i][j] = codeToPiece(rank[j]);
			}
		}

		return new ChessBoard(board);
	}

	public static ChessPiece codeToPiece(String code){
		PieceType type = PieceType.getByChessCode(code.charAt(1) + "");
		if(type != null){
			return new ChessPiece(type, GameSide.getByChessCode("" + code.charAt(0)));
		} else {
			return null;
		}
	}
}
