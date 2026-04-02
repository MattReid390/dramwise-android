package org.me.gcu.dramwise.util;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

public class DateUtilTest {

    @Test
    public void startOfTodayMillis_hourIsZero() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(DateUtil.startOfTodayMillis());
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
    }

    @Test
    public void startOfTodayMillis_minuteIsZero() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(DateUtil.startOfTodayMillis());
        assertEquals(0, cal.get(Calendar.MINUTE));
    }

    @Test
    public void startOfTodayMillis_secondIsZero() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(DateUtil.startOfTodayMillis());
        assertEquals(0, cal.get(Calendar.SECOND));
    }

    @Test
    public void startOfTodayMillis_millisecondIsZero() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(DateUtil.startOfTodayMillis());
        assertEquals(0, cal.get(Calendar.MILLISECOND));
    }

    @Test
    public void endOfTodayMillis_hourIsTwentyThree() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(DateUtil.endOfTodayMillis());
        assertEquals(23, cal.get(Calendar.HOUR_OF_DAY));
    }

    @Test
    public void endOfTodayMillis_minuteIsFiftyNine() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(DateUtil.endOfTodayMillis());
        assertEquals(59, cal.get(Calendar.MINUTE));
    }

    @Test
    public void endOfTodayMillis_secondIsFiftyNine() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(DateUtil.endOfTodayMillis());
        assertEquals(59, cal.get(Calendar.SECOND));
    }

    @Test
    public void endOfTodayMillis_millisecondIsNineNineNine() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(DateUtil.endOfTodayMillis());
        assertEquals(999, cal.get(Calendar.MILLISECOND));
    }

    @Test
    public void startIsBeforeEnd() {
        assertTrue(DateUtil.startOfTodayMillis() < DateUtil.endOfTodayMillis());
    }

    @Test
    public void startAndEndAreOnSameDay() {
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(DateUtil.startOfTodayMillis());

        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(DateUtil.endOfTodayMillis());

        assertEquals(start.get(Calendar.DAY_OF_YEAR), end.get(Calendar.DAY_OF_YEAR));
        assertEquals(start.get(Calendar.YEAR), end.get(Calendar.YEAR));
    }

    @Test
    public void startAndEndSpanFullDay() {
        long expectedMs = (23 * 3600 + 59 * 60 + 59) * 1000L + 999L;
        long actualMs = DateUtil.endOfTodayMillis() - DateUtil.startOfTodayMillis();
        assertEquals(expectedMs, actualMs);
    }

    @Test
    public void formatDateTime_returnsCorrectFormat() {
        // Fix a known timestamp: 15 January 2024 at 14:30:00 local time
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 15, 14, 30, 0);
        cal.set(Calendar.MILLISECOND, 0);
        String formatted = DateUtil.formatDateTime(cal.getTimeInMillis());
        assertEquals("15/01/2024 14:30", formatted);
    }

    @Test
    public void formatDateTime_singleDigitDayAndMonth_arePadded() {
        // 5 March 2024 at 09:05 — both day and month are single digit
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.MARCH, 5, 9, 5, 0);
        cal.set(Calendar.MILLISECOND, 0);
        String formatted = DateUtil.formatDateTime(cal.getTimeInMillis());
        assertEquals("05/03/2024 09:05", formatted);
    }

    @Test
    public void formatDateTime_midnightTimestamp_formatsCorrectly() {
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JUNE, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        String formatted = DateUtil.formatDateTime(cal.getTimeInMillis());
        assertEquals("01/06/2024 00:00", formatted);
    }

    @Test
    public void formatDateTime_endOfDayTimestamp_formatsCorrectly() {
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.DECEMBER, 31, 23, 59, 0);
        cal.set(Calendar.MILLISECOND, 0);
        String formatted = DateUtil.formatDateTime(cal.getTimeInMillis());
        assertEquals("31/12/2024 23:59", formatted);
    }
}