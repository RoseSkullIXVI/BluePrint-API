package com.blueprint.api.controlers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.multipart.MultipartFile;

import com.blueprint.api.Blueprint.BlueprintCreation;
import com.blueprint.api.FolderWatch.DirectoryWatchService;
import com.blueprint.api.Service_Implementation.FileParsingService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;





@Controller
@CrossOrigin("http://localhost:8081")
public class FileHandlerController {
 private final FileParsingService fileParsingService;
 private final Bucket bucket;
 private final DirectoryWatchService directoryWatchService;
 
    public FileHandlerController(FileParsingService fileParsingService, DirectoryWatchService directoryWatchService) {
        this.fileParsingService = fileParsingService;
        Bandwidth limit = Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1)));
        this.bucket = Bucket.builder()
            .addLimit(limit)
            .build();
        this.directoryWatchService = directoryWatchService;
  

    }
        @PostMapping("/upload")
        public ResponseEntity<String> uploadFile(@RequestHeader Map<String,String> headers,@RequestParam("file") MultipartFile file,@AuthenticationPrincipal OAuth2User principal) {
            if (principal == null) {
                return ResponseEntity.status(403).body("Unauthorized access");
            }
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

        @PostMapping("/import")
    public ResponseEntity<String> importing(@AuthenticationPrincipal OAuth2User principal,@RequestParam("Velocity") String Velocity, 
                                           @RequestParam("TypeofSource") String TypeofSource,
                                           @RequestParam("TypeofData") String TypeofData,
                                           @RequestParam("Value") String Value,
                                           @RequestParam("Veracity") String Veracity,
                                           @RequestParam("directoryUrl") String directoryUrl){
                                            if (principal == null) {
                                                return ResponseEntity.status(403).body("Unauthorized access");
                                            }
        try {
            Path PathdirectoryUrl = Paths.get(directoryUrl);
            directoryWatchService.watchDirectoryPath(PathdirectoryUrl, TypeofSource, TypeofData, Value, Veracity, Velocity);
            return ResponseEntity.ok("Directory watching : " + " " + directoryUrl);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while initializing directory watching.");
        }
    }

}

