package com.actionhouse.actionhouse.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {

        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    public String upload(MultipartFile file) throws IOException {
        Map resultado = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "actionhouse",
                        "resource_type", "image"
                )
        );
        return (String) resultado.get("secure_url");
    }

    public void delete(String imageUrl) {
        try {
            // Extraer public_id de la URL
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extractPublicId(String url) {
        // URL: https://res.cloudinary.com/cloud/image/upload/v123/actionhouse/filename.jpg
        String[] parts = url.split("/");
        String filename = parts[parts.length - 1];
        String nameWithoutExt = filename.substring(0, filename.lastIndexOf('.'));
        return "actionhouse/" + nameWithoutExt;
    }
}