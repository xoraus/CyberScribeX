package com.xoraus.cyberscribex.mapper;

import com.xoraus.cyberscribex.entity.Post;
import com.xoraus.cyberscribex.payload.PostDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(source = "category.id", target = "categoryId")
    PostDto toDto(Post post);

    @Mapping(source = "categoryId", target = "category.id")
    Post toEntity(PostDto postDto);

    List<PostDto> toDtoList(List<Post> posts);
}
