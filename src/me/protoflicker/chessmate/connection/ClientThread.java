package me.protoflicker.chessmate.connection;

import lombok.Getter;
import me.protoflicker.chessmate.Server;
import me.protoflicker.chessmate.connection.handler.HeartbeatHandler;
import me.protoflicker.chessmate.console.Logger;
import me.protoflicker.chessmate.data.Database;
import me.protoflicker.chessmate.data.DatabaseContainer;
import me.protoflicker.chessmate.manager.GameManager;
import me.protoflicker.chessmate.manager.IntelManager;
import me.protoflicker.chessmate.manager.InvitationManager;
import me.protoflicker.chessmate.manager.LoginManager;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;
import me.protoflicker.chessmate.protocol.packet.connection.ConnectPacket;
import me.protoflicker.chessmate.protocol.packet.connection.DisconnectPacket;
import me.protoflicker.chessmate.protocol.packet.connection.PingPacket;
import me.protoflicker.chessmate.protocol.packet.connection.PongPacket;
import me.protoflicker.chessmate.protocol.packet.connection.response.ConnectionSuccessfulPacket;
import me.protoflicker.chessmate.protocol.packet.connection.response.IncompatibleProtocolPacket;
import me.protoflicker.chessmate.util.DataUtils;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.channels.ClosedChannelException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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



	//This usage of Class<?> could raise a lot of potential bugs with ClassLoaders...
	@Getter
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


		packetHandlers.put(ConnectPacket.class, (c, packet) -> {
			ConnectPacket p = (ConnectPacket) packet;
			if(p.isCompatible()){
				if(c.connectToDatabase()){
					Logger.getInstance().log("Successfully accepted connection from " + getClientName());

					c.initBaseHandlers();

					isProtocolCompliant = true;
					packetHandlers.remove(ConnectPacket.class);

					c.sendPacket(new ConnectionSuccessfulPacket());
					return;
				}
			} else {
				Logger.getInstance().log("Rejecting connection from " + getClientName() + " due to incompatible protocol");
				c.sendPacket(new IncompatibleProtocolPacket(ConnectPacket.PROTOCOL_VERSION));
			}

			c.tryClose();
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

		LoginManager.registerHandlers(this);
		GameManager.registerHandlers(this);
		InvitationManager.registerHandlers(this);
		IntelManager.registerHandlers(this);

		packetHandlers.put(DisconnectPacket.class, (c, p) -> {
			c.tryClose();
		});
	}

	private void cycle(){
		isOnCycle = true;
		while(!isInterrupted() && (System.currentTimeMillis() - lastReceived) <= 60000){
			try {
				Object object = DataUtils.deserializeObjectFromStream(inputStream);
				if(object instanceof Packet && object instanceof ClientPacket packet){
					lastReceived = System.currentTimeMillis();
					List<Map.Entry<Class<?>, PacketHandler>> handlers = getHandlersByPacket(packet);
					if(!handlers.isEmpty()){
						Logger.getInstance().log("Packet received: " + packet.getClass().getSimpleName(), Logger.LogLevel.DEBUG);
						for(Map.Entry<Class<?>, PacketHandler> entry : handlers){
							entry.getValue().handle(this, packet);
						}
					} else {
						Logger.getInstance().log("Received unhandled packet: " + packet.getClass().getSimpleName(),
								Logger.LogLevel.DEBUG);
					}
				} else {
					Logger.getInstance().log("Received suspicious non-ClientPacket object", Logger.LogLevel.NOTICE);
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
				break; //connection closed, socket closed, or interruption
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

	private List<Map.Entry<Class<?>, PacketHandler>> getHandlersByPacket(ClientPacket object){
		return packetHandlers.entrySet().stream()
				.filter(e -> e.getKey().isInstance(object) || object.getClass().equals(e.getKey()))
				.collect(Collectors.toList());
	}


	public synchronized void sendPacket(ServerPacket packet){
		if(outputStream != null){
			try {
//				System.out.println(new Gson().toJson(packet));
				DataUtils.serializeObjectToStream(packet, socket.getOutputStream());
			} catch (IOException ignored){
				//display warning?
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

		onDisconnect();

		Logger.getInstance().log("Disconnecting " + getClientName(), Logger.LogLevel.NOTICE);
		try {
			this.socket.close();
			this.database.disconnect();
		} catch (IOException ignored){

		}
	}

	private void onDisconnect(){
		GameManager.onDisconnect(this);
	}
}
