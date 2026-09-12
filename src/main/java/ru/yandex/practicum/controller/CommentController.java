package ru.yandex.practicum.controller;

import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.CommentResponse;
import ru.yandex.practicum.dto.CreateCommentRequest;
import ru.yandex.practicum.dto.UpdateCommentRequest;
import ru.yandex.practicum.service.CommentService;

/**
 * REST-контроллер для управления комментариями к постам.
 * Все эндпоинты начинаются с /api/posts/{postId}/comments.
 *
 * Реализация п. 9 (проектирование слоёв: Controller) и п. 15 (написание контроллеров).
 * Эндпоинты соответствуют ТЗ бэкенда:
 * - GET    /api/posts/{postId}/comments                — получение всех комментариев к посту
 * - GET    /api/posts/{postId}/comments/{commentId}    — получение комментария
 * - POST   /api/posts/{postId}/comments                — добавление комментария
 * - PUT    /api/posts/{postId}/comments/{commentId}    — редактирование комментария
 * - DELETE /api/posts/{postId}/comments/{commentId}    — удаление комментария
 */
@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Получить все комментарии к посту.
     * ТЗ: GET /api/posts/{id}/comments — возвращает JSON-массив.
     */
    @GetMapping
    public List<CommentResponse> getCommentsByPostId(@PathVariable long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    /**
     * Получить комментарий по идентификаторам поста и комментария.
     * ТЗ: GET /api/posts/{id}/comments/{commentId}
     */
    @GetMapping("/{commentId}")
    public CommentResponse getComment(@PathVariable long postId, @PathVariable long commentId) {
        return commentService.getComment(postId, commentId);
    }

    /**
     * Создать новый комментарий к посту.
     * ТЗ: POST /api/posts/{id}/comments — фронт присылает text и postId.
     */
    @PostMapping
    public CommentResponse createComment(@PathVariable long postId, @RequestBody CreateCommentRequest request) {
        return commentService.createComment(postId, request);
    }

    /**
     * Редактировать комментарий.
     * ТЗ: PUT /api/posts/{id}/comments/{commentId}
     */
    @PutMapping("/{commentId}")
    public CommentResponse updateComment(
            @PathVariable long postId, @PathVariable long commentId, @RequestBody UpdateCommentRequest request) {
        return commentService.updateComment(postId, commentId, request);
    }

    /**
     * Удалить комментарий.
     * ТЗ: DELETE /api/posts/{id}/comments/{commentId} — возвращает 200 OK.
     */
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable long postId, @PathVariable long commentId) {
        commentService.deleteComment(postId, commentId);
    }
}
