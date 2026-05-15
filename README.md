# Library Management System

# Joan Antonio Rodriguez Munoz

### Student ID 2533309

A Java course project that models a small library: **users** (students, teachers, admins), **items** (books, DVDs, magazines), **CSV persistence**, **borrow/return rules**, **stream and recursive search**, and **admin reports** via a `Reportable` interface.

| | |
| --- | --- |
| **Build** | Maven |
| **Language** | Java 23 |
| **Group / artifact** | `org.example` / `ProgrammingLibraryManagementSystem` |

---

## Features

- **Domain model:** abstract `User` and `Item`; concrete `Student`, `Teacher`, `Admin`, `Book`, `DVD`, `Magazine`.
- **ISBN-13:** `Book` validates and normalizes ISBN through `org.example.util.Validation`.
- **Library service:** static `Library` holds in-memory `users` and `items`; `load()` / `loadItems()` / `loadUsers()` read CSV; `export()` writes sorted backups.
- **Borrow / return:** `Library.borrow` and `Library.returnItem` enforce role rules and throw `LibraryOperationException` when an operation is invalid.
- **Search:** `streamSearch` and `recursiveSearch` return a `Map` of `ItemType` → sorted `Set<Item>`. Book matches are **deduplicated by ISBN** (one representative copy per ISBN, smallest id wins).
- **Matching:** keyword is matched case-insensitively against each item’s **`toString()`** output (so titles, authors, directors, etc. are all searchable as long as they appear in `toString`).
- **Reporting:** `Admin` implements `Reportable` for status sections and a full user listing.
- **Tests:** JUnit 5 tests under `src/test/java` for the library, admin reports, books, and validation.

---

## Requirements

- **JDK 23** (matches `maven.compiler.source` / `target` in `pom.xml`).
- **Apache Maven 3.8+** (or use your IDE’s bundled Maven).

Optional: **Lombok** support in your IDE (annotation processing) for generated getters/setters on model classes.

---

## CSV formats

Parsing uses `Scanner.next()` on whitespace-separated tokens, so **each logical record should be a single line** and field values should **not contain commas** (values are split on `,`).

### Items — `src/main/resources/items.csv`

| Prefix | Columns |
| --- | --- |
| `BOOK` | `BOOK`, id, title, status, ISBN, author, genre |
| `DVD` | `DVD`, id, title, status, director, durationMinutes |
| `MAGAZINE` | `MAGAZINE`, id, title, status, issueNumber, publisher |

**Status** must be exactly: `IN_STORE`, `BORROWED`, or `LOST`.

Example:

```text
BOOK,00001,Clean Code,IN_STORE,9780132350884,Robert C. Martin,Software
DVD,00002,Sample Film,IN_STORE,Jane Doe,120
MAGAZINE,00003,Tech Monthly,IN_STORE,42,ACME Publishing
```

Invalid ISBNs cause `Book` construction to throw `IllegalArgumentException` when the line is parsed into a `Book`.

### Users — `src/main/resources/users.csv`

| Prefix | Columns |
| --- | --- |
| `STUDENT` | `STUDENT`, id, name, optional list of borrowed **item ids** … |
| `TEACHER` | `TEACHER`, id, name, optional borrowed item ids … |
| `ADMIN` | `ADMIN`, id, name |

Borrowed item ids must refer to items already present in `items.csv`; the loader resolves ids to `Item` references.

Example:

```text
ADMIN,00000,Head Librarian
STUDENT,00010,Pat,00001
TEACHER,00020,Taylor,00002
```

---

## Business rules (borrow / return)

| Rule | Behavior |
| --- | --- |
| Admin | Cannot borrow or return (no loan list). |
| Lost item | Cannot be borrowed. |
| Already borrowed | Cannot be borrowed again until returned. |
| Student | Only **books**; at most **5** books out at once (`Constants.MAX_BOOKS_STUDENT`). |
| Teacher | Any item type; at most **10** items out at once (`Constants.MAX_ITEMS_TEACHER`). |
| Return | User must actually have the item in their borrowed list. |

Violations throw **`LibraryOperationException`** with an explanatory message.

---

## Project layout

```text
src/main/java/org/example/
  Main.java                 # Entry point (stub)
  domain/                   # Users, items, Library
  exceptions/               # LibraryOperationException
  interfaces/               # Reportable
  util/                     # Constants, Validation (ISBN-13)

src/main/resources/
  items.csv
  users.csv

src/test/java/org/example/
  domain/                   # LibraryTest, AdminReportTest, BookTest
  util/                     # ValidationTest
```

---

## License / use

Educational submission; reuse only within your course’s academic integrity rules.
