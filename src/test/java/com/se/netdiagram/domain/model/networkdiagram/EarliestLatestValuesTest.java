package com.se.netdiagram.domain.model.networkdiagram;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

public class EarliestLatestValuesTest {

    @Test
    public void calcEarliestValues_ReturnsANewInstanceWhereEarliestAndLatestValuesArePresent() {
        EarliestLatestValues earliestLatestValues = new EarliestLatestValues();

        EarliestLatestValues newEarliestLatestValues = earliestLatestValues
                .calcEarliestValues(new ArrayList<>(), new Duration(1));

        assertTrue(newEarliestLatestValues.earliestStart().isPresent());
        assertTrue(newEarliestLatestValues.earliestStart().getAsLong() == 0);

        assertTrue(newEarliestLatestValues.earliestFinish().isPresent());
        assertTrue(newEarliestLatestValues.earliestFinish().getAsLong() == 1);

        assertFalse(earliestLatestValues.earliestStart().isPresent());
        assertFalse(earliestLatestValues.earliestFinish().isPresent());
    }

    @Test(expected = IllegalStateException.class)
    public void when_earliestValuesHaveNotBeenCalculated_calcLatestValuesAndSlack_throwsAnException() {
        EarliestLatestValues earliestLatestValues = new EarliestLatestValues();

        int projectEnd = 10;

        earliestLatestValues
                .calcLatestValuesAndSlack(new ArrayList<>(), new Duration(1), projectEnd);
    }

    @Test
    public void when_earliestValuesHaveBeenCalculated_calcLatestValuesAndSlack_returns_newUpdatedValues() {
        EarliestLatestValues earliestLatestValues = new EarliestLatestValues();

        int projectEnd = 10;

        EarliestLatestValues newEarliestValues = earliestLatestValues
                .calcEarliestValues(new ArrayList<>(), new Duration(1));

        EarliestLatestValues newLatestValues = newEarliestValues
                .calcLatestValuesAndSlack(new ArrayList<>(), new Duration(1), projectEnd);

        assertTrue(newLatestValues.latestStart().isPresent());
        assertTrue(newLatestValues.latestStart().getAsLong() == projectEnd - 1);

        assertTrue(newLatestValues.latestFinish().isPresent());
        assertTrue(newLatestValues.latestFinish().getAsLong() == projectEnd);

        assertFalse(newEarliestValues.latestStart().isPresent());
        assertFalse(newEarliestValues.latestFinish().isPresent());
    }

}
