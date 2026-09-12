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
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционные тесты слоя DAO комментариев с реальной БД H2.
 *
 * Реализация п. 17 (интеграционные тесты на DAO с Embedded In-Memory H2).
 */
@SpringJUnitConfig(classes = {DataConfig.class, PostDaoImpl.class, CommentDaoImpl.class,
        IntegrationTestConfig.class})
@Transactional
class CommentDaoTest {

    @Autowired
    private PostDao postDao;

    @Autowired
    private CommentDao commentDao;

    private long createPost() {
        Post post = new Post();
        post.setTitle("Пост для комментариев");
        post.setText("Текст");
        post.setTags("");
        post.setLikesCount(0L);
        return postDao.save(post).getId();
    }

    @Test
    void saveAndFindByIdTest() {
        long postId = createPost();

        Comment comment = new Comment();
        comment.setText("Первый комментарий");
        comment.setPostId(postId);
        long commentId = commentDao.save(comment).getId();

        Comment found = commentDao.findById(postId, commentId);

        assertEquals(commentId, found.getId());
        assertEquals("Первый комментарий", found.getText());
        assertEquals(postId, found.getPostId());
    }

    @Test
    void findByPostIdReturnsAllCommentsInOrderTest() {
        long postId = createPost();

        Comment c1 = new Comment();
        c1.setText("Первый");
        c1.setPostId(postId);
        long id1 = commentDao.save(c1).getId();

        Comment c2 = new Comment();
        c2.setText("Второй");
        c2.setPostId(postId);
        long id2 = commentDao.save(c2).getId();

        List<Comment> comments = commentDao.findByPostId(postId);

        assertEquals(2, comments.size());
        assertEquals(id1, comments.get(0).getId());
        assertEquals(id2, comments.get(1).getId());
    }

    @Test
    void findByPostIdDoesNotReturnCommentsOfOtherPostsTest() {
        long postId1 = createPost();
        long postId2 = createPost();

        Comment comment = new Comment();
        comment.setText("Комментарий к посту 1");
        comment.setPostId(postId1);
        commentDao.save(comment);

        assertTrue(commentDao.findByPostId(postId1).size() == 1);
        assertTrue(commentDao.findByPostId(postId2).isEmpty());
    }

    @Test
    void updateCommentTest() {
        long postId = createPost();

        Comment comment = new Comment();
        comment.setText("Старый текст");
        comment.setPostId(postId);
        long commentId = commentDao.save(comment).getId();

        Comment found = commentDao.findById(postId, commentId);
        found.setText("Новый текст");

        commentDao.update(found);

        assertEquals("Новый текст", commentDao.findById(postId, commentId).getText());
    }

    @Test
    void deleteCommentTest() {
        long postId = createPost();

        Comment comment = new Comment();
        comment.setText("Удаляемый комментарий");
        comment.setPostId(postId);
        long commentId = commentDao.save(comment).getId();

        commentDao.deleteById(postId, commentId);

        assertThrows(NotFoundException.class, () -> commentDao.findById(postId, commentId));
        assertTrue(commentDao.findByPostId(postId).isEmpty());
    }

    @Test
    void deletePostCascadesCommentsTest() {
        long postId = createPost();

        Comment comment = new Comment();
        comment.setText("Комментарий");
        comment.setPostId(postId);
        commentDao.save(comment);

        assertFalse(commentDao.findByPostId(postId).isEmpty());

        postDao.deleteById(postId);

        assertTrue(commentDao.findByPostId(postId).isEmpty());
    }

    @Test
    void findByIdForMissingCommentThrowsTest() {
        long postId = createPost();
        assertThrows(NotFoundException.class, () -> commentDao.findById(postId, 9999L));
    }
}