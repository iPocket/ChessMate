package me.protoflicker.chessmate.connection;

import lombok.Getter;
import me.protoflicker.chessmate.Server;
import me.protoflicker.chessmate.connection.handler.HeartbeatHandler;
import me.protoflicker.chessmate.console.Logger;
import me.protoflicker.chessmate.data.Database;
import me.protoflicker.chessmate.data.DatabaseContainer;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.connection.ConnectPacket;
import me.protoflicker.chessmate.protocol.packet.connection.DisconnectPacket;
import me.protoflicker.chessmate.protocol.packet.connection.PingPacket;
import me.protoflicker.chessmate.protocol.packet.connection.PongPacket;
import me.protoflicker.chessmate.util.DataUtils;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.channels.ClosedChannelException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClientThread extends Thread implements DatabaseContainer {

	@Getter
	private final Database database;


	@Getter
	private final Socket socket;

	@Getter
	private InputStream inputStream;

	@Getter
	private OutputStream outputStream;

	@Getter
	private boolean isOnCycle;

	@Getter
	private boolean isProtocolCompliant = false;



	//last time a packet was received
	@Getter
	private long lastReceived = System.currentTimeMillis();

	@Getter
	private PingPacket lastPing = new PingPacket(System.currentTimeMillis());



	//This usage of Class<?> raises a lot of potential bugs with ClassLoaders...
	private final Map<Class<?>, PacketHandler> packetHandlers = Collections.synchronizedMap(new HashMap<>());


	public static String getClientName(Socket socket){
		return socket.getInetAddress().getHostName() + ":" + socket.getPort();
	}


	public ClientThread(Socket socket){
		super("C/" + getClientName(socket));

		this.socket = socket;
		this.database = Server.getInstance().getDataManager().getNewDatabase();
	}

	public String getClientName(){
		return getClientName(this.socket);
	}

	@Override
	public void run(){
		Logger.initExceptionHandler(this::tryClose);

		Server.getInstance().addClientThread(this);

		Logger.getInstance().log("Accepting connection from " + getClientName(),
				Logger.LogLevel.NOTICE);

		try {
			socket.setSoTimeout(30000); //30sec
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
		} catch (Exception e){
			throw new RuntimeException(e);
		}


		packetHandlers.put(ConnectPacket.class, (c, p) -> {
			if(connectToDatabase()){
				Logger.getInstance().log("Successfully accepted connection from " + getClientName());

				initBaseHandlers();

				isProtocolCompliant = true;
				packetHandlers.remove(ConnectPacket.class);
			} else {
				tryClose();
			}
		});


		cycle();
		close();
	}

	private boolean connectToDatabase(){
		try {
			database.connect();
		} catch (SQLException e){
			Logger.getInstance().log("Failed to connect to database for client:", Logger.LogLevel.ERROR);
			Logger.getInstance().logStackTrace(e, Logger.LogLevel.ERROR);
			return false;
		}

		return true;
	}

	private void initBaseHandlers(){
		packetHandlers.put(PingPacket.class, HeartbeatHandler::handlePing);
		packetHandlers.put(PongPacket.class, HeartbeatHandler::handlePong);

		packetHandlers.put(DisconnectPacket.class, HeartbeatHandler::handleDisconnect);
	}

	private void cycle(){
		isOnCycle = true;
		while(!isInterrupted() && (System.currentTimeMillis() - lastReceived) <= 60000){
			try {
				Object object = DataUtils.deserializeObjectFromStream(inputStream);
				if(object instanceof Packet packet){
					lastReceived = System.currentTimeMillis();
					PacketHandler handler = packetHandlers.get(packet.getClass());
					if(handler != null){
						Logger.getInstance().log("Packet received: " + packet.getName(), Logger.LogLevel.DEBUG);
						handler.handle(this, packet);
					} else {
						Logger.getInstance().log("Received unhandled packet " + packet.getName(),
								Logger.LogLevel.DEBUG);
					}
				} else {
					Logger.getInstance().log("Received suspicious non-packet object", Logger.LogLevel.NOTICE);
				}
			} catch (ObjectStreamException e){
//				Logger.getInstance().log("Received suspicious non-object bytes", Logger.LogLevel.NOTICE);
				continue;
			} catch (SocketTimeoutException e){
				if(isProtocolCompliant){
					lastPing = new PingPacket(System.currentTimeMillis());
					sendPacket(lastPing);
				}
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

	public void tryClose(){
		if(isOnCycle){
			interrupt();
		} else {
			close();
		}
	}

	public void close(){
		Server.getInstance().removeClientThread(this);

		Logger.getInstance().log("Disconnecting " + getClientName(), Logger.LogLevel.NOTICE);
		try {
			this.socket.close();
			this.database.disconnect();
		} catch (IOException ignored){

		}
	}
}
