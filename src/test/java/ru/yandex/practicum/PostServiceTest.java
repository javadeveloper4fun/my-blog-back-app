package ru.yandex.practicum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.config.DataConfig;
import ru.yandex.practicum.dao.CommentDaoImpl;
import ru.yandex.practicum.dao.PostDaoImpl;
import ru.yandex.practicum.dto.CreatePostRequest;
import ru.yandex.practicum.dto.PostListResponse;
import ru.yandex.practicum.exception.InvalidRequestException;
import ru.yandex.practicum.service.CommentServiceImpl;
import ru.yandex.practicum.service.PostServiceImpl;

/**
 * Интеграционные тесты бизнес-логики сервисов с реальной БД H2.
 *
 * Спринт 3: Тема 10 «TestContext Framework» (@SpringJUnitConfig + @Transactional)
 * и Тема 11 «Практика по тестированию Spring-приложений».
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
class PostServiceTest {

    @Autowired
    private PostServiceImpl postService;

    private long createPost(String title) {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle(title);
        request.setText("Текст поста");
        request.setTags(java.util.List.of());
        return postService.createPost(request).getId();
    }

    @Test
    void paginationMetadataTest() {
        createPost("Пост 1");
        createPost("Пост 2");
        createPost("Пост 3");

        PostListResponse firstPage = postService.getPosts("", 1, 2);
        assertEquals(2, firstPage.getPosts().size());
        assertEquals(false, firstPage.isHasPrev());
        assertEquals(true, firstPage.isHasNext());
        assertEquals(2, firstPage.getLastPage());

        PostListResponse secondPage = postService.getPosts("", 2, 2);
        assertEquals(1, secondPage.getPosts().size());
        assertEquals(true, secondPage.isHasPrev());
        assertEquals(false, secondPage.isHasNext());
        assertEquals(2, secondPage.getLastPage());
    }

    @Test
    void emptyPageMetadataTest() {
        PostListResponse response = postService.getPosts("", 1, 10);
        assertEquals(0, response.getPosts().size());
        assertEquals(false, response.isHasPrev());
        assertEquals(false, response.isHasNext());
        assertEquals(1, response.getLastPage());
    }

    @Test
    void pageNumberLessThanOneThrowsTest() {
        assertThrows(InvalidRequestException.class, () -> postService.getPosts("", 0, 10));
        assertThrows(InvalidRequestException.class, () -> postService.getPosts("", -1, 10));
    }

    @Test
    void pageSizeLessThanOneThrowsTest() {
        assertThrows(InvalidRequestException.class, () -> postService.getPosts("", 1, 0));
        assertThrows(InvalidRequestException.class, () -> postService.getPosts("", 1, -5));
    }

    @Test
    void pageSizeExceedsMaximumThrowsTest() {
        assertThrows(InvalidRequestException.class, () -> postService.getPosts("", 1, 101));
    }
}