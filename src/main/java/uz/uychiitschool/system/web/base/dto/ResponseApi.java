package uz.uychiitschool.system.web.base.dto;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseApi<T>  {

    @Getter
    @Setter
    String message;

    Boolean success;

    @Getter
    @Setter
    T data;


    public boolean isSuccess() {
        return success;
    }

    public static <T> ResponseApi<T> createResponse(T data, String message, boolean success) {
        return ResponseApi.<T>builder()
                .data(data)
                .message(message)
                .success(success)
                .build();
    }

    public static <T> ResponseApi<T> createResponse(String message, boolean success) {
        return ResponseApi.<T>builder()
                .data(null)
                .message(message)
                .success(success)
                .build();
    }
}