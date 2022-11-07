package me.protoflicker.chessmate.connection;

import java.net.Socket;

public class ClientHandler implements Runnable {

	private final Socket socket;

	public ClientHandler(Socket socket){
		this.socket = socket;
	}

	@Override
	public void run(){

	}
}
