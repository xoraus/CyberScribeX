package com.xoraus.cyberscribex.service;

import com.xoraus.cyberscribex.payload.PostDto;

import java.util.List;

public interface PostService {
    PostDto createPost(PostDto post);
    List<PostDto> getAllPosts();
    PostDto getPostById(long id);
    PostDto updatePost( PostDto post, long id);
    void deletePostById(long id);

}
