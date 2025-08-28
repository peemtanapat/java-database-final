package com.project.code.Controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.project.code.Model.ApiResponse;
import com.project.code.exception.StoreNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StoreNotFoundException.class)
    public ApiResponse<?> handle(StoreNotFoundException ex) {
        return new ApiResponse<>("error", ex.getMessage(), null);
    }

    // 2. Define the `handleJsonParseException` Method:
    // - Annotate with `@ExceptionHandler(HttpMessageNotReadableException.class)` to
    // handle cases where the request body is not correctly formatted (e.g., invalid
    // JSON).
    // - The `HttpMessageNotReadableException` typically occurs when the input data
    // cannot be deserialized or is improperly formatted.
    // - Use `@ResponseStatus(HttpStatus.BAD_REQUEST)` to specify that the response
    // status will be **400 Bad Request** when this exception is thrown.
    // - The method should return a `Map<String, Object>` with the following key:
    // - **`message`**: The error message should indicate that the input provided is
    // invalid. The value should be `"Invalid input: The data provided is not
    // valid."`.

}
