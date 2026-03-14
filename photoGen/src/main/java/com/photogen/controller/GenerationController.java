package com.photogen.controller;

import com.photogen.dto.Images;
import com.photogen.dto.StringResponse;
import com.photogen.dto.requests.EnhanceImageRequest;
import com.photogen.dto.requests.GenerateRequest;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public interface GenerationController {
    StringResponse generateImage(GenerateRequest prompt);
    StringResponse getStatus(String taskId);
    Images getResult(String taskId);
    StringResponse enhance(MultipartFile file, String prompt) throws Exception;
}
