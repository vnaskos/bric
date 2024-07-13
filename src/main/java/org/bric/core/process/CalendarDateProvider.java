package org.bric.core.process;

import java.util.Calendar;

public class CalendarDateProvider implements DateProvider {

    private final Calendar calendar;

    public CalendarDateProvider() {
        calendar = Calendar.getInstance();
    }

    @Override
    public int day() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public int month() {
        return calendar.get(Calendar.MONTH) + 1;
    }

    @Override
    public int year() {
        return calendar.get(Calendar.YEAR);
    }
}
