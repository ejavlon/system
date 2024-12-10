package uz.uychiitschool.system.web.core.enums;

import uz.uychiitschool.system.web.base.exception.InvalidGenderException;

import java.util.Arrays;

public enum GroupStatus {
    OPEN,
    CLOSED,
    GATHERING;

    public static boolean isValidStatus(String gender) {
        for (GroupStatus g : values()) {
            if (g.name().equalsIgnoreCase(gender)) {
                return true;
            }
        }
        return false;
    }

    public static GroupStatus fromString(String status) {
        return Arrays.stream(values())
                .filter(g -> g.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new InvalidGenderException("Invalid status: " + status));
    }
}
