import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

public class JsonParser {

	/**
	 * Flatten the json input and store them in a map
	 *
	 * @param json the given json input
	 * @return the flattened json input as key value pairs of a map
	 */
	static Map<Object, Object> flattenJson(String json) {
		
		if (json == null || json.isEmpty()) {
			return null;
		}

		Map<Object, Object> parsedJsonMap = new HashMap<>();

		try {
			// Parse input to a json node
			JsonNode jsonNode = new ObjectMapper().readTree(json);
			// Read the json and store them in a map as key-value pairs
			populateMap(null, jsonNode, parsedJsonMap);
	    } catch (IOException e) {
	    	// If the input is an invalid json
	    	System.out.println("Encountered IOException when processing Json " + e);
	    	e.printStackTrace();
	    	return null;
	    } catch (IllegalArgumentException e) {
	    	// If the arguments passed to populateMap are not valid
	    	System.out.println("The json object is not valid " + e);
	    	return null;
	    } 

		return parsedJsonMap;
	}

	/**
	 * Populate the map by reading the json nodes
	 *
	 * @param currentKey 	the current key. In nested json, the the path to the terminal value is represented by "." 
	 * @param jsonNode		the json node generated from the input string
	 * @param parsedJsonMap the map in which the key-value pairs should be stored
	 */
	static void populateMap(Object currentKey, JsonNode jsonNode, Map<Object, Object> parsedJsonMap) {
		
		if (jsonNode == null || parsedJsonMap == null) {
			throw new IllegalArgumentException("Json node cannot be null");
		}

		if(jsonNode.isObject()) {
			ObjectNode objectNode = (ObjectNode) jsonNode;
			Iterator<Map.Entry<String, JsonNode>> iter = objectNode.fields();
			// If nested json objects, the keys will be separated by "."
			Object keyPrefix = (currentKey == null || currentKey.toString().isEmpty()) ? "" : currentKey + ".";

			while (iter.hasNext()) {
				Map.Entry<String, JsonNode> entry = iter.next();
				populateMap(keyPrefix + entry.getKey(), entry.getValue(), parsedJsonMap);
		    }
		} else if (jsonNode.isValueNode()) {
			// Add key and value to the map
			ValueNode valueNode = (ValueNode) jsonNode;
			parsedJsonMap.put(currentKey, valueNode);
		} else {
			// If the json is not an object and contains arrays, then it violates one of the assumptions
			throw new IllegalArgumentException("Invalid input: " + jsonNode.toString());
		}
	}

	/**
	 * Generate a flattened json string from the given map
	 *
	 * @param jsonMap the map containing the keys as the path to every terminal value
	 * @return the generated flattened json string
	 */
	static String getFlattenedJson(Map<Object, Object> jsonMap) {

		if (jsonMap == null || jsonMap.isEmpty()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		for (Map.Entry<Object, Object> entry : jsonMap.entrySet()) {
			sb.append(String.format("\t\"%s\":%s,\n", entry.getKey(), entry.getValue()));
		}
		// Delete the ',' from the last line
		sb.deleteCharAt(sb.length()-2);
		sb.append("}");
		
		return isValidJson(sb.toString()) ? sb.toString() : null;
	}

	/**
	 * Check if the given string is a valid json
	 *
	 * @param str the input string
	 * @return boolean value that indicates if the string is a valid json 
	 */
	static boolean isValidJson(String str) {

		if (str == null || str.isEmpty()) {
			return false;
		}
		
		try {
	        new JSONObject(str);
	        return true;
	    } catch (JSONException e) {
	    	System.out.println("The flattened json string is not a valid json" + e + " " + str);
	    	return false;
	    }
	}

	public static void main(String[] args) {

		// Read the input
		Scanner scanner = new Scanner(System.in);
		StringBuilder sb = new StringBuilder();
		while (scanner.hasNext()) {
			sb.append(scanner.next());
		}
		scanner.close();

		// Flatten json input
		Map<Object, Object> flattenedJsonMap = flattenJson(sb.toString());
		String result = getFlattenedJson(flattenedJsonMap);
		if (result != null && !result.isEmpty()) {
			System.out.println(result);
		}
	}

}
