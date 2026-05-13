package org.example.util;

/**
 * ISBN-13: 13 digits with EAN-13 check digit (see ISBN International Agency).
 */
public final class Validation {

    private Validation() {
    }

    public static String normalizeIsbn(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.replaceAll("[\\s-]", "");
    }

    public static boolean isValidIsbn13(String raw) {
        String digits = normalizeIsbn(raw);
        if (!digits.matches("\\d{13}")) {
            return false;
        }
        return isbn13CheckDigitValid(digits);
    }

    private static boolean isbn13CheckDigitValid(String digits) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int n = digits.charAt(i) - '0';
            sum += (i % 2 == 0) ? n : n * 3;
        }
        int check = (10 - (sum % 10)) % 10;
        return check == (digits.charAt(12) - '0');
    }
}
