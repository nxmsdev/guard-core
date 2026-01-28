package dev.nxms.guardcore.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeParser {

    // Wzorzec dla czasu trwania: 1d2h30m15.5s250ms20t
    private static final Pattern DURATION_PATTERN = Pattern.compile(
            "(?:(\\d+)d)?(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+(?:\\.\\d+)?)s)?(?:(\\d+)ms)?(?:(\\d+)t)?"
    );

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // 1 tick = 50ms (20 ticków = 1 sekunda)
    private static final long MS_PER_TICK = 50;

    /**
     * Parsuje string czasu trwania do milisekund.
     * Format: 1d2h30m15.5s250ms20t (dni, godziny, minuty, sekundy z ułamkami, milisekundy, ticki)
     */
    public static long parseDuration(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            return -1;
        }

        Matcher matcher = DURATION_PATTERN.matcher(timeString.toLowerCase());

        if (!matcher.matches()) {
            return -1;
        }

        double totalMillis = 0;

        // Dni
        if (matcher.group(1) != null) {
            totalMillis += Long.parseLong(matcher.group(1)) * 24 * 60 * 60 * 1000;
        }

        // Godziny
        if (matcher.group(2) != null) {
            totalMillis += Long.parseLong(matcher.group(2)) * 60 * 60 * 1000;
        }

        // Minuty
        if (matcher.group(3) != null) {
            totalMillis += Long.parseLong(matcher.group(3)) * 60 * 1000;
        }

        // Sekundy (z ułamkami)
        if (matcher.group(4) != null) {
            totalMillis += Double.parseDouble(matcher.group(4)) * 1000;
        }

        // Milisekundy
        if (matcher.group(5) != null) {
            totalMillis += Long.parseLong(matcher.group(5));
        }

        // Ticki
        if (matcher.group(6) != null) {
            totalMillis += Long.parseLong(matcher.group(6)) * MS_PER_TICK;
        }

        return totalMillis > 0 ? (long) totalMillis : -1;
    }

    /**
     * Konwertuje milisekundy na czytelny format czasu trwania.
     */
    public static String formatDuration(long millis) {
        long days = millis / (1000 * 60 * 60 * 24);
        millis %= (1000 * 60 * 60 * 24);

        long hours = millis / (1000 * 60 * 60);
        millis %= (1000 * 60 * 60);

        long minutes = millis / (1000 * 60);
        millis %= (1000 * 60);

        long seconds = millis / 1000;
        millis %= 1000;

        long ms = millis;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d");
        if (hours > 0) sb.append(hours).append("h");
        if (minutes > 0) sb.append(minutes).append("m");
        if (seconds > 0) sb.append(seconds).append("s");
        if (ms > 0) sb.append(ms).append("ms");

        if (sb.length() == 0) sb.append("0s");

        return sb.toString();
    }

    /**
     * Sprawdza czy string jest prawidłowym formatem czasu trwania.
     */
    public static boolean isValidDuration(String timeString) {
        return parseDuration(timeString) > 0;
    }

    /**
     * Parsuje string czasu godzinowego (HH:mm).
     */
    public static LocalTime parseTimeOfDay(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            return null;
        }

        try {
            return LocalTime.parse(timeString, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Sprawdza czy string jest prawidłowym formatem czasu godzinowego.
     */
    public static boolean isValidTimeOfDay(String timeString) {
        return parseTimeOfDay(timeString) != null;
    }

    /**
     * Sprawdza czy aktualny czas mieści się w podanym zakresie.
     */
    public static boolean isCurrentTimeInRange(String from, String to) {
        LocalTime fromTime = parseTimeOfDay(from);
        LocalTime toTime = parseTimeOfDay(to);

        if (fromTime == null || toTime == null) {
            return true;
        }

        LocalTime now = LocalTime.now();

        if (fromTime.isAfter(toTime)) {
            return !now.isBefore(fromTime) || !now.isAfter(toTime);
        }

        return !now.isBefore(fromTime) && !now.isAfter(toTime);
    }
}