package com.xoraus.cyberscribex.service;

import com.xoraus.cyberscribex.entity.Comment;
import com.xoraus.cyberscribex.payload.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long postId, CommentDto commentDto);
    List<CommentDto> getAllComments(Long postId);
}
