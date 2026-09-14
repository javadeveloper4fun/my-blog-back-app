package ru.yandex.practicum.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.Post;

/**
 * JDBC-реализация DAO для постов.
 * Все запросы к таблице posts выполняются через JdbcTemplate.
 *
 * Спринт 3: Тема 2 «Spring как IoC-контейнер» (DI через конструктор)
 * и Тема 4 «Создание бинов через Java-аннотации» (@Repository).
 * Используется JdbcTemplate — компонент Spring JDBC (Тема 8 практика).
 * Реализация п. 9 (проектирование слоёв: DAO) и п. 15 (написание DAO).
 * П. 14 (интеграция Spring с СУБД через JdbcTemplate).
 */
@Repository
public class PostDaoImpl implements PostDao {

    private final JdbcTemplate jdbcTemplate;

    public PostDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Post> postRowMapper = (rs, rowNum) -> {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setTitle(rs.getString("title"));
        post.setText(rs.getString("text"));
        post.setTags(rs.getString("tags"));
        post.setLikesCount(rs.getLong("likes_count"));
        post.setImage(rs.getBytes("image"));
        post.setCommentsCount(rs.getLong("comments_count"));
        return post;
    };

    @Override
    public List<Post> findAll(String search, int pageNumber, int pageSize) {
        String whereClause = buildSearchClause(search);
        String sql = "SELECT p.*, " + "(SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) AS comments_count "
                + "FROM posts p"
                + whereClause + " ORDER BY p.id DESC LIMIT ? OFFSET ?";
        Object[] params = buildQueryParams(search, pageSize, (pageNumber - 1) * pageSize);

        return jdbcTemplate.query(sql, postRowMapper, params);
    }

    @Override
    public long countPosts(String search) {
        String whereClause = buildSearchClause(search);
        String countSql = "SELECT COUNT(*) FROM posts p" + whereClause;
        return jdbcTemplate.queryForObject(countSql, Long.class, getSearchParams(search));
    }

    @Override
    public Post findById(long id) {
        String sql = "SELECT p.*, " + "(SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) AS comments_count "
                + "FROM posts p WHERE p.id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, postRowMapper, id);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            throw new NotFoundException("Пост с id=" + id + " не найден");
        }
    }

    @Override
    public Post save(Post post) {
        String sql = "INSERT INTO posts (title, text, tags, likes_count) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, post.getTitle());
                    ps.setString(2, post.getText());
                    ps.setString(3, post.getTags());
                    ps.setLong(4, post.getLikesCount() != null ? post.getLikesCount() : 0);
                    return ps;
                },
                keyHolder);
        post.setId(keyHolder.getKey().longValue());
        return post;
    }

    @Override
    public Post update(Post post) {
        String sql = "UPDATE posts SET title = ?, text = ?, tags = ? WHERE id = ?";
        jdbcTemplate.update(sql, post.getTitle(), post.getText(), post.getTags(), post.getId());
        return findById(post.getId());
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM posts WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public long incrementLikes(long id) {
        String sql = "UPDATE posts SET likes_count = likes_count + 1 WHERE id = ?";
        jdbcTemplate.update(sql, id);
        String selectSql = "SELECT likes_count FROM posts WHERE id = ?";
        return jdbcTemplate.queryForObject(selectSql, Long.class, id);
    }

    @Override
    public void updateImage(long id, byte[] image) {
        String sql = "UPDATE posts SET image = ? WHERE id = ?";
        jdbcTemplate.update(sql, image, id);
    }

    @Override
    public byte[] getImage(long id) {
        String sql = "SELECT image FROM posts WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, byte[].class, id);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            throw new NotFoundException("Картинка поста с id=" + id + " не найдена");
        }
    }

    private String buildSearchClause(String search) {
        if (search == null || search.trim().isEmpty()) {
            return "";
        }
        List<String> words = Arrays.stream(search.split("\\s+"))
                .filter(w -> !w.isEmpty())
                .toList();
        List<String> conditions = new java.util.ArrayList<>();
        List<String> tagWords = new java.util.ArrayList<>();
        List<String> titleWords = new java.util.ArrayList<>();
        for (String w : words) {
            if (w.startsWith("#")) {
                tagWords.add(w);
            } else {
                titleWords.add(w);
            }
        }
        tagWords.forEach(w -> conditions.add("tags LIKE ?"));
        if (!titleWords.isEmpty()) {
            conditions.add("title LIKE ?");
        }
        if (conditions.isEmpty()) {
            return "";
        }
        return " WHERE " + String.join(" AND ", conditions);
    }

    private Object[] getSearchParams(String search) {
        if (search == null || search.trim().isEmpty()) {
            return new Object[] {};
        }
        List<String> words = Arrays.stream(search.split("\\s+"))
                .filter(w -> !w.isEmpty())
                .toList();
        List<Object> params = new java.util.ArrayList<>();
        for (String w : words) {
            if (w.startsWith("#")) {
                params.add("%" + w.substring(1) + "%");
            }
        }
        List<String> titleWords =
                words.stream().filter(w -> !w.isEmpty() && !w.startsWith("#")).toList();
        if (!titleWords.isEmpty()) {
            params.add("%" + String.join(" ", titleWords) + "%");
        }
        return params.toArray();
    }

    private Object[] buildQueryParams(String search, int limit, int offset) {
        Object[] searchParams = getSearchParams(search);
        Object[] params = new Object[searchParams.length + 2];
        System.arraycopy(searchParams, 0, params, 0, searchParams.length);
        params[searchParams.length] = limit;
        params[searchParams.length + 1] = offset;
        return params;
    }
}