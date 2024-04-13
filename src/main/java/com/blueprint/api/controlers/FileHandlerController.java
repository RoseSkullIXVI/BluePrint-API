package com.blueprint.api.controlers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.blueprint.api.Blueprint.BlueprintCreation;
import com.blueprint.api.Service_Implementation.FileParsingService;

import java.time.Duration;
import java.util.Map;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;



@RestController
@CrossOrigin("http://localhost:8081")
public class FileHandlerController {
 private final FileParsingService fileParsingService;
 private final Bucket bucket;
 
 
    public FileHandlerController(FileParsingService fileParsingService) {
        this.fileParsingService = fileParsingService;
        Bandwidth limit = Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1)));
        this.bucket = Bucket.builder()
            .addLimit(limit)
            .build();

    }
        @PostMapping("/upload")
        public ResponseEntity<String> uploadFile(@RequestHeader Map<String,String> headers,@RequestParam("file") MultipartFile file) {
            String fileType = file.getContentType(); // This gets the MIME type of the file
            String filename = file.getOriginalFilename();
            BlueprintCreation BlueprintCreation = new BlueprintCreation();
            if (bucket.tryConsume(1)) {
                try {
                    String keywords = fileParsingService.parseFile(file, fileType);
                    String blueprint = BlueprintCreation.getBlueprint(keywords, headers, filename);
                    return ResponseEntity.ok(blueprint);
                } catch (UnsupportedOperationException e) {
                    return ResponseEntity.badRequest().body("Unsupported file type: " + fileType);
                } catch (Exception e) {
                    return ResponseEntity.internalServerError().body("An error occurred while processing the file.");
                }
            }
            return ResponseEntity.status(429).body("Too many requests");
           
        }
    }

