package com.example.smarthealth.controller;

import com.example.smarthealth.dto.common.ApiSuccess;
import com.example.smarthealth.dto.social.GroupDtos;
import com.example.smarthealth.service.GroupService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/social/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @Operation(summary = "Create group (public or private)")
    @PostMapping
    public ResponseEntity<ApiSuccess<GroupDtos.GroupResponse>> create(
            @Valid @RequestBody GroupDtos.CreateGroupRequest req) {
        return ResponseEntity.ok(ApiSuccess.success("Group created", groupService.createGroup(req)));
    }

    @Operation(summary = "Join group (public only in current implementation)")
    @PostMapping("/{groupId}/join")
    public ResponseEntity<ApiSuccess<Void>> join(@PathVariable Long groupId) {
        groupService.joinGroup(groupId);
        return ResponseEntity.ok(ApiSuccess.success("Joined group", null));
    }

    @Operation(summary = "Get groups that current user belongs to")
    @GetMapping("/me")
    public ResponseEntity<ApiSuccess<List<GroupDtos.GroupResponse>>> myGroups() {
        return ResponseEntity.ok(ApiSuccess.success("My groups fetched", groupService.myGroups()));
    }

    @Operation(summary = "Get members of a group")
    @GetMapping("/{groupId}/members")
    public ResponseEntity<ApiSuccess<List<GroupDtos.GroupMemberResponse>>> members(@PathVariable Long groupId) {
        return ResponseEntity.ok(ApiSuccess.success("Group members fetched", groupService.members(groupId)));
    }
}