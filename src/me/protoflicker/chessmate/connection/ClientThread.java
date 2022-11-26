package me.protoflicker.chessmate.connection;

import lombok.Getter;
import me.protoflicker.chessmate.Server;
import me.protoflicker.chessmate.console.Logger;
import me.protoflicker.chessmate.data.Database;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.PingPacket;
import me.protoflicker.chessmate.util.DataUtils;
import me.protoflicker.chessmate.util.DatabaseThread;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import java.sql.SQLException;

public class ClientThread extends Thread implements DatabaseThread {

	@Getter
	private final Socket socket;

	@Getter
	private InputStream inputStream;

	@Getter
	private OutputStream outputStream;

	@Getter
	private final Database database;

	public ClientThread(Socket socket){
		super("C/" + socket.getInetAddress().getHostName() + ":" + socket.getPort());

		this.socket = socket;
		this.database = Server.getInstance().getDataManager().getNewDatabase();
	}

	public String getClientName(){
		return socket.getInetAddress().getHostName() + ":" + socket.getPort();
	}

	@Override
	public void run(){
		Logger.initExceptionHandler(this::close);

		Logger.getInstance().log("Accepting connection from " + getClientName(), Logger.LogLevel.NOTICE);
		try {
			database.connect();
		} catch (SQLException e){
			Logger.getInstance().log("Failed to connect to database for client:", Logger.LogLevel.FATAL);
			throw new RuntimeException(e);
		}

		try {
			socket.setSoTimeout(300000);
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
		} catch (Exception e){
			throw new RuntimeException(e);
		}

		Logger.getInstance().log("Successfully connected " + getClientName(), Logger.LogLevel.NOTICE);

		cycle();

		close();
	}

	private void cycle(){
		while(true){
			try {
				Object object = DataUtils.deserializeObjectFromStream(inputStream);
				if(object instanceof Packet packet){
					Logger.getInstance().log("Packet received: " + packet.getName()
							+ ", " + packet.getClass().getName());
					if(packet instanceof PingPacket pingPacket){
						Logger.getInstance().log("Count: " + pingPacket.getCount());
						sendPacket(new PingPacket(pingPacket.getCount()));
					}
				} else {
					Logger.getInstance().log("Received suspicious non-packet object", Logger.LogLevel.NOTICE);
				}
			} catch (ObjectStreamException e){
//					Logger.getInstance().log("Received suspicious non-object bytes", Logger.LogLevel.NOTICE);
				continue;
			} catch (EOFException | ClosedChannelException | SocketException | InterruptedIOException e){
				break;
			} catch (IOException e){
				Logger.getInstance().logStackTrace(e, Logger.LogLevel.WARNING);
				continue;
			} catch (Exception e){
				Logger.getInstance().logStackTrace(e, Logger.LogLevel.ERROR);
				continue;
			}
		}
	}

	public synchronized void sendPacket(Packet packet){
		if(outputStream != null){
			try {
				DataUtils.serializeObjectToStream(packet, outputStream);
			} catch (IOException ignored){

			}
		}
	}

	public synchronized void close(){
		Server.getInstance().removeClientThread(this);

		Logger.getInstance().log("Disconnecting " + getClientName(), Logger.LogLevel.NOTICE);
		try {
			this.socket.close();
			this.database.disconnect();
		} catch (IOException ignored){

		}
	}
}
