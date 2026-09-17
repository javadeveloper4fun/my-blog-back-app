package ru.yandex.practicum.exception;

/**
 * Исключение "не найдено".
 * Спринт 3: Тема 8 «Практика по разработке Spring Framework».
 * Бросается из слоя DAO, когда запрошенная запись (пост или комментарий) отсутствует в БД.
 * Обрабатывается в ExceptionController (@RestControllerAdvice) и возвращает фронтенду
 * JSON-ошибку со статусом 404. Не требуется по постановке, но нужно для корректной работы фронта.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
