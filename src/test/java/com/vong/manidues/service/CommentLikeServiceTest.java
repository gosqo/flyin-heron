package com.vong.manidues.service;

import com.vong.manidues.repository.CommentLikeRepository;
import com.vong.manidues.repository.CommentRepository;
import com.vong.manidues.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class CommentLikeServiceTest {

    @InjectMocks
    private CommentLikeService commentLikeService;
    @Mock
    private CommentLikeRepository commentLikeRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CommentRepository commentRepository;

    @Test
    void constructCommentLikeService() {
        CommentLikeService commentLikeService = new CommentLikeService(commentLikeRepository, memberRepository, commentRepository);
    }
}
