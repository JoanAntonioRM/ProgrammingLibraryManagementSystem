package org.example.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.interfaces.Reportable;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
public class Admin extends User implements Reportable {

    public Admin(String name){
        this.id = String.format("%05d",nextId++);
        this.name = name;
        //TODO: add to list of users in library
    }
}
