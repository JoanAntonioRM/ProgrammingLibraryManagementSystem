package org.example.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Teacher extends User {
    private static final int MAX_ITEMS = 10;

    public Teacher(String id, String name, List<Item> borrowedItems) {
        super(id, name, borrowedItems);
    }

    public Teacher(String name) {
        super(name);
    }
}
