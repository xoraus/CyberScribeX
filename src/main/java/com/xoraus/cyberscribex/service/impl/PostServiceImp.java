package com.xoraus.cyberscribex.service.impl;

import com.xoraus.cyberscribex.entity.Category;
import com.xoraus.cyberscribex.entity.Post;
import com.xoraus.cyberscribex.exception.ResourceNotFoundException;
import com.xoraus.cyberscribex.payload.PostDto;
import com.xoraus.cyberscribex.payload.PostResponse;
import com.xoraus.cyberscribex.repository.CategoryRepository;
import com.xoraus.cyberscribex.repository.PostRepository;
import com.xoraus.cyberscribex.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImp implements PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;

    public PostServiceImp(PostRepository postRepository, ModelMapper modelMapper, CategoryRepository categoryRepository) {
        this.postRepository = postRepository;
        this.modelMapper = modelMapper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public PostDto createPost(PostDto postDto) {
        Category category =  categoryRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", postDto.getCategoryId()));

        // convert DTO to Entity
        Post post = mapToEntity(postDto);
        post.setCategory(category);
        Post newPost = postRepository.save(post);

        // convert Entity to DTO
        return mapToDTO(newPost);
    }

    @Override
    public PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Post> posts =  postRepository.findAll(pageable);

        // get content for Page object

        List<Post> listOfPosts = posts.getContent();

        List<PostDto> content=  listOfPosts.stream().map(this::mapToDTO).collect(Collectors.toList());

        PostResponse postResponse = new PostResponse();
        postResponse.setContent(content);
        postResponse.setPageNo(posts.getNumber());
        postResponse.setPageSize(posts.getSize());
        postResponse.setTotalElements(posts.getTotalElements());
        postResponse.setTotalPages(posts.getTotalPages());
        postResponse.setLast(posts.isLast());

        return postResponse;
    }

    @Override
    public PostDto getPostById(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        return mapToDTO(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, long id) {
        // get post by id from the database
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        Category category = categoryRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", postDto.getCategoryId()));

        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());
        post.setCategory(category);
        Post updatedPost = postRepository.save(post);
        return mapToDTO(updatedPost);
    }

    @Override
    public void deletePostById(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        postRepository.delete(post);
    }

    @Override
    public List<PostDto> getPostsByCategory(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        List<Post> posts = postRepository.findByCategoryId(categoryId);

        return posts.stream().map((post) -> mapToDTO(post))
                .collect(Collectors.toList());
    }

    // Entity to DTO
    private PostDto mapToDTO(Post post){
        return modelMapper.map(post, PostDto.class);
    }

    // DTO to Entity
    private Post mapToEntity(PostDto postDto){
        return modelMapper.map(postDto, Post.class);
    }

}
