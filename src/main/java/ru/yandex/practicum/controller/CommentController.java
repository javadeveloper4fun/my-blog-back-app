package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
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
 * Спринт 3: Тема 8 «Практика по разработке Spring Framework» (Spring MVC / REST).
 * Реализация п. 9 (проектирование слоёв: Controller) и п. 15 (написание контроллеров).
 * Эндпоинты соответствуют постановке бэкенда:
 * - GET    /api/posts/{id}/comments                — получение всех комментариев к посту
 * - GET    /api/posts/{id}/comments/{commentId}    — получение комментария
 * - POST   /api/posts/{id}/comments                — добавление комментария
 * - PUT    /api/posts/{id}/comments/{commentId}    — редактирование комментария
 * - DELETE /api/posts/{id}/comments/{commentId}    — удаление комментария
 *
 * Спринт 4: Тема 2 «Spring Boot как развитие Spring Framework» (REST-контроллеры).
 * Реализация постановки спринта 4: контроллер работает на автоконфигурированном
 * Spring Boot Web без ручной Java-конфигурации MVC и web.xml.
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
     * Постановка: GET /api/posts/{id}/comments — возвращает JSON-массив.
     * Нечисловой postId (например, "undefined") интерпретируется как пустой список:
     * собранный фронтенд при открытии поста первым запросом обращается к /comments,
     * когда id ещё не загружен, и 500-ответ уронил бы страницу.
     *
     * @param postId идентификатор поста
     * @return список комментариев поста
     */
    @GetMapping
    public List<CommentResponse> getCommentsByPostId(@PathVariable String postId) {
        long id;
        try {
            id = Long.parseLong(postId);
        } catch (NumberFormatException e) {
            return List.of();
        }
        return commentService.getCommentsByPostId(id);
    }

    /**
     * Получить комментарий по идентификаторам поста и комментария.
     * Постановка: GET /api/posts/{id}/comments/{commentId}
     *
     * @param postId    идентификатор поста
     * @param commentId идентификатор комментария
     * @return комментарий
     */
    @GetMapping("/{commentId}")
    public CommentResponse getComment(@PathVariable long postId, @PathVariable long commentId) {
        return commentService.getComment(postId, commentId);
    }

    /**
     * Создать новый комментарий к посту.
     * Постановка: POST /api/posts/{id}/comments — фронт присылает text и postId.
     *
     * @param postId  идентификатор поста
     * @param request запрос на создание комментария
     * @return созданный комментарий
     */
    @PostMapping
    public CommentResponse createComment(@PathVariable long postId, @Valid @RequestBody CreateCommentRequest request) {
        return commentService.createComment(postId, request);
    }

    /**
     * Редактировать комментарий.
     * Постановка: PUT /api/posts/{id}/comments/{commentId}
     *
     * @param postId    идентификатор поста
     * @param commentId идентификатор комментария
     * @param request   запрос на обновление комментария
     * @return обновлённый комментарий
     */
    @PutMapping("/{commentId}")
    public CommentResponse updateComment(
            @PathVariable long postId, @PathVariable long commentId, @Valid @RequestBody UpdateCommentRequest request) {
        return commentService.updateComment(postId, commentId, request);
    }

    /**
     * Удалить комментарий.
     * Постановка: DELETE /api/posts/{id}/comments/{commentId} — возвращает 200 OK.
     *
     * @param postId    идентификатор поста
     * @param commentId идентификатор комментария
     */
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable long postId, @PathVariable long commentId) {
        commentService.deleteComment(postId, commentId);
    }
}