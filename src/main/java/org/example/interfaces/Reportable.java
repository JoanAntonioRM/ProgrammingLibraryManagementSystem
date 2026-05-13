package org.example.interfaces;

import org.example.domain.Item;
import org.example.domain.Library;
import org.example.domain.User;

public interface Reportable {

    static String genItemReport() {
        StringBuilder report = new StringBuilder();
        for (Item item : Library.items) {
            report.append(item).append("\n");
        }
        return report.toString();
    }

    static String genUserReport() {
        StringBuilder report = new StringBuilder();
        for (User user : Library.users) {
            report.append(user).append("\n");
        }
        return report.toString();
    }
}
