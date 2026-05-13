package org.example.domain;

import org.example.interfaces.Reportable;

public class Admin extends User implements Reportable {

    public Admin(String name){
        this.id = String.format("%05d",nextId++);
        this.name = name;
    }
}
