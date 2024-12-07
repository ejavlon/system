package uz.uychiitschool.system.web.core.enums;

import lombok.Getter;

public enum CourseType {
    COMPUTER_LITERACY(3),
    GRAPHIC_DESIGN(3),
    JAVA_BACKEND(8);

    @Getter
    private final int month;

    private CourseType(int month){
        this.month = month;
    }
}
