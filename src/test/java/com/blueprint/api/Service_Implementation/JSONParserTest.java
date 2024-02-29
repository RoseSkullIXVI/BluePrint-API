package com.blueprint.api.Service_Implementation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JSONParserTest {

    private JSONParser jsonParser;

    @BeforeEach
    public void setUp() {
        jsonParser = new JSONParser();
    }

    @Test
    public void testFileParserWithValidJson() throws Exception {
        // Prepare a mock JSON file
        String jsonContent = "{\"key1\":\"value1\",\"key2\":\"value2\"}";
        MockMultipartFile mockFile = new MockMultipartFile("file", "filename.json", "application/json", jsonContent.getBytes());

        // Perform the test
        String result = jsonParser.FileParser(mockFile);

        // Verify the result
        assertTrue(result.contains("key1") && result.contains("key2"), "The keys should be correctly parsed from the JSON file.");
    }

    @Test
    public void testFileParserWithInvalidJson() throws Exception {
        // Prepare a mock invalid JSON file (e.g., corrupted or empty)
        MockMultipartFile mockFile = new MockMultipartFile("file", "filename.json", "application/json", "".getBytes());

        // Perform the test
        String result = jsonParser.FileParser(mockFile);

        // Verify the result
        assertEquals("[Error parsing file]", result, "The result should indicate an error in parsing.");
    }

    @Test
    public void testSupports() {
        // Test with supported type
        assertTrue(jsonParser.supports("application/json"), "The supports method should return true for 'application/json'.");
        
        // Test with unsupported type
        assertEquals(false, jsonParser.supports("text/plain"), "The supports method should return false for unsupported types.");
    }
}
