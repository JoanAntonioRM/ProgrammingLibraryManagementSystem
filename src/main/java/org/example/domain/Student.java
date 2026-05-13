package org.example.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Student extends User{
    private static final int MAX_BOOKS = 5;

    public Student(String id, String name, List<Item> borrowedItems) {
        super(id, name, borrowedItems);
    }

}
