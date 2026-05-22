package com.xoraus.cyberscribex.service.impl;

import com.xoraus.cyberscribex.entity.Comment;
import com.xoraus.cyberscribex.entity.Post;
import com.xoraus.cyberscribex.exception.CyberScribeXAPIException;
import com.xoraus.cyberscribex.exception.ResourceNotFoundException;
import com.xoraus.cyberscribex.mapper.CommentMapper;
import com.xoraus.cyberscribex.payload.CommentDto;
import com.xoraus.cyberscribex.repository.CommentRepository;
import com.xoraus.cyberscribex.repository.PostRepository;
import com.xoraus.cyberscribex.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentRepository commentRepository,
                              PostRepository postRepository,
                              CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    @Transactional
    public CommentDto createComment(Long postId, CommentDto commentDto) {
        Post post = getPostById(postId);

        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created with id: {} for post id: {}", savedComment.getId(), postId);
        return commentMapper.toDto(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getAllComments(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post", "id", postId);
        }
        List<Comment> comments = commentRepository.findByPostId(postId);
        return commentMapper.toDtoList(comments);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getCommentById(Long postId, Long commentId) {
        Post post = getPostById(postId);
        Comment comment = getCommentByIdAndCheckBelongsToPost(commentId, post);
        return commentMapper.toDto(comment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long postId, Long commentId, CommentDto commentDto) {
        Post post = getPostById(postId);
        Comment comment = getCommentByIdAndCheckBelongsToPost(commentId, post);

        comment.setName(commentDto.getName());
        comment.setEmail(commentDto.getEmail());
        comment.setComment(commentDto.getComment());

        Comment updatedComment = commentRepository.save(comment);
        log.info("Comment updated with id: {} for post id: {}", commentId, postId);
        return commentMapper.toDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long postId, Long commentId) {
        Post post = getPostById(postId);
        Comment comment = getCommentByIdAndCheckBelongsToPost(commentId, post);
        commentRepository.delete(comment);
        log.info("Comment deleted with id: {} for post id: {}", commentId, postId);
    }

    private Post getPostById(Long postId) {
        return postRepository.findActiveById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
    }

    private Comment getCommentByIdAndCheckBelongsToPost(Long commentId, Post post) {
        Comment comment = commentRepository.findActiveById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!comment.getPost().getId().equals(post.getId())) {
            throw new CyberScribeXAPIException(HttpStatus.BAD_REQUEST,
                    "Comment does not belong to post with id: " + post.getId());
        }
        return comment;
    }
}
