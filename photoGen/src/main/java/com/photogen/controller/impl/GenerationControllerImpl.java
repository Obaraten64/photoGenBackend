package com.photogen.controller.impl;

import com.photogen.controller.GenerationController;
import com.photogen.dto.Images;
import com.photogen.dto.StringResponse;
import com.photogen.dto.requests.GenerateRequest;
import com.photogen.service.GenerationService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GenerationControllerImpl implements GenerationController {
    private final GenerationService generationService;

    @PostMapping("/generate")
    public StringResponse generateImage(@RequestBody GenerateRequest promptRequest) {
        return new StringResponse(generationService.submitPrompt(promptRequest.prompt()));
    }

    @GetMapping("/status/{taskId}")
    public StringResponse getStatus(@PathVariable String taskId) {
        return new StringResponse(generationService.getStatus(taskId).name());
    }

    @GetMapping("/result/{taskId}")
    public Images getResult(@PathVariable String taskId) {
        return generationService.getResult(taskId);
    }
}
