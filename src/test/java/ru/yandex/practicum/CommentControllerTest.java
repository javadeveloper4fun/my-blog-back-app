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
import ru.yandex.practicum.controller.CommentController;
import ru.yandex.practicum.dto.CommentResponse;
import ru.yandex.practicum.dto.CreateCommentRequest;
import ru.yandex.practicum.exception.ExceptionController;
import ru.yandex.practicum.service.CommentService;

/**
 * Unit-тесты CommentController с использованием MockMvc (standalone).
 * Проверяют REST-эндпоинты комментариев с мокированием CommentService.
 *
 * Спринт 3: Тема 9 «Модульное и интеграционное тестирование в Spring»
 * (JUnit 5 + Mockito + MockMvc standalone).
 * Реализация п. 17 (интеграционные тесты на MVC с WebMvc).
 */
@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    private MockMvc mockMvc;
    private CommentService commentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        commentService = mock(CommentService.class);
        CommentController commentController = new CommentController(commentService);
        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                .setControllerAdvice(new ExceptionController())
                .build();
    }

    @Test
    void getCommentsByPostIdTest() throws Exception {
        CommentResponse comment = new CommentResponse();
        comment.setId(1L);
        comment.setText("comment-text");
        comment.setPostId(1L);

        when(commentService.getCommentsByPostId(1L)).thenReturn(List.of(comment));

        String body = mockMvc.perform(get("/api/posts/1/comments"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(body.contains("\"id\":1"));
        assertTrue(body.contains("\"text\":\"comment-text\""));
        assertTrue(body.contains("\"postId\":1"));
    }

    @Test
    void createCommentTest() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setText("new-comment");
        request.setPostId(1L);

        CommentResponse created = new CommentResponse();
        created.setId(1L);
        created.setText("new-comment");
        created.setPostId(1L);

        when(commentService.createComment(eq(1L), any(CreateCommentRequest.class)))
                .thenReturn(created);

        String body = mockMvc.perform(post("/api/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(body.contains("\"id\":1"));
        assertTrue(body.contains("\"text\":\"new-comment\""));
    }

    @Test
    void deleteCommentTest() throws Exception {
        mockMvc.perform(delete("/api/posts/1/comments/1")).andExpect(status().isOk());
    }
}
