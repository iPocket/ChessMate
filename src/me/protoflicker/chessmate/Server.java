package me.protoflicker.chessmate;

import lombok.Getter;
import me.protoflicker.chessmate.connection.ClientHandler;
import me.protoflicker.chessmate.console.Logger;
import me.protoflicker.chessmate.data.DataManager;
import me.protoflicker.chessmate.data.SQLDatabase;
import me.protoflicker.chessmate.util.JSONConfig;

import javax.management.InstanceAlreadyExistsException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {


	@Getter
	private static Server instance = null;
	//

	private final static int PORT = 13372;

	private ServerSocket serverSocket;



	//
	@Getter
	private final JSONConfig config;

	@Getter
	private final DataManager dataManager;


	public static void init(JSONConfig config) throws InstanceAlreadyExistsException {
		if(Server.instance == null){
			Server.instance = new Server(config);
		} else {
			throw new InstanceAlreadyExistsException("Server is already instantiated.");
		}
	}

	public Server(JSONConfig config){
		this.config = config;

		SQLDatabase database = new SQLDatabase(
				config.getByPointer("database.ip"),
				config.getByPointer("database.port"),
				config.getByPointer("database.database"),
				config.getByPointer("database.username"),
				config.getByPointer("database.password"));
		this.dataManager = new DataManager(database);
		if(dataManager.connect()){
			Logger.getInstance().log("Successfully connected to database.");
			dataManager.createTables();
		}
	}

	public void cycle(){
		try {
			this.serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Socket socket = null;
		while(true){
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				Logger.getInstance().log("Error while accepting client: ", Logger.LogLevel.WARNING);
				Logger.getInstance().logStackTrace(e, Logger.LogLevel.WARNING);
				continue;
			}

			// new thread for a client
			new Thread(new ClientHandler(socket)).start();
		}
	}
}
