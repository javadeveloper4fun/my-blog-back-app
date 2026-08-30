package ru.yandex.practicum.exception;

/**
 * Исключение "не найдено".
 * Бросается, когда запрашиваемый ресурс (пост или комментарий) не существует в БД.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
