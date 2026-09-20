package ru.yandex.practicum;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.config.DataConfig;
import ru.yandex.practicum.dao.CommentDao;
import ru.yandex.practicum.dao.CommentDaoImpl;
import ru.yandex.practicum.dao.PostDao;
import ru.yandex.practicum.dao.PostDaoImpl;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;

/**
 * Интеграционные тесты слоя DAO постов с реальной БД H2.
 * Проверяют корректность SQL-запросов, поиска, пагинации и подсчёта комментариев.
 *
 * Спринт 3: Тема 10 «TestContext Framework» (@SpringJUnitConfig + @Transactional)
 * и Тема 11 «Практика по тестированию Spring-приложений».
 * Реализация п. 17 (интеграционные тесты на DAO с Embedded In-Memory H2).
 */
@SpringJUnitConfig(classes = {DataConfig.class, PostDaoImpl.class, CommentDaoImpl.class, IntegrationTestConfig.class})
@Transactional
class PostDaoTest {

    @Autowired
    private PostDao postDao;

    @Autowired
    private CommentDao commentDao;

    private long createPost(String title, String text, List<String> tags) {
        Post post = new Post();
        post.setTitle(title);
        post.setText(text);
        post.setTags(tags);
        post.setLikesCount(0L);
        return postDao.save(post).getId();
    }

    @Test
    void saveAndFindByIdTest() {
        long id = createPost("Первый пост", "Текст первого поста", List.of("java", "spring"));

        Post found = postDao.findById(id);

        assertEquals(id, found.getId());
        assertEquals("Первый пост", found.getTitle());
        assertEquals("Текст первого поста", found.getText());
        assertEquals(List.of("java", "spring"), found.getTags());
        assertEquals(0L, found.getLikesCount());
        assertEquals(0L, found.getCommentsCount());
    }

    @Test
    void findAllEmptyListTest() {
        List<Post> posts = postDao.findAll("", 1, 10);

        assertNotNull(posts);
        assertTrue(posts.isEmpty());
        assertEquals(0L, postDao.countPosts(""));
    }

    @Test
    void findAllWithPaginationTest() {
        createPost("Пост 1", "Текст 1", List.of());
        createPost("Пост 2", "Текст 2", List.of());
        createPost("Пост 3", "Текст 3", List.of());

        List<Post> firstPage = postDao.findAll("", 1, 2);
        assertEquals(2, firstPage.size());
        assertEquals(3L, postDao.countPosts(""));

        List<Post> secondPage = postDao.findAll("", 2, 2);
        assertEquals(1, secondPage.size());
    }

    @Test
    void findAllSortedByDescTest() {
        long first = createPost("Ранний пост", "Текст", List.of());
        long second = createPost("Поздний пост", "Текст", List.of());

        List<Post> posts = postDao.findAll("", 1, 10);

        assertEquals(second, posts.get(0).getId());
        assertEquals(first, posts.get(1).getId());
    }

    @Test
    void searchBySubstringTest() {
        createPost("Как варить Кофе", "Рецепт", List.of());
        createPost("Погода в городе", "Прогноз", List.of());
        createPost("Кофе с молоком", "Рецепт", List.of());

        List<Post> posts = postDao.findAll("Кофе", 1, 10);

        assertEquals(2, posts.size());
        assertEquals(2L, postDao.countPosts("Кофе"));
        assertTrue(posts.stream().anyMatch(p -> p.getTitle().equals("Как варить Кофе")));
        assertTrue(posts.stream().anyMatch(p -> p.getTitle().equals("Кофе с молоком")));
    }

    @Test
    void searchByTagTest() {
        createPost("Пост по java", "Текст", List.of("java", "backend"));
        createPost("Пост по весне", "Текст", List.of("spring"));

        List<Post> posts = postDao.findAll("#java", 1, 10);

        assertEquals(1, posts.size());
        assertEquals("Пост по java", posts.get(0).getTitle());
    }

    @Test
    void searchByTagIsCaseInsensitiveTest() {
        createPost("Пост по java", "Текст", List.of("JAVA", "backend"));

        List<Post> posts = postDao.findAll("#Java", 1, 10);

        assertEquals(1, posts.size());
        assertEquals("Пост по java", posts.get(0).getTitle());
    }

    @Test
    void searchByTagDoesNotMatchPartialTagTest() {
        createPost("Пост про javascript", "Текст", List.of("javascript"));
        createPost("Пост про java core", "Текст", List.of("java"));

        List<Post> posts = postDao.findAll("#java", 1, 10);

        assertEquals(1, posts.size());
        assertEquals("Пост про java core", posts.get(0).getTitle());
    }

    @Test
    void searchByTagAndSubstringTest() {
        createPost("Урок по Spring", "Текст", List.of("java"));
        createPost("Урок по Java", "Текст", List.of("java"));
        createPost("Другой урок", "Текст", List.of("spring"));

        List<Post> posts = postDao.findAll("Урок #java", 1, 10);

        assertEquals(2, posts.size());
    }

    @Test
    void truncateLongTextNotAppliedInDaoTest() {
        String longText = "а".repeat(200);
        createPost("Пост с длинным текстом", longText, List.of());

        List<Post> posts = postDao.findAll("", 1, 10);

        assertEquals(1, posts.size());
        assertEquals(200, posts.get(0).getText().length());
    }

    @Test
    void updatePostTest() {
        long id = createPost("Старое название", "Старый текст", List.of("java"));

        Post post = postDao.findById(id);
        post.setTitle("Новое название");
        post.setText("Новый текст");
        post.setTags(List.of("spring", "web"));

        Post updated = postDao.update(post);

        assertEquals("Новое название", updated.getTitle());
        assertEquals("Новый текст", updated.getText());
        assertEquals(List.of("spring", "web"), updated.getTags());
    }

    @Test
    void deleteByIdTest() {
        long id = createPost("Пост на удаление", "Текст", List.of());

        postDao.deleteById(id);

        assertThrows(NotFoundException.class, () -> postDao.findById(id));
    }

    @Test
    void incrementLikesTest() {
        long id = createPost("Пост с лайком", "Текст", List.of());

        assertEquals(1L, postDao.incrementLikes(id));
        assertEquals(2L, postDao.incrementLikes(id));
        assertEquals(2L, postDao.findById(id).getLikesCount());
    }

    @Test
    void commentsCountInListTest() {
        long id = createPost("Пост", "Текст", List.of());

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

        List<Post> posts = postDao.findAll("", 1, 10);
        Post listed = posts.get(0);
        assertEquals(2L, listed.getCommentsCount());
    }

    @Test
    void updateImageAndGetImageTest() {
        long id = createPost("Пост с картинкой", "Текст", List.of());
        byte[] image = new byte[] {1, 2, 3, 4};

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

    @Test
    void incrementLikesForMissingPostThrowsTest() {
        assertThrows(NotFoundException.class, () -> postDao.incrementLikes(9999L));
    }

    @Test
    void updateImageForMissingPostThrowsTest() {
        assertThrows(NotFoundException.class, () -> postDao.updateImage(9999L, new byte[] {1, 2}));
    }
}
