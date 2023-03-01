package me.protoflicker.chessmate.protocol.chess;

import lombok.Getter;
import lombok.Setter;
import me.protoflicker.chessmate.protocol.chess.enums.GameSide;
import me.protoflicker.chessmate.protocol.chess.enums.GameStatus;
import me.protoflicker.chessmate.protocol.chess.enums.MoveType;
import me.protoflicker.chessmate.protocol.chess.enums.PieceType;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChessBoard implements Serializable, Cloneable {

	@Getter
	private ChessPiece[][] board = new ChessPiece[8][8];

	@Getter
	private final Timestamp startingTime;

	@Getter
	private List<PerformedChessMove> performedMoves = new ArrayList<>();

	@Getter
	private List<ChessPiece> takenPieces = new ArrayList<>();

	@Getter
	@Setter
	private GameStatus gameStatus = GameStatus.ONGOING;

	@Getter
	private final Map<GameSide, Long> timeRemaining = new HashMap<>();

	@Getter
	private final long timeConstraint;

	@Getter
	private final long timeIncrement;

	@Getter
	@Setter
	private boolean isDummy;

	public ChessBoard(ChessPiece[][] board){
		this(board, new Timestamp(System.currentTimeMillis()));
	}

	public ChessBoard(ChessPiece[][] board, Timestamp startingTime){
		this(board, startingTime, 0, 0);
	}

	public ChessBoard(ChessPiece[][] board, Timestamp startingTime, long timeConstraint, long timeIncrement){
		this.board = board;
		this.startingTime = startingTime;
		this.timeConstraint = timeConstraint;
		this.timeIncrement = timeIncrement;
		if(timeConstraint > 0){
			for(GameSide side : GameSide.values()){
				timeRemaining.put(side, timeConstraint * 1000);
			}
		}
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

	//experimental
	private List<ChessPiece> findRawPieces(GameSide side){
		List<ChessPiece> list = new ArrayList<>();
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				ChessPiece piece = board[i][j];
				if(piece != null && (side == null || piece.getGameSide().equals(side))){
					list.add(piece);
				}
			}
		}

		return list;
	}

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
		if(!performedMoves.isEmpty()){
			return performedMoves.get(performedMoves.size() - 1);
		} else {
			return null;
		}
	}

	public PerformedChessMove getPenultimatePerformedMove(){
		if(performedMoves.size() > 1){
			return performedMoves.get(performedMoves.size() - 2);
		} else {
			return null;
		}
	}

	public int getNumberOfPerformedMoves(){
		return performedMoves.size();
	}

	public List<ChessMove> getMoves(LocatableChessPiece piece){
		return getMoves(piece, true, true);
	}

	public List<ChessMove> getThreateningMoves(LocatableChessPiece piece){
		return getMoves(piece, true, false);
	}

	public List<ChessMove> getMoves(LocatableChessPiece piece, boolean considerKings){
		return getMoves(piece, considerKings, true);
	}


	public List<ChessMove> getMoves(LocatableChessPiece piece, boolean considerKings, boolean includeCastle){
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

				if(includeCastle && considerKings && !isUnderThreat(piece, false)
						&& performedMoves.stream().noneMatch(p ->  p.getMove().getPieceFrom().equals(loc)
						|| p.getMove().getPieceTo().equals(loc))){
					List<LocatableChessPiece> rooks = findPieces(PieceType.ROOK,
							piece.getGameSide()).stream().filter(r -> r.getPosition().getRank() == loc.getRank()).collect(Collectors.toList());

					for(LocatableChessPiece rook : rooks){
						if(performedMoves.stream().noneMatch(p -> p.getMove().getMoveType() == MoveType.CASTLE || p.getMove().getPieceFrom().equals(rook.getPosition()) ||
								p.getMove().getPieceTo().equals(rook.getPosition()))){
							boolean canCastle = true;

							int length = loc.getFileDifference(rook.getPosition());
							boolean isIncrease = rook.getPosition().getFile() > loc.getFile();
							double fileChange = (length + 1) / 2.0;
							int kingFile = (int) (isIncrease ? Math.floor(fileChange) : -Math.floor(fileChange));

							ChessPosition to;
							for(int i = 1; i < length; i++){
								to = new ChessPosition(loc.getRank(), loc.getFile() + (i * (isIncrease ? 1 : -1)));
								if(getRawPieceAtLocation(to.getRank(), to.getFile()) != null){
									canCastle = false;
									break;
								} else if((isIncrease ? to.getFile() <= loc.getFile() + kingFile : to.getFile() >= loc.getFile() - kingFile)
										&& isUnderThreat(to, piece.getGameSide())){//is part of chess rules
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
				moves.forEach(m -> m.setPieceMoved(piece.getPiece().getType()));
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

				ChessPosition right = new ChessPosition(loc.getRank() + rankChange, loc.getFile() + 1);
				if(getRawPieceAtLocation(right.getRank(), right.getFile()) != null){
					addMove(moves, MoveType.MOVE, piece, right);
				}

				ChessPosition left = new ChessPosition(loc.getRank() + rankChange, loc.getFile() - 1);
				if(getRawPieceAtLocation(left.getRank(), left.getFile()) != null){
					addMove(moves, MoveType.MOVE, piece, left);
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
						&& lastMove.getMove().getPieceTo().getRank() == loc.getRank()
						&& lastMove.getMove().getPieceTo().getRankDifference(lastMove.getMove().getPieceFrom()) == 2){
					addMove(moves, MoveType.EN_PASSANT, piece,
							new ChessPosition(loc.getRank() + rankChange,
									loc.getFile() + (lastMove.getMove().getPieceTo().getFile() - loc.getFile())));
				}
				break;
			}
		}


		if(considerKings){
			moves.removeIf(m -> { //threatens own king
				ChessBoard newBoard = this.clone();
				newBoard.setGameStatus(GameStatus.ONGOING);
				newBoard.setDummy(true);
				newBoard.performMove(m, new Timestamp(System.currentTimeMillis()));
				return newBoard.isKingUnderThreat(piece.getGameSide());
			});
		}

		return moves;
	}

	private void addMove(List<ChessMove> moves, MoveType moveType, LocatableChessPiece piece, ChessPosition to){
		if(to.getRank() >= 0 && to.getRank() <= 7 && to.getFile() >= 0 && to.getFile() <= 7){
			LocatableChessPiece r = getPieceAtLocation(to);
			if(moveType.equals(MoveType.MOVE) && r != null){
				moveType = MoveType.TAKE;
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
		performedMoves.add(new PerformedChessMove(time, move));
		if(!isDummy && move.getMoveType().isPieceMove()){
			removeTime(move.getGameSide(), getTimeTakenForLastMove() - timeIncrement*1000 - 500);
		}
		updateGameStatus();
	}

	private void doMove(ChessMove move){
		ChessPosition from = move.getPieceFrom();
		ChessPosition to = move.getPieceTo();
		switch(move.getMoveType()){

			case MOVE, TAKE -> {
				PieceType newType = move.getPieceMoved();

				if(newType.equals(PieceType.PAWN)){
					if((to.getRank() == 7 && move.getGameSide().equals(GameSide.WHITE))
							|| (to.getRank() == 0 && move.getGameSide().equals(GameSide.BLACK))){
						if(move.getPromotionPiece() == null){
							move.setPromotionPiece(PieceType.QUEEN);
						}
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
				double fileChange = (length + 1) / 2.0;
				boolean isIncrease = to.getFile() > from.getFile();
				int kingFileChange = (int) (isIncrease ? Math.floor(fileChange) : -Math.floor(fileChange));
				ChessPosition kingPosition = new ChessPosition(from.getRank(), from.getFile() + kingFileChange);
				ChessPosition rookPosition = new ChessPosition(from.getRank(), from.getFile() + kingFileChange - (isIncrease ? 1 : -1));

				board[from.getRank()][from.getFile()] = null;
				board[kingPosition.getRank()][kingPosition.getFile()] = new ChessPiece(PieceType.KING, move.getGameSide());

				if(!kingPosition.equals(to)){
					board[to.getRank()][to.getFile()] = null;
				}

				board[rookPosition.getRank()][rookPosition.getFile()] = new ChessPiece(PieceType.ROOK, move.getGameSide());
				break;
			}

			case EN_PASSANT -> {
				movePiece(from, to, new ChessPiece(move.getPieceMoved(), move.getGameSide()));
				replacePiece(new ChessPosition(from.getRank(), to.getFile()), null);
				break;
			}

			case RESIGNATION -> {
				setGameStatus(move.getGameSide() == GameSide.WHITE ? GameStatus.BLACK_WIN_RESIGNATION : GameStatus.WHITE_WIN_RESIGNATION);
				break;
			}

			case DRAW_AGREEMENT -> {
				setGameStatus(GameStatus.DRAW_BY_AGREEMENT);
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
				replacePiece(to, getAndRemoveLastTakenPiece());
			}

			case CASTLE -> {
				int length = from.getFileDifference(to);
				double fileChange = (length + 1) / 2.0;
				boolean isIncrease = to.getFile() > from.getFile();
				int kingFileChange = (int) (isIncrease ? Math.floor(fileChange) : -Math.floor(fileChange));
				ChessPosition kingPosition = new ChessPosition(from.getRank(), from.getFile() + kingFileChange);
				ChessPosition rookPosition = new ChessPosition(from.getRank(), from.getFile() + kingFileChange - (isIncrease ? 1 : -1));

				board[rookPosition.getRank()][rookPosition.getFile()] = null;
				board[to.getRank()][to.getFile()] = new ChessPiece(PieceType.ROOK, move.getGameSide());

				if(!kingPosition.equals(to)){
					board[kingPosition.getRank()][kingPosition.getFile()] = null;
				}

				board[from.getRank()][from.getFile()] = new ChessPiece(PieceType.KING, move.getGameSide());
				break;
			}

			case EN_PASSANT -> {
				replacePiece(new ChessPosition(from.getRank(), to.getFile()), getAndRemoveLastTakenPiece());
				movePiece(to, from, new ChessPiece(move.getPieceMoved(), move.getGameSide()));
				break;
			}

			case RESIGNATION, DRAW_AGREEMENT -> {
				setGameStatus(GameStatus.ONGOING);
				break;
			}
		}
	}

	//for experimental purposes
	public ChessMove tryGetValidMove(ChessPosition from, ChessPosition to){
		LocatableChessPiece piece = getPieceAtLocation(from);
		if(piece != null){
			List<ChessMove> moves = getMoves(piece);
			for(ChessMove move : moves){
				if(move.getPieceTo().equals(to)){
					return move;
				}
			}
		}

		return null;
	}

	public boolean isValid(ChessMove move){
		if(gameStatus == GameStatus.ONGOING && getCurrentTurn() == move.getGameSide()){
			if(move.getMoveType().isPieceMove()){
				LocatableChessPiece piece = getPieceAtLocation(move.getPieceFrom());
				if(piece != null){
					return getMoves(piece).stream().anyMatch(m -> m.equals(move));
				}
			} else {
				return true;
			}
		}

		return false;
	}

	public ChessBoard getBoardAtMove(int moveNumber){
		int numberOfMoves = getNumberOfPerformedMoves();
		PerformedChessMove originalLast = getLastPerformedMove();

		if(numberOfMoves > moveNumber){
			PerformedChessMove last;
			ChessBoard newBoard = this.clone();
			for(int i = 0; i < numberOfMoves - moveNumber; i++){
				newBoard.undoLastMove();
			}

			return newBoard;
		} else {
			return this.clone();
		}
	}

	public void undoLastMove(){
		PerformedChessMove last = getLastPerformedMove();
		undoMove(last.getMove());
		if(!isDummy && last.getMove().getMoveType().isPieceMove()){
			addTime(last.getMove().getGameSide(), getTimeTakenForLastMove() - timeIncrement*1000 - 500);
		}
		performedMoves.remove(last);
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

	public ChessPiece getAndRemoveLastTakenPiece(){
		return takenPieces.remove(takenPieces.size()-1);
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
			for(ChessMove move : getMoves(piece, considerKings, false)){
				if(move.getMoveType().isCanTake() && move.getPieceTo().equals(position)){
					return true;
				}
			}
		}

		return false;
	}

	public boolean hasLegalMoves(GameSide gameSide){
		for(LocatableChessPiece piece : findPieces(gameSide)){
			if(!getMoves(piece, true).isEmpty()){
				return true;
			}
		}

		return false;
	}

	public boolean isKingUnderThreat(GameSide gameSide){
		for(LocatableChessPiece king : getKings(gameSide)){
			if(isUnderThreat(king, false)){
				return true;
			}
		}

		return false;
	}

	public boolean isInsufficientMaterial(GameSide side){
		List<ChessPiece> pieces = findRawPieces(side);
		//lone king, lone king + knight/bishop
		return pieces.size() == 1
				|| (pieces.size() == 2 && (pieces.contains(new ChessPiece(PieceType.KNIGHT, side))
				|| pieces.contains(new ChessPiece(PieceType.BISHOP, side))));
	}

	public boolean isDrawByInsufficientMaterial(){
		return isInsufficientMaterial(GameSide.WHITE) && isInsufficientMaterial(GameSide.BLACK);
	}

	public long getTimeTakenForLastMove(){
		if(performedMoves.size() == 1){
			return getLastPerformedMove().getTimePlayed().getTime() - startingTime.getTime();
//			return 0;
		} else {
			return getLastPerformedMove().getTimePlayed().getTime() - getPenultimatePerformedMove().getTimePlayed().getTime();
		}
	}

	public long getTimeOfLastMove(){
		if(performedMoves.size() == 0){
			return startingTime.getTime();
		} else {
			return getLastPerformedMove().getTimePlayed().getTime();
		}
	}

	public GameSide getCurrentTurn(){
		return performedMoves.size() % 2 == 0 ? GameSide.WHITE : GameSide.BLACK;
	}

	private void updateGameStatus(){
		if(!isDummy && gameStatus == GameStatus.ONGOING){
			updateTimingStatus();
			if(gameStatus != GameStatus.ONGOING){
				return;
			}

			GameSide turn = getCurrentTurn();
			if(!hasLegalMoves(turn)){
				if(isKingUnderThreat(turn)){
					setGameStatus(turn == GameSide.WHITE ? GameStatus.BLACK_WIN_CHECKMATE : GameStatus.WHITE_WIN_CHECKMATE);
				} else {
					setGameStatus(GameStatus.DRAW_BY_STALEMATE);
				}
			} else if(isDrawByInsufficientMaterial()){
				setGameStatus(GameStatus.DRAW_BY_INSUFFICIENT_MATERIAL);
			}
		}
	}

	public void updateTimingStatus(){
		GameSide turn = getCurrentTurn();

		if(!timeRemaining.isEmpty()){
			Long current = timeRemaining.get(turn);
			Long opponent = timeRemaining.get(turn.getOpposite());
			if(opponent <= 0){
				timeout(turn.getOpposite());
				return;
			} else if(current <= 0){
				timeout(turn);
				return;
			}

			if(getTimeOfLastMove() + current <= System.currentTimeMillis()){
				timeout(turn);
			}
		}
	}

	private void timeout(GameSide side){
		if(isInsufficientMaterial(side.getOpposite())){
			setGameStatus(GameStatus.DRAW_BY_INSUFFICIENT_VS_TIMING);
		} else {
			setGameStatus(side == GameSide.WHITE ? GameStatus.BLACK_WIN_TIMEOUT : GameStatus.WHITE_WIN_TIMEOUT);
		}
	}

	public void addTime(GameSide side, long time){
		if(!timeRemaining.isEmpty()){
			timeRemaining.replace(side, timeRemaining.get(side) + time);
		}
	}

	public void removeTime(GameSide side, long time){
		if(!timeRemaining.isEmpty()){
			timeRemaining.replace(side, timeRemaining.get(side) - time);
		}
	}

	public String getAsciiDisplay(){
		return getAsciiDisplay(false);
	}

	public String getAsciiDisplay(boolean useAnsi){
		StringBuilder display = new StringBuilder("\n");
		for(int i = 7; i >= 0; i--){
			for(int j = 0; j < 8; j++){
				ChessPiece piece = board[i][j];
				if(piece != null){
					if(useAnsi){
						display.append("\u001B[40m");
					}
					display.append(piece.getAsciiDisplay());
					if(useAnsi){
						display.append("\u001B[0m");
					}
				} else {
					display.append("\u001B[30m▇\u001B[0m");
				}
			}
			display.append("\n");
		}
		return display.toString();
	}

	private static ChessPiece[][] cloneBoard(ChessPiece[][] board){
		ChessPiece[][] newBoard = new ChessPiece[8][8];
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				ChessPiece piece = board[i][j];
				if(piece != null){
					newBoard[i][j] = piece.clone();
				}
			}
		}

		return newBoard;
	}

	private static List<PerformedChessMove> clonePerformedMoves(List<PerformedChessMove> moves){
		List<PerformedChessMove> newMoves = new ArrayList<>();
		for(PerformedChessMove move : moves){
			newMoves.add(move.clone());
		}
		return newMoves;
	}

	private static List<ChessPiece> clonePieces(List<ChessPiece> pieces){
		List<ChessPiece> newPieces = new ArrayList<>();
		for(ChessPiece piece : pieces){
			newPieces.add(piece.clone());
		}
		return newPieces;
	}

	@Override
	public ChessBoard clone(){
		try {
			ChessBoard clone = (ChessBoard) super.clone();
			clone.board = cloneBoard(board);
			clone.performedMoves = clonePerformedMoves(performedMoves);
			clone.takenPieces = clonePieces(takenPieces);
			return clone;
		} catch (CloneNotSupportedException e){
			throw new AssertionError();
		}
	}
}
