package com.almirpgu.shortener.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateLinkRequest {

    @NotBlank(message = "originalUrl must not be blank")
    private String originalUrl;
}
