package me.protoflicker.chessmate.protocol.chess;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

public final class ChessPosition implements Serializable {

	@Getter
	private final int rank;
	@Getter
	private final int file;

	public ChessPosition(int rank, int file){
		this.rank = rank;
		this.file = file;
	}

	public String getChessNotation(){
		return getChessNotation(rank, file);
	}

	@Override
	public boolean equals(Object obj){
		if(obj == this){
			return true;
		}
		if(obj == null || obj.getClass() != this.getClass()){
			return false;
		}
		var that = (ChessPosition) obj;
		return this.rank == that.rank &&
				this.file == that.file;
	}

	public int getRankDifference(ChessPosition pos){
		return Math.abs(pos.getRank() - rank);
	}

	public int getFileDifference(ChessPosition pos){
		return Math.abs(pos.getFile() - file);
	}

	public static String getChessNotation(int rank, int file){
		return "" + ((char) (97 + file)) + (rank + 1);
	}

	public static ChessPosition fromChessNotation(String notation){
		return new ChessPosition(Integer.parseInt("" + notation.charAt(1)) - 1, ((int) (notation.charAt(0))) - 97);
	}

	@Override
	public int hashCode(){
		return Objects.hash(rank, file);
	}

	@Override
	public String toString(){
		return this.getChessNotation();
	}
}
