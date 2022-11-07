package me.protoflicker.chessmate;

import me.protoflicker.chessmate.console.Logger;
import me.protoflicker.chessmate.util.JSONConfig;

import java.io.File;
import java.net.URL;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Main {

	public static final String WORKING_DIRECTORY = System.getProperty("user.dir");

	public static final String NAME = "ChessMate";
	public static final String VERSION = "0.01";

	public static void main(String[] args) throws Exception {
		Logger.init();

		Logger.getInstance().log("Starting " + NAME + " v" + VERSION + "...");

		String configPath;
		if(args.length > 0){
			configPath = args[0]; // TODO parse config path properly
		} else {
			configPath = WORKING_DIRECTORY + File.separator + "config" + File.separator + "config.json";
		}

		File configFile = new File(configPath);
		if(!configFile.exists()){
			Logger.getInstance().log("The provided config file path (" + configPath + ") does not exist. " +
					"Creating a new one at this path...", Logger.LogLevel.NOTICE);


			if(configFile.createNewFile()){
				URL defaultConfigUrl = Main.class.getClassLoader().getResource("config.json");
				assert defaultConfigUrl != null; //config file should always exist in the bundled resources root
				File defaultConfigFile = new File(defaultConfigUrl.toURI());
				Files.copy(defaultConfigFile.toPath(), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} else {
				throw new FileSystemException("Unable to create new config file at " + configFile.getPath());
			}

			Logger.getInstance().log("Successfully created a new config file at " + configFile.getPath());
		}

		Logger.getInstance().log("Loading config file...");
		JSONConfig config = new JSONConfig(configFile);
		Logger.getInstance().log("Successfully loaded config file.");
		String ip = config.getByPointer("database.ip");
		Logger.getInstance().log(ip, Logger.LogLevel.NOTICE);

		Server.init(config);
		Server.getInstance().cycle();
	}
}
