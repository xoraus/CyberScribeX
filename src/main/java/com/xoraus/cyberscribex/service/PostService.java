package com.xoraus.cyberscribex.service;

import com.xoraus.cyberscribex.payload.PostDto;
import com.xoraus.cyberscribex.payload.PostResponse;

import java.util.List;

public interface PostService {
    PostDto createPost(PostDto postDto);
    PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir);
    PostDto getPostById(Long id);
    PostDto updatePost(PostDto postDto, Long id);
    void deletePostById(Long id);
    List<PostDto> getPostsByCategory(Long categoryId);
    List<PostDto> searchPosts(String query);
}
