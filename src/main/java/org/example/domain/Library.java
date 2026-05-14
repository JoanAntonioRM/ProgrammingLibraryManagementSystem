package org.example.domain;

import org.example.exceptions.LibraryOperationException;
import org.example.util.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Library {
    public static List<User> users = new ArrayList<>();
    public static List<Item> items = new ArrayList<>();

    public static void export() {
        exportItems();
        exportUsers();
    }

    private static void exportItems() {
        File file = new File(Constants.ITEMS_CSV_PATH);
        List<Item> copy = new ArrayList<>(items);
        copy.sort(Comparator.comparing(Item::getId));
        try (FileWriter fileWriter = new FileWriter(file)) {
            for (Item item : copy) {
                if (item instanceof Book) {
                    fileWriter.write(String.format(
                            "BOOK,%s,%s,%s,%s,%s,%s\n",
                            item.getId(),
                            item.getTitle(),
                            item.getStatus(),
                            ((Book) item).getIsbn(),
                            ((Book) item).getAuthor(),
                            ((Book) item).getGenre()));
                } else if (item instanceof DVD) {
                    fileWriter.write(String.format(
                            "DVD,%s,%s,%s,%s,%d\n",
                            item.getId(),
                            item.getTitle(),
                            item.getStatus(),
                            ((DVD) item).getDirector(),
                            ((DVD) item).getDuration()));
                } else if (item instanceof Magazine) {
                    fileWriter.write(String.format(
                            "MAGAZINE,%s,%s,%s,%d,%s\n",
                            item.getId(),
                            item.getTitle(),
                            item.getStatus(),
                            ((Magazine) item).getIssueNumber(),
                            ((Magazine) item).getPublisher()));
                }
            }
        } catch (IOException e) {
        }
    }

    private static void exportUsers() {
        File file = new File(Constants.USERS_CSV_PATH);
        List<User> copy = new ArrayList<>(users);
        copy.sort(Comparator.comparing(User::getId));
        try (FileWriter fileWriter = new FileWriter(file)) {
            for (User user : copy) {
                if (user instanceof Student) {
                    fileWriter.write("STUDENT");
                } else if (user instanceof Teacher) {
                    fileWriter.write("TEACHER");
                } else if (user instanceof Admin) {
                    fileWriter.write("ADMIN");
                } else {
                    continue;
                }
                fileWriter.write(",");
                fileWriter.write(user.getId());
                fileWriter.write(",");
                fileWriter.write(user.getName());
                List<Item> loans = user.getBorrowedItems();
                if (loans != null) {
                    for (Item item : loans) {
                        fileWriter.write(",");
                        fileWriter.write(item.getId());
                    }
                }
                fileWriter.write("\n");
            }
        } catch (IOException e) {
        }
    }

    public static void load() {
        items.clear();
        users.clear();
        loadItems();
        loadUsers();
    }

    public static void loadItems() {
        File file = new File(Constants.ITEMS_CSV_PATH);
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String line = scanner.next();
                String[] elements = line.split(",");

                if (elements.length < 4) {
                    continue;
                }

                String type = elements[0];

                Item.Status status = switch (elements[3]) {
                    case "BORROWED" -> Item.Status.BORROWED;
                    case "IN_STORE" -> Item.Status.IN_STORE;
                    case "LOST" -> Item.Status.LOST;
                    default -> null;
                };
                if (status == null) {
                    continue;
                }

                switch (type) {
                    case "BOOK" -> {
                        if (elements.length >= 7) {
                            items.add(new Book(
                                    elements[1],
                                    elements[2],
                                    status,
                                    elements[4],
                                    elements[5],
                                    elements[6]));
                        }
                    }
                    case "DVD" -> {
                        if (elements.length >= 6) {
                            items.add(new DVD(
                                    elements[1],
                                    elements[2],
                                    status,
                                    elements[4],
                                    Integer.parseInt(elements[5])));
                        }
                    }
                    case "MAGAZINE" -> {
                        if (elements.length >= 6) {
                            items.add(new Magazine(
                                    elements[1],
                                    elements[2],
                                    status,
                                    Integer.parseInt(elements[4]),
                                    elements[5]));
                        }
                    }
                    default -> {
                    }
                }
            }
        } catch (FileNotFoundException e) {
        }
    }

    public static void loadUsers() {
        File file = new File(Constants.USERS_CSV_PATH);
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String line = scanner.next();
                String[] elements = line.split(",");

                if (elements.length < 3) {
                    continue;
                }

                String type = elements[0];

                List<Item> borrowedItems = new ArrayList<>();
                if (elements.length > 3) {
                    String[] itemIDs = Arrays.copyOfRange(elements, 3, elements.length);
                    for (String id : itemIDs) {
                        for (Item item : items) {
                            if (item.getId().equals(id)) {
                                borrowedItems.add(item);
                                break;
                            }
                        }
                    }
                }

                switch (type) {
                    case "STUDENT" -> users.add(new Student(elements[1], elements[2], borrowedItems));
                    case "TEACHER" -> users.add(new Teacher(elements[1], elements[2], borrowedItems));
                    case "ADMIN" -> users.add(new Admin(elements[1], elements[2]));
                    default -> {
                    }
                }
            }
        } catch (FileNotFoundException e) {

        }
    }

    private static boolean matchesKeyword(Item item, String keywordLower) {
        String text = item.toString().toLowerCase(Locale.ROOT);
        return text.contains(keywordLower);
    }

    private static Map<ItemType, Set<Item>> buildTypeMap(List<Item> filteredItems) {
        Comparator<Item> byId = Comparator.comparing(Item::getId);
        Map<ItemType, Set<Item>> map = new TreeMap<>();
        map.put(ItemType.BOOK, new TreeSet<>(byId));
        map.put(ItemType.DVD, new TreeSet<>(byId));
        map.put(ItemType.MAGAZINE, new TreeSet<>(byId));
        for (Item item : filteredItems) {
            if (item instanceof Book) {
                map.get(ItemType.BOOK).add(item);
            } else if (item instanceof DVD) {
                map.get(ItemType.DVD).add(item);
            } else if (item instanceof Magazine) {
                map.get(ItemType.MAGAZINE).add(item);
            }
        }
        return map;
    }

    /**
     * For search: one {@link Book} result per ISBN (copy with smallest id kept). Other item types unchanged.
     */
    private static List<Item> dedupeSearchMatches(List<Item> matched) {
        List<Book> books = new ArrayList<>();
        List<Item> other = new ArrayList<>();
        for (Item item : matched) {
            if (item instanceof Book b) {
                books.add(b);
            } else {
                other.add(item);
            }
        }
        books.sort(Comparator.comparing(Item::getId));
        Map<String, Book> onePerIsbn = new LinkedHashMap<>();
        for (Book b : books) {
            onePerIsbn.putIfAbsent(b.getIsbn(), b);
        }
        List<Item> out = new ArrayList<>(onePerIsbn.values());
        out.addAll(other);
        return out;
    }

    public static Map<ItemType, Set<Item>> streamSearch(String keyword) {
        String keywordLower = keyword == null ? "" : keyword.toLowerCase(Locale.ROOT);
        List<Item> filteredItems = items.stream()
                .filter(item -> matchesKeyword(item, keywordLower))
                .toList();
        return buildTypeMap(dedupeSearchMatches(filteredItems));
    }

    public static Map<ItemType, Set<Item>> recursiveSearch(String keyword) {
        String keywordLower = keyword == null ? "" : keyword.toLowerCase(Locale.ROOT);
        List<Item> matched = new ArrayList<>();
        collectMatchesRecursive(items, 0, keywordLower, matched);
        return buildTypeMap(dedupeSearchMatches(matched));
    }

    private static void collectMatchesRecursive(
            List<Item> source, int index, String keywordLower, List<Item> matched) {
        if (index >= source.size()) {
            return;
        }
        Item current = source.get(index);
        if (matchesKeyword(current, keywordLower)) {
            matched.add(current);
        }
        collectMatchesRecursive(source, index + 1, keywordLower, matched);
    }

    /**
     * Records a loan: updates item status and the user's loan list.
     *
     * @throws LibraryOperationException if the loan breaks library rules
     */
    public static void borrow(User user, Item item) throws LibraryOperationException {
        if (user == null || item == null) {
            throw new LibraryOperationException("User and item must not be null.");
        }
        if (user instanceof Admin) {
            throw new LibraryOperationException("Administrators cannot borrow items.");
        }
        if (item.getStatus() == Item.Status.LOST) {
            throw new LibraryOperationException("That item is lost and cannot be borrowed.");
        }
        if (item.getStatus() == Item.Status.BORROWED) {
            throw new LibraryOperationException("That item is already on loan.");
        }
        if (user instanceof Student) {
            if (!(item instanceof Book)) {
                throw new LibraryOperationException("Students may only borrow books.");
            }
            long booksOut = 0;
            List<Item> loans = user.getBorrowedItems();
            if (loans != null) {
                for (Item loan : loans) {
                    if (loan instanceof Book) {
                        booksOut = booksOut + 1;
                    }
                }
            }
            if (booksOut >= Constants.MAX_BOOKS_STUDENT) {
                throw new LibraryOperationException(
                        "Student book limit reached (" + Constants.MAX_BOOKS_STUDENT + ").");
            }
        }
        if (user instanceof Teacher) {
            List<Item> loans = user.getBorrowedItems();
            int count = loans == null ? 0 : loans.size();
            if (count >= Constants.MAX_ITEMS_TEACHER) {
                throw new LibraryOperationException(
                        "Teacher borrowing limit reached (" + Constants.MAX_ITEMS_TEACHER + ").");
            }
        }
        item.setStatus(Item.Status.BORROWED);
        List<Item> toLoan = user.getBorrowedItems();
        if (toLoan == null) {
            throw new LibraryOperationException("User has no loan list.");
        }
        toLoan.add(item);
    }

    /**
     * Ends a loan: item becomes available again and is removed from the user's list.
     *
     * @throws LibraryOperationException if the return is not allowed
     */
    public static void returnItem(User user, Item item) throws LibraryOperationException {
        if (user == null || item == null) {
            throw new LibraryOperationException("User and item must not be null.");
        }
        if (user instanceof Admin) {
            throw new LibraryOperationException("Administrators do not have loans to return.");
        }
        List<Item> loans = user.getBorrowedItems();
        if (loans == null || !loans.contains(item)) {
            throw new LibraryOperationException("This user is not borrowing that item.");
        }
        item.setStatus(Item.Status.IN_STORE);
        loans.remove(item);
    }

    public enum ItemType {
        BOOK,
        DVD,
        MAGAZINE
    }
}
