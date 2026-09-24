package ru.yandex.practicum.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.Comment;

/**
 * JDBC-реализация DAO для комментариев.
 * Все запросы к таблице comments выполняются через JdbcTemplate.
 *
 * Спринт 3: Тема 2 «Spring как IoC-контейнер» (DI через конструктор)
 * и Тема 4 «Создание бинов через Java-аннотации» (@Repository).
 * Используется JdbcTemplate — компонент Spring JDBC (Тема 8 практика).
 * Реализация п. 9 (проектирование слоёв: DAO) и п. 15 (написание DAO).
 *
 * Спринт 4: Тема 7 «Автоконфигурации и стартеры» —
 * JdbcTemplate автоконфигурируется Spring Boot Data JDBC
 * (spring-boot-starter-data-jdbc), DataSource H2 — стартером Data JDBC.
 * Реализация постановки спринта 4: ручная Java-конфигурация СУБД заменена
 * автоконфигурацией Spring Boot.
 */
@Repository
public class CommentDaoImpl implements CommentDao {
    private final JdbcTemplate jdbcTemplate;

    public CommentDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Comment> commentRowMapper = (rs, rowNum) -> {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setText(rs.getString("text"));
        comment.setPostId(rs.getLong("post_id"));
        return comment;
    };

    @Override
    public List<Comment> findByPostId(long postId) {
        String sql = "SELECT * FROM comments WHERE post_id = ? ORDER BY id";
        return jdbcTemplate.query(sql, commentRowMapper, postId);
    }

    @Override
    public Comment findById(long postId, long commentId) {
        String sql = "SELECT * FROM comments WHERE post_id = ? AND id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, commentRowMapper, postId, commentId);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            throw new NotFoundException("Комментарий с id=" + commentId + " к посту id=" + postId + " не найден");
        }
    }

    @Override
    public Comment save(Comment comment) {
        String sql = "INSERT INTO comments (text, post_id) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, comment.getText());
                    ps.setLong(2, comment.getPostId());
                    return ps;
                },
                keyHolder);
        comment.setId(keyHolder.getKey().longValue());
        return comment;
    }

    @Override
    public Comment update(Comment comment) {
        String sql = "UPDATE comments SET text = ? WHERE id = ? AND post_id = ?";
        int updated = jdbcTemplate.update(sql, comment.getText(), comment.getId(), comment.getPostId());
        if (updated == 0) {
            throw new NotFoundException(
                    "Комментарий с id=" + comment.getId() + " к посту id=" + comment.getPostId() + " не найден");
        }
        return findById(comment.getPostId(), comment.getId());
    }

    @Override
    public void deleteById(long postId, long commentId) {
        String sql = "DELETE FROM comments WHERE post_id = ? AND id = ?";
        jdbcTemplate.update(sql, postId, commentId);
    }
}