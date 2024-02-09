package com.blueprint.api.Service_Implementation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    public void save(MultipartFile file) {
        try {
            Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
  
    }
    
}
