package uz.uychiitschool.system.web.base.enums;

import uz.uychiitschool.system.web.base.exception.InvalidGenderException;

import java.util.Arrays;

public enum Gender {
    MALE, FEMALE;

    public static boolean isValidGender(String gender) {
        for (Gender g : values()) {
            if (g.name().equalsIgnoreCase(gender)) {
                return true;
            }
        }
        return false;
    }

    public static Gender fromString(String gender) {
        return Arrays.stream(values())
                .filter(g -> g.name().equalsIgnoreCase(gender))
                .findFirst()
                .orElseThrow(() -> new InvalidGenderException("Invalid gender: " + gender));
    }
}
