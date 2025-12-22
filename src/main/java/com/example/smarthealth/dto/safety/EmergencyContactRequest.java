package com.example.smarthealth.dto.safety;

import lombok.Data;

@Data
public class EmergencyContactRequest {
    private String name;         
    private String phoneNumber;  
    private String relationship;
}