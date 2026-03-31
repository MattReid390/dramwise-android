package org.me.gcu.dramwise.data;

/**
 * Represents the total alcohol units consumed on a specific day.
 * This is a simple data model used for storing and passing daily
 * consumption values (e.g., for charts, summaries, or database rows).
 */
public class DailyUnits {

    /** The name of the day (e.g., "Monday", "Tue", or a date string). */
    public String day;

    /** The total number of units consumed on that day. */
    public float totalUnits;
}