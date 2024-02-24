package com.blueprint.api.Service_Implementation;

import java.util.List;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileParsingService {
    private final List<FileParseInterface> fileParsers;

    public FileParsingService(List<FileParseInterface> fileParsers) {
        this.fileParsers = fileParsers;
    }

    public String parseFile(MultipartFile file, String type) {
        FileParseInterface fileparse = fileParsers.stream() 
                .filter(t -> t.supports(type))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported file type: " + type));
        String keywords = fileparse.FileParser(file);
        return keywords;
    }
}
