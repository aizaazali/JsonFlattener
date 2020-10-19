import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Assert;

public class JsonParserTest {

    /**
     * This test verifies that the json is correctly flattened for the given
     * input and stored in map as key-value pairs
     */
    @Test
    public void testFlattenJson() {

        String jsonString = "{\"a\":1,\"b\":true,\"c\":{\"d\":3,\"e\":\"test\"}}";
        Map<Object, Object> actualJsonMap = JsonParser.flattenJson(jsonString);
        Map<Object, Object> expectedJsonMap = new HashMap<>();
        expectedJsonMap.put("a", "1");
        expectedJsonMap.put("b", "true");
        expectedJsonMap.put("c.d", "3");
        expectedJsonMap.put("c.e", "\"test\"");
        Assert.assertEquals(expectedJsonMap.size(), actualJsonMap.size());
        for (Map.Entry<Object, Object> entry : expectedJsonMap.entrySet()) {
            Assert.assertTrue(actualJsonMap.containsKey(entry.getKey()));
            Assert.assertEquals(actualJsonMap.get(entry.getKey()).toString(), entry.getValue());
        }
    }

    /**
     * This test verifies that the json is correctly flattened for nested jsons
     */
    @Test
    public void testFlattenJsonWithNestedJsonInput() {

        String jsonString = "{\"a\":1,\"b\":true,\"c\":{\"d\":{\"e\":4,\"f\":false},\"g\":\"test\"},\"h\":\"help\"}";

        Map<Object, Object> actualJsonMap = JsonParser.flattenJson(jsonString);
        Map<Object, Object> expectedJsonMap = new HashMap<>();
        expectedJsonMap.put("a", "1");
        expectedJsonMap.put("b", "true");
        expectedJsonMap.put("c.d.e", "4");
        expectedJsonMap.put("c.d.f", "false");
        expectedJsonMap.put("c.g", "\"test\"");
        expectedJsonMap.put("h", "\"help\"");
        Assert.assertEquals(expectedJsonMap.size(), actualJsonMap.size());
        for (Map.Entry<Object, Object> entry : expectedJsonMap.entrySet()) {
            Assert.assertTrue(actualJsonMap.containsKey(entry.getKey()));
            Assert.assertEquals(actualJsonMap.get(entry.getKey()).toString(), entry.getValue());
        }
    }

    /**
     * This test verifies that the flattenJson returns null for empty inputs
     */
    @Test
    public void testFlattenJsonWithEmptyStringInput() {

        String jsonString = "";
        Map<Object, Object> actualJsonMap = JsonParser.flattenJson(jsonString);
        Assert.assertNull(actualJsonMap);
    }

    /**
     * This test verifies that the flattenJson returns null for null inputs
     */
    @Test
    public void testFlattenJsonWithNullInput() {

        Map<Object, Object> actualJsonMap = JsonParser.flattenJson(null);
        Assert.assertNull(actualJsonMap);
    }

    /**
     * This test verifies that the flattenJson handles invalid inputs
     */
    @Test
    public void testFlattenJsonWithInvalidInput() {

        String jsonString = "some random string";
        // Throws a JsonParseException which is one of the IOExceptions
        Map<Object, Object> actualJsonMap = JsonParser.flattenJson(jsonString);
        Assert.assertNull(actualJsonMap);
    }

    /**
     * This test verifies that the flattenJson handles unsupported inputs like
     * arrays
     */
    @Test
    public void testFlattenJsonWithArrayInput() {

        String jsonString = "{\"a\":1,\"b\":true,\"c\":{\"d\":[3,4,5],\"e\":\"test\"}}";
        Map<Object, Object> actualJsonMap = JsonParser.flattenJson(jsonString);
        Assert.assertNull(actualJsonMap);
    }

    /**
     * This test verifies that the key-value pairs are populated correctly in
     * the map
     */
    @Test
    public void testPopulateMap() {

        String jsonString = "{\"a\":1,\"b\":true,\"c\":{\"d\":3,\"e\":\"test\"}}";
        JsonNode jsonNode = null;
        try {
            jsonNode = new ObjectMapper().readTree(jsonString);
        } catch (IOException e) {
            System.out.println("Encountered IOException " + e);
            jsonNode = null;
        }

        Map<Object, Object> actualJsonMap = new HashMap<>();
        JsonParser.populateMap(null, jsonNode, actualJsonMap);

        Map<Object, Object> expectedJsonMap = new HashMap<>();
        expectedJsonMap.put("a", "1");
        expectedJsonMap.put("b", "true");
        expectedJsonMap.put("c.d", "3");
        expectedJsonMap.put("c.e", "\"test\"");
        Assert.assertEquals(expectedJsonMap.size(), actualJsonMap.size());
        for (Map.Entry<Object, Object> entry : expectedJsonMap.entrySet()) {
            Assert.assertTrue(actualJsonMap.containsKey(entry.getKey()));
            Assert.assertEquals(actualJsonMap.get(entry.getKey()).toString(), entry.getValue());
        }
    }

    /**
     * This test verifies that the key-value pairs are populated correctly with
     * a given key prefix
     */
    @Test
    public void testPopulateMapWithKeyPrefix() {
        String jsonString = "{\"a\":1,\"b\":true}";
        JsonNode jsonNode = null;
        try {
            jsonNode = new ObjectMapper().readTree(jsonString);
        } catch (IOException e) {
            System.out.println("Encountered IOException " + e);
            jsonNode = null;
        }

        Map<Object, Object> actualJsonMap = new HashMap<>();
        JsonParser.populateMap("key", jsonNode, actualJsonMap);

        Map<Object, Object> expectedJsonMap = new HashMap<>();
        expectedJsonMap.put("key.a", "1");
        expectedJsonMap.put("key.b", "true");
        Assert.assertEquals(expectedJsonMap.size(), actualJsonMap.size());
        for (Map.Entry<Object, Object> entry : expectedJsonMap.entrySet()) {
            Assert.assertTrue(actualJsonMap.containsKey(entry.getKey()));
            Assert.assertEquals(actualJsonMap.get(entry.getKey()).toString(), entry.getValue());
        }
    }

    /**
     * Test populateMap when json node is null
     */
    @Test(expected = IllegalArgumentException.class)
    public void testPopulateMapWithJsonNodeAsNull() {

        Map<Object, Object> actualJsonMap = new HashMap<>();
        JsonParser.populateMap("", null, actualJsonMap);
    }

    /**
     * Test populateMap when json map is null
     */
    @Test(expected = IllegalArgumentException.class)
    public void testPopulateMapWithJsonMapAsNull() {

        String jsonString = "{\"a\":1,\"b\":true,\"c\":{\"d\":3,\"e\":\"test\"}";
        JsonNode jsonNode = null;
        try {
            jsonNode = new ObjectMapper().readTree(jsonString);
        } catch (IOException e) {
            System.out.println("Encountered IOException " + e);
            jsonNode = null;
        }

        JsonParser.populateMap("", jsonNode, null);
    }

    /**
     * Test populateMap for unsupported inputs like arrays
     */
    @Test(expected = IllegalArgumentException.class)
    public void testPopulateMapWithArrayInput() {

        String jsonString = "{\"a\":1,\"b\":{\"c\":[3,4,5],\"d\":\"test\"}}";
        JsonNode jsonNode = null;
        try {
            jsonNode = new ObjectMapper().readTree(jsonString);
        } catch (IOException e) {
            System.out.println("Encountered IOException " + e);
            jsonNode = null;
        }

        Map<Object, Object> actualJsonMap = new HashMap<>();
        JsonParser.populateMap("key", jsonNode, actualJsonMap);
    }

    /**
     * This test verifies that the validation of json works correctly
     */
    @Test
    public void testIsValidJson() {
        String jsonString = "{\"a\":1,\"b\":true,\"c.d\":3,\"c.e\":\"test\"}";
        Assert.assertTrue(JsonParser.isValidJson(jsonString));
    }

    /**
     * This test verifies that the validation of json handles invalid inputs
     */
    @Test
    public void testIsValidJsonWithInvalidInput() {
        String jsonString = "some random string";
        Assert.assertFalse(JsonParser.isValidJson(jsonString));
    }

    /**
     * Test isValidJson when null input
     */
    @Test
    public void testIsValidJsonWithNullInput() {
        Assert.assertFalse(JsonParser.isValidJson(null));
    }

    /**
     * Test isValidJson when no input
     */
    @Test
    public void testIsValidJsonWithNoInput() {
        Assert.assertFalse(JsonParser.isValidJson(""));
    }

    /**
     * This test verifies that the json is parsed correctly from the key-value
     * pairs of the map
     */
    @Test
    public void testGetFlattenedJson() {

        Map<Object, Object> jsonMap = new HashMap<>();
        jsonMap.put("a", 1);
        jsonMap.put("b", true);
        jsonMap.put("c.d", 3);
        jsonMap.put("c.e", "\"test\"");

        String expectedFlattenedString = "{\n\t\"a\":1,\n\t\"b\":true,\n\t\"c.d\":3,\n\t\"c.e\":\"test\"\n}";

        String actualFlattenedString = JsonParser.getFlattenedJson(jsonMap);
        Assert.assertEquals(expectedFlattenedString, actualFlattenedString);
    }

    /**
     * Test getFlattenedJson when no input
     */
    @Test
    public void testGetFlattenedJsonWhenNoInput() {

        String actualFlattenedString = JsonParser.getFlattenedJson(Collections.emptyMap());
        Assert.assertNull(actualFlattenedString);
    }

    /**
     * Test getFlattenedJson when null input
     */
    @Test
    public void testGetFlattenedJsonWhenInputNull() {

        String actualFlattenedString = JsonParser.getFlattenedJson(null);
        Assert.assertNull(actualFlattenedString);
    }
}
