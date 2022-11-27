package me.protoflicker.chessmate.protocol.packet;

import me.protoflicker.chessmate.protocol.Packet;

public class PingPacket extends Packet {

	private long time;
	private int count = 0;

	public PingPacket(){

	}

	public PingPacket(int count){
		this.count = count;
		this.time = System.currentTimeMillis();
	}

	public long getTime(){
		return time;
	}

	public int getCount(){
		return count;
	}
}
