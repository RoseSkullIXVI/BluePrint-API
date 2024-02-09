package com.blueprint.api.Service_Implementation;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageInterface {
    public void init();
    public void save(MultipartFile file);
}
