package me.protoflicker.chessmate;

import me.protoflicker.chessmate.console.ConsoleThread;
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
	public static final String VERSION = "1.0";

	public static final String CONFIG_FOLDER = WORKING_DIRECTORY + File.separator + "config";

	public static final Thread MAIN_THREAD = Thread.currentThread();
	public static ConsoleThread CONSOLE_THREAD;

	public static void main(String[] args) throws Exception {
		Logger.init();

		Logger.getInstance().log("Starting " + NAME + " v" + VERSION + "...");

		CONSOLE_THREAD = new ConsoleThread();
//		CONSOLE_THREAD.start();

		Logger.getInstance().log("Loading config file...");

		JSONConfig config = getConfig(args);

		Logger.getInstance().setDebug(Boolean.TRUE.equals(config.getByPointer("console.debug")));
		Server.init(config);
		Server.getInstance().start();
	}

	private static JSONConfig getConfig(String[] args) throws FileSystemException {
		String configPath;
		if(args.length > 0){
			configPath = args[0]; // TODO parse config path properly
		} else {
			configPath = CONFIG_FOLDER + File.separator + "config.json";
		}

		File configFile = new File(configPath);
		if(!configFile.exists()){
			Logger.getInstance().log("The provided config file path (" + configPath + ") does not exist. " +
					"Creating a new one at this path...", Logger.LogLevel.NOTICE);
			createConfigFile(configFile);
			Logger.getInstance().log("Successfully created a new config file at " + configFile.getPath());
		}

		JSONConfig config;
		try {
			config = new JSONConfig(configFile);
		} catch (Exception e){
			Logger.getInstance().log("Failed to load config file:", Logger.LogLevel.FATAL);
			throw new RuntimeException(e);
		}
		return config;
	}

	private static void createConfigFile(File configFile) throws FileSystemException {
		File configFolderFile = new File(CONFIG_FOLDER);
		if(!configFolderFile.exists()){
			if(!configFolderFile.mkdir()){
				throw new FileSystemException("Unable to create new config folder at " + configFolderFile.getPath());
			}
		}

		try {
			if(configFile.createNewFile()){
				URL defaultConfigUrl = Main.class.getClassLoader().getResource("config.json"); //TODO: Use resource stream so that this works in .jar
				assert defaultConfigUrl != null; //config file should always exist in the bundled resources root
				File defaultConfigFile = new File(defaultConfigUrl.toURI());
				Files.copy(defaultConfigFile.toPath(), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Exception e){
			Logger.getInstance().log("Unable to create new config file at " + configFile.getPath(), Logger.LogLevel.FATAL);
			throw new RuntimeException(e);
		}
	}
}
