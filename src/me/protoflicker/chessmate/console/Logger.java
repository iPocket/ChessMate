package me.protoflicker.chessmate.console;


import lombok.Getter;
import me.protoflicker.chessmate.Main;
import me.protoflicker.chessmate.util.ANSIFormat;

import javax.management.InstanceAlreadyExistsException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.file.FileSystemException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Logger {

	public enum LogLevel {
		INFO(ANSIFormat.WHITE),
		NOTICE(ANSIFormat.LIGHT_BLUE),
		WARNING(ANSIFormat.ORANGE),
		ERROR(ANSIFormat.RED),
		FATAL( ANSIFormat.RED + ANSIFormat.REVERSE);

		private final String color;

		LogLevel(String color){
			this.color = color;
		}

		public String getColor(){
			return this.color;
		}
	}

	@Getter
	private static Logger instance = null;

	private final PrintStream logStream;

	public static void init() throws Exception {
		if(Logger.instance == null){
			Logger.instance = new Logger();
		} else {
			throw new InstanceAlreadyExistsException("Logger is already instantiated.");
		}
	}


	private Logger() throws FileSystemException, FileNotFoundException {
		Thread.setDefaultUncaughtExceptionHandler(new ErrorHandler());

		File logDirectory = new File(Main.WORKING_DIRECTORY + File.separator + "logs");
		if(!logDirectory.exists()){
			if(!logDirectory.mkdir()){
				throw new FileSystemException("Unable to create log directory " + logDirectory.getPath());
			}
		}

		LocalDateTime now = LocalDateTime.now();
		String logFile = logDirectory + File.separator + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));

		this.logStream = new PrintStream(logFile + /*"--" + i +*/ ".log");
	}

	public void log(String text){
		this.log(text, LogLevel.INFO);
	}

	public void log(String text, LogLevel level){
		LocalDateTime now = LocalDateTime.now();
		String output = "[" + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] "
				+ ANSIFormat.AQUA + "[" + Thread.currentThread().getName() + ANSIFormat.WHITE + "/" + level.getColor()
				+ level + ANSIFormat.RESET + "] " + text;
		System.out.println(output + ANSIFormat.RESET);
		this.logStream.println(ANSIFormat.strip(output));
	}
}
