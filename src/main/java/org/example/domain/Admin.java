package org.example.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.interfaces.Reportable;

import java.util.ArrayList;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Admin extends User implements Reportable {

    public Admin(String id, String name) {
        super(id, name, new ArrayList<>());
    }

    public Admin(String name) {
        super(name);
    }

    @Override
    public boolean borrow(Item item) {
        return false;
    }
}
