package org.example.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Three tests for {@link Book} construction and ISBN rules (one logical "feature group").
 */
public class BookTest {

    private static final String VALID_ISBN = "9780000000002";

    @Test
    @DisplayName("Book 1: four-argument constructor accepts valid ISBN and sets IN_STORE")
    public void bookTest1() {
        Book book = new Book("Title", VALID_ISBN, "Author", "Genre");
        Assertions.assertEquals(VALID_ISBN, book.getIsbn());
        Assertions.assertEquals(Item.Status.IN_STORE, book.getStatus());
    }

    @Test
    @DisplayName("Book 2: four-argument constructor rejects invalid ISBN")
    public void bookTest2() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Book("Title", "9780000000001", "Author", "Genre"));
    }

    @Test
    @DisplayName("Book 3: full constructor with id and status stores fields")
    public void bookTest3() {
        Book book = new Book("00005", "T", Item.Status.BORROWED, VALID_ISBN, "A", "G");
        Assertions.assertEquals("00005", book.getId());
        Assertions.assertEquals("T", book.getTitle());
        Assertions.assertEquals(Item.Status.BORROWED, book.getStatus());
        Assertions.assertEquals(VALID_ISBN, book.getIsbn());
    }
}
