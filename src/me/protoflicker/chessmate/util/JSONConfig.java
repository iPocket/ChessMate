package me.protoflicker.chessmate.util;

import lombok.Getter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public final class JSONConfig {

	@Getter
	private final File file;

	@Getter
	private final JSONObject config;

	public JSONConfig(File file) throws IOException, ParseException {
		this.file = file;
		this.config = (JSONObject) new JSONParser().parse(new FileReader(file));
	}

	public JSONObject getObject(String field){
		return (JSONObject) this.config.get(field);
	}


	public JSONObject getObject(JSONObject object, String field){
		return (JSONObject) object.get(field);
	}

	public String getString(JSONObject object, String field){
		return (String) object.get(field);
	}

	public boolean getBoolean(JSONObject object, String field){
		return (boolean) object.get(field);
	}

	public long getLong(JSONObject object, String field){
		return (long) object.get(field);
	}

	public int getInt(JSONObject object, String field){
		return ((Long) this.getLong(object, field)).intValue();
	}

	public float getFloat(JSONObject object, String field){
		return ((Long) this.getLong(object, field)).floatValue();
	}

	public double getDouble(JSONObject object, String field){
		return ((Long) this.getLong(object, field)).doubleValue();
	}

	public short getShort(JSONObject object, String field){
		return ((Long) this.getLong(object, field)).shortValue();
	}


	public String getString(String field){
		return this.getString(this.config, field);
	}

	public boolean getBoolean(String field){
		return this.getBoolean(this.config, field);
	}

	public long getLong(String field){
		return this.getLong(this.config, field);
	}

	public int getInt(String field){
		return this.getInt(this.config, field);
	}

	public float getFloat(String field){
		return this.getFloat(this.config, field);
	}

	public double getDouble(String field){
		return this.getDouble(this.config, field);
	}

	public short getShort(String field){
		return this.getShort(this.config, field);
	}
}