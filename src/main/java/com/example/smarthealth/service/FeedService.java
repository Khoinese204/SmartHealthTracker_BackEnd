package com.example.smarthealth.service;

import com.example.smarthealth.config.CurrentUserService;
import com.example.smarthealth.dto.social.FeedDtos;
import com.example.smarthealth.dto.social.UserSummaryDto;
import com.example.smarthealth.enums.PostVisibility;
import com.example.smarthealth.model.auth.User;
import com.example.smarthealth.model.social.*;
import com.example.smarthealth.repository.GroupMemberRepository;
import com.example.smarthealth.repository.PostCommentRepository;
import com.example.smarthealth.repository.PostLikeRepository;
import com.example.smarthealth.repository.PostRepository;
import com.example.smarthealth.repository.PostShareRepository;
import com.example.smarthealth.repository.UserRepository;
import java.util.function.Function;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public FeedDtos.FeedItemResponse createPost(FeedDtos.CreatePostRequest req) {
        User me = currentUserService.getCurrentUser();

        PostVisibility visibility = req.getVisibility() == null ? PostVisibility.PUBLIC : req.getVisibility();

        if (visibility == PostVisibility.GROUP && req.getGroupId() == null) {
            throw new IllegalArgumentException("groupId is required when visibility=GROUP");
        }

        UserSummaryDto meSummary = new UserSummaryDto(
                me.getId(),
                me.getFullName(),
                me.getAvatarUrl());

        Post saved = postRepository.save(Post.builder()
                .userId(me.getId())
                .content(req.getContent())
                .imageUrl(req.getImageUrl())
                .achievementUserId(req.getAchievementUserId())
                .visibility(visibility)
                .groupId(visibility == PostVisibility.GROUP ? req.getGroupId() : null)
                .build());

        return FeedDtos.FeedItemResponse.builder()
                .id(saved.getId())
                .user(meSummary)
                .content(saved.getContent())
                .imageUrl(saved.getImageUrl())
                .achievementUserId(saved.getAchievementUserId())
                .createdAt(saved.getCreatedAt())
                .visibility(saved.getVisibility()) // ✅ giờ không còn null
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
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        // chỉ owner được sửa
        if (!Objects.equals(post.getUserId(), me.getId())) {
            throw new IllegalArgumentException("You are not allowed to update this post");
        }

        boolean hasAnyUpdate = (req.getContent() != null) ||
                (req.getImageUrl() != null) ||
                (req.getVisibility() != null) ||
                (req.getGroupId() != null);

        if (!hasAnyUpdate) {
            throw new IllegalArgumentException("Nothing to update");
        }

        // content
        if (req.getContent() != null) {
            String c = req.getContent().trim();
            if (c.isBlank())
                throw new IllegalArgumentException("content cannot be blank");
            post.setContent(c);
        }

        // imageUrl (cho phép set null để remove ảnh nếu FE gửi null)
        if (req.getImageUrl() != null) {
            post.setImageUrl(req.getImageUrl().trim().isBlank() ? null : req.getImageUrl().trim());
        }

        // visibility + groupId rule
        if (req.getVisibility() != null) {
            PostVisibility newVis = req.getVisibility();

            if (newVis == PostVisibility.GROUP) {
                // groupId bắt buộc khi GROUP (ưu tiên req.groupId nếu có, nếu không thì dùng
                // cái cũ)
                Long groupId = (req.getGroupId() != null) ? req.getGroupId() : post.getGroupId();
                if (groupId == null)
                    throw new IllegalArgumentException("groupId is required when visibility=GROUP");
                post.setGroupId(groupId);
            } else {
                // không phải group thì groupId phải null
                post.setGroupId(null);
            }

            post.setVisibility(newVis);
        } else {
            // visibility không đổi nhưng groupId có thể được gửi lên (chỉ hợp lệ nếu post
            // đang GROUP)
            if (req.getGroupId() != null) {
                if (post.getVisibility() != PostVisibility.GROUP) {
                    throw new IllegalArgumentException("groupId can only be updated when visibility=GROUP");
                }
                post.setGroupId(req.getGroupId());
            }
        }

        Post saved = postRepository.save(post);

        UserSummaryDto meSummary = new UserSummaryDto(
                me.getId(),
                me.getFullName(),
                me.getAvatarUrl());

        // Trả về response giống feed item
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
    public void deletePost(Long postId) {
        User me = currentUserService.getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        if (!Objects.equals(post.getUserId(), me.getId())) {
            throw new IllegalArgumentException("You are not allowed to delete this post");
        }

        // Nếu bạn có comment/like/share FK cascade thì ok.
        // Nếu chưa cascade: cần xóa likes/comments/shares trước để tránh FK constraint.
        // postLikeRepository.deleteByPostId(postId);
        // postCommentRepository.deleteByPostId(postId);
        // postShareRepository.deleteByOriginalPostId(postId); (tùy bạn định nghĩa)
        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public Page<FeedDtos.FeedItemResponse> getLatestFeed(int page, int size) {
        User me = currentUserService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<Long> myGroupIds = groupMemberRepository.findGroupIdsByUserId(me.getId());

        Page<Post> posts = (myGroupIds == null || myGroupIds.isEmpty())
                ? postRepository.findVisibleFeedNoGroups(
                        me.getId(),
                        PostVisibility.PUBLIC,
                        PostVisibility.PRIVATE,
                        pageable)
                : postRepository.findVisibleFeed(
                        me.getId(),
                        myGroupIds,
                        PostVisibility.PUBLIC,
                        PostVisibility.PRIVATE,
                        PostVisibility.GROUP,
                        pageable);

        if (posts.isEmpty())
            return posts.map(p -> FeedDtos.FeedItemResponse.builder().build());

        List<Post> postList = posts.getContent();

        List<Long> postIds = postList.stream().map(Post::getId).toList();
        List<Long> userIds = postList.stream().map(Post::getUserId).distinct().toList();

        Map<Long, UserSummaryDto> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(
                        User::getId,
                        u -> new UserSummaryDto(
                                u.getId(),
                                u.getFullName(),
                                u.getAvatarUrl())));

        Map<Long, Long> likeCounts = toCountMap(postLikeRepository.countLikesByPostIds(postIds));
        Map<Long, Long> commentCounts = toCountMap(postCommentRepository.countCommentsByPostIds(postIds));
        Set<Long> likedByMe = new HashSet<>(postLikeRepository.findLikedPostIds(me.getId(), postIds));

        return posts.map(p -> FeedDtos.FeedItemResponse.builder()
                .id(p.getId())
                .user(userMap.get(p.getUserId())) // ✅ UserSummaryDto
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
        Post p = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        // 3) Build userMap (1 user thôi)
        User author = userRepository.findById(p.getUserId()).orElse(null);
        UserSummaryDto authorSummary = (author == null)
                ? null
                : new UserSummaryDto(author.getId(), author.getFullName(), author.getAvatarUrl());

        // 4) Like/comment count + likedByMe
        long likeCount = postLikeRepository.countByPostId(p.getId());
        long commentCount = postCommentRepository.countByPostId(p.getId());
        boolean likedByMe = postLikeRepository.existsByPostIdAndUserId(p.getId(), me.getId());

        // 5) Response
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

}