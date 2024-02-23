package com.blueprint.api.Service_Implementation;

import org.springframework.web.multipart.MultipartFile;

public interface FileParseInterface {
    void FileParser(MultipartFile file);
    boolean supports(String type);
}

