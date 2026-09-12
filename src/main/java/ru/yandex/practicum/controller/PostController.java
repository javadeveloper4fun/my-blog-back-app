package ru.yandex.practicum.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.dto.CreatePostRequest;
import ru.yandex.practicum.dto.PostListResponse;
import ru.yandex.practicum.dto.PostResponse;
import ru.yandex.practicum.dto.UpdatePostRequest;
import ru.yandex.practicum.service.PostService;

/**
 * REST-контроллер для управления постами блога.
 * Все эндпоинты начинаются с /api/posts.
 *
 * Реализация п. 9 (проектирование слоёв: Controller) и п. 15 (написание контроллеров).
 * Эндпоинты соответствуют ТЗ бэкенда:
 * - GET  /api/posts              — получение списка постов с поиском и пагинацией
 * - POST /api/posts/{id}         — получение поста (POST вместо GET — особенность фронтенда)
 * - POST /api/posts              — добавление поста
 * - PUT  /api/posts/{id}         — редактирование поста
 * - DELETE /api/posts/{id}       — удаление поста
 * - POST /api/posts/{id}/likes   — инкремент лайков
 * - PUT  /api/posts/{id}/image   — обновление картинки
 * - GET  /api/posts/{id}/image   — получение картинки
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * Получить список постов с поиском и пагинацией.
     * ТЗ: GET /api/posts?search=&pageNumber=1&pageSize=10
     * П. 9 (слой Controller), п. 15 (написание контроллеров).
     */
    @GetMapping
    public PostListResponse getPosts(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        return postService.getPosts(search, pageNumber, pageSize);
    }

    /**
     * Получить пост по идентификатору.
     * ТЗ: POST /api/posts/{id} (POST вместо GET — особенность фронтенда).
     */
    @PostMapping("/{id}")
    public PostResponse getPost(@PathVariable long id) {
        return postService.getPost(id);
    }

    /**
     * Добавить новый пост.
     * ТЗ: POST /api/posts — фронт присылает title, text, tags.
     */
    @PostMapping
    public PostResponse createPost(@RequestBody CreatePostRequest request) {
        return postService.createPost(request);
    }

    /**
     * Редактировать пост.
     * ТЗ: PUT /api/posts/{id} — фронт присылает id, title, text, tags.
     */
    @PutMapping("/{id}")
    public PostResponse updatePost(@PathVariable long id, @RequestBody UpdatePostRequest request) {
        return postService.updatePost(id, request);
    }

    /**
     * Удалить пост со всеми комментариями.
     * ТЗ: DELETE /api/posts/{id} — возвращает 200 OK.
     */
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable long id) {
        postService.deletePost(id);
    }

    /**
     * Инкремент числа лайков поста на 1.
     * ТЗ: POST /api/posts/{id}/likes — возвращает обновлённое число лайков.
     */
    @PostMapping("/{id}/likes")
    public long addLike(@PathVariable long id) {
        return postService.addLike(id);
    }

    /**
     * Обновить картинку поста.
     * ТЗ: PUT /api/posts/{id}/image — фронтенд отправляет multipart/form-data.
     * Content-Disposition: form-data; name="image"; filename="image_name.jpg"
     */
    @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateImage(@PathVariable long id, @RequestParam("image") MultipartFile image) throws java.io.IOException {
        postService.updateImage(id, image.getBytes());
    }

    /**
     * Получить картинку поста.
     * ТЗ: GET /api/posts/{id}/image — возвращает массив байт.
     */
    @GetMapping(value = "/{id}/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImage(@PathVariable long id) {
        return postService.getImage(id);
    }
}
