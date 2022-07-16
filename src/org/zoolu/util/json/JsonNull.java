package org.zoolu.util.json;


public class JsonNull implements JsonValue {

	public static final JsonNull NULL=new JsonNull();
	
	private JsonNull() {
	}

	@Override
	public String toString() {
		return "null";
	}
}
