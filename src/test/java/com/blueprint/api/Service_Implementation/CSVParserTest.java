package com.blueprint.api.Service_Implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@ExtendWith(MockitoExtension.class)
public class CSVParserTest {

    @Mock
    private MultipartFile file;

    @Test
    public void testFileParserValidCSV() throws Exception {
        // Setup
        String csvContent = "header1,header2\nvalue1,value2";
        InputStream is = new ByteArrayInputStream(csvContent.getBytes());
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.csv", "text/csv", is);

        CSVParser parser = new CSVParser();

        // Execute
        String result = parser.FileParser(mockFile);

        // Verify
        assertEquals("[\"header1\",\"header2\"]", result);
    }

    @Test
    public void testFileParserWithError() throws Exception {
        // Setup a scenario to cause an exception or pass an invalid file
        when(file.getInputStream()).thenThrow(new RuntimeException("Test exception"));
        
        CSVParser parser = new CSVParser();

        // Execute
        String result = parser.FileParser(file);

        // Verify
        assertEquals("Error parsing file", result);
    }

    @Test
    public void testSupports() {
        // Setup
        CSVParser parser = new CSVParser();

        // Execute & Verify
        assertEquals(true, parser.supports("text/csv"));
        assertEquals(false, parser.supports("application/json"));
    }
}

