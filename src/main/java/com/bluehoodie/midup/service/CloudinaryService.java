package com.bluehoodie.midup.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class CloudinaryService {

    private Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Async("taskExecutor")
    public CompletableFuture<Map> processImage(byte[] image, int width, int height) throws IOException {
        Map response;

       Transformation transformation = new Transformation().crop("fit").width(width).height(height);
       response = cloudinary.uploader().upload(image, ObjectUtils.asMap("transformation", transformation));

       return CompletableFuture.completedFuture(response);
    }

}
