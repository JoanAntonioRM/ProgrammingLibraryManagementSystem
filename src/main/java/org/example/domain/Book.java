package org.example.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.util.Validation;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class Book extends Item{
    private String isbn;
    private String author;
    private String genre;

    public Book(String id, String title, Status status, String isbn, String author, String genre) {
        super(id, title, status);
        this.isbn = Validation.normalizeIsbn(isbn);
        if (!Validation.isValidIsbn13(this.isbn)) {
            throw new IllegalArgumentException("Invalid ISBN-13: " + isbn);
        }
        this.author = author;
        this.genre = genre;
    }

    public Book(String title, String isbn, String author, String genre) {
        super(title);
        this.isbn = Validation.normalizeIsbn(isbn);
        if (!Validation.isValidIsbn13(this.isbn)) {
            throw new IllegalArgumentException("Invalid ISBN-13: " + isbn);
        }
        this.author = author;
        this.genre = genre;
    }
}
