package org.example.domain;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
public abstract class User {
    protected String id;
    protected String name;
    protected List<Item> borrowedItems;

    protected static int nextId = 1;

    public User(String name) {
        this.id = String.format("%05d",nextId++);
        this.name = name;
        this.borrowedItems = new ArrayList<>();
    }
}
