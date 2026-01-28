package dev.nxms.guardcore.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Narzędzie do parsowania i formatowania czasu.
 * Obsługuje format czasu trwania (1d2h30m15s) oraz format godzinowy (HH:mm).
 */
public class TimeParser {

    // Wzorzec dla czasu trwania: 1d2h30m15s
    private static final Pattern DURATION_PATTERN = Pattern.compile(
            "(?:(\\d+)d)?(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?"
    );

    // Format dla czasu godzinowego
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Parsuje string czasu trwania do milisekund.
     * Format: 1d2h30m15s (dni, godziny, minuty, sekundy)
     *
     * @param timeString String w formacie czasu trwania
     * @return Czas w milisekundach lub -1 jeśli format jest nieprawidłowy
     */
    public static long parseDuration(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            return -1;
        }

        Matcher matcher = DURATION_PATTERN.matcher(timeString.toLowerCase());

        if (!matcher.matches()) {
            return -1;
        }

        long totalMillis = 0;

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

        // Sekundy
        if (matcher.group(4) != null) {
            totalMillis += Long.parseLong(matcher.group(4)) * 1000;
        }

        return totalMillis > 0 ? totalMillis : -1;
    }

    /**
     * Konwertuje milisekundy na czytelny format czasu trwania.
     *
     * @param millis Czas w milisekundach
     * @return String w formacie 1d2h30m15s
     */
    public static String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        seconds %= 60;
        minutes %= 60;
        hours %= 24;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d");
        if (hours > 0) sb.append(hours).append("h");
        if (minutes > 0) sb.append(minutes).append("m");
        if (seconds > 0 || sb.length() == 0) sb.append(seconds).append("s");

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
     *
     * @param timeString String w formacie HH:mm (np. "11:30")
     * @return LocalTime lub null jeśli format jest nieprawidłowy
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
     *
     * @param from Czas początkowy (HH:mm)
     * @param to Czas końcowy (HH:mm)
     * @return true jeśli aktualny czas jest w zakresie
     */
    public static boolean isCurrentTimeInRange(String from, String to) {
        LocalTime fromTime = parseTimeOfDay(from);
        LocalTime toTime = parseTimeOfDay(to);

        if (fromTime == null || toTime == null) {
            return true; // Jeśli format jest nieprawidłowy, pozwól na spawn
        }

        LocalTime now = LocalTime.now();

        // Obsługa zakresu przechodzącego przez północ
        if (fromTime.isAfter(toTime)) {
            return !now.isBefore(fromTime) || !now.isAfter(toTime);
        }

        return !now.isBefore(fromTime) && !now.isAfter(toTime);
    }
}