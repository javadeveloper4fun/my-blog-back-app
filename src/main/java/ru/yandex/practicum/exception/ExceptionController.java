package ru.yandex.practicum.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Глобальный обработчик исключений.
 * Ловит все исключения в контроллерах и возвращает клиенту JSON с описанием ошибки.
 *
 * Спринт 3: Тема 4 «Создание бинов через Java-аннотации» (@RestControllerAdvice).
 * Улучшение: обработка ошибок (400, 404, 500) не требуется по постановке,
 * но реализована для корректной работы с фронтендом.
 * 400 — обязательные поля по постановке («все поля обязательные»).
 */
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(NotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    /**
     * Ошибки валидации обязательных полей тела запроса (@RequestBody + @Valid).
     *
     * @param e исключение валидации Spring MVC
     * @return JSON-ошибка со статусом 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        return Map.of("error", "Bad Request");
    }

    /**
     * Ошибки отсутствия обязательных query-параметров (search, pageNumber, pageSize).
     *
     * @param e исключение отсутствующего параметра запроса
     * @return JSON-ошибка со статусом 400
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMissingRequestParameter(MissingServletRequestParameterException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleInternalError(Exception e) {
        return Map.of("error", e.getMessage());
    }
}
