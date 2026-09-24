package ru.yandex.practicum.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dao.CommentDao;
import ru.yandex.practicum.dao.PostDao;
import ru.yandex.practicum.dto.CommentResponse;
import ru.yandex.practicum.dto.CreateCommentRequest;
import ru.yandex.practicum.dto.UpdateCommentRequest;
import ru.yandex.practicum.model.Comment;

/**
 * Реализация сервиса комментариев.
 * Содержит бизнес-логику: конвертация DTO <-> модель, обработка ошибок.
 *
 * Спринт 3: Тема 2 «Spring как IoC-контейнер» (DI через конструктор)
 * и Тема 4 «Создание бинов через Java-аннотации» (@Service).
 * Реализация п. 9 (проектирование слоёв: Service) и п. 15 (написание сервисов).
 *
 * Спринт 4: Тема 7 «Автоконфигурации и стартеры» — @Service-бин
 * регистрируется через компонентное сканирование Spring Boot.
 * Реализация постановки спринта 4: слой сервисов переписан без изменений логики.
 */
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentDao commentDao;
    private final PostDao postDao;

    public CommentServiceImpl(CommentDao commentDao, PostDao postDao) {
        this.commentDao = commentDao;
        this.postDao = postDao;
    }

    @Override
    public List<CommentResponse> getCommentsByPostId(long postId) {
        List<Comment> comments = commentDao.findByPostId(postId);
        return comments.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public CommentResponse getComment(long postId, long commentId) {
        Comment comment = commentDao.findById(postId, commentId);
        return toResponse(comment);
    }

    @Override
    public CommentResponse createComment(long postId, CreateCommentRequest request) {
        postDao.findById(postId);
        Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setPostId(postId);
        Comment saved = commentDao.save(comment);
        return toResponse(saved);
    }

    @Override
    public CommentResponse updateComment(long postId, long commentId, UpdateCommentRequest request) {
        Comment comment = commentDao.findById(postId, commentId);
        comment.setText(request.getText());
        Comment updated = commentDao.update(comment);
        return toResponse(updated);
    }

    @Override
    public void deleteComment(long postId, long commentId) {
        commentDao.deleteById(postId, commentId);
    }

    private CommentResponse toResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setText(comment.getText());
        response.setPostId(comment.getPostId());
        return response;
    }
}