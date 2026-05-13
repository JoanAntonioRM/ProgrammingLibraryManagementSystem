package org.example.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class DVD extends Item {
    private String director;
    private int duration;

    public DVD(String id, String title, Status status, String director, int duration) {
        super(id, title, status);
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be a positive number of minutes.");
        }
        this.director = director;
        this.duration = duration;
    }

    public DVD(String title, String director, int duration) {
        super(title);
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be a positive number of minutes.");
        }
        this.director = director;
        this.duration = duration;
    }
}
