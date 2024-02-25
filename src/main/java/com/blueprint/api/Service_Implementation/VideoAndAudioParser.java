package com.blueprint.api.Service_Implementation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component
public class VideoAndAudioParser implements FileParseInterface {
    private static final Set<String> Supported_Video_Audio_Types = new HashSet<>();
    static {
        Supported_Video_Audio_Types.add("audio/wav");
        Supported_Video_Audio_Types.add("video/mp4");
        Supported_Video_Audio_Types.add("video/quicktime");
        Supported_Video_Audio_Types.add("video/x-msvideo");
    }

    @Override
    public String FileParser(MultipartFile file) {
        String keywords = "";
        List<String> audio_video_tags = new ArrayList<>();
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file.getInputStream());
            for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                audio_video_tags.add(tag.toString());               
            }
            }
            Gson gson = new GsonBuilder().create();
            keywords = gson.toJson(audio_video_tags);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keywords;
    }

    @Override
    public boolean supports(String type) {
        return Supported_Video_Audio_Types.contains(type);
    }
    
}
