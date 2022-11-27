package me.protoflicker.chessmate.connection;

import lombok.Getter;
import me.protoflicker.chessmate.Server;
import me.protoflicker.chessmate.console.Logger;
import me.protoflicker.chessmate.data.Database;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.DisconnectPacket;
import me.protoflicker.chessmate.protocol.packet.PingPacket;
import me.protoflicker.chessmate.util.DataUtils;
import me.protoflicker.chessmate.util.DatabaseThread;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ClientThread extends Thread implements DatabaseThread {

	@Getter
	private final Socket socket;

	@Getter
	private InputStream inputStream;

	@Getter
	private OutputStream outputStream;

	@Getter
	private final Database database;

	@Getter
	private boolean isOnCycle;

	//This usage of Class<?> raises a lot of potential bugs with ClassLoaders...
	private final Map<Class<?>, PacketHandler> packetHandlers = new HashMap<>();

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
		Logger.initExceptionHandler(this::tryClose);

		Logger.getInstance().log("Accepting connection from " + getClientName(), Logger.LogLevel.NOTICE);
		try {
			database.connect();
		} catch (SQLException e){
			Logger.getInstance().log("Failed to connect to database for client:", Logger.LogLevel.FATAL);
			throw new RuntimeException(e);
		}

		try {
			socket.setSoTimeout(30000/*0*/);
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
		} catch (Exception e){
			throw new RuntimeException(e);
		}

		Logger.getInstance().log("Successfully connected " + getClientName(), Logger.LogLevel.NOTICE);

		packetHandlers.put(PingPacket.class, (c, p) -> {
			PingPacket packet = (PingPacket) p;
			Logger.getInstance().log("Ping Count: " + packet.getCount());
			sendPacket(new PingPacket(packet.getCount()));
			try {
				database.refresh();
			} catch(SQLException e){
				throw new RuntimeException(e);
			}
		});

		packetHandlers.put(DisconnectPacket.class, (c, p) -> {
			DisconnectPacket packet = (DisconnectPacket) p;
			Logger.getInstance().log("Received disconnect packet", Logger.LogLevel.NOTICE);
			c.tryClose();
		});


		cycle();
		close();
	}

	private void cycle(){
		isOnCycle = true;
		while(!isInterrupted()){
			try {
				Object object = DataUtils.deserializeObjectFromStream(inputStream);
				if(object instanceof Packet packet){
					Logger.getInstance().log("Packet received: " + packet.getName());
					PacketHandler handler = packetHandlers.get(packet.getClass());
					if(handler != null){
						handler.handle(this, packet);
					}
				} else {
					Logger.getInstance().log("Received suspicious non-packet object", Logger.LogLevel.NOTICE);
				}
			} catch (ObjectStreamException e){
//				Logger.getInstance().log("Received suspicious non-object bytes", Logger.LogLevel.NOTICE);
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
		isOnCycle = false;
	}

	public synchronized void sendPacket(Packet packet){
		if(outputStream != null){
			try {
				DataUtils.serializeObjectToStream(packet, socket.getOutputStream());
			} catch (IOException ignored){

			}
		}
	}

	public synchronized void tryClose(){
		if(isOnCycle){
			this.interrupt();
		} else {
			close();
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
