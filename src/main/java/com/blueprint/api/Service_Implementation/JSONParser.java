package com.blueprint.api.Service_Implementation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class JSONParser implements FileParseInterface{
    List<String> keys = new ArrayList<>();
    ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public String FileParser(MultipartFile file) {
        try(InputStream is = file.getInputStream()) {
            JsonNode jsonNode = mapper.readTree(is);
            Iterator<String> iterator = jsonNode.fieldNames();
            iterator.forEachRemaining(e -> keys.add(e)); 
        } catch (Exception e) {
            e.printStackTrace();
            keys.add("Error parsing file");
        }
       return keys.toString();
        
    }

    @Override
    public boolean supports(String type) {
        return type.equals("application/json");
    }

    
}
