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
    private final ExecutorService executor = Executors.newSingleThreadExecutor();


    public DirectoryWatchService(WebClient.Builder webClientBuilder) throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    public void watchDirectoryPath(Path path) {
        executor.submit(() -> {
         try {
            // First, handle existing files
            scanAndSendExistingFiles(path);

            // Register the directory with the WatchService
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
                        sendPostRequest(createdFilePath);
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

    private void scanAndSendExistingFiles(Path directory) throws IOException {
        try (Stream<Path> paths = Files.walk(directory, 1)) {
            paths.filter(Files::isRegularFile).forEach(this::sendPostRequest);
        }
    }

    private void sendPostRequest(Path filePath) {
        try {
            FileSystemResource resource = new FileSystemResource(filePath.toFile());
            webClient.post()
                .uri("/stream")
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
