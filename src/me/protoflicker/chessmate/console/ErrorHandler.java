package me.protoflicker.chessmate.console;

class ErrorHandler implements Thread.UncaughtExceptionHandler {

	public void uncaughtException(Thread t, Throwable e) {
		Logger.getInstance().log(e.getClass().getName() + ": " + e.getMessage(), Logger.LogLevel.FATAL);
		for(StackTraceElement element : e.getStackTrace()){
			Logger.getInstance().log("    at " + element.getClassName() + "." + element.getMethodName()
					+ "(" + element.getFileName() + ":" + element.getLineNumber() + ")", Logger.LogLevel.FATAL);
		}
	}
}