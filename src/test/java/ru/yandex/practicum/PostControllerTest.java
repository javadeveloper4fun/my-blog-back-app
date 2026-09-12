package ru.yandex.practicum;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.controller.PostController;
import ru.yandex.practicum.dto.CreatePostRequest;
import ru.yandex.practicum.dto.PostListResponse;
import ru.yandex.practicum.dto.PostResponse;
import ru.yandex.practicum.exception.ExceptionController;
import ru.yandex.practicum.service.PostService;

/**
 * Unit-тесты PostController с использованием MockMvc (standalone).
 * Проверяют REST-эндпоинты постов с мокированием PostService.
 *
 * Спринт 3: Тема 9 «Модульное и интеграционное тестирование в Spring»
 * (JUnit 5 + Mockito + MockMvc standalone).
 * Реализация п. 17 (интеграционные тесты на MVC с WebMvc).
 */
@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    private MockMvc mockMvc;
    private PostService postService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        postService = mock(PostService.class);
        PostController postController = new PostController(postService);
        mockMvc = MockMvcBuilders.standaloneSetup(postController)
                .setControllerAdvice(new ExceptionController())
                .build();
    }

    @Test
    void getPostsTest() throws Exception {
        PostListResponse response = new PostListResponse();
        response.setPosts(List.of());
        response.setHasPrev(false);
        response.setHasNext(false);
        response.setLastPage(1);

        when(postService.getPosts(anyString(), anyInt(), anyInt())).thenReturn(response);

        String body = mockMvc.perform(get("/api/posts")
                        .param("search", "")
                        .param("pageNumber", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(body.contains("\"lastPage\":1"));
        assertTrue(body.contains("\"hasPrev\":false"));
        assertTrue(body.contains("\"hasNext\":false"));
    }

    @Test
    void getPostByIdTest() throws Exception {
        PostResponse post = new PostResponse();
        post.setId(1L);
        post.setTitle("title");
        post.setText("text");
        post.setTags(List.of("tag"));
        post.setLikesCount(0L);
        post.setCommentsCount(0L);

        when(postService.getPost(1L)).thenReturn(post);

        String body = mockMvc.perform(post("/api/posts/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(body.contains("\"id\":1"));
        assertTrue(body.contains("\"title\":\"title\""));
        assertTrue(body.contains("\"likesCount\":0"));
    }

    @Test
    void createPostTest() throws Exception {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("new-post");
        request.setText("new-text");
        request.setTags(List.of("tag1"));

        PostResponse created = new PostResponse();
        created.setId(1L);
        created.setTitle("new-post");
        created.setText("new-text");
        created.setTags(List.of("tag1"));
        created.setLikesCount(0L);
        created.setCommentsCount(0L);

        when(postService.createPost(any(CreatePostRequest.class))).thenReturn(created);

        String body = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(body.contains("\"id\":1"));
        assertTrue(body.contains("\"title\":\"new-post\""));
    }

    @Test
    void deletePostTest() throws Exception {
        mockMvc.perform(delete("/api/posts/1")).andExpect(status().isOk());
    }
}
