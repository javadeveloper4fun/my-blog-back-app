package ru.yandex.practicum;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import javax.sql.DataSource;

/**
 * Тестовая конфигурация: транзакционный менеджер для изоляции интеграционных тестов.
 * Без него @Transactional в тестах не может откатывать изменения между тестами.
 */
@Configuration
public class IntegrationTestConfig {

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}