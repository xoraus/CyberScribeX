package com.xoraus.cyberscribex.service;

import com.xoraus.cyberscribex.payload.CommentDto;

public interface CommentService {
    CommentDto createComment(Long postId, CommentDto commentDto);
}
