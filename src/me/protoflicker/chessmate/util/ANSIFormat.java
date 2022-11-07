package me.protoflicker.chessmate.util;

public abstract class ANSIFormat {

	public static final String RESET = "\u001B[0m";

	public static final String BOLD = "\u001B[1m";
	public static final String ITALIC = "\u001B[3m";
	public static final String UNDERLINE = "\u001B[4m";
	public static final String REVERSE = "\u001B[7m";

	public static final String WHITE = "\u001B[97m";
	public static final String BLACK = "\u001B[30m";
	public static final String BLUE = "\u001B[34m";
	public static final String GREEN = "\u001B[32m";
	public static final String RED = "\u001B[91m";
	public static final String BROWN = "\u001B[31m";
	public static final String PURPLE = "\u001B[35m";
	public static final String ORANGE = "\u001B[33m";
	public static final String YELLOW = "\u001B[93m";
	public static final String LIGHT_GREEN = "\u001B[92m";
	public static final String DARK_AQUA = "\u001B[36m";
	public static final String AQUA = "\u001B[96m";
	public static final String LIGHT_BLUE = "\u001B[94m";
	public static final String PINK = "\u001B[95m";
	public static final String GRAY = "\u001B[90m";
	public static final String LIGHT_GRAY = "\u001B[37m";

	public static String strip(String text){
		return text
				.replace(RESET, "")

				.replace(BOLD, "")
				.replace(ITALIC, "")
				.replace(UNDERLINE, "")
				.replace(REVERSE, "")

				.replace(WHITE, "")
				.replace(BLACK, "")
				.replace(BLUE, "")
				.replace(GREEN, "")
				.replace(RED, "")
				.replace(BROWN, "")
				.replace(PURPLE, "")
				.replace(ORANGE, "")
				.replace(YELLOW, "")
				.replace(LIGHT_GREEN, "")
				.replace(DARK_AQUA, "")
				.replace(AQUA, "")
				.replace(LIGHT_BLUE, "")
				.replace(PINK, "")
				.replace(GRAY, "")
				.replace(LIGHT_GRAY, "");
	}
}