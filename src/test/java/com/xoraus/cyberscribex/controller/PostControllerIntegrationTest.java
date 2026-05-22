package com.xoraus.cyberscribex.controller;

import com.xoraus.cyberscribex.entity.Category;
import com.xoraus.cyberscribex.entity.Role;
import com.xoraus.cyberscribex.entity.User;
import com.xoraus.cyberscribex.payload.LoginDto;
import com.xoraus.cyberscribex.repository.CategoryRepository;
import com.xoraus.cyberscribex.repository.RoleRepository;
import com.xoraus.cyberscribex.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Post Controller Integration Tests")
class PostControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private Category category;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        String suffix = String.valueOf(System.currentTimeMillis());

        Role adminRole = roleRepository.save(Role.builder().name("ROLE_ADMIN_" + suffix).build());
        Role userRole = roleRepository.save(Role.builder().name("ROLE_USER_" + suffix).build());

        User admin = User.builder()
                .name("Admin")
                .username("admin_" + suffix)
                .email("admin_" + suffix + "@example.com")
                .password(passwordEncoder.encode("Admin@123"))
                .roles(Set.of(adminRole, userRole))
                .accountNonLocked(true)
                .build();
        userRepository.save(admin);

        category = Category.builder()
                .name("Technology_" + suffix)
                .description("Tech posts")
                .build();
        categoryRepository.save(category);

        adminToken = given()
                .contentType(ContentType.JSON)
                .body(LoginDto.builder().usernameOrEmail("admin_" + suffix).password("Admin@123").build())
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("data.accessToken");
    }

    @Test
    @DisplayName("Should create and retrieve post")
    void shouldCreateAndRetrievePost() {
        String postJson = String.format("""
            {
                "title": "Integration Test Post",
                "description": "A post created during integration testing",
                "content": "This is the full content of the integration test post.",
                "categoryId": %d
            }
            """, category.getId());

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(postJson)
                .when()
                .post("/api/v1/posts")
                .then()
                .statusCode(201)
                .body("success", equalTo(true))
                .body("data.title", equalTo("Integration Test Post"));

        given()
                .when()
                .get("/api/v1/posts")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.content", hasSize(greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("Should return 404 for non-existent post")
    void shouldReturn404ForNonExistentPost() {
        given()
                .when()
                .get("/api/v1/posts/99999")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("error", equalTo("Not Found"));
    }
}
