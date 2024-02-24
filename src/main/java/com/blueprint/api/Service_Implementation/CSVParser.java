package com.blueprint.api.Service_Implementation;

import java.io.InputStreamReader;
import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CSVParser implements FileParseInterface{

    @Override
    public String FileParser(MultipartFile file) {
      String headerJson = "";
      try(Reader reader = new InputStreamReader(file.getInputStream())){
      CSVReader csvReader = new CSVReaderBuilder(reader).build(); 
      String[] header = csvReader.readNext();
      Gson gson = new GsonBuilder().create();
      headerJson = gson.toJson(header);
      }
      catch (Exception e) {
         e.printStackTrace();
         headerJson = "Error parsing file";
      }
      return headerJson;
      
    }

    @Override
    public boolean supports(String type) {
       return type.equals("text/csv");
    }
    

    
}
