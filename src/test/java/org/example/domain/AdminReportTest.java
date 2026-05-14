package org.example.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

/**
 * Three tests per {@link Admin} report method.
 */
public class AdminReportTest {

    private Admin admin;

    @BeforeEach
    public void reset() {
        Library.items.clear();
        Library.users.clear();
        admin = new Admin("00001", "Librarian");
    }

    @Test
    @DisplayName("reportItemsByStatus 1: includes only items with that status")
    public void reportItemsByStatusTest1() {
        Book inStore = new Book("00001", "A", Item.Status.IN_STORE, "9780000000002", "X", "G");
        Book borrowed = new Book("00002", "B", Item.Status.BORROWED, "9783161484100", "Y", "G");
        Library.items.add(inStore);
        Library.items.add(borrowed);

        String report = admin.reportItemsByStatus(Item.Status.IN_STORE);

        Assertions.assertTrue(report.contains("IN_STORE"));
        Assertions.assertTrue(report.contains("A"));
        Assertions.assertFalse(report.contains("00002"));
    }

    @Test
    @DisplayName("reportItemsByStatus 2: empty section when no items have that status")
    public void reportItemsByStatusTest2() {
        Library.items.add(new Book("00001", "Only", Item.Status.IN_STORE, "9780000000002", "A", "G"));

        String report = admin.reportItemsByStatus(Item.Status.LOST);

        Assertions.assertTrue(report.contains("LOST"));
        Assertions.assertFalse(report.contains("Only"));
    }

    @Test
    @DisplayName("reportItemsByStatus 3: borrowed section lists borrowed book title")
    public void reportItemsByStatusTest3() {
        Library.items.add(new Book("00001", "Loaned", Item.Status.BORROWED, "9780000000002", "A", "G"));

        String report = admin.reportItemsByStatus(Item.Status.BORROWED);

        Assertions.assertTrue(report.contains("BORROWED"));
        Assertions.assertTrue(report.contains("Loaned"));
    }

    @Test
    @DisplayName("reportAllUsers 1: lists student name and id")
    public void reportAllUsersTest1() {
        Library.users.add(new Student("00010", "Pat", new ArrayList<>()));

        String report = admin.reportAllUsers();

        Assertions.assertTrue(report.contains("Pat"));
        Assertions.assertTrue(report.contains("00010"));
    }

    @Test
    @DisplayName("reportAllUsers 2: lists teacher when present")
    public void reportAllUsersTest2() {
        Library.users.add(new Teacher("00011", "Taylor", new ArrayList<>()));

        String report = admin.reportAllUsers();

        Assertions.assertTrue(report.contains("Taylor"));
        Assertions.assertTrue(report.contains("00011"));
    }

    @Test
    @DisplayName("reportAllUsers 3: multiple users appear in report text")
    public void reportAllUsersTest3() {
        Library.users.add(new Student("00010", "Pat", new ArrayList<>()));
        Library.users.add(new Teacher("00011", "Taylor", new ArrayList<>()));

        String report = admin.reportAllUsers();

        Assertions.assertTrue(report.contains("Pat"));
        Assertions.assertTrue(report.contains("Taylor"));
    }

    @Test
    @DisplayName("reportAllItemsByStatusSections 1: contains IN_STORE heading")
    public void reportAllItemsByStatusSectionsTest1() {
        Library.items.add(new Book("00001", "Only", Item.Status.IN_STORE, "9780000000002", "A", "G"));

        String report = admin.reportAllItemsByStatusSections();

        Assertions.assertTrue(report.contains("IN_STORE"));
    }

    @Test
    @DisplayName("reportAllItemsByStatusSections 2: contains BORROWED and LOST headings")
    public void reportAllItemsByStatusSectionsTest2() {
        Library.items.add(new Book("00001", "B", Item.Status.BORROWED, "9780000000002", "A", "G"));

        String report = admin.reportAllItemsByStatusSections();

        Assertions.assertTrue(report.contains("BORROWED"));
        Assertions.assertTrue(report.contains("LOST"));
    }

    @Test
    @DisplayName("reportAllItemsByStatusSections 3: includes DVD line under correct status block")
    public void reportAllItemsByStatusSectionsTest3() {
        Library.items.add(new DVD("00001", "Film", Item.Status.IN_STORE, "Dir", 60));

        String report = admin.reportAllItemsByStatusSections();

        Assertions.assertTrue(report.contains("IN_STORE"));
        Assertions.assertTrue(report.contains("Film"));
    }
}
