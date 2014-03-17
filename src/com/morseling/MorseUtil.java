package com.morseling;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MorseUtil {
	
	public static String serialize(Map<String, String> map) {
		if (map == null) {
			return null;
		}

		JSONObject json = new JSONObject(map);
		return json.toString();
	}

	public static Map<String, String> deserialize(String flattenMap) {
		if (flattenMap == null) {
			return null;
		}

		try {
			Map<String ,String>	data = new HashMap<String, String>();
			JSONObject json = new JSONObject(flattenMap);
			Iterator<String> keys = json.keys();
			while (keys.hasNext()) {
				String key = keys.next();
				data.put(key, json.getString(key));
			}		
			return data;
		} catch (JSONException e) {}

		return null;
	}
}
