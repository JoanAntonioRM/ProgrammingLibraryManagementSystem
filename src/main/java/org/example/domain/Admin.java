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

    @Override
    public String reportItemsByStatus(Item.Status status) {
        if (Library.items == null) {
            return "";
        }
        StringBuilder report = new StringBuilder();
        report.append(status.toString());
        report.append(":\n");
        for (Item item : Library.items) {
            if (item.getStatus() == status) {
                report.append(item.toString());
                report.append("\n");
            }
        }
        return report.toString();
    }

    @Override
    public String reportAllItemsByStatusSections() {
        StringBuilder report = new StringBuilder();
        Item.Status[] allStatuses = Item.Status.values();
        for (int i = 0; i < allStatuses.length; i++) {
            Item.Status currentStatus = allStatuses[i];
            String section = reportItemsByStatus(currentStatus);
            report.append(section);
            report.append("\n");
        }
        return report.toString();
    }

    @Override
    public String reportAllUsers() {
        if (Library.users == null) {
            return "";
        }
        StringBuilder report = new StringBuilder();
        for (User user : Library.users) {
            report.append(user.toString());
            report.append("\n");
        }
        return report.toString();
    }
}
