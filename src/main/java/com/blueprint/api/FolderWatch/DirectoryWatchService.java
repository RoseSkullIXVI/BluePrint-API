package com.blueprint.api.FolderWatch;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import java.nio.file.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@Service
public class DirectoryWatchService {
    private WatchService watchService;
    private WebClient webClient;
    private final ExecutorService executor = Executors.newCachedThreadPool(); // Updated to cached thread pool

    public DirectoryWatchService(WebClient.Builder webClientBuilder) throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    public void watchDirectoryPath(Path path, String TypeofSource, String TypeofData, String Value, String Veracity, String Velocity) {
        executor.submit(() -> {
            try {
                scanAndSendExistingFiles(path,TypeofSource,TypeofData,Value,Veracity,Velocity);

                path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY
                );

                System.out.println("Watching path: " + path);

                // Start the infinite loop to watch for directory changes
                while (true) {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();
                        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                            Path createdFilePath = path.resolve((Path) event.context());
                            System.out.println("File created: " + createdFilePath);
                            sendPostRequest(createdFilePath, TypeofSource, TypeofData, Value, Veracity, Velocity);
                        }
                    }
                    key.reset();
                }
            } catch (IOException | InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Stopped watching " + path);
            }
        });
    }

    private void scanAndSendExistingFiles(Path directory, String TypeofSource, String TypeofData, String Value, String Veracity, String Velocity) throws IOException {
        try (Stream<Path> paths = Files.walk(directory, 1)) {
            paths.filter(Files::isRegularFile)
                 .forEach(filePath -> sendPostRequest(filePath, TypeofSource, TypeofData, Value, Veracity, Velocity));
        }
    }

    private void sendPostRequest(Path filePath, String TypeofSource, String TypeofData, String Value, String Veracity , String Velocity) {
        try {
            FileSystemResource resource = new FileSystemResource(filePath.toFile());
            webClient.post()
                .uri("/stream")
                .header("Type-of-Source", TypeofSource)  
                .header("Type-of-Data", TypeofData)      
                .header("Value", Value)                  
                .header("Veracity", Veracity)
                .header("Velocity", Velocity)            
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", resource))
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(response -> System.out.println("Response from API: " + response));
        } catch (Exception e) {
            System.out.println("Error sending file " + filePath + ": " + e.getMessage());
        }
    }
}
