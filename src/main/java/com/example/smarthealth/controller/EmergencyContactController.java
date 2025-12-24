package com.example.smarthealth.controller;

import com.example.smarthealth.dto.safety.EmergencyContactRequest;
import com.example.smarthealth.model.safety.EmergencyContact;
import com.example.smarthealth.service.SafetyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/safety/contacts")
@RequiredArgsConstructor
public class EmergencyContactController {

    private final SafetyService safetyService;

    @PostMapping
    public ResponseEntity<EmergencyContact> addContact(
            @RequestBody EmergencyContactRequest request) {
        return ResponseEntity.ok(safetyService.addContact(request));
    }

    @GetMapping
    public ResponseEntity<List<EmergencyContact>> getContacts() {
        return ResponseEntity.ok(safetyService.getContacts());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        safetyService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }
}