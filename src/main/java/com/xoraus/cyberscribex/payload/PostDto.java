package com.xoraus.cyberscribex.payload;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class PostDto {
    private long id;

    @NotEmpty
    @Size(min = 3,message = "Post title should have at least 3 characters", max = 255)
    private String title;

    @NotEmpty
    @Size(min = 10,message = "Post description should have at least 10 characters", max = 255)
    private String description;

    @NotEmpty
    private String content;

    private Set<CommentDto> comments;

    private Long categoryId;
}
