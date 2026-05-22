package com.xoraus.cyberscribex.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Blog Post Data Transfer Object")
public class PostDto {

    @Schema(description = "Unique identifier of the post", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Title of the blog post", example = "Getting Started with Spring Boot", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @Schema(description = "Short description of the post", example = "A comprehensive guide to Spring Boot", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @Schema(description = "Full content of the post", example = "Spring Boot makes it easy to create stand-alone...", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Content is required")
    private String content;

    @Schema(description = "Category ID associated with the post", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @Schema(description = "Comments associated with the post", accessMode = Schema.AccessMode.READ_ONLY)
    private Set<CommentDto> comments;

    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
