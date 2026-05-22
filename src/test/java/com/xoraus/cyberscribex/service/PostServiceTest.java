package com.xoraus.cyberscribex.service;

import com.xoraus.cyberscribex.entity.Category;
import com.xoraus.cyberscribex.entity.Post;
import com.xoraus.cyberscribex.exception.ResourceNotFoundException;
import com.xoraus.cyberscribex.mapper.PostMapper;
import com.xoraus.cyberscribex.payload.PostDto;
import com.xoraus.cyberscribex.payload.PostResponse;
import com.xoraus.cyberscribex.repository.CategoryRepository;
import com.xoraus.cyberscribex.repository.PostRepository;
import com.xoraus.cyberscribex.service.impl.PostServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Post Service Unit Tests")
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostServiceImp postService;

    private Post post;
    private PostDto postDto;
    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Technology")
                .description("Tech posts")
                .build();

        post = Post.builder()
                .id(1L)
                .title("Test Post")
                .description("Test Description")
                .content("Test Content")
                .category(category)
                .build();

        postDto = PostDto.builder()
                .id(1L)
                .title("Test Post")
                .description("Test Description")
                .content("Test Content")
                .categoryId(1L)
                .build();
    }

    @Test
    @DisplayName("Should create post successfully")
    void shouldCreatePost() {
        given(categoryRepository.findActiveById(1L)).willReturn(Optional.of(category));
        given(postMapper.toEntity(postDto)).willReturn(post);
        given(postRepository.save(any(Post.class))).willReturn(post);
        given(postMapper.toDto(post)).willReturn(postDto);

        PostDto result = postService.createPost(postDto);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Post");
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("Should throw exception when category not found during create")
    void shouldThrowExceptionWhenCategoryNotFound() {
        given(categoryRepository.findActiveById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> postService.createPost(postDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found");
    }

    @Test
    @DisplayName("Should return paginated posts")
    void shouldReturnPaginatedPosts() {
        Page<Post> postPage = new PageImpl<>(List.of(post));
        given(postRepository.findAllActive(any(Pageable.class))).willReturn(postPage);
        given(postMapper.toDtoList(anyList())).willReturn(List.of(postDto));

        PostResponse result = postService.getAllPosts(0, 10, "createdAt", "desc");

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should search posts by query")
    void shouldSearchPosts() {
        given(postRepository.searchPosts("test")).willReturn(List.of(post));
        given(postMapper.toDtoList(anyList())).willReturn(List.of(postDto));

        List<PostDto> result = postService.searchPosts("test");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Post");
    }

    @Test
    @DisplayName("Should throw exception for empty search query")
    void shouldThrowExceptionForEmptySearchQuery() {
        assertThatThrownBy(() -> postService.searchPosts("  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Search query must not be empty");
    }
}
