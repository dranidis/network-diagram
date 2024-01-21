package com.se.netdiagram.domain.model.networkdiagram;

import org.junit.Test;

public class DurationTest {

    @Test(expected = IllegalArgumentException.class)
    public void duration_should_not_be_negative() {
        new Duration(-1);
    }

}
