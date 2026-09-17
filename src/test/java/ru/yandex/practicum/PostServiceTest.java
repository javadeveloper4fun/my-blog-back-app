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
import ru.yandex.practicum.dto.CreateCommentRequest;
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

    @Autowired
    private CommentServiceImpl commentService;

    private long createPost(String title) {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle(title);
        request.setText("Текст поста");
        request.setTags(java.util.List.of());
        return postService.createPost(request).getId();
    }

    private long addComment(long postId, String text) {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setText(text);
        request.setPostId(postId);
        return commentService.createComment(postId, request).getId();
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

    @Test
    void truncateLongTextInListTest() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Пост с длинным текстом");
        request.setText("а".repeat(200));
        request.setTags(java.util.List.of());
        postService.createPost(request);

        PostListResponse response = postService.getPosts("", 1, 10);

        assertEquals(1, response.getPosts().size());
        assertEquals(129, response.getPosts().get(0).getText().length());
        assertEquals(true, response.getPosts().get(0).getText().startsWith("а".repeat(128)));
        assertEquals(true, response.getPosts().get(0).getText().endsWith("…"));
    }

    @Test
    void fullTextNotTruncatedForSinglePostTest() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Пост с длинным текстом");
        request.setText("а".repeat(200));
        request.setTags(java.util.List.of());
        postService.createPost(request);

        PostListResponse list = postService.getPosts("", 1, 10);
        assertEquals(129, list.getPosts().get(0).getText().length());

        assertEquals(
                200,
                postService.getPost(list.getPosts().get(0).getId()).getText().length());
    }

    @Test
    void tagsAreNormalizedOnCreateTest() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Пост с тегами");
        request.setText("Текст");
        request.setTags(java.util.List.of("  Java ", "java", "Spring", " ", "SQL"));

        var created = postService.createPost(request);

        assertEquals(java.util.List.of("java", "spring", "sql"), created.getTags());
    }

    @Test
    void nullTagsBecomeEmptyListTest() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Пост без тегов");
        request.setText("Текст");
        request.setTags(null);

        var created = postService.createPost(request);

        assertEquals(java.util.List.of(), created.getTags());
    }

    @Test
    void tagWithCommaThrowsTest() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Пост с невалидным тегом");
        request.setText("Текст");
        request.setTags(java.util.List.of("java,backend"));

        assertThrows(InvalidRequestException.class, () -> postService.createPost(request));
    }
}
