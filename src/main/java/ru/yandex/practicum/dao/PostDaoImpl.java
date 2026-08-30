package ru.yandex.practicum.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dto.PostListResponse;
import ru.yandex.practicum.dto.PostResponse;
import ru.yandex.practicum.model.Post;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JDBC-реализация DAO для постов.
 * Все запросы к таблице posts выполняются через JdbcTemplate.
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
        return post;
    };

    private final RowMapper<PostResponse> postResponseRowMapper = (rs, rowNum) -> {
        PostResponse response = new PostResponse();
        response.setId(rs.getLong("id"));
        response.setTitle(rs.getString("title"));
        response.setText(rs.getString("text"));
        String tagsStr = rs.getString("tags");
        if (tagsStr != null && !tagsStr.isEmpty()) {
            response.setTags(Arrays.asList(tagsStr.split(",")));
        } else {
            response.setTags(List.of());
        }
        response.setLikesCount(rs.getLong("likes_count"));
        response.setCommentsCount(rs.getLong("comments_count"));
        return response;
    };

    @Override
    public PostListResponse findAll(String search, int pageNumber, int pageSize) {
        String whereClause = buildSearchClause(search);
        String countSql = "SELECT COUNT(*) FROM posts" + whereClause;
        long totalPosts = jdbcTemplate.queryForObject(countSql, Long.class, getSearchParams(search));
        int lastPage = (int) Math.ceil((double) totalPosts / pageSize);
        if (lastPage == 0) lastPage = 1;

        String sql = "SELECT p.*, " +
                "(SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) AS comments_count " +
                "FROM posts p" + whereClause +
                " ORDER BY p.id DESC LIMIT ? OFFSET ?";
        Object[] params = buildQueryParams(search, pageSize, (pageNumber - 1) * pageSize);

        List<PostResponse> posts = jdbcTemplate.query(sql, postResponseRowMapper, params);

        PostListResponse response = new PostListResponse();
        response.setPosts(posts);
        response.setHasPrev(pageNumber > 1);
        response.setHasNext(pageNumber < lastPage);
        response.setLastPage(lastPage);
        return response;
    }

    @Override
    public Post findById(long id) {
        String sql = "SELECT * FROM posts WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, postRowMapper, id);
    }

    @Override
    public Post save(Post post) {
        String sql = "INSERT INTO posts (title, text, tags, likes_count) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getText());
            ps.setString(3, post.getTags());
            ps.setLong(4, post.getLikesCount() != null ? post.getLikesCount() : 0);
            return ps;
        }, keyHolder);
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
        return jdbcTemplate.queryForObject(sql, byte[].class, id);
    }

    private String buildSearchClause(String search) {
        if (search == null || search.trim().isEmpty()) {
            return "";
        }
        StringBuilder where = new StringBuilder(" WHERE ");
        List<String> conditions = Arrays.stream(search.split("\\s+"))
                .filter(w -> !w.isEmpty())
                .map(word -> {
                    if (word.startsWith("#")) {
                        return "tags LIKE ?";
                    } else {
                        return "title LIKE ?";
                    }
                })
                .collect(Collectors.toList());
        if (conditions.isEmpty()) {
            return "";
        }
        where.append(String.join(" AND ", conditions));
        return where.toString();
    }

    private Object[] getSearchParams(String search) {
        if (search == null || search.trim().isEmpty()) {
            return new Object[]{};
        }
        return Arrays.stream(search.split("\\s+"))
                .filter(w -> !w.isEmpty())
                .map(word -> "%" + word.substring(word.startsWith("#") ? 1 : 0) + "%")
                .toArray();
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
