package me.protoflicker.chessmate.console;


import lombok.Getter;
import lombok.Setter;
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
		DEBUG(ANSIFormat.GRAY, System.out),
		INFO(ANSIFormat.WHITE, System.out),
		NOTICE(ANSIFormat.LIGHT_BLUE, System.out),
		WARNING(ANSIFormat.ORANGE, System.out),
		ERROR(ANSIFormat.RED, System.err),
		FATAL( ANSIFormat.RED + ANSIFormat.REVERSE, System.err);

		@Getter
		private final String color;

		@Getter
		private final PrintStream stream;

		LogLevel(String color, PrintStream stream){
			this.color = color;
			this.stream = stream;
		}
	}

	@Getter
	private static Logger instance = null;

	private final PrintStream logStream;

	@Getter
	@Setter
	private boolean isDebug = true;

	public static void init() throws Exception {
		if(Logger.instance == null){
			Logger.instance = new Logger();
		} else {
			throw new InstanceAlreadyExistsException("Logger is already instantiated.");
		}
	}

	public static void initExceptionHandler(){
		Thread.setDefaultUncaughtExceptionHandler(new ErrorHandler());
	}

	public static void initExceptionHandler(Runnable runnable){
		Thread.setDefaultUncaughtExceptionHandler(new ErrorHandler(runnable));
	}

	private Logger() throws FileSystemException, FileNotFoundException {
		initExceptionHandler();

		File logDirectory = new File(Main.WORKING_DIRECTORY + File.separator + "logs");
		if(!logDirectory.exists()){
			if(!logDirectory.mkdir()){
				throw new FileSystemException("Unable to create log directory " + logDirectory.getPath());
			}
		}

		LocalDateTime now = LocalDateTime.now();
		String logFile = logDirectory + File.separator
				+ now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) + ".log";

		this.logStream = new PrintStream(logFile);

		log("Logging to file " + logFile.toString());
	}

	public void log(String text){
		this.log(text, LogLevel.INFO);
	}

	public void log(String text, LogLevel level){
		if(isDebug || level != LogLevel.DEBUG){
			LocalDateTime now = LocalDateTime.now();
			String header = "[" + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] "
					+ ANSIFormat.AQUA + "[" + Thread.currentThread().getName() + ANSIFormat.WHITE + "/"
					+ level.getColor() + level + ANSIFormat.RESET + "] ";
			for(String line : text.split("\n")){
				level.getStream().println(header + line + ANSIFormat.RESET);
				this.logStream.println(ANSIFormat.strip(header + line));
			}
		}
	}

	public void logStackTrace(Throwable e, LogLevel level){
		Logger.getInstance().log(e.getClass().getName() + ": " + e.getMessage(), level);
		for(StackTraceElement element : e.getStackTrace()){
			Logger.getInstance().log("----> at " + element.getClassName() + "." + element.getMethodName()
					+ "(" + element.getFileName() + ":" + element.getLineNumber() + ")", level);
		}
	}
}
