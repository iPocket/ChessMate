package me.protoflicker.chessmate;

import com.google.common.net.InetAddresses;
import lombok.Getter;
import me.protoflicker.chessmate.connection.ClientThread;
import me.protoflicker.chessmate.console.Logger;
import me.protoflicker.chessmate.data.DataManager;
import me.protoflicker.chessmate.data.Database;
import me.protoflicker.chessmate.data.DatabaseContainer;
import me.protoflicker.chessmate.util.JSONConfig;

import javax.management.InstanceAlreadyExistsException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Server {


	public static final int BACKLOG_SIZE = 50;

	private static final int DEFAULT_PORT = 13372;


	@Getter
	private static Server instance = null;





	private ServerSocket serverSocket;




	@Getter
	private final JSONConfig config;

	@Getter
	private final DataManager dataManager;

	@Getter
	private final Database database;



	private final Set<ClientThread> clientThreads = Collections.synchronizedSet(new HashSet<>());

	private boolean shuttingDown = false;


	public static void init(JSONConfig config) throws InstanceAlreadyExistsException {
		if(Server.instance == null){
			Server.instance = new Server(config);
		} else {
			throw new InstanceAlreadyExistsException("Server is already instantiated.");
		}
	}

	public Server(JSONConfig config){
		this.config = config;
		this.dataManager = new DataManager(
				config.getByPointer("database.ip"),
				config.getByPointer("database.port"),
				config.getByPointer("database.database"),
				config.getByPointer("database.username"),
				config.getByPointer("database.password"));

		this.database = dataManager.getNewDatabase();
	}

	public void addClientThread(ClientThread e){
		clientThreads.add(e);
	}

	public void removeClientThread(ClientThread e){
		clientThreads.remove(e);
	}

	public static Database getThreadDatabase(){
		Thread thread = Thread.currentThread();
		if(thread instanceof DatabaseContainer container){
			return container.getDatabase();
		} else {
			if(!thread.equals(Main.MAIN_THREAD)){
				Logger.getInstance().log("Passing main thread database connection to thread "
						+ thread.getName(), Logger.LogLevel.ERROR);
			}

			return Server.getInstance().getDatabase();
		}
	}

	public void start(){
		Logger.getInstance().log("Connecting to database on main thread...");
		initDatabase();

		Logger.getInstance().log("Starting server...");


		String ip = fetchIp();
		long port = fetchPort();
		try {
			this.serverSocket = new ServerSocket((int) port, BACKLOG_SIZE, InetAddresses.forString(ip));
		} catch (IOException e) {
			Logger.getInstance().log("Error while binding to server port: ", Logger.LogLevel.FATAL);
			throw new RuntimeException(e);
		}

		Runtime.getRuntime().addShutdownHook(new Thread(null, this::closeServer, "ShutdownHandler"));
		Logger.initExceptionHandler(this::closeServer);

		Logger.getInstance().log("Server is now running on " + serverSocket.getInetAddress().getHostName() + ":" +
				serverSocket.getLocalPort());

		cycle();
	}

	private void initDatabase(){
		try {
			database.connect();
			DataManager.createTables(database);
		} catch (SQLException e){
			Logger.getInstance().log("Failed to connect to database on main thread:", Logger.LogLevel.FATAL);
			throw new RuntimeException(e);
		}
	}

	private String fetchIp(){
		Object ipPointer = config.getByPointer("server.ip");
		String ip;
		if(ipPointer != null){
			ip = (String) ipPointer;
		} else {
			throw new RuntimeException("Error while fetching server ip from config.");
		}
		return ip;
	}

	private long fetchPort(){
		Object portPointer = config.getByPointer("server.port");
		long port;
		if(portPointer != null){
			port = (long) portPointer;
		} else {
			throw new RuntimeException("Error while fetching server port from config.");
		}
		return port;
	}

	private void cycle(){
		Socket socket = null;
		while(!shuttingDown){
			try {
				socket = serverSocket.accept();
			} catch (SocketException e){
				break; //socket closed
			} catch (IOException e) {
				Logger.getInstance().log("Error while accepting client: ", Logger.LogLevel.ERROR);
				Logger.getInstance().logStackTrace(e, Logger.LogLevel.ERROR);
				continue;
			}

			ClientThread clientThread = new ClientThread(socket);
			clientThread.start();
		}

		try {
			serverSocket.close();
			database.disconnect();
		} catch (Exception ignored){

		}
	}

	private synchronized void closeServer(){
		Logger.getInstance().log("Shutting down server, may take 30 seconds...");

		shuttingDown = true;
		try {
			for(ClientThread thread : clientThreads){
				thread.tryClose();
			}

			//noinspection LoopConditionNotUpdatedInsideLoop
			while(!clientThreads.isEmpty()){
				continue;
			}

			Main.MAIN_THREAD.interrupt();
		} catch (Exception ignored){

		}
	}
}
