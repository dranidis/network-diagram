package com.se.netdiagram.domain.model.networkdiagram;

import org.junit.Test;

import com.se.netdiagram.domain.model.networkdiagram.date.Duration;

public class DurationTest {

    @Test(expected = IllegalArgumentException.class)
    public void duration_should_not_be_negative() {
        new Duration(-1);
    }

}
