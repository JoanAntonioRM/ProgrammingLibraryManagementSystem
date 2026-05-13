package org.example.interfaces;

import org.example.domain.Item;

/**
 * Reporting for administrators. Admin implements this interface and provides the methods.
 */
public interface Reportable {

    /**
     * Build a text report that lists only items with the given status
     * (for example BORROWED, IN_STORE, or LOST).
     */
    String reportItemsByStatus(Item.Status status);

    /**
     * Build a text report of all items. Items are grouped in sections,
     * one section for each possible status.
     */
    String reportAllItemsByStatusSections();

    /**
     * Build a text report that lists every user in the library.
     * Each user is written on its own row in the report text.
     */
    String reportAllUsers();
}
