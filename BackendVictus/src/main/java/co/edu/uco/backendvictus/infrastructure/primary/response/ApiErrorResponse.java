package co.edu.uco.backendvictus.infrastructure.primary.response;

import java.time.OffsetDateTime;

public record ApiErrorResponse(boolean success, String code, String message, String path, OffsetDateTime timestamp) {

    public static ApiErrorResponse of(final String code, final String message, final String path) {
        return new ApiErrorResponse(false, code, message, path, OffsetDateTime.now());
    }
}
