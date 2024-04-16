package com.xoraus.cyberscribex.service;

import com.xoraus.cyberscribex.payload.PostDto;
import com.xoraus.cyberscribex.payload.PostResponse;

import java.util.List;

public interface PostService {
    PostDto createPost(PostDto post);
    PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir);
    PostDto getPostById(long id);
    PostDto updatePost( PostDto post, long id);
    void deletePostById(long id);
    List<PostDto> getPostsByCategory(Long categoryId);
}
