package me.protoflicker.chessmate.console;

class ErrorHandler implements Thread.UncaughtExceptionHandler {

	public void uncaughtException(Thread t, Throwable e) {
		Logger.getInstance().logStackTrace(e, Logger.LogLevel.FATAL);
	}
}