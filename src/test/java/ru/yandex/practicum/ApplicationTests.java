package ru.yandex.practicum;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.config.DataConfig;
import ru.yandex.practicum.dao.CommentDaoImpl;
import ru.yandex.practicum.dao.PostDaoImpl;
import ru.yandex.practicum.dto.CommentResponse;
import ru.yandex.practicum.dto.CreateCommentRequest;
import ru.yandex.practicum.dto.CreatePostRequest;
import ru.yandex.practicum.dto.PostResponse;
import ru.yandex.practicum.dto.UpdateCommentRequest;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.service.CommentServiceImpl;
import ru.yandex.practicum.service.PostServiceImpl;

/**
 * Интеграционные тесты сервисов и DAO.
 * Проверяют корректность работы бизнес-логики с реальной БД H2.
 *
 * Реализация п. 16 (тесты на слой сервисов с Spring Test Framework)
 * и п. 17 (интеграционные тесты на DAO с Embedded In-Memory H2).
 */
@SpringJUnitConfig(
        classes = {
            DataConfig.class,
            PostServiceImpl.class,
            CommentServiceImpl.class,
            PostDaoImpl.class,
            CommentDaoImpl.class,
            IntegrationTestConfig.class
        })
@Transactional
class ApplicationTests {

    @Autowired
    private PostServiceImpl postService;

    @Autowired
    private CommentServiceImpl commentService;

    @Test
    void contextLoads() {
        assertNotNull(postService);
        assertNotNull(commentService);
    }

    @Test
    void createAndFindPostTest() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Тестовый пост");
        request.setText("Текст тестового поста");
        request.setTags(List.of("тест", "java"));

        PostResponse created = postService.createPost(request);
        assertNotNull(created.getId());
        assertEquals("Тестовый пост", created.getTitle());

        PostResponse found = postService.getPost(created.getId());
        assertEquals(created.getId(), found.getId());
        assertEquals("Тестовый пост", found.getTitle());
    }

    @Test
    void deletePostTest() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Пост для удаления");
        request.setText("Текст");
        request.setTags(List.of());

        PostResponse created = postService.createPost(request);
        long id = created.getId();

        postService.deletePost(id);

        assertThrows(NotFoundException.class, () -> postService.getPost(id));
    }

    @Test
    void addLikeTest() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Пост с лайком");
        request.setText("Текст");
        request.setTags(List.of());

        PostResponse created = postService.createPost(request);
        assertEquals(0L, created.getLikesCount());

        long newCount = postService.addLike(created.getId());
        assertEquals(1L, newCount);
    }

    @Test
    void commentCrudTest() {
        CreatePostRequest postRequest = new CreatePostRequest();
        postRequest.setTitle("Пост для комментариев");
        postRequest.setText("Текст");
        postRequest.setTags(List.of());
        long postId = postService.createPost(postRequest).getId();

        CreateCommentRequest request = new CreateCommentRequest();
        request.setText("Первый комментарий");
        request.setPostId(postId);

        CommentResponse created = commentService.createComment(postId, request);
        assertNotNull(created.getId());
        assertEquals("Первый комментарий", created.getText());
        assertEquals(postId, created.getPostId());

        CommentResponse found = commentService.getComment(postId, created.getId());
        assertEquals("Первый комментарий", found.getText());

        assertEquals(1, commentService.getCommentsByPostId(postId).size());

        UpdateCommentRequest updateRequest = new UpdateCommentRequest();
        updateRequest.setId(created.getId());
        updateRequest.setText("Отредактированный комментарий");
        updateRequest.setPostId(postId);

        CommentResponse updated = commentService.updateComment(postId, created.getId(), updateRequest);
        assertEquals("Отредактированный комментарий", updated.getText());

        commentService.deleteComment(postId, created.getId());
        assertEquals(0, commentService.getCommentsByPostId(postId).size());
    }
}
