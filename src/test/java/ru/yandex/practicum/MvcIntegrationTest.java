package ru.yandex.practicum;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.config.DataConfig;
import ru.yandex.practicum.config.WebConfig;

/**
 * Интеграционные тесты MVC с полным контекстом Spring (WebConfig + DataConfig)
 * и встроенной БД H2.
 * Поднимают реальные контроллеры, сервисы и DAO через MockMvcBuilders.webAppContextSetup.
 *
 * Спринт 3: Тема 9 «Модульное и интеграционное тестирование в Spring» (MockMvc)
 * и Тема 10 «TestContext Framework» (@SpringJUnitWebConfig + @Transactional).
 * Реализация п. 17 (интеграционные тесты на MVC с WebMvc и Embedded H2).
 */
@SpringJUnitWebConfig(classes = {WebConfig.class, DataConfig.class, IntegrationTestConfig.class})
@Transactional
class MvcIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void createPostViaControllerTest() throws Exception {
        String request = """
                {"title":"Интеграционный пост","text":"Текст поста","tags":["java","spring"]}""";

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Интеграционный пост"))
                .andExpect(jsonPath("$.tags", hasSize(2)))
                .andExpect(jsonPath("$.likesCount").value(0))
                .andExpect(jsonPath("$.commentsCount").value(0));
    }

    @Test
    void searchAndPaginationTest() throws Exception {
        String request = """
                {"title":"Погода в Москве","text":"Текст","tags":["weather"]}""";
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Погода в Питере\",\"text\":\"Текст 2\",\"tags\":[]}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/posts")
                        .param("search", "Погода")
                        .param("pageNumber", "1")
                        .param("pageSize", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts", hasSize(1)))
                .andExpect(jsonPath("$.posts[0].title").value("Погода в Питере"))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.lastPage").value(2));
    }

    @Test
    void addLikeToOneHundredTest() throws Exception {
        String request = """
                {"title":"Пост без лайков","text":"Текст","tags":[]}""";
        String body = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = extractId(body);

        mockMvc.perform(post("/api/posts/" + id + "/likes")).andExpect(status().isOk());
        mockMvc.perform(post("/api/posts/" + id + "/likes")).andExpect(status().isOk());
        mockMvc.perform(post("/api/posts/" + id + "/likes"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    void addCommentsCountTest() throws Exception {
        String request = """
                {"title":"Пост с комментариями","text":"Текст","tags":[]}""";
        String body = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn()
                .getResponse()
                .getContentAsString();
        long id = extractId(body);

        mockMvc.perform(post("/api/posts/" + id + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Комментарий 1\",\"postId\":" + id + "}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/posts/" + id + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Комментарий 2\",\"postId\":" + id + "}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/posts")
                        .param("search", "")
                        .param("pageNumber", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts[?(@.id==" + id + ")]").exists())
                .andExpect(
                        jsonPath("$.posts[?(@.id==" + id + ")].commentsCount").value(2));
    }

    @Test
    void createPostWithoutTitleTest() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Текст без названия\",\"tags\":[]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPostsWithoutParamsTest() throws Exception {
        mockMvc.perform(get("/api/posts")).andExpect(status().isBadRequest());
    }

    @Test
    void getPostByGetMethodTest() throws Exception {
        String request = """
                {"title":"Пост для GET","text":"Текст","tags":["test"]}""";
        String body = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn()
                .getResponse()
                .getContentAsString();
        long id = extractId(body);

        mockMvc.perform(get("/api/posts/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Пост для GET"))
                .andExpect(jsonPath("$.text").value("Текст"))
                .andExpect(jsonPath("$.tags", hasSize(1)))
                .andExpect(jsonPath("$.likesCount").value(0))
                .andExpect(jsonPath("$.commentsCount").value(0));
    }

    @Test
    void getCommentsWithInvalidPostIdTest() throws Exception {
        mockMvc.perform(get("/api/posts/undefined/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void deletePostCascadeCommentsTest() throws Exception {
        String request = """
                {"title":"Пост на удаление","text":"Текст","tags":[]}""";
        String body = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn()
                .getResponse()
                .getContentAsString();
        long id = extractId(body);

        mockMvc.perform(post("/api/posts/" + id + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Комментарий\",\"postId\":" + id + "}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/posts/" + id + "/likes")).andExpect(status().isOk());

        mockMvc.perform(delete("/api/posts/" + id)).andExpect(status().isOk());

        mockMvc.perform(post("/api/posts/" + id)).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/posts/" + id + "/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    private long extractId(String json) {
        return Long.parseLong(json.replaceAll(".*\"id\":(\\d+).*", "$1"));
    }
}
