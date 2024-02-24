package com.blueprint.api.controlers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.blueprint.api.Service_Implementation.FileParsingService;



@Controller
@CrossOrigin("http://localhost:8080")
public class FileHandlerController {
 private final FileParsingService fileParsingService;

 
    public FileHandlerController(FileParsingService fileParsingService) {
        this.fileParsingService = fileParsingService;
    }
        @PostMapping("/upload")
        public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
            String fileType = file.getContentType(); // This gets the MIME type of the file
            try {
                String keywords = fileParsingService.parseFile(file, fileType);
                return ResponseEntity.ok(keywords);
            } catch (UnsupportedOperationException e) {
                return ResponseEntity.badRequest().body("Unsupported file type: " + fileType);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body("An error occurred while processing the file.");
            }
        }
    }