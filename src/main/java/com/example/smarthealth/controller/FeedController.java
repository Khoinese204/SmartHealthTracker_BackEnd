package com.example.smarthealth.controller;

import com.example.smarthealth.dto.common.ApiSuccess;
import com.example.smarthealth.dto.social.FeedDtos;
import com.example.smarthealth.service.FeedService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/social/posts")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @Operation(summary = "Create a post (text + optional imageUrl + optional achievementUserId)")
    @PostMapping
    public ResponseEntity<ApiSuccess<FeedDtos.FeedItemResponse>> createPost(
            @Valid @RequestBody FeedDtos.CreatePostRequest req) {
        return ResponseEntity.ok(ApiSuccess.success("Post created", feedService.createPost(req)));
    }

    @Operation(summary = "Update a post (owner only)")
    @PatchMapping("/{postId}")
    public ResponseEntity<ApiSuccess<FeedDtos.FeedItemResponse>> updatePost(
            @PathVariable Long postId,
            @RequestBody FeedDtos.UpdatePostRequest req) {
        var updated = feedService.updatePost(postId, req);
        return ResponseEntity.ok(ApiSuccess.success("Post updated", updated));
    }

    @Operation(summary = "Delete a post (owner only)")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiSuccess<Object>> deletePost(@PathVariable Long postId) {
        feedService.deletePost(postId);
        return ResponseEntity.ok(ApiSuccess.success("Post deleted", null));
    }

    @Operation(summary = "Get latest newsfeed (only PUBLIC)")
    @GetMapping
    public ResponseEntity<ApiSuccess<Page<FeedDtos.FeedItemResponse>>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiSuccess.success("Feed fetched", feedService.getLatestFeed(page, size)));
    }

    @Operation(summary = "Like a post")
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiSuccess<Void>> like(@PathVariable Long postId) {
        feedService.likePost(postId);
        return ResponseEntity.ok(ApiSuccess.success("Liked", null));
    }

    @Operation(summary = "Unlike a post")
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<ApiSuccess<Void>> unlike(@PathVariable Long postId) {
        feedService.unlikePost(postId);
        return ResponseEntity.ok(ApiSuccess.success("Unliked", null));
    }

    @Operation(summary = "Get likes of a post (paginated)")
    @GetMapping("/{postId}/likes")
    public ResponseEntity<?> getPostLikes(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = feedService.getPostLikes(postId, page, size);
        return ResponseEntity.ok(ApiSuccess.success("Likes fetched successfully", result));
    }

    @Operation(summary = "Add comment to post")
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiSuccess<FeedDtos.CommentResponse>> addComment(
            @PathVariable Long postId,
            @Valid @RequestBody FeedDtos.CreateCommentRequest req) {
        return ResponseEntity.ok(ApiSuccess.success("Comment added", feedService.addComment(postId, req)));
    }

    @Operation(summary = "Get comments of a post (paginated)")
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiSuccess<Page<FeedDtos.CommentResponse>>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiSuccess.success("Comments fetched", feedService.getComments(postId, page, size)));
    }

    @Operation(summary = "Share a post")
    @PostMapping("/{postId}/shares")
    public ResponseEntity<ApiSuccess<FeedDtos.ShareResponse>> sharePost(
            @PathVariable Long postId,
            @RequestBody FeedDtos.SharePostRequest req) {
        return ResponseEntity.ok(ApiSuccess.success("Post shared", feedService.sharePost(postId, req)));
    }

    @Operation(summary = "Get shares of a post (paginated)")
    @GetMapping("/{postId}/shares")
    public ResponseEntity<ApiSuccess<Page<FeedDtos.ShareResponse>>> getShares(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiSuccess.success("Shares fetched", feedService.getShares(postId, page, size)));
    }

    @Operation(summary = "Get post by id")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiSuccess<FeedDtos.FeedItemResponse>> getPostById(@PathVariable Long postId) {
        return ResponseEntity.ok(
                ApiSuccess.success("Post fetched", feedService.getPostById(postId)));
    }

    @Operation(summary = "Get group newsfeed (only GROUP)")
    @GetMapping("/{groupId}/feed")
    public ResponseEntity<ApiSuccess<Page<FeedDtos.FeedItemResponse>>> groupFeed(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                ApiSuccess.success("Group feed fetched", feedService.getGroupFeed(groupId, page, size)));
    }

}