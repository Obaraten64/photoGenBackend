package com.photogen.service;

import org.springframework.web.multipart.MultipartFile;

public interface EnhancerService {
    String enhanceFromUpload(MultipartFile file, String prompt, Boolean withBody) throws Exception;
}
