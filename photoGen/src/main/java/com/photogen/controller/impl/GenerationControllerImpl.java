package com.photogen.controller.impl;

import com.photogen.controller.GenerationController;
import com.photogen.dto.Images;
import com.photogen.dto.StringResponse;
import com.photogen.dto.requests.GenerateRequest;
import com.photogen.service.EnhancerService;
import com.photogen.service.GenerationService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class GenerationControllerImpl implements GenerationController {
    private final GenerationService generationService;
    private final EnhancerService enchancerService;

    @PostMapping("/generate")
    public StringResponse generateImage(@RequestBody GenerateRequest promptRequest) {
        return new StringResponse(generationService.submitPrompt(promptRequest));
    }

    @GetMapping("/status/{taskId}")
    public StringResponse getStatus(@PathVariable String taskId) {
        return new StringResponse(generationService.getStatus(taskId).name());
    }

    @GetMapping("/result/{taskId}")
    public Images getResult(@PathVariable String taskId) {
        return generationService.getResult(taskId);
    }

    @PostMapping(value = "/enchance", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StringResponse enhance(
            @RequestPart("file") MultipartFile file,
            @RequestPart("prompt") String prompt,
            @RequestPart("withBody") Boolean withBody
    ) throws Exception {
        return new StringResponse(enchancerService.enhanceFromUpload(file, prompt, withBody));
    }
}
