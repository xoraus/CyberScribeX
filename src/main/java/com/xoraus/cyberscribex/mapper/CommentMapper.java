package com.xoraus.cyberscribex.mapper;

import com.xoraus.cyberscribex.entity.Comment;
import com.xoraus.cyberscribex.payload.CommentDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentDto toDto(Comment comment);

    Comment toEntity(CommentDto commentDto);

    List<CommentDto> toDtoList(List<Comment> comments);
}
