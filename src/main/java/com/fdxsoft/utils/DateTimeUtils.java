package com.fdxsoft.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' hh:mm a");

    public static String descriptiveFormat(LocalDateTime dateTime) {

        if (dateTime == null) {
            return null;
        }

        return dateTime.format(OUTPUT_FORMATTER)
                .replace("AM", "A.M.")
                .replace("PM", "P.M.");
    }
}
