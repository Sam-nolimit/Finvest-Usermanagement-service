package com.prunny.auth.dto.request;

import com.prunny.auth.enums.Category;
import com.prunny.auth.enums.Kitchen;
import com.prunny.auth.enums.Type;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
public class PropertyRequest {
    private String title;
    private Category category;
    private Type type;
    private Kitchen kitchen;
    private BigDecimal price;
    private String address;
    private String locationDescription;
    @NotEmpty
    private MultipartFile file;


    // Getters and Setters
}

