package me.protoflicker.chessmate.protocol;

import java.io.Serializable;

public abstract class Packet implements Serializable {
	public String getName(){
		return this.getClass().getSimpleName();
	}
}
