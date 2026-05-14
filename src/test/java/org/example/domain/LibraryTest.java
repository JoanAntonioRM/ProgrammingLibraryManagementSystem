package org.example.domain;

import org.example.exceptions.LibraryOperationException;
import org.example.util.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Unit tests for {@link Library}. Each public method has three tests: {@code nameTest1},
 * {@code nameTest2}, {@code nameTest3}.
 */
public class LibraryTest {

    static final String VALID_ISBN = "9780000000002";

    @BeforeEach
    public void resetLibrary() {
        Library.items.clear();
        Library.users.clear();
    }

    @Test
    @DisplayName("borrow 1: successful borrow sets item to BORROWED")
    public void borrowTest1() throws LibraryOperationException {
        Student student = new Student("The sentient potato");
        Book book = new Book("How to get 100% on my project", VALID_ISBN, "Patrick", "Fantasy");
        Library.items.add(book);
        Library.users.add(student);

        Library.borrow(student, book);

        Assertions.assertEquals(Item.Status.BORROWED, book.getStatus());
    }

    @Test
    @DisplayName("borrow 2: cannot borrow the same item twice")
    public void borrowTest2() throws LibraryOperationException {
        Student student = new Student("Patrick");
        Book book = new Book("I want to sleep", VALID_ISBN, "Sleepy Turtle", "Action");
        Library.items.add(book);
        Library.users.add(student);
        Library.borrow(student, book);

        Assertions.assertThrows(LibraryOperationException.class, () -> Library.borrow(student, book));
    }

    @Test
    @DisplayName("borrow 3: student cannot borrow a DVD")
    public void borrowTest3() {
        Library.users.add(new Student("Pat"));
        Item dvd = new DVD("Doc", "Dir", 60);
        Library.items.add(dvd);
        Student s = (Student) Library.users.get(0);

        Assertions.assertThrows(LibraryOperationException.class, () -> Library.borrow(s, dvd));
    }

    @Test
    @DisplayName("returnItem 1: after return item status is IN_STORE")
    public void returnItemTest1() throws LibraryOperationException {
        Student student = new Student("The sentient potato");
        Book book = new Book("How to get 100% on my project", VALID_ISBN, "Patrick", "Fantasy");
        Library.items.add(book);
        Library.users.add(student);
        Library.borrow(student, book);

        Library.returnItem(student, book);

        Assertions.assertEquals(Item.Status.IN_STORE, book.getStatus());
    }

    @Test
    @DisplayName("returnItem 2: cannot return when user is not borrowing that item")
    public void returnItemTest2() {
        Student student = new Student("Pat");
        Book book = new Book("X", VALID_ISBN, "A", "G");
        Library.items.add(book);
        Library.users.add(student);

        Assertions.assertThrows(LibraryOperationException.class, () -> Library.returnItem(student, book));
    }

    @Test
    @DisplayName("returnItem 3: return removes item from user's loan list")
    public void returnItemTest3() throws LibraryOperationException {
        Student student = new Student("Sam");
        Book book = new Book("Y", VALID_ISBN, "B", "G");
        Library.items.add(book);
        Library.users.add(student);
        Library.borrow(student, book);
        Assertions.assertEquals(1, student.getBorrowedItems().size());

        Library.returnItem(student, book);

        Assertions.assertEquals(0, student.getBorrowedItems().size());
    }

    @Test
    @DisplayName("streamSearch 1: finds book by author keyword (case-insensitive)")
    public void streamSearchTest1() {
        Library.items.add(new Book("T", VALID_ISBN, "Patrick", "Fantasy"));

        int actual = countBooksInSearch(Library.streamSearch("patrick"));

        Assertions.assertEquals(1, actual);
    }

    @Test
    @DisplayName("streamSearch 2: finds book by title substring")
    public void streamSearchTest2() {
        Library.items.add(new Book("How to catch them all", VALID_ISBN, "Fan", "Fantasy"));

        int actual = countBooksInSearch(Library.streamSearch("catch them all"));

        Assertions.assertEquals(1, actual);
    }

    @Test
    @DisplayName("streamSearch 3: returns empty book set when nothing matches")
    public void streamSearchTest3() {
        Library.items.add(new Book("Alpha", VALID_ISBN, "A", "G"));

        int actual = countBooksInSearch(Library.streamSearch("no such title"));

        Assertions.assertEquals(0, actual);
    }

    @Test
    @DisplayName("recursiveSearch 1: book count matches streamSearch for same keyword")
    public void recursiveSearchTest1() {
        Library.items.add(new Book("Ran out of Ideas", VALID_ISBN, "Patrick", "Fantasy"));

        int streamCount = countBooksInSearch(Library.streamSearch("patrick"));
        int recursiveCount = countBooksInSearch(Library.recursiveSearch("patrick"));

        Assertions.assertEquals(streamCount, recursiveCount);
        Assertions.assertEquals(1, recursiveCount);
    }

    @Test
    @DisplayName("recursiveSearch 2: finds book by title")
    public void recursiveSearchTest2() {
        Library.items.add(new Book("How to not procrastinate", VALID_ISBN, "Impossible", "Fantasy"));

        int actual = countBooksInSearch(Library.recursiveSearch("procrastinate"));

        Assertions.assertEquals(1, actual);
    }

    @Test
    @DisplayName("recursiveSearch 3: keeps one book per ISBN like streamSearch when copies match")
    public void recursiveSearchTest3() {
        Library.items.add(new Book("Java", VALID_ISBN, "IDK", "Education"));
        Library.items.add(new Book("00002", "Java", Item.Status.IN_STORE, VALID_ISBN, "IDK", "Education"));

        int streamBooks = countBooksInSearch(Library.streamSearch("Java"));
        int recursiveBooks = countBooksInSearch(Library.recursiveSearch("Java"));

        Assertions.assertEquals(1, streamBooks);
        Assertions.assertEquals(streamBooks, recursiveBooks);
    }

    @Nested
    class CsvAndExportTests {

        @AfterEach
        void restoreEmptyCsvFiles() throws Exception {
            Path itemsPath = Path.of(Constants.ITEMS_CSV_PATH);
            Path usersPath = Path.of(Constants.USERS_CSV_PATH);
            Files.writeString(itemsPath, "", StandardCharsets.UTF_8);
            Files.writeString(usersPath, "", StandardCharsets.UTF_8);
        }

        @Test
        @DisplayName("export 1: creates items file content after exporting one book")
        public void exportTest1() throws Exception {
            Library.items.add(new Book("00001", "My Title", Item.Status.IN_STORE, VALID_ISBN, "Auth", "Gen"));
            Library.export();

            String content = Files.readString(Path.of(Constants.ITEMS_CSV_PATH), StandardCharsets.UTF_8);

            Assertions.assertTrue(content.contains("BOOK"));
            Assertions.assertTrue(content.contains("00001"));
            Assertions.assertTrue(content.contains(VALID_ISBN));
        }

        @Test
        @DisplayName("export 2: exported line includes DVD fields")
        public void exportTest2() throws Exception {
            Library.items.add(new DVD("00001", "Film", Item.Status.IN_STORE, "Dir", 90));
            Library.export();

            String content = Files.readString(Path.of(Constants.ITEMS_CSV_PATH), StandardCharsets.UTF_8);

            Assertions.assertTrue(content.contains("DVD"));
            Assertions.assertTrue(content.contains("Dir"));
            Assertions.assertTrue(content.contains("90"));
        }

        @Test
        @DisplayName("export 3: writes user row with role and id")
        public void exportTest3() throws Exception {
            Library.users.add(new Student("00010", "Pat", new ArrayList<>()));
            Library.export();

            String content = Files.readString(Path.of(Constants.USERS_CSV_PATH), StandardCharsets.UTF_8);

            Assertions.assertTrue(content.contains("STUDENT"));
            Assertions.assertTrue(content.contains("00010"));
            Assertions.assertTrue(content.contains("Pat"));
        }

        @Test
        @DisplayName("load 1: loads one book from items CSV into Library.items")
        public void loadTest1() throws Exception {
            Files.writeString(
                    Path.of(Constants.ITEMS_CSV_PATH),
                    "BOOK,00001,MyTitle,IN_STORE," + VALID_ISBN + ",Author,Genre\n",
                    StandardCharsets.UTF_8);
            Files.writeString(Path.of(Constants.USERS_CSV_PATH), "", StandardCharsets.UTF_8);

            Library.load();

            Assertions.assertEquals(1, Library.items.size());
            Assertions.assertEquals(0, Library.users.size());
        }

        @Test
        @DisplayName("load 2: loads student with borrowed item id from users CSV")
        public void loadTest2() throws Exception {
            Files.writeString(
                    Path.of(Constants.ITEMS_CSV_PATH),
                    "BOOK,00001,T,IN_STORE," + VALID_ISBN + ",A,G\n",
                    StandardCharsets.UTF_8);
            Files.writeString(
                    Path.of(Constants.USERS_CSV_PATH),
                    "STUDENT,00010,Pat,00001\n",
                    StandardCharsets.UTF_8);

            Library.load();

            Assertions.assertEquals(1, Library.users.size());
            Student s = (Student) Library.users.get(0);
            Assertions.assertEquals(1, s.getBorrowedItems().size());
        }

        @Test
        @DisplayName("load 3: loads DVD row from items CSV")
        public void loadTest3() throws Exception {
            Files.writeString(
                    Path.of(Constants.ITEMS_CSV_PATH),
                    "DVD,00001,DvdTitle,IN_STORE,DirectorName,120\n",
                    StandardCharsets.UTF_8);
            Files.writeString(Path.of(Constants.USERS_CSV_PATH), "", StandardCharsets.UTF_8);

            Library.load();

            Assertions.assertEquals(1, Library.items.size());
            Assertions.assertTrue(Library.items.get(0) instanceof DVD);
        }

        @Test
        @DisplayName("loadItems 1: adds one book when items file has one BOOK line")
        public void loadItemsTest1() throws Exception {
            Library.items.clear();
            Files.writeString(
                    Path.of(Constants.ITEMS_CSV_PATH),
                    "BOOK,00001,X,IN_STORE," + VALID_ISBN + ",A,G\n",
                    StandardCharsets.UTF_8);

            Library.loadItems();

            Assertions.assertEquals(1, Library.items.size());
        }

        @Test
        @DisplayName("loadItems 2: skips line when status token is not valid")
        public void loadItemsTest2() throws Exception {
            Library.items.clear();
            Files.writeString(
                    Path.of(Constants.ITEMS_CSV_PATH),
                    "BOOK,00001,X,UNKNOWN_STATUS," + VALID_ISBN + ",A,G\n",
                    StandardCharsets.UTF_8);

            Library.loadItems();

            Assertions.assertEquals(0, Library.items.size());
        }

        @Test
        @DisplayName("loadItems 3: loads magazine row")
        public void loadItemsTest3() throws Exception {
            Library.items.clear();
            Files.writeString(
                    Path.of(Constants.ITEMS_CSV_PATH),
                    "MAGAZINE,00001,MagTitle,IN_STORE,5,PublisherCo\n",
                    StandardCharsets.UTF_8);

            Library.loadItems();

            Assertions.assertEquals(1, Library.items.size());
            Assertions.assertTrue(Library.items.get(0) instanceof Magazine);
        }

        @Test
        @DisplayName("loadUsers 1: loads student with empty loan list when CSV has no loan ids")
        public void loadUsersTest1() throws Exception {
            Library.items.add(new Book("00001", "T", Item.Status.IN_STORE, VALID_ISBN, "A", "G"));
            Files.writeString(
                    Path.of(Constants.USERS_CSV_PATH),
                    "STUDENT,00010,Pat\n",
                    StandardCharsets.UTF_8);

            Library.loadUsers();

            Assertions.assertEquals(1, Library.users.size());
            Assertions.assertEquals(0, ((Student) Library.users.get(0)).getBorrowedItems().size());
        }

        @Test
        @DisplayName("loadUsers 2: links loan id to existing item")
        public void loadUsersTest2() throws Exception {
            Library.items.add(new Book("00001", "T", Item.Status.IN_STORE, VALID_ISBN, "A", "G"));
            Files.writeString(
                    Path.of(Constants.USERS_CSV_PATH),
                    "TEACHER,00011,Taylor,00001\n",
                    StandardCharsets.UTF_8);

            Library.loadUsers();

            Teacher t = (Teacher) Library.users.get(0);
            Assertions.assertEquals(1, t.getBorrowedItems().size());
            Assertions.assertEquals("00001", t.getBorrowedItems().get(0).getId());
        }

        @Test
        @DisplayName("loadUsers 3: loads admin row")
        public void loadUsersTest3() throws Exception {
            Library.items.clear();
            Files.writeString(
                    Path.of(Constants.USERS_CSV_PATH),
                    "ADMIN,00012,Lee\n",
                    StandardCharsets.UTF_8);

            Library.loadUsers();

            Assertions.assertEquals(1, Library.users.size());
            Assertions.assertTrue(Library.users.get(0) instanceof Admin);
        }
    }

    private static int countBooksInSearch(Map<Library.ItemType, Set<Item>> result) {
        Set<Item> books = result.get(Library.ItemType.BOOK);
        return books == null ? 0 : books.size();
    }
}
