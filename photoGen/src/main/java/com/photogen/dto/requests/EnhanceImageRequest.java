package com.photogen.dto.requests;

import java.util.List;

public record EnhanceImageRequest(List<String> imageUrls, String prompt) {
}
