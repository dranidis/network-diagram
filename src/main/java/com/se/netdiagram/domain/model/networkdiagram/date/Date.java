package com.se.netdiagram.domain.model.networkdiagram.date;

import java.util.NoSuchElementException;

public class Date {
    private long date;
    private boolean dateIsPresent = false;

    public Date(long i) {
        date = i;
        dateIsPresent = true;
    }

    public Date(int i, boolean b) {
        date = i;
        dateIsPresent = b;
    }

    public static Date empty() {
        return new Date(0, false);
    }

    public long getAsLong() {
        if (!dateIsPresent)
            throw new NoSuchElementException("Date is not present");
        return date;
    }

    public static Date ofLong(long i) {
        return new Date(i);
    }

    public boolean isDateIsPresent() {
        return dateIsPresent;
    }

}
