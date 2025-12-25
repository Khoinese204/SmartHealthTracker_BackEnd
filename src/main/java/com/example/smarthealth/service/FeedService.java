package com.example.smarthealth.service;

import com.example.smarthealth.config.CurrentUserService;
import com.example.smarthealth.dto.social.FeedDtos;
import com.example.smarthealth.dto.social.UserSummaryDto;
import com.example.smarthealth.enums.PostVisibility;
import com.example.smarthealth.helper.CheckOwner;
import com.example.smarthealth.model.auth.User;
import com.example.smarthealth.model.social.*;
import com.example.smarthealth.repository.GroupMemberRepository;
import com.example.smarthealth.repository.GroupRepository;
import com.example.smarthealth.repository.PostCommentRepository;
import com.example.smarthealth.repository.PostLikeRepository;
import com.example.smarthealth.repository.PostRepository;
import com.example.smarthealth.repository.PostShareRepository;
import com.example.smarthealth.repository.UserRepository;
import java.util.function.Function;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final CurrentUserService currentUserService;
    private final PostShareRepository postShareRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
    private final CheckOwner checkOwner;

    @Transactional
    public FeedDtos.FeedItemResponse createPost(FeedDtos.CreatePostRequest req) {
        User me = currentUserService.getCurrentUser();

        if (req.getContent() == null || req.getContent().trim().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "content is required");
        }

        PostVisibility vis = (req.getVisibility() != null) ? req.getVisibility() : PostVisibility.PUBLIC;

        Long groupId = null;
        if (vis == PostVisibility.GROUP) {
            if (req.getGroupId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "groupId is required when visibility=GROUP");
            }

            // ✅ ABAC: OWNER/MEMBER đều post được
            boolean isMember = groupMemberRepository.existsByGroupIdAndUserId(req.getGroupId(), me.getId());
            if (!isMember) {
                // chống probe groupId
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found");
            }
            groupId = req.getGroupId();
        } else {
            // PUBLIC/PRIVATE => không gắn group
            if (req.getGroupId() != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "groupId must be null when visibility != GROUP");
            }
        }

        Post saved = postRepository.save(Post.builder()
                .userId(me.getId())
                .content(req.getContent().trim())
                .imageUrl(req.getImageUrl())
                .visibility(vis)
                .groupId(groupId)
                .achievementUserId(req.getAchievementUserId())
                .build());

        UserSummaryDto meSummary = new UserSummaryDto(me.getId(), me.getFullName(), me.getAvatarUrl());

        return FeedDtos.FeedItemResponse.builder()
                .id(saved.getId())
                .user(meSummary)
                .content(saved.getContent())
                .imageUrl(saved.getImageUrl())
                .achievementUserId(saved.getAchievementUserId())
                .createdAt(saved.getCreatedAt())
                .visibility(saved.getVisibility())
                .likeCount(0)
                .commentCount(0)
                .likedByMe(false)
                .isShare(false)
                .build();
    }

    @Transactional
    public FeedDtos.FeedItemResponse updatePost(Long postId, FeedDtos.UpdatePostRequest req) {
        User me = currentUserService.getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        // ✅ ABAC: author OR group owner (moderation) mới được sửa
        if (!checkOwner.canEditOrDelete(me, post)) {
            // chống IDOR/probing
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
            // hoặc:
            // throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }

        boolean hasAnyUpdate = (req.getContent() != null)
                || (req.getImageUrl() != null)
                || (req.getVisibility() != null)
                || (req.getGroupId() != null);

        if (!hasAnyUpdate) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nothing to update");
        }

        // content
        if (req.getContent() != null) {
            String c = req.getContent().trim();
            if (c.isBlank())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "content cannot be blank");
            post.setContent(c);
        }

        // imageUrl (cho phép set null bằng cách gửi chuỗi rỗng)
        if (req.getImageUrl() != null) {
            String img = req.getImageUrl().trim();
            post.setImageUrl(img.isBlank() ? null : img);
        }

        // visibility + groupId rule
        if (req.getVisibility() != null) {
            PostVisibility newVis = req.getVisibility();

            if (newVis == PostVisibility.GROUP) {
                Long newGroupId = (req.getGroupId() != null) ? req.getGroupId() : post.getGroupId();
                if (newGroupId == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "groupId is required when visibility=GROUP");
                }

                // ✅ ABAC: muốn set GROUP vào group nào => phải là member group đó
                boolean isMember = groupMemberRepository.existsByGroupIdAndUserId(newGroupId, me.getId());
                if (!isMember)
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found");

                post.setGroupId(newGroupId);
                post.setVisibility(PostVisibility.GROUP);

            } else {
                // PUBLIC/PRIVATE => groupId null
                post.setGroupId(null);
                post.setVisibility(newVis);
            }

        } else {
            // visibility không đổi nhưng groupId gửi lên
            if (req.getGroupId() != null) {
                if (post.getVisibility() != PostVisibility.GROUP) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "groupId can only be updated when visibility=GROUP");
                }

                // ✅ ABAC: đổi groupId => phải là member group mới
                boolean isMember = groupMemberRepository.existsByGroupIdAndUserId(req.getGroupId(), me.getId());
                if (!isMember)
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found");

                post.setGroupId(req.getGroupId());
            }
        }

        Post saved = postRepository.save(post);

        // author summary của bài (không phải me, vì moderation có thể sửa bài người
        // khác)
        User author = userRepository.findById(saved.getUserId()).orElse(null);
        UserSummaryDto authorSummary = (author == null)
                ? null
                : new UserSummaryDto(author.getId(), author.getFullName(), author.getAvatarUrl());

        return FeedDtos.FeedItemResponse.builder()
                .id(saved.getId())
                .user(authorSummary)
                .content(saved.getContent())
                .imageUrl(saved.getImageUrl())
                .achievementUserId(saved.getAchievementUserId())
                .createdAt(saved.getCreatedAt())
                .visibility(saved.getVisibility())
                .likeCount(postLikeRepository.countByPostId(saved.getId()))
                .commentCount(postCommentRepository.countByPostId(saved.getId()))
                .likedByMe(postLikeRepository.existsByPostIdAndUserId(saved.getId(), me.getId()))
                .isShare(false)
                .build();
    }

    @Transactional
    public void deletePost(Long postId) {
        User me = currentUserService.getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        // ✅ ABAC: author OR group owner mới được xoá
        if (!checkOwner.canEditOrDelete(me, post)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }

        // Nếu chưa cascade FK: xóa likes/comments trước
        // postLikeRepository.deleteByPostId(postId);
        // postCommentRepository.deleteByPostId(postId);

        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public Page<FeedDtos.FeedItemResponse> getLatestFeed(int page, int size) {
        User me = currentUserService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Post> posts = postRepository.findByVisibility(PostVisibility.PUBLIC, pageable);

        if (posts.isEmpty()) {
            return posts.map(p -> FeedDtos.FeedItemResponse.builder().build());
        }

        List<Post> postList = posts.getContent();

        List<Long> postIds = postList.stream().map(Post::getId).toList();
        List<Long> userIds = postList.stream().map(Post::getUserId).distinct().toList();

        Map<Long, UserSummaryDto> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(
                        User::getId,
                        u -> new UserSummaryDto(u.getId(), u.getFullName(), u.getAvatarUrl())));

        Map<Long, Long> likeCounts = toCountMap(postLikeRepository.countLikesByPostIds(postIds));
        Map<Long, Long> commentCounts = toCountMap(postCommentRepository.countCommentsByPostIds(postIds));
        Set<Long> likedByMe = new HashSet<>(postLikeRepository.findLikedPostIds(me.getId(), postIds));

        return posts.map(p -> FeedDtos.FeedItemResponse.builder()
                .id(p.getId())
                .user(userMap.get(p.getUserId()))
                .content(p.getContent())
                .imageUrl(p.getImageUrl())
                .achievementUserId(p.getAchievementUserId())
                .createdAt(p.getCreatedAt())
                .visibility(p.getVisibility())
                .likeCount(likeCounts.getOrDefault(p.getId(), 0L))
                .commentCount(commentCounts.getOrDefault(p.getId(), 0L))
                .likedByMe(likedByMe.contains(p.getId()))
                .isShare(false)
                .build());
    }

    @Transactional(readOnly = true)
    public FeedDtos.FeedItemResponse getPostById(Long postId) {
        User me = currentUserService.getCurrentUser();

        // 1) Lấy post
        Post p = postRepository.findReadableById(postId, me.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND));

        // 3) Build userMap (1 user thôi)
        User author = userRepository.findById(p.getUserId()).orElse(null);
        UserSummaryDto authorSummary = (author == null)
                ? null
                : new UserSummaryDto(author.getId(), author.getFullName(), author.getAvatarUrl());

        long likeCount = postLikeRepository.countByPostId(p.getId());
        long commentCount = postCommentRepository.countByPostId(p.getId());
        boolean likedByMe = postLikeRepository.existsByPostIdAndUserId(p.getId(), me.getId());

        return FeedDtos.FeedItemResponse.builder()
                .id(p.getId())
                .user(authorSummary)
                .content(p.getContent())
                .imageUrl(p.getImageUrl())
                .achievementUserId(p.getAchievementUserId())
                .createdAt(p.getCreatedAt())
                .visibility(p.getVisibility())
                .likeCount(likeCount)
                .commentCount(commentCount)
                .likedByMe(likedByMe)
                .isShare(false)
                .build();
    }

    @Transactional
    public void likePost(Long postId) {
        User me = currentUserService.getCurrentUser();
        if (!postRepository.existsById(postId))
            throw new EntityNotFoundException("Post not found");

        if (postLikeRepository.existsByPostIdAndUserId(postId, me.getId()))
            return;
        postLikeRepository.save(PostLike.builder()
                .postId(postId)
                .userId(me.getId()) // ✅ ĐÚNG
                .build());
    }

    @Transactional
    public void unlikePost(Long postId) {
        User me = currentUserService.getCurrentUser();
        PostLike like = postLikeRepository.findByPostIdAndUserId(postId, me.getId()).orElse(null);
        if (like != null)
            postLikeRepository.delete(like);
    }

    @Transactional
    public FeedDtos.CommentResponse addComment(Long postId, FeedDtos.CreateCommentRequest req) {
        User me = currentUserService.getCurrentUser();
        if (!postRepository.existsById(postId))
            throw new EntityNotFoundException("Post not found");

        UserSummaryDto meSummary = new UserSummaryDto(me.getId(), me.getFullName(), me.getAvatarUrl());

        PostComment saved = postCommentRepository.save(PostComment.builder()
                .postId(postId)
                .userId(me.getId())
                .content(req.getContent())
                .build());

        return FeedDtos.CommentResponse.builder()
                .id(saved.getId())
                .postId(saved.getPostId())
                .user(meSummary)
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<FeedDtos.CommentResponse> getComments(Long postId, int page, int size) {
        if (!postRepository.existsById(postId))
            throw new EntityNotFoundException("Post not found");

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PostComment> commentPage = postCommentRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable);

        List<Long> userIds = commentPage.getContent().stream()
                .map(PostComment::getUserId)
                .distinct()
                .toList();

        Map<Long, UserSummaryDto> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(
                        User::getId,
                        u -> new UserSummaryDto(u.getId(), u.getFullName(), u.getAvatarUrl())));

        return commentPage.map(c -> FeedDtos.CommentResponse.builder()
                .id(c.getId())
                .postId(c.getPostId())
                .user(userMap.get(c.getUserId())) // ✅
                .content(c.getContent())
                .createdAt(c.getCreatedAt())
                .build());
    }

    private Map<Long, Long> toCountMap(List<Object[]> rows) {
        return rows.stream().collect(Collectors.toMap(
                r -> (Long) r[0],
                r -> (Long) r[1]));
    }

    @Transactional
    public FeedDtos.ShareResponse sharePost(Long postId, FeedDtos.SharePostRequest req) {
        Post original = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        User me = currentUserService.getCurrentUser();
        PostVisibility visibility = parseVisibility(req.getVisibility());

        if (visibility == PostVisibility.GROUP && req.getGroupId() == null) {
            throw new IllegalArgumentException("groupId is required when visibility=GROUP");
        }

        PostShare share = PostShare.builder()
                .originalPostId(original.getId())
                .sharedByUserId(me.getId())
                .message(req.getMessage())
                .visibility(visibility)
                .sharedToGroupId(visibility == PostVisibility.GROUP ? req.getGroupId() : null)
                .build();

        share = postShareRepository.save(share);

        User originalAuthor = userRepository.findById(original.getUserId()).orElse(null);

        return toShareResponse(share, original, me, originalAuthor); // ✅ đủ 4 params
    }

    @Transactional(readOnly = true)
    public Page<FeedDtos.ShareResponse> getShares(Long postId, int page, int size) {

        Page<PostShare> shares = postShareRepository.findByOriginalPostIdOrderByCreatedAtDesc(
                postId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        return shares.map(s -> {
            User sharedBy = userRepository.findById(s.getSharedByUserId()).orElse(null);
            Post original = postRepository.findById(s.getOriginalPostId()).orElse(null);

            User originalAuthor = (original == null)
                    ? null
                    : userRepository.findById(original.getUserId()).orElse(null);

            return toShareResponse(s, original, sharedBy, originalAuthor); // ✅ đủ 4 params
        });
    }

    private UserSummaryDto toUserSummary(User u) {
        if (u == null)
            return null;

        return new UserSummaryDto(
                u.getId(),
                u.getFullName(),
                u.getAvatarUrl());
    }

    private FeedDtos.FeedItemResponse toFeedItem(Post p, User user) {
        if (p == null)
            return null;

        return FeedDtos.FeedItemResponse.builder()
                .id(p.getId())
                .user(toUserSummary(user)) // ✅
                .content(p.getContent())
                .imageUrl(p.getImageUrl())
                .achievementUserId(p.getAchievementUserId())
                .createdAt(p.getCreatedAt())
                .likeCount(0)
                .commentCount(0)
                .likedByMe(false)
                .isShare(false)
                .visibility(p.getVisibility() == null
                        ? PostVisibility.PUBLIC
                        : p.getVisibility())
                .build();
    }

    private FeedDtos.ShareResponse toShareResponse(
            PostShare s,
            Post originalPost,
            User sharedBy,
            User originalAuthor // 👈 thêm user của post gốc
    ) {
        return FeedDtos.ShareResponse.builder()
                .shareId(s.getId())
                .visibility(s.getVisibility() == null ? PostVisibility.PUBLIC : s.getVisibility())
                .groupId(s.getSharedToGroupId())
                .message(s.getMessage())
                .createdAt(s.getCreatedAt())
                .sharedBy(toUserSummary(sharedBy))
                .originalPost(toFeedItem(originalPost, originalAuthor)) // ✅
                .build();
    }

    private PostVisibility parseVisibility(PostVisibility v) {
        return (v == null) ? PostVisibility.PUBLIC : v;
    }

    @Transactional(readOnly = true)
    public Page<FeedDtos.LikeUserResponse> getPostLikes(Long postId, int page, int size) {
        postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        Page<PostLike> likesPage = postLikeRepository.findByPostIdOrderByCreatedAtDesc(
                postId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        if (likesPage.isEmpty()) {
            return likesPage.map(l -> FeedDtos.LikeUserResponse.builder().build());
        }

        List<Long> userIds = likesPage.getContent().stream()
                .map(PostLike::getUserId)
                .distinct()
                .toList();

        Map<Long, UserSummaryDto> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(
                        User::getId,
                        u -> new UserSummaryDto(
                                u.getId(),
                                u.getFullName(),
                                u.getAvatarUrl())));

        return likesPage.map(like -> FeedDtos.LikeUserResponse.builder()
                .user(userMap.get(like.getUserId())) // ✅ trả thẳng UserSummaryDto
                .likedAt(like.getCreatedAt())
                .build());
    }

    @Transactional(readOnly = true)
    public Page<FeedDtos.FeedItemResponse> getGroupFeed(Long groupId, int page, int size) {
        User me = currentUserService.getCurrentUser();

        // (optional) group tồn tại không?
        if (!groupRepository.existsById(groupId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found");
        }

        // ✅ ABAC: không phải member => chặn luôn (khuyên dùng 404 để chống probe)
        boolean isMember = groupMemberRepository.existsByGroupIdAndUserId(groupId, me.getId());
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found");
            // hoặc 403 nếu bạn muốn rõ ràng:
            // throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member of this
            // group");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // giờ query không cần exists membership nữa cũng được
        Page<Post> posts = postRepository.findByGroupIdAndVisibilityIn(
                groupId,
                List.of(PostVisibility.GROUP, PostVisibility.PRIVATE),
                pageable);

        if (posts.isEmpty()) {
            return posts.map(p -> FeedDtos.FeedItemResponse.builder().build());
        }

        List<Post> postList = posts.getContent();
        List<Long> postIds = postList.stream().map(Post::getId).toList();
        List<Long> userIds = postList.stream().map(Post::getUserId).distinct().toList();

        Map<Long, UserSummaryDto> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(
                        User::getId,
                        u -> new UserSummaryDto(u.getId(), u.getFullName(), u.getAvatarUrl())));

        Map<Long, Long> likeCounts = toCountMap(postLikeRepository.countLikesByPostIds(postIds));
        Map<Long, Long> commentCounts = toCountMap(postCommentRepository.countCommentsByPostIds(postIds));
        Set<Long> likedByMe = new HashSet<>(postLikeRepository.findLikedPostIds(me.getId(), postIds));

        return posts.map(p -> FeedDtos.FeedItemResponse.builder()
                .id(p.getId())
                .user(userMap.get(p.getUserId()))
                .content(p.getContent())
                .imageUrl(p.getImageUrl())
                .achievementUserId(p.getAchievementUserId())
                .createdAt(p.getCreatedAt())
                .visibility(p.getVisibility())
                .likeCount(likeCounts.getOrDefault(p.getId(), 0L))
                .commentCount(commentCounts.getOrDefault(p.getId(), 0L))
                .likedByMe(likedByMe.contains(p.getId()))
                .isShare(false)
                .build());
    }

}