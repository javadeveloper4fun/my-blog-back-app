package ru.yandex.practicum.exception;

/**
 * Исключение некорректного запроса.
 * Бросается из слоя сервиса, когда параметры запроса не проходят проверку
 * (например, невалидные значения пагинации pageNumber/pageSize или некорректные теги).
 * Обрабатывается в ExceptionController (@RestControllerAdvice) и возвращает клиенту
 * JSON-ошибку со статусом 400 Bad Request.
 */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}