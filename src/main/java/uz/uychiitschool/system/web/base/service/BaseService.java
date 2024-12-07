package uz.uychiitschool.system.web.base.service;

import uz.uychiitschool.system.web.base.dto.ResponseApi;

public class BaseService {

    public <T> ResponseApi<T> errorMessage() {
        return ResponseApi.<T>builder()
                .success(false)
                .message("An error occurred. Please try again later.")
                .build();
    }

    public <T> ResponseApi<T> errorMessage(String msg) {
        return ResponseApi.<T>builder()
                .success(false)
                .message(msg)
                .build();
    }
}

