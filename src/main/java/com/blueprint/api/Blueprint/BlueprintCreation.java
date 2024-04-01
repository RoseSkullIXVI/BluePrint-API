package com.blueprint.api.Blueprint;

import java.util.Map;
import net.minidev.json.JSONObject;


public class BlueprintCreation {

    public String getBlueprint(String keywords, Map<String, String> record , String filename){
        JSONObject blueprint = new JSONObject();
        blueprint.put("keywords", keywords);
        blueprint.put("filename", filename );
        blueprint.put("user-agent", record.get("user-agent").toString());
        blueprint.put("host", record.get("host").toString());
        String contentLengthString = record.get("content-length").toString();
        String volume = contentVolume(contentLengthString);
        blueprint.put("volume", volume);

        return blueprint.toString();                
    } 

    private String contentVolume (String contentLength){
        int length = Integer.parseInt(contentLength);
        if(length < 1024){
            return length + "B";
        } else if(length < 1048576){
            return length/1024 + "KB";
        } else if (length < 1073741824){
            return length/1048576 + "MB";
        } else {
            return length/1073741824 + "GB";
        }
    }      
    
}
