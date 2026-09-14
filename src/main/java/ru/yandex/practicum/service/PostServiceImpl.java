package ru.yandex.practicum.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dao.PostDao;
import ru.yandex.practicum.dto.CreatePostRequest;
import ru.yandex.practicum.dto.PostListResponse;
import ru.yandex.practicum.dto.PostResponse;
import ru.yandex.practicum.dto.UpdatePostRequest;
import ru.yandex.practicum.exception.InvalidRequestException;
import ru.yandex.practicum.model.Post;

/**
 * Реализация сервиса постов.
 * Содержит бизнес-логику: конвертация DTO <-> модель, обработка ошибок.
 *
 * Спринт 3: Тема 2 «Spring как IoC-контейнер» (DI через конструктор)
 * и Тема 4 «Создание бинов через Java-аннотации» (@Service).
 * Реализация п. 9 (проектирование слоёв: Service) и п. 15 (написание сервисов).
 * Улучшение: бизнес-логика конвертации тегов (String <-> List) вынесена в сервис.
 */
@Service
public class PostServiceImpl implements PostService {

    private final PostDao postDao;

    /**
     * Максимально допустимый размер страницы при пагинации.
     */
    private static final int MAX_PAGE_SIZE = 100;

    public PostServiceImpl(PostDao postDao) {
        this.postDao = postDao;
    }

    @Override
    public PostListResponse getPosts(String search, int pageNumber, int pageSize) {
        validatePagination(pageNumber, pageSize);
        return postDao.findAll(search, pageNumber, pageSize);
    }

    @Override
    public PostResponse getPost(long id) {
        Post post = postDao.findById(id);
        return toResponse(post);
    }

    @Override
    public PostResponse createPost(CreatePostRequest request) {
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setText(request.getText());
        post.setTags(convertTagsToString(request.getTags()));
        post.setLikesCount(0L);
        Post saved = postDao.save(post);
        return toResponse(saved);
    }

    @Override
    public PostResponse updatePost(long id, UpdatePostRequest request) {
        Post post = postDao.findById(id);
        post.setTitle(request.getTitle());
        post.setText(request.getText());
        post.setTags(convertTagsToString(request.getTags()));
        Post updated = postDao.update(post);
        return toResponse(updated);
    }

    @Override
    public void deletePost(long id) {
        postDao.deleteById(id);
    }

    @Override
    public long addLike(long id) {
        return postDao.incrementLikes(id);
    }

    @Override
    public void updateImage(long id, byte[] image) {
        postDao.updateImage(id, image);
    }

    @Override
    public byte[] getImage(long id) {
        return postDao.getImage(id);
    }

    private void validatePagination(int pageNumber, int pageSize) {
        if (pageNumber < 1) {
            throw new InvalidRequestException("pageNumber должен быть не меньше 1");
        }
        if (pageSize < 1) {
            throw new InvalidRequestException("pageSize должен быть не меньше 1");
        }
        if (pageSize > MAX_PAGE_SIZE) {
            throw new InvalidRequestException("pageSize не должен превышать " + MAX_PAGE_SIZE);
        }
    }

    private PostResponse toResponse(Post post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setText(post.getText());
        response.setTags(convertStringToTags(post.getTags()));
        response.setLikesCount(post.getLikesCount());
        response.setCommentsCount(post.getCommentsCount() != null ? post.getCommentsCount() : 0L);
        return response;
    }

    private String convertTagsToString(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "";
        }
        return String.join(",", tags);
    }

    private List<String> convertStringToTags(String tagsStr) {
        if (tagsStr == null || tagsStr.isEmpty()) {
            return new ArrayList<>();
        }
        return List.of(tagsStr.split(","));
    }
}
