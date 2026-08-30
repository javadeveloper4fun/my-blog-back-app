package ru.yandex.practicum.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.CreatePostRequest;
import ru.yandex.practicum.dto.PostListResponse;
import ru.yandex.practicum.dto.PostResponse;
import ru.yandex.practicum.dto.UpdatePostRequest;
import ru.yandex.practicum.service.PostService;

/**
 * REST-контроллер для управления постами блога.
 * Все эндпоинты начинаются с /api/posts.
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /** Получить список постов с поиском и пагинацией */
    @GetMapping
    public PostListResponse getPosts(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        return postService.getPosts(search, pageNumber, pageSize);
    }

    /** Получить пост по идентификатору (POST вместо GET по заданию) */
    @PostMapping("/{id}")
    public PostResponse getPost(@PathVariable long id) {
        return postService.getPost(id);
    }

    /** Создать новый пост */
    @PostMapping
    public PostResponse createPost(@RequestBody CreatePostRequest request) {
        return postService.createPost(request);
    }

    /** Обновить существующий пост */
    @PutMapping("/{id}")
    public PostResponse updatePost(@PathVariable long id, @RequestBody UpdatePostRequest request) {
        return postService.updatePost(id, request);
    }

    /** Удалить пост по идентификатору */
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable long id) {
        postService.deletePost(id);
    }

    /** Увеличить количество лайков на 1 */
    @PostMapping("/{id}/like")
    public long addLike(@PathVariable long id) {
        return postService.addLike(id);
    }

    /** Загрузить картинку к посту */
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateImage(@PathVariable long id, @RequestBody byte[] image) {
        postService.updateImage(id, image);
    }

    /** Получить картинку поста */
    @GetMapping(value = "/{id}/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImage(@PathVariable long id) {
        return postService.getImage(id);
    }
}
