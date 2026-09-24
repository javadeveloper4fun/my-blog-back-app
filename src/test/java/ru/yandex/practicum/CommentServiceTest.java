package ru.yandex.practicum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.CommentResponse;
import ru.yandex.practicum.dto.CreateCommentRequest;
import ru.yandex.practicum.dto.CreatePostRequest;
import ru.yandex.practicum.dto.UpdateCommentRequest;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.service.CommentServiceImpl;
import ru.yandex.practicum.service.PostServiceImpl;

/**
 * Интеграционные тесты бизнес-логики сервиса комментариев с реальной БД H2.
 *
 * Спринт 4: Тема 10 «SpringBootTest для тестирования Spring Boot-приложений» —
 * аннотация SpringBootTest автоматически поднимает контекст,
 * аннотация Transactional изолирует тесты.
 * Реализация постановки спринта 4: тесты слоя сервисов переписаны на Spring Boot Test.
 */
@SpringBootTest
@Transactional
class CommentServiceTest {
    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private PostServiceImpl postService;

    private long createPost() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Пост для комментариев");
        request.setText("Текст поста");
        request.setTags(List.of());
        return postService.createPost(request).getId();
    }

    private long addComment(long postId, String text) {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setText(text);
        request.setPostId(postId);
        return commentService.createComment(postId, request).getId();
    }

    @Test
    void createCommentTest() {
        long postId = createPost();
        long commentId = addComment(postId, "Первый комментарий");

        CommentResponse created = commentService.getComment(postId, commentId);
        assertEquals(commentId, created.getId());
        assertEquals("Первый комментарий", created.getText());
        assertEquals(postId, created.getPostId());
    }

    @Test
    void createCommentForMissingPostThrowsTest() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setText("Комментарий к несуществующему посту");
        request.setPostId(9999L);

        assertThrows(NotFoundException.class, () -> commentService.createComment(9999L, request));
    }

    @Test
    void getCommentsByPostIdReturnsCommentsInOrderTest() {
        long postId = createPost();
        long firstId = addComment(postId, "Первый");
        long secondId = addComment(postId, "Второй");

        List<CommentResponse> comments = commentService.getCommentsByPostId(postId);
        assertEquals(2, comments.size());
        assertEquals(firstId, comments.get(0).getId());
        assertEquals(secondId, comments.get(1).getId());
    }

    @Test
    void getCommentsByPostIdEmptyTest() {
        long postId = createPost();
        assertTrue(commentService.getCommentsByPostId(postId).isEmpty());
    }

    @Test
    void getCommentForMissingCommentThrowsTest() {
        long postId = createPost();
        assertThrows(NotFoundException.class, () -> commentService.getComment(postId, 9999L));
    }

    @Test
    void updateCommentTest() {
        long postId = createPost();
        long commentId = addComment(postId, "Старый текст");

        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setId(commentId);
        request.setText("Новый текст");
        request.setPostId(postId);

        CommentResponse updated = commentService.updateComment(postId, commentId, request);
        assertEquals("Новый текст", updated.getText());
        assertEquals(commentId, updated.getId());
        assertEquals(postId, updated.getPostId());
    }

    @Test
    void updateCommentForMissingCommentThrowsTest() {
        long postId = createPost();

        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setId(9999L);
        request.setText("Новый текст");
        request.setPostId(postId);

        assertThrows(NotFoundException.class, () -> commentService.updateComment(postId, 9999L, request));
    }

    @Test
    void deleteCommentTest() {
        long postId = createPost();
        long commentId = addComment(postId, "Удаляемый комментарий");

        commentService.deleteComment(postId, commentId);
        assertTrue(commentService.getCommentsByPostId(postId).isEmpty());
        assertThrows(NotFoundException.class, () -> commentService.getComment(postId, commentId));
    }
}