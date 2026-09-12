package ru.yandex.practicum;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * Тестовая конфигурация: транзакционный менеджер для изоляции интеграционных тестов.
 * Без него @Transactional в тестах не может откатывать изменения между тестами.
 *
 * Спринт 3: Тема 10 «TestContext Framework» — вспомогательный @Bean
 * для @Transactional в тестах. Пример Java-конфигурации (Тема 5).
 */
@Configuration
public class IntegrationTestConfig {

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
