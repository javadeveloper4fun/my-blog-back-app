package ru.yandex.practicum;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.config.DataConfig;
import ru.yandex.practicum.dao.CommentDao;
import ru.yandex.practicum.dao.CommentDaoImpl;
import ru.yandex.practicum.dao.PostDao;
import ru.yandex.practicum.dao.PostDaoImpl;
import ru.yandex.practicum.dto.PostListResponse;
import ru.yandex.practicum.dto.PostResponse;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционные тесты слоя DAO постов с реальной БД H2.
 * Проверяют корректность SQL-запросов, поиска, пагинации и подсчёта комментариев.
 *
 * Реализация п. 17 (интеграционные тесты на DAO с Embedded In-Memory H2).
 */
@SpringJUnitConfig(classes = {DataConfig.class, PostDaoImpl.class, CommentDaoImpl.class,
        IntegrationTestConfig.class})
@Transactional
class PostDaoTest {

    @Autowired
    private PostDao postDao;

    @Autowired
    private CommentDao commentDao;

    private long createPost(String title, String text, String tags) {
        Post post = new Post();
        post.setTitle(title);
        post.setText(text);
        post.setTags(tags);
        post.setLikesCount(0L);
        return postDao.save(post).getId();
    }

    @Test
    void saveAndFindByIdTest() {
        long id = createPost("Первый пост", "Текст первого поста", "java,spring");

        Post found = postDao.findById(id);

        assertEquals(id, found.getId());
        assertEquals("Первый пост", found.getTitle());
        assertEquals("Текст первого поста", found.getText());
        assertEquals("java,spring", found.getTags());
        assertEquals(0L, found.getLikesCount());
        assertEquals(0L, found.getCommentsCount());
    }

    @Test
    void findAllEmptyListTest() {
        PostListResponse response = postDao.findAll("", 1, 10);

        assertNotNull(response);
        assertTrue(response.getPosts().isEmpty());
        assertFalse(response.isHasPrev());
        assertFalse(response.isHasNext());
        assertEquals(1, response.getLastPage());
    }

    @Test
    void findAllWithPaginationTest() {
        createPost("Пост 1", "Текст 1", "");
        createPost("Пост 2", "Текст 2", "");
        createPost("Пост 3", "Текст 3", "");

        PostListResponse firstPage = postDao.findAll("", 1, 2);
        assertEquals(2, firstPage.getPosts().size());
        assertFalse(firstPage.isHasPrev());
        assertTrue(firstPage.isHasNext());
        assertEquals(2, firstPage.getLastPage());

        PostListResponse secondPage = postDao.findAll("", 2, 2);
        assertEquals(1, secondPage.getPosts().size());
        assertTrue(secondPage.isHasPrev());
        assertFalse(secondPage.isHasNext());
    }

    @Test
    void findAllSortedByDescTest() {
        long first = createPost("Ранний пост", "Текст", "");
        long second = createPost("Поздний пост", "Текст", "");

        PostListResponse response = postDao.findAll("", 1, 10);

        assertEquals(second, response.getPosts().get(0).getId());
        assertEquals(first, response.getPosts().get(1).getId());
    }

    @Test
    void searchBySubstringTest() {
        createPost("Как варить Кофе", "Рецепт", "");
        createPost("Погода в городе", "Прогноз", "");
        createPost("Кофе с молоком", "Рецепт", "");

        PostListResponse response = postDao.findAll("Кофе", 1, 10);

        assertEquals(2, response.getPosts().size());
        assertTrue(response.getPosts().stream().anyMatch(p -> p.getTitle().equals("Как варить Кофе")));
        assertTrue(response.getPosts().stream().anyMatch(p -> p.getTitle().equals("Кофе с молоком")));
    }

    @Test
    void searchByTagTest() {
        createPost("Пост по java", "Текст", "java,backend");
        createPost("Пост по весне", "Текст", "spring");

        PostListResponse response = postDao.findAll("#java", 1, 10);

        assertEquals(1, response.getPosts().size());
        assertEquals("Пост по java", response.getPosts().get(0).getTitle());
    }

    @Test
    void searchByTagAndSubstringTest() {
        createPost("Урок по Spring", "Текст", "java");
        createPost("Урок по Java", "Текст", "java");
        createPost("Другой урок", "Текст", "spring");

        PostListResponse response = postDao.findAll("Урок #java", 1, 10);

        assertEquals(2, response.getPosts().size());
    }

    @Test
    void truncateLongTextInListTest() {
        String longText = "а".repeat(200);
        createPost("Пост с длинным текстом", longText, "");

        PostListResponse response = postDao.findAll("", 1, 10);

        assertEquals(1, response.getPosts().size());
        assertEquals(129, response.getPosts().get(0).getText().length());
        assertTrue(response.getPosts().get(0).getText().startsWith("а".repeat(128)));
        assertTrue(response.getPosts().get(0).getText().endsWith("…"));
    }

    @Test
    void updatePostTest() {
        long id = createPost("Старое название", "Старый текст", "java");

        Post post = postDao.findById(id);
        post.setTitle("Новое название");
        post.setText("Новый текст");
        post.setTags("spring");

        Post updated = postDao.update(post);

        assertEquals("Новое название", updated.getTitle());
        assertEquals("Новый текст", updated.getText());
        assertEquals("spring", updated.getTags());
    }

    @Test
    void deleteByIdTest() {
        long id = createPost("Пост на удаление", "Текст", "");

        postDao.deleteById(id);

        assertThrows(NotFoundException.class, () -> postDao.findById(id));
    }

    @Test
    void incrementLikesTest() {
        long id = createPost("Пост с лайком", "Текст", "");

        assertEquals(1L, postDao.incrementLikes(id));
        assertEquals(2L, postDao.incrementLikes(id));
        assertEquals(2L, postDao.findById(id).getLikesCount());
    }

    @Test
    void commentsCountInListTest() {
        long id = createPost("Пост", "Текст", "");

        Comment c1 = new Comment();
        c1.setText("Первый");
        c1.setPostId(id);
        commentDao.save(c1);

        Comment c2 = new Comment();
        c2.setText("Второй");
        c2.setPostId(id);
        commentDao.save(c2);

        Post found = postDao.findById(id);
        assertEquals(2L, found.getCommentsCount());

        PostListResponse response = postDao.findAll("", 1, 10);
        PostResponse listed = response.getPosts().get(0);
        assertEquals(2L, listed.getCommentsCount());
    }

    @Test
    void updateImageAndGetImageTest() {
        long id = createPost("Пост с картинкой", "Текст", "");
        byte[] image = new byte[]{1, 2, 3, 4};

        postDao.updateImage(id, image);

        assertArrayEquals(image, postDao.getImage(id));
    }

    @Test
    void findByIdForMissingPostThrowsTest() {
        assertThrows(NotFoundException.class, () -> postDao.findById(9999L));
    }

    @Test
    void getImageForMissingPostThrowsTest() {
        assertThrows(NotFoundException.class, () -> postDao.getImage(9999L));
    }
}