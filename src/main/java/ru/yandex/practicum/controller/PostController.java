package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
 * Спринт 3: Тема 8 «Практика по разработке Spring Framework» (Spring MVC / REST).
 * Реализация п. 9 (проектирование слоёв: Controller) и п. 15 (написание контроллеров).
 * Эндпоинты соответствуют постановке бэкенда:
 * - GET  /api/posts              — получение списка постов с поиском и пагинацией
 * - POST /api/posts/{id}         — получение поста (POST вместо GET — особенность фронтенда)
 * - POST /api/posts              — добавление поста
 * - PUT  /api/posts/{id}         — редактирование поста
 * - DELETE /api/posts/{id}       — удаление поста
 * - POST /api/posts/{id}/likes   — инкремент лайков
 * - PUT  /api/posts/{id}/image   — обновление картинки
 * - GET  /api/posts/{id}/image   — получение картинки
 *
 * Спринт 4: Тема 2 «Spring Boot как развитие Spring Framework» (REST-контроллеры).
 * Реализация постановки спринта 4: контроллер работает на автоконфигурированном
 * Spring Boot Web без ручной Java-конфигурации MVC и web.xml.
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
     * Постановка: GET /api/posts?search=&pageNumber=1&pageSize=10 — все параметры обязательные.
     * П. 9 (слой Controller), п. 15 (написание контроллеров).
     *
     * @param search     строка поиска
     * @param pageNumber номер страницы
     * @param pageSize   число постов на странице
     * @return ответ со списком постов
     */
    @GetMapping
    public PostListResponse getPosts(
            @RequestParam String search, @RequestParam int pageNumber, @RequestParam int pageSize) {
        return postService.getPosts(search, pageNumber, pageSize);
    }

    /**
     * Получить пост по идентификатору.
     * Постановка: POST /api/posts/{id}. Дополнительно поддерживается GET /api/posts/{id}:
     * собранный фронтенд открывает пост GET-запросом, поэтому эндпоинт принимает оба метода.
     *
     * @param id идентификатор поста
     * @return пост
     */
    @RequestMapping(
            value = "/{id}",
            method = {RequestMethod.POST, RequestMethod.GET})
    public PostResponse getPost(@PathVariable long id) {
        return postService.getPost(id);
    }

    /**
     * Добавить новый пост.
     * Постановка: POST /api/posts — фронт присылает title, text, tags.
     *
     * @param request запрос на создание поста
     * @return созданный пост
     */
    @PostMapping
    public PostResponse createPost(@Valid @RequestBody CreatePostRequest request) {
        return postService.createPost(request);
    }

    /**
     * Редактировать пост.
     * Постановка: PUT /api/posts/{id} — фронт присылает id, title, text, tags.
     *
     * @param id      идентификатор поста
     * @param request запрос на обновление поста
     * @return обновлённый пост
     */
    @PutMapping("/{id}")
    public PostResponse updatePost(@PathVariable long id, @Valid @RequestBody UpdatePostRequest request) {
        return postService.updatePost(id, request);
    }

    /**
     * Удалить пост со всеми комментариями.
     * Постановка: DELETE /api/posts/{id} — возвращает 200 OK.
     *
     * @param id идентификатор поста
     */
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable long id) {
        postService.deletePost(id);
    }

    /**
     * Инкремент числа лайков поста на 1.
     * Постановка: POST /api/posts/{id}/likes — возвращает обновлённое число лайков.
     *
     * @param id идентификатор поста
     * @return обновлённое количество лайков
     */
    @PostMapping("/{id}/likes")
    public long addLike(@PathVariable long id) {
        return postService.addLike(id);
    }

    /**
     * Обновить картинку поста.
     * Постановка: PUT /api/posts/{id}/image — фронтенд отправляет multipart/form-data.
     * Content-Disposition: form-data; name="image"; filename="image_name.jpg"
     *
     * @param id    идентификатор поста
     * @param image загружаемый файл изображения
     */
    @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateImage(@PathVariable long id, @RequestParam("image") MultipartFile image)
            throws java.io.IOException {
        postService.updateImage(id, image.getBytes());
    }

    /**
     * Получить картинку поста.
     * Постановка: GET /api/posts/{id}/image — возвращает массив байт.
     *
     * @param id идентификатор поста
     * @return байты изображения
     */
    @GetMapping(value = "/{id}/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImage(@PathVariable long id) {
        return postService.getImage(id);
    }
}