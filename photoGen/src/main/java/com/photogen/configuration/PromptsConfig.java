package com.photogen.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PromptsConfig {

    @Bean(name = "cleanPrompt")
    public String cleanPrompt() {
        return """
                Use the uploaded product image as reference.
                Keep only the product and improve image quality unless the user explicitly asks otherwise.
                Preserve the product details as much as possible.""";
    }

    @Bean(name = "withBodyPrompt")
    public String withBodyPrompt() {
        return """
                Follow the user request as the highest priority.
                If there is a conflict, prioritize the user request.
                
                Use the uploaded image as reference.
                Preserve the exact product design, color, material, shape, and details unless the user explicitly asks otherwise.
                
                The image may include a model or human body if it looks natural.
                Keep the result realistic and suitable for a professional fashion photoshoot.
                Ensure the product remains the clear focus of the image.""";
    }

    @Bean(name = "withoutBodyPrompt")
    public String withoutBodyPrompt() {
        return """
                Follow the user request as the highest priority.
                If there is a conflict, prioritize the user request.
                
                Use the uploaded image as reference.
                Preserve the exact product design, color, material, shape, and details unless the user explicitly asks otherwise.
                
                Remove all human body parts including hands, arms, legs, face, or torso.
                Keep only the clothing or footwear product.
                
                The final image must contain only the product with no human body visible.
                Improve lighting, sharpness, and realism to look like a professional e-commerce product photo.""";
    }
}
