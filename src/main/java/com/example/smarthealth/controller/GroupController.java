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

    @Operation(summary = "Create group (private: family, friends, ...)")
    @PostMapping
    public ResponseEntity<ApiSuccess<GroupDtos.GroupResponse>> create(
            @Valid @RequestBody GroupDtos.CreateGroupRequest req) {
        return ResponseEntity.ok(ApiSuccess.success("Group created", groupService.createGroup(req)));
    }

    @Operation(summary = "Join group (private groups)")
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

     @Operation(summary = "Invite a user to join a private group (creates a pending invitation)")
    @PostMapping("/{groupId}/invites")
    public ResponseEntity<ApiSuccess<GroupDtos.InviteResponse>> invite(
            @PathVariable Long groupId,
            @Valid @RequestBody GroupDtos.CreateInviteRequest req
    ) {
        return ResponseEntity.ok(ApiSuccess.success("Invitation created", groupService.invite(groupId, req)));
    }

    @Operation(summary = "Get pending invitations of current user (NOT in My Groups)")
    @GetMapping("/invites/pending")
    public ResponseEntity<ApiSuccess<List<GroupDtos.PendingInviteResponse>>> myPendingInvites() {
        return ResponseEntity.ok(ApiSuccess.success("Pending invitations fetched", groupService.myPendingInvites()));
    }

    @Operation(summary = "Accept an invitation -> move from Pending to My Groups")
    @PostMapping("/invites/{inviteId}/accept")
    public ResponseEntity<ApiSuccess<Void>> acceptInvite(@PathVariable Long inviteId) {
        groupService.acceptInvite(inviteId);
        return ResponseEntity.ok(ApiSuccess.success("Invitation accepted", null));
    }

    @Operation(summary = "Decline an invitation -> remove from Pending (disappears everywhere)")
    @PostMapping("/invites/{inviteId}/decline")
    public ResponseEntity<ApiSuccess<Void>> declineInvite(@PathVariable Long inviteId) {
        groupService.declineInvite(inviteId);
        return ResponseEntity.ok(ApiSuccess.success("Invitation declined", null));
    }

       @Operation(summary = "Leave a group -> remove from My Groups")
    @PostMapping("/{groupId}/leave")
    public ResponseEntity<ApiSuccess<Void>> leave(@PathVariable Long groupId) {
        groupService.leaveGroup(groupId);
        return ResponseEntity.ok(ApiSuccess.success("Left group", null));
    }

    @Operation(summary = "Remove a member from group (owner only)")
    @DeleteMapping("/{groupId}/members/{memberUserId}")
    public ResponseEntity<ApiSuccess<Void>> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long memberUserId
    ) {
        groupService.removeMember(groupId, memberUserId);
        return ResponseEntity.ok(ApiSuccess.success("Member removed", null));
    }

    @Operation(summary = "Revoke/cancel an invitation (owner only)")
    @DeleteMapping("/{groupId}/invites/{inviteId}")
    public ResponseEntity<ApiSuccess<Void>> revokeInvite(
            @PathVariable Long groupId,
            @PathVariable Long inviteId
    ) {
        groupService.revokeInvite(groupId, inviteId);
        return ResponseEntity.ok(ApiSuccess.success("Invitation revoked", null));
    }


}