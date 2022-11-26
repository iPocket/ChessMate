package me.protoflicker.chessmate.console;

class ErrorHandler implements Thread.UncaughtExceptionHandler {

	private Runnable runnable = null;

	public ErrorHandler(){

	}

	public ErrorHandler(Runnable runnable){
		this.runnable = runnable;
	}

	public void uncaughtException(Thread t, Throwable e){
		Logger.getInstance().logStackTrace(e, Logger.LogLevel.FATAL);
		if(runnable != null){
			runnable.run();
		}
	}
}