package com.example.smarthealth.service;

import com.example.smarthealth.dto.safety.EmergencyContactRequest;
import com.example.smarthealth.dto.safety.FallEventRequest;
import com.example.smarthealth.dto.safety.SosRequest;
import com.example.smarthealth.model.auth.User;
import com.example.smarthealth.model.safety.EmergencyContact;
import com.example.smarthealth.model.safety.FallEvent;
import com.example.smarthealth.model.safety.SosEvent;
import com.example.smarthealth.repository.EmergencyContactRepository;
import com.example.smarthealth.repository.FallEventRepository;
import com.example.smarthealth.repository.SosEventRepository;
import com.example.smarthealth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SafetyService {

    private final EmergencyContactRepository contactRepository;
    private final FallEventRepository fallEventRepository;
    private final SosEventRepository sosEventRepository;
    private final UserRepository userRepository;

    @Transactional
    public EmergencyContact addContact(Long userId, EmergencyContactRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        EmergencyContact contact = EmergencyContact.builder()
                .user(user)
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .relationship(request.getRelationship())
                .build();

        return contactRepository.save(contact);
    }

    public List<EmergencyContact> getContacts(Long userId) {
        return contactRepository.findByUserId(userId);
    }

    @Transactional
    public void deleteContact(Long contactId) {
        contactRepository.deleteById(contactId);
    }

    @Transactional
    public FallEvent logFallEvent(Long userId, FallEventRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FallEvent event = FallEvent.builder()
                .user(user)
                .detectedAt(request.getDetectedAt())
                .locationLat(request.getLatitude())
                .locationLng(request.getLongitude())
                .extraData(request.getSensorData())
                .confirmed(request.getConfirmed() != null ? request.getConfirmed() : false)
                .build();

        FallEvent savedEvent = fallEventRepository.save(event);

        triggerEmergencyAlert(user, savedEvent);

        return savedEvent;
    }

    private void triggerEmergencyAlert(User user, FallEvent event) {
        List<EmergencyContact> contacts = contactRepository.findByUserId(user.getId());

        if (contacts.isEmpty()) {
            System.out.println(
                    "⚠️ CẢNH BÁO: User " + user.getFullName() + " bị ngã nhưng KHÔNG CÓ người thân nào để báo!");
            return;
        }

        System.out.println("🚨🚨🚨 PHÁT HIỆN TÉ NGÃ - ĐANG GỬI CẢNH BÁO 🚨🚨🚨");
        System.out.println("👤 Nạn nhân: " + user.getFullName());
        System.out.println("📍 Vị trí: " + event.getLocationLat() + ", " + event.getLocationLng());
        System.out.println("----- DANH SÁCH GỬI TIN -----");

        for (EmergencyContact contact : contacts) {
            System.out.println("📲 Đang gửi SMS tới: " + contact.getName() + " (" + contact.getPhoneNumber() + ")");
            System.out.println("   Nội dung: 'KHẨN CẤP! " + user.getFullName() + " vừa bị ngã tại tọa độ "
                    + event.getLocationLat() + "," + event.getLocationLng() + ". Hãy kiểm tra ngay!'");
        }
        System.out.println("-----------------------------");
    }

    @Transactional
    public SosEvent triggerSos(Long userId, SosRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SosEvent event = SosEvent.builder()
                .user(user)
                .triggeredAt(request.getTriggeredAt() != null ? request.getTriggeredAt() : LocalDateTime.now())
                .locationLat(request.getLatitude())
                .locationLng(request.getLongitude())
                .status("NEW")
                .build();

        SosEvent savedEvent = sosEventRepository.save(event);

        sendSosAlert(user, savedEvent);

        return savedEvent;
    }

    private void sendSosAlert(User user, SosEvent event) {
        List<EmergencyContact> contacts = contactRepository.findByUserId(user.getId());

        System.out.println("🚨🚨🚨 TÍN HIỆU SOS KHẨN CẤP 🚨🚨🚨");
        System.out.println("👤 Người cầu cứu: " + user.getFullName());
        System.out.println("📍 Vị trí: " + event.getLocationLat() + ", " + event.getLocationLng());

        if (contacts.isEmpty()) {
            System.out.println("⚠️ Không tìm thấy người thân để báo tin!");
            return;
        }

        for (EmergencyContact contact : contacts) {
            System.out.println("📲 Gửi tin tới: " + contact.getName() + " - 'SOS! " + user.getFullName()
                    + " đang gặp nguy hiểm và cần giúp đỡ ngay tại vị trí định vị!'");
        }
    }
}