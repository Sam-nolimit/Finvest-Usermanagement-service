package com.prunny.auth.dto.response;

import com.prunny.auth.model.Property;
import com.prunny.auth.enums.Category;
import com.prunny.auth.enums.Kitchen;
import com.prunny.auth.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PropertyResponse {
    private Long id;
    private String title;
    private Category category;
    private Type type;
    private Kitchen kitchen;
    private BigDecimal price;
    private String address;
    private String locationDescription;
    private String photo;
    private boolean isAvailable;

    // Constructor to initialize PropertyResponse from Property
    public PropertyResponse(Property property) {
        this.id = property.getId();
        this.title = property.getTitle();
        this.category = property.getCategory();
        this.type = property.getType();
        this.kitchen = property.getKitchen();
        this.price = property.getPrice();
        this.address = property.getAddress();
        this.locationDescription = property.getLocationDescription();
        this.photo = property.getPhoto();
        this.isAvailable = property.isAvailable();
    }
}
