package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.dao.PostDao;
import ru.yandex.practicum.dto.CreatePostRequest;
import ru.yandex.practicum.dto.PostListResponse;
import ru.yandex.practicum.dto.PostResponse;
import ru.yandex.practicum.dto.UpdatePostRequest;
import ru.yandex.practicum.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса постов.
 * Содержит бизнес-логику: конвертация DTO ↔ модель, обработка ошибок.
 */
@Service
public class PostServiceImpl implements PostService {

    private final PostDao postDao;

    public PostServiceImpl(PostDao postDao) {
        this.postDao = postDao;
    }

    @Override
    public PostListResponse getPosts(String search, int pageNumber, int pageSize) {
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

    private PostResponse toResponse(Post post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setText(post.getText());
        response.setTags(convertStringToTags(post.getTags()));
        response.setLikesCount(post.getLikesCount());
        response.setCommentsCount(0L);
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
