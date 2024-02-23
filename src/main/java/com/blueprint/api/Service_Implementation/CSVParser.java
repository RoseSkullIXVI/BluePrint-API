package com.blueprint.api.Service_Implementation;

import java.io.InputStreamReader;
import java.io.Reader;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CSVParser implements FileParseInterface{

    @Override
    public void FileParser(MultipartFile file) {
      try(Reader reader = new InputStreamReader(file.getInputStream())){
      CSVReader csvReader = new CSVReaderBuilder(reader).build(); 
      String[] header = csvReader.readNext();
      System.out.println(header);

      }
      catch (Exception e) {
         e.printStackTrace();
      }
    }

    @Override
    public boolean supports(String type) {
       return type.equals("text/csv");
    }
    

    
}
