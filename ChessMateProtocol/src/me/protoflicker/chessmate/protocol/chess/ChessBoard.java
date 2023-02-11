package me.protoflicker.chessmate.protocol.chess;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.enums.GameSide;
import me.protoflicker.chessmate.protocol.enums.MoveType;
import me.protoflicker.chessmate.protocol.enums.PieceType;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ChessBoard implements Serializable, Cloneable {

	@Getter
	private ChessPiece[][] board = new ChessPiece[8][8];

	@Getter
	private List<PerformedChessMove> performedMoves = new ArrayList<>();

	@Getter
	private List<ChessPiece> takenPieces = new ArrayList<>();

	public ChessBoard(ChessPiece[][] board){
		this.board = board;
	}

//	public ChessBoard(ChessPiece[][] board, List<PerformedChessMove> performedMoves){
//		this.board = board;
//		this.performedMoves = performedMoves;
//	}
//
//	public ChessBoard(ChessPiece[][] board, List<PerformedChessMove> performedMoves, List<ChessPiece> takenPieces){
//		this.board = board;
//		this.performedMoves = performedMoves;
//		this.takenPieces = takenPieces;
//	}

//	public ChessPiece[] getRank(int rank){
//		return board[rank];
//	}
//
//	public ChessPiece[] getFile(int file){
//		ChessPiece[] array = new ChessPiece[8];
//		for(int i = 0; i < 8; i++){
//			array[i] = board[i][file];
//		}
//		return array;
//	}

	public List<LocatableChessPiece> findPieces(PieceType type, GameSide side){
		List<LocatableChessPiece> list = new ArrayList<>();
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				ChessPiece piece = board[i][j];
				if(piece != null && (type == null || piece.getType().equals(type)) && (side == null || piece.getGameSide().equals(side))){
					list.add(new LocatableChessPiece(piece, new ChessPosition(i, j)));
				}
			}
		}

		return list;
	}

	public List<LocatableChessPiece> findPieces(GameSide side){
		return findPieces(null, side);
	}

	public List<LocatableChessPiece> findPieces(PieceType type){
		return findPieces(type, null);
	}


	//prohibited
	private ChessPiece getRawPieceAtLocation(int rank, int file){
		if(rank >= 0 && rank <= 7 && file >= 0 && file <= 7){
			return board[rank][file];
		} else {
			return null;
		}
	}

	public LocatableChessPiece getPieceAtLocation(ChessPosition position){
		if(position.getRank() >= 0 && position.getRank() <= 7 && position.getFile() >= 0 && position.getFile() <= 7){
			ChessPiece piece = board[position.getRank()][position.getFile()];
			if(piece != null){
				return new LocatableChessPiece(piece, position);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public LocatableChessPiece getPieceAtLocation(int rank, int file){
		return getPieceAtLocation(new ChessPosition(rank, file));
	}

	public List<LocatableChessPiece> getKings(GameSide gameSide){
		return findPieces(PieceType.KING, gameSide);
	}

	public PerformedChessMove getLastPerformedMove(){
		if(performedMoves.size() > 0){
			return performedMoves.get(performedMoves.size() - 1);
		} else {
			return null;
		}
	}

	public List<ChessMove> getMoves(LocatableChessPiece piece){
		return getMoves(piece, true);
	}

	public List<ChessMove> getMoves(LocatableChessPiece piece, boolean considerKings){
		ChessPosition loc = piece.getPosition();
		List<ChessMove> moves = new ArrayList<>();

		switch(piece.getType()){

			case KING -> {
				addMove(moves, MoveType.MOVE, piece, new ChessPosition(loc.getRank() + 1, loc.getFile()));
				addMove(moves, MoveType.MOVE, piece, new ChessPosition(loc.getRank() + 1, loc.getFile() + 1));
				addMove(moves, MoveType.MOVE, piece, new ChessPosition(loc.getRank(), loc.getFile() + 1));
				addMove(moves, MoveType.MOVE, piece, new ChessPosition(loc.getRank() - 1, loc.getFile() + 1));
				addMove(moves, MoveType.MOVE, piece, new ChessPosition(loc.getRank() - 1, loc.getFile()));
				addMove(moves, MoveType.MOVE, piece, new ChessPosition(loc.getRank() - 1, loc.getFile() - 1));
				addMove(moves, MoveType.MOVE, piece, new ChessPosition(loc.getRank(), loc.getFile() - 1));
				addMove(moves, MoveType.MOVE, piece, new ChessPosition(loc.getRank() + 1, loc.getFile() - 1));

				if(considerKings && !isUnderThreat(piece, false)
						&& performedMoves.stream().noneMatch(p -> p.getMove().getPieceFrom().equals(loc))){
					List<LocatableChessPiece> rooks = findPieces(PieceType.ROOK,
							piece.getGameSide()).stream().filter(r -> r.getPosition().getRank() == loc.getRank()).toList();

					for(LocatableChessPiece rook : rooks){
						if(performedMoves.stream().noneMatch(p -> p.getMove().getPieceFrom().equals(rook.getPosition()))){
							boolean canCastle = true;

							int length = loc.getFileDifference(rook.getPosition());
							boolean isIncrease = rook.getPosition().getFile() > loc.getFile();
							ChessPosition to;
							for(int i = 1; i <= length; i++){
								to = new ChessPosition(loc.getRank(), loc.getFile() + (i * (isIncrease ? 1 : -1)));
								if(getRawPieceAtLocation(to.getRank(), to.getFile()) != null){
									canCastle = false;
									break;
								} else if(isUnderThreat(to, piece.getGameSide())){
									canCastle = false;
									break;
								}
							}

							if(canCastle){
								addMove(moves, MoveType.CASTLE, piece, rook.getPosition());
							}
						}
					}
				}
				break;
			}

			case QUEEN -> {
				moves.addAll(getMoves(piece.getVersion(PieceType.ROOK), considerKings));
				moves.addAll(getMoves(piece.getVersion(PieceType.BISHOP), considerKings));
				break;
			}

			case ROOK -> {
				ChessPosition to;
				if(loc.getRank() < 7){
					for(int i = loc.getRank()+1; i <= 7; i++){
						to = new ChessPosition(i, loc.getFile());
						addMove(moves, MoveType.MOVE, piece, to);
						if(getRawPieceAtLocation(to.getRank(), to.getFile()) != null){
							break;
						}
					}
				}

				if(loc.getRank() > 0){
					for(int i = loc.getRank()-1; i >= 0; i--){
						to = new ChessPosition(i, loc.getFile());
						addMove(moves, MoveType.MOVE, piece, to);
						if(getRawPieceAtLocation(to.getRank(), to.getFile()) != null){
							break;
						}
					}
				}

				if(loc.getFile() < 7){
					for(int i = loc.getFile()+1; i <= 7; i++){
						to = new ChessPosition(loc.getRank(), i);
						addMove(moves, MoveType.MOVE, piece, to);
						if(getRawPieceAtLocation(to.getRank(), to.getFile()) != null){
							break;
						}
					}
				}

				if(loc.getFile() > 0){
					for(int i = loc.getFile()-1; i >= 0; i--){
						to = new ChessPosition(loc.getRank(), i);
						addMove(moves, MoveType.MOVE, piece, to);
						if(getRawPieceAtLocation(to.getRank(), to.getFile()) != null){
							break;
						}
					}
				}

				break;
			}
			case BISHOP -> {
				ChessPosition to;
				if(loc.getRank() < 7 && loc.getFile() < 7){
					for(int i = 1; i <= Math.min(Math.abs(loc.getRank() - 7), Math.abs(loc.getFile() - 7)); i++){
						to = new ChessPosition(loc.getRank() + i, loc.getFile() + i);
						addMove(moves, MoveType.MOVE, piece, to);
						if(getRawPieceAtLocation(to.getRank(), to.getFile()) != null){
							break;
						}
					}
				}

				if(loc.getRank() > 0 && loc.getFile() > 0){
					for(int i = 1; i <= Math.min(loc.getRank(), loc.getFile()); i++){
						to = new ChessPosition(loc.getRank() - i, loc.getFile() - i);
						addMove(moves, MoveType.MOVE, piece, to);
						if(getRawPieceAtLocation(to.getRank(), to.getFile()) != null){
							break;
						}
					}
				}

				if(loc.getRank() < 7 && loc.getFile() > 0){
					for(int i = 1; i <= Math.min(Math.abs(loc.getRank() - 7), loc.getFile()); i++){
						to = new ChessPosition(loc.getRank() + i, loc.getFile() - i);
						addMove(moves, MoveType.MOVE, piece, to);
						if(getRawPieceAtLocation(to.getRank(), to.getFile()) != null){
							break;
						}
					}
				}

				if(loc.getRank() < 7 && loc.getFile() > 0){
					for(int i = 1; i <= Math.min(loc.getRank(), Math.abs(loc.getFile() - 7)); i++){
						to = new ChessPosition(loc.getRank() - i, loc.getFile() + i);
						addMove(moves, MoveType.MOVE, piece, to);
						if(getRawPieceAtLocation(to.getRank(), to.getFile()) != null){
							break;
						}
					}
				}

				break;
			}
			case KNIGHT -> {
				addMove(moves, MoveType.MOVE, piece, new ChessPosition(loc.getRank() + 1, loc.getFile() + 2));
				addMove(moves, MoveType.MOVE, piece, new ChessPosition(loc.getRank() + 2, loc.getFile() + 1));

				addMove(moves, MoveType.MOVE, piece, new ChessPosition(loc.getRank() + 1, loc.getFile() - 2));
				addMove(moves, MoveType.MOVE, piece, new ChessPosition(loc.getRank() + 2, loc.getFile() - 1));

				addMove(moves, MoveType.MOVE, piece, new ChessPosition(loc.getRank() - 1, loc.getFile() - 2));
				addMove(moves, MoveType.MOVE, piece, new ChessPosition(loc.getRank() - 2, loc.getFile() - 1));

				addMove(moves, MoveType.MOVE, piece, new ChessPosition(loc.getRank() - 1, loc.getFile() + 2));
				addMove(moves, MoveType.MOVE, piece, new ChessPosition(loc.getRank() - 2, loc.getFile() + 1));
				break;
			}
			case PAWN -> {
				int rankChange = piece.getGameSide() == GameSide.WHITE ? 1 : -1;
				ChessPosition forward = new ChessPosition(loc.getRank() + rankChange, loc.getFile());
				if(getRawPieceAtLocation(forward.getRank(), forward.getFile()) == null){
					addMove(moves, MoveType.MOVE, piece, forward);
				}

				int startingRank = piece.getGameSide() == GameSide.WHITE ? 1 : 6;
				if(loc.getRank() == startingRank){
					ChessPosition forwardTwice = new ChessPosition(loc.getRank() + rankChange * 2, loc.getFile());
					if(getRawPieceAtLocation(forwardTwice.getRank(), forwardTwice.getFile()) == null){
						addMove(moves, MoveType.MOVE, piece, forwardTwice);
					}
				}

				PerformedChessMove lastMove = getLastPerformedMove();
				if(lastMove != null
						&& lastMove.getMove().getPieceMoved() == PieceType.PAWN
						&& lastMove.getMove().getPieceTo().getRankDifference(lastMove.getMove().getPieceFrom()) == 2){
					addMove(moves, MoveType.EN_PASSANT, piece, new ChessPosition(loc.getRank() + rankChange, loc.getFile() + 1));
					addMove(moves, MoveType.EN_PASSANT, piece, new ChessPosition(loc.getRank() + rankChange, loc.getFile() - 1));
				}
				break;
			}
		}


		if(considerKings){
			List<LocatableChessPiece> kings = getKings(piece.getGameSide());
			System.out.println("King: " + kings.get(0).getPosition() + "");
			moves.removeIf(m -> { //threatens own king
				ChessBoard newBoard = this.clone();
				newBoard.performMove(m, new Timestamp(System.currentTimeMillis()));
				for(LocatableChessPiece king : kings){
					if(newBoard.isUnderThreat(king, false)){
						return true;
					}
				}

				return false;
			});
		}

		return moves;
	}

	private void addMove(List<ChessMove> moves, MoveType moveType, LocatableChessPiece piece, ChessPosition to){
		if(to.getRank() >= 0 && to.getRank() <= 7 && to.getFile() >= 0 && to.getFile() <= 7){
			LocatableChessPiece r = getPieceAtLocation(to);
			if(moveType.equals(MoveType.MOVE) || moveType.equals(MoveType.EN_PASSANT)){
				if(r != null){
					moveType = moveType.getTakeVersion();
				}
			}

			if(moveType.isCanTake()){
				if(r != null && r.getGameSide().equals(piece.getGameSide())){
					return;
				}
			}

			moves.add(new ChessMove(moveType, piece.getGameSide(), piece.getType(), piece.getPosition(), to));
		}
	}

	public void initMoves(List<PerformedChessMove> moves){
		for(PerformedChessMove move : moves){
			performMove(move.getMove(), move.getTimePlayed());
		}
	}

	public void performMove(ChessMove move, Timestamp time){
		doMove(move);
		performedMoves.add(new PerformedChessMove(performedMoves.size() + 1, time, move));
	}

	public void doMove(ChessMove move){
		ChessPosition from = move.getPieceFrom();
		ChessPosition to = move.getPieceTo();
		switch(move.getMoveType()){

			case MOVE, TAKE -> {
				PieceType newType = move.getPieceMoved();

				if(newType.equals(PieceType.PAWN)){
					if((to.getRank() == 7 && move.getGameSide().equals(GameSide.WHITE))
							|| (to.getRank() == 0 && move.getGameSide().equals(GameSide.BLACK))){
						newType = move.getPromotionPiece();
					} else {
						move.setPromotionPiece(null);
					}
				} else {
					move.setPromotionPiece(null);
				}

				movePiece(from, to, new ChessPiece(newType, move.getGameSide()));
				break;
			}

			case CASTLE -> {
				int length = from.getFileDifference(to);
				double fileChange = length / 2.0;
				boolean isIncrease = to.getFile() > from.getFile();
				double kingFile = isIncrease ? Math.ceil(fileChange) : -Math.floor(fileChange);

				movePiece(from, new ChessPosition(from.getRank(), (int) (from.getFile() + kingFile)),
						new ChessPiece(move.getPieceMoved(), move.getGameSide())); //king

				movePiece(from, new ChessPosition(from.getRank(), (int) (from.getFile() + kingFile) - (isIncrease ? 1 : -1)),
						new ChessPiece(PieceType.ROOK, move.getGameSide())); //rook
				break;
			}

			case EN_PASSANT, EN_PASSANT_TAKE -> {
				movePiece(from, to, new ChessPiece(move.getPieceMoved(), move.getGameSide()));
				replacePiece(new ChessPosition(from.getRank(), to.getFile()), null);
				break;
			}
		}
	}

	//has to be last move to be undone
	//could've taken different approach (list containing all board[][] modifications, list containing lists), perhaps later to support variants?
	private void undoMove(ChessMove move){
		ChessPosition from = move.getPieceFrom();
		ChessPosition to = move.getPieceTo();
		switch(move.getMoveType()){

			case MOVE -> {
				movePiece(to, from, new ChessPiece(move.getPromotionPiece() != null ? PieceType.PAWN : move.getPieceMoved(), move.getGameSide()));
				break;
			}

			case TAKE -> {
				movePiece(to, from, new ChessPiece(move.getPromotionPiece() != null ? PieceType.PAWN : move.getPieceMoved(), move.getGameSide()));
				replacePiece(to, getLastTakenPiece());
			}

			case CASTLE -> {
				int length = from.getFileDifference(to);
				double fileChange = length / 2.0;
				boolean isIncrease = to.getFile() > from.getFile();
				double kingFile = isIncrease ? Math.ceil(fileChange) : -Math.floor(fileChange);

				movePiece(new ChessPosition(from.getRank(), (int) (from.getFile() + kingFile)), from,
						new ChessPiece(move.getPieceMoved(), move.getGameSide())); //king

				movePiece(new ChessPosition(from.getRank(), (int) (from.getFile() + kingFile) - (isIncrease ? 1 : -1)), from,
						new ChessPiece(PieceType.ROOK, move.getGameSide())); //rook
				break;
			}

			case EN_PASSANT -> {
				replacePiece(new ChessPosition(from.getRank(), to.getFile()), getLastTakenPiece());
				movePiece(to, from, new ChessPiece(move.getPieceMoved(), move.getGameSide()));
				break;
			}

			case EN_PASSANT_TAKE -> {
				replacePiece(new ChessPosition(from.getRank(), to.getFile()), getLastTakenPiece());
				movePiece(to, from, new ChessPiece(move.getPieceMoved(), move.getGameSide()));
				replacePiece(to, getLastTakenPiece());
				break;
			}
		}
	}

	public ChessBoard getBoardAtMove(int moveNumber){
		PerformedChessMove originalLast = getLastPerformedMove();

		if(originalLast.getMoveNumber() > moveNumber){
			PerformedChessMove last;
			ChessBoard newBoard = this.clone();
			for(int i = 0; i < originalLast.getMoveNumber() - moveNumber; i++){
				last = newBoard.getLastPerformedMove();
				newBoard.undoMove(last.getMove());
				newBoard.performedMoves.remove(last);
			}

			return newBoard;
		} else {
			return this.clone();
		}
	}

	public void replacePiece(LocatableChessPiece locatableChessPiece, ChessPiece newPiece){
		replacePiece(locatableChessPiece.getPosition(), newPiece);
	}

	public void replacePiece(ChessPosition position, ChessPiece newPiece){
		if(position != null){
			ChessPiece oldPiece = getRawPieceAtLocation(position.getRank(), position.getFile());
			board[position.getRank()][position.getFile()] = newPiece;
			if(oldPiece != null){
				takenPieces.add(oldPiece.clone());
			}
		}
	}

	public void movePiece(ChessPosition from, ChessPosition to){
		movePiece(from, to, getRawPieceAtLocation(from.getRank(), from.getFile()));
	}

	public void movePiece(ChessPosition from, ChessPosition to, ChessPiece piece){
		board[from.getRank()][from.getFile()] = null;
		replacePiece(to, piece);
	}

	public ChessPiece getLastTakenPiece(){
		return takenPieces.get(takenPieces.size()-1);
	}

	public boolean isUnderThreat(LocatableChessPiece piece){
		return isUnderThreat(piece.getPosition(), piece.getGameSide(), true);
	}

	public boolean isUnderThreat(LocatableChessPiece piece, boolean considerKings){
		return isUnderThreat(piece.getPosition(), piece.getGameSide(), considerKings);
	}

	public boolean isUnderThreat(ChessPosition position, GameSide gameSide){
		return isUnderThreat(position, gameSide, true);
	}

	public boolean isUnderThreat(ChessPosition position, GameSide gameSide, boolean considerKings){
		List<LocatableChessPiece> pieces = findPieces(gameSide.getOpposite());
		for(LocatableChessPiece piece : pieces){
			for(ChessMove move : getMoves(piece, considerKings)){
				if(move.getMoveType().isCanTake() && move.getPieceTo().equals(position)){
					return true;
				}
			}
		}

		return false;
	}

	public boolean isCheckmateFor(GameSide gameSide){
		for(LocatableChessPiece king : getKings(gameSide)){
			if(getMoves(king).size() == 0){
				return true;
			}
		}

		return false;
	}

	@Override
	public ChessBoard clone(){
		try {
			ChessBoard clone = (ChessBoard) super.clone();
			clone.board = board.clone();
			clone.performedMoves = new ArrayList<>(performedMoves);
			this.takenPieces = new ArrayList<>(takenPieces);
			return clone;
		} catch(CloneNotSupportedException e){
			throw new AssertionError();
		}
	}
}
