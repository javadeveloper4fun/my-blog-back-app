package ru.yandex.practicum;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.practicum.config.DataConfig;
import ru.yandex.practicum.dao.CommentDaoImpl;
import ru.yandex.practicum.dao.PostDaoImpl;
import ru.yandex.practicum.dto.CreatePostRequest;
import ru.yandex.practicum.dto.PostResponse;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.service.CommentServiceImpl;
import ru.yandex.practicum.service.PostServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {DataConfig.class, PostServiceImpl.class,
        CommentServiceImpl.class, PostDaoImpl.class, CommentDaoImpl.class})
class ApplicationTests {

    @Autowired
    private PostServiceImpl postService;

    @Autowired
    private CommentServiceImpl commentService;

    @Test
    void contextLoads() {
        assertNotNull(postService);
        assertNotNull(commentService);
    }

    @Test
    void createAndFindPostTest() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Тестовый пост");
        request.setText("Текст тестового поста");
        request.setTags(List.of("тест", "java"));

        PostResponse created = postService.createPost(request);
        assertNotNull(created.getId());
        assertEquals("Тестовый пост", created.getTitle());

        PostResponse found = postService.getPost(created.getId());
        assertEquals(created.getId(), found.getId());
        assertEquals("Тестовый пост", found.getTitle());
    }

    @Test
    void deletePostTest() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Пост для удаления");
        request.setText("Текст");
        request.setTags(List.of());

        PostResponse created = postService.createPost(request);
        long id = created.getId();

        postService.deletePost(id);

        assertThrows(NotFoundException.class,
                () -> postService.getPost(id));
    }

    @Test
    void addLikeTest() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Пост с лайком");
        request.setText("Текст");
        request.setTags(List.of());

        PostResponse created = postService.createPost(request);
        assertEquals(0L, created.getLikesCount());

        long newCount = postService.addLike(created.getId());
        assertEquals(1L, newCount);
    }
}
