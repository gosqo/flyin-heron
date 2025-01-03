package com.gosqo.flyinheron.service;

import com.gosqo.flyinheron.domain.Board;
import com.gosqo.flyinheron.domain.Comment;
import com.gosqo.flyinheron.domain.CommentLike;
import com.gosqo.flyinheron.domain.Member;
import com.gosqo.flyinheron.dto.comment.*;
import com.gosqo.flyinheron.dto.commentlike.GetCommentsLikedByResponse;
import com.gosqo.flyinheron.global.exception.ThrowIf;
import com.gosqo.flyinheron.global.utility.AuthHeaderUtility;
import com.gosqo.flyinheron.repository.BoardRepository;
import com.gosqo.flyinheron.repository.CommentLikeRepository;
import com.gosqo.flyinheron.repository.CommentRepository;
import com.gosqo.flyinheron.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    public static final int LIKED_COMMENTS_SLICE_SIZE = 10;
    public static final int NORMAL_COMMENTS_SLICE_SIZE = 6;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final ClaimExtractor claimExtractor;
    private final CommentLikeRepository commentLikeRepository;

    private static PageRequest getNormalCommentsPageRequest(int pageNumber) {
        pageNumber = pageNumber - 1;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");

        return PageRequest.of(pageNumber, NORMAL_COMMENTS_SLICE_SIZE, sort);
    }

    private static PageRequest getLikedCommentsPageRequest(int pageNumber) {
        pageNumber = pageNumber - 1;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");

        return PageRequest.of(pageNumber, LIKED_COMMENTS_SLICE_SIZE, sort);
    }

    /**
     * 회원 자신이 좋아요한 댓글들을 반환. 마이페이지에서 조회
     *
     * @param memberEmail 요청한 멤버의 이메일
     * @param pageNumber  조회할 페이지의 번호
     * @return 좋아요한 코멘트 Slice 가 담긴 DTO
     */
    public GetCommentsLikedByResponse getCommentsLikedBy(String memberEmail, Integer pageNumber) {
        final Member member = memberRepository.findByEmail(memberEmail).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 회원의 댓글 좋아요 여부 조회 요청.")
        );
        final Slice<Comment> comments = commentLikeRepository.findByMemberId(
                member.getId(), getLikedCommentsPageRequest(pageNumber)
        ).map(CommentLike::getComment);

        return GetCommentsLikedByResponse.builder()
                .comments(comments)
                .build();
    }

    public CommentDeleteResponse remove(Long id, HttpServletRequest request) {
        final String token = AuthHeaderUtility.extractAccessToken(request);
        final String requesterUserEmail = claimExtractor.extractUserEmail(token);
        final Member requester = memberRepository.findByEmail(requesterUserEmail).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 회원의 댓글 삭제 요청.")
        );
        final Comment storedComment = commentRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 댓글 삭제 요청.")
        );

        ThrowIf.NotMatchedResourceOwner(requester, storedComment.getMember().getId());

        commentRepository.delete(storedComment);

        return CommentDeleteResponse.builder()
                .status(200)
                .message("댓글 삭제가 정상적으로 처리됐습니다.")
                .build();
    }

    public CommentUpdateResponse modify(Long id, HttpServletRequest request, CommentUpdateRequest requestBody) {
        final String token = AuthHeaderUtility.extractAccessToken(request);
        final String requesterUserEmail = claimExtractor.extractUserEmail(token);
        final Member requester = memberRepository.findByEmail(requesterUserEmail).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 회원의 댓글 수정 요청.")
        );
        final Comment storedComment = commentRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 댓글 수정 요청.")
        );

        ThrowIf.NotMatchedResourceOwner(requester, storedComment.getMember().getId());

        storedComment.updateContent(requestBody.getContent());
        storedComment.updateUpdateDate();

        Comment updatedComment = commentRepository.save(storedComment);

        return CommentUpdateResponse.builder()
                .status(200)
                .message("댓글을 수정했습니다.")
                .updatedComment(CommentGetResponse.of(updatedComment))
                .build();
    }

    public CommentRegisterResponse register(HttpServletRequest request, CommentRegisterRequest requestBody) {
        final String token = AuthHeaderUtility.extractAccessToken(request);
        final String requestUserEmail = claimExtractor.extractUserEmail(token);
        final Member member = memberRepository.findByEmail(requestUserEmail).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 회원의 댓글 등록 요청. (토큰 서명 키 유출 가능성)")
        );
        final Board board = boardRepository.findById(requestBody.getBoardId()).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 게시물에 댓글 등록 요청.")
        );

        Comment comment = commentRepository.save(Comment.builder()
                .member(member)
                .board(board)
                .content(requestBody.getContent())
                .build());

        return CommentRegisterResponse.builder()
                .status(201)
                .message("댓글을 등록했습니다.")
                .comment(CommentGetResponse.of(comment))
                .build();
    }

    public CommentGetResponse get(Long id) {
        return CommentGetResponse.of(commentRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 댓글 조회 요청")));
    }

    @Transactional(readOnly = true)
    public Slice<CommentGetResponse> getSliceOfComments(Long boardId, int pageNumber) {
        Pageable pageable = getNormalCommentsPageRequest(pageNumber);
        Slice<Comment> found = commentRepository.findByBoardId(boardId, pageable);

        return new SliceImpl<>(
                found.get().map(CommentGetResponse::of).toList()
                , found.getPageable()
                , found.hasNext()
        );
    }
}
