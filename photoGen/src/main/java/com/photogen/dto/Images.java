package com.photogen.dto;

import java.util.List;

public record Images(List<GeneratedImage> images, String description) {
}
