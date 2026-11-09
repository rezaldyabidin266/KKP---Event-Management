
package com.skyline.skatecross.common.exception;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Format standar error response API")
public class ApiErrorResponse {

    @Schema(description = "Waktu error dalam UTC")
    private final Instant timestamp;

    @Schema(example = "409")
    private final int status;

    @Schema(example = "Conflict")
    private final String error;

    @Schema(example = "Email sudah digunakan")
    private final String message;

    @Schema(example = "/api/users")
    private final String path;

    @Schema(description = "Detail kesalahan validasi per field")
    private final Map<String, String> fieldErrors;

    public ApiErrorResponse(
            int status,
            String error,
            String message,
            String path) {

        this(status, error, message, path, null);
    }

    public ApiErrorResponse(
            int status,
            String error,
            String message,
            String path,
            Map<String, String> fieldErrors) {

        this.timestamp = Instant.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.fieldErrors = fieldErrors;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}
