package ru.yandex.practicum.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.CommentResponse;
import ru.yandex.practicum.dto.CreateCommentRequest;
import ru.yandex.practicum.dto.UpdateCommentRequest;
import ru.yandex.practicum.service.CommentService;

import java.util.List;

/**
 * REST-контроллер для управления комментариями к постам.
 * Все эндпоинты начинаются с /api/posts/{id}/comments.
 */
@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /** Получить все комментарии к посту */
    @GetMapping
    public List<CommentResponse> getCommentsByPostId(@PathVariable long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    /** Получить комментарий по идентификаторам поста и комментария */
    @GetMapping("/{commentId}")
    public CommentResponse getComment(@PathVariable long postId, @PathVariable long commentId) {
        return commentService.getComment(postId, commentId);
    }

    /** Создать новый комментарий к посту */
    @PostMapping
    public CommentResponse createComment(@PathVariable long postId, @RequestBody CreateCommentRequest request) {
        return commentService.createComment(postId, request);
    }

    /** Обновить существующий комментарий */
    @PutMapping("/{commentId}")
    public CommentResponse updateComment(
            @PathVariable long postId,
            @PathVariable long commentId,
            @RequestBody UpdateCommentRequest request) {
        return commentService.updateComment(postId, commentId, request);
    }

    /** Удалить комментарий по идентификаторам поста и комментария */
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable long postId, @PathVariable long commentId) {
        commentService.deleteComment(postId, commentId);
    }
}
