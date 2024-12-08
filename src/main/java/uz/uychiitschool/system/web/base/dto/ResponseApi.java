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
}