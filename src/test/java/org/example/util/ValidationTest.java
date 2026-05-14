package org.example.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Three tests per public static method in {@link Validation}.
 */
public class ValidationTest {

    @Test
    @DisplayName("isValidIsbn13 1: accepts known valid ISBN-13")
    public void isValidIsbn13Test1() {
        Assertions.assertTrue(Validation.isValidIsbn13("9780000000002"));
    }

    @Test
    @DisplayName("isValidIsbn13 2: accepts hyphenated ISBN")
    public void isValidIsbn13Test2() {
        Assertions.assertTrue(Validation.isValidIsbn13("978-0-000-000000-2"));
    }

    @Test
    @DisplayName("isValidIsbn13 3: rejects wrong check digit and short input")
    public void isValidIsbn13Test3() {
        Assertions.assertFalse(Validation.isValidIsbn13("9780000000001"));
        Assertions.assertFalse(Validation.isValidIsbn13("123"));
    }

    @Test
    @DisplayName("normalizeIsbn 1: strips hyphens and spaces")
    public void normalizeIsbnTest1() {
        Assertions.assertEquals("9780000000002", Validation.normalizeIsbn("978-0 00-000000-2"));
    }

    @Test
    @DisplayName("normalizeIsbn 2: null becomes empty string")
    public void normalizeIsbnTest2() {
        Assertions.assertEquals("", Validation.normalizeIsbn(null));
    }

    @Test
    @DisplayName("normalizeIsbn 3: plain digits unchanged")
    public void normalizeIsbnTest3() {
        Assertions.assertEquals("9780000000002", Validation.normalizeIsbn("9780000000002"));
    }
}
