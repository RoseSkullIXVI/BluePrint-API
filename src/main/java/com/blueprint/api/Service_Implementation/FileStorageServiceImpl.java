package com.blueprint.api.Service_Implementation;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.python.util.PythonInterpreter;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class FileStorageServiceImpl implements FileStorageInterface{
     private final Path root = Paths.get("Uploads_Demo");

    @Override
    public void init() {
        try {
            Files.createDirectories(root);
        }catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }        
    }

    @Override
    public String save(MultipartFile file) {
        try (PythonInterpreter pyInterp = new PythonInterpreter()) {
                Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
                
                StringWriter writer = new StringWriter();
                pyInterp.setOut(writer);

                String scriptPath = Paths.get("C:", "Users", "athos", "Desktop", "BluePrint - Api", "api", "Python_Code", "CSV_Data_Extraction.py").toString();
                File scriptFile = new File(scriptPath);
            if (!scriptFile.exists()) {
                throw new RuntimeException("File not found: " + scriptFile.getAbsolutePath());
            }
            //pyInterp.set(filename, scriptFile); // This is the line that is setting the variable to the python environment
            pyInterp.execfile(scriptPath);
            String results = writer.toString();
            return results;
        } catch (Exception e) {
            throw new RuntimeException("Inside Error: " + e.getMessage());
        }
}
    
}
