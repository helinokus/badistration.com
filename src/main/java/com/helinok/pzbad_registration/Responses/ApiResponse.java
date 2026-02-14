package com.helinok.pzbad_registration.Responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse {
    private String message;
    private Object data;

    public ApiResponse() {
    }

    public ApiResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public ApiResponse(String message) {
        this.message = message;
    }

    public ApiResponse(Object data) {
        this.data = data;
    }
}