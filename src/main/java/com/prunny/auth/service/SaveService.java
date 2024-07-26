package com.prunny.auth.service;


import com.prunny.auth.model.Save;

import java.util.List;

public interface SaveService {
    Save saveProperty(Long propertyId);
    void unSaveProperty( Long propertyId);
    List<Save> getSaveByPropertyId(Long propertyId);
    List<Save> getSavedPropertiesByUser();
}