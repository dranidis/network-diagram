package com.se.netdiagram.domain.model.networkdiagram;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import com.se.netdiagram.domain.model.networkdiagram.date.Duration;

public class EarliestLatesTest {

    @Test
    public void calcEarliestValues_ReturnsANewInstanceWhereEarliestAndLatestValuesArePresent() {
        EarliestLatest earliestLatestValues = new EarliestLatest();

        EarliestLatest newEarliestLatestValues = earliestLatestValues
                .calculateEarliest(new ArrayList<>(), new Duration(1));

        assertTrue(newEarliestLatestValues.earliestStart().isDateIsPresent());
        assertTrue(newEarliestLatestValues.earliestStart().getAsLong() == 0);

        assertTrue(newEarliestLatestValues.earliestFinish().isDateIsPresent());
        assertTrue(newEarliestLatestValues.earliestFinish().getAsLong() == 1);

        assertFalse(earliestLatestValues.earliestStart().isDateIsPresent());
        assertFalse(earliestLatestValues.earliestFinish().isDateIsPresent());
    }

    @Test(expected = IllegalStateException.class)
    public void when_earliestValuesHaveNotBeenCalculated_calcLatestValuesAndSlack_throwsAnException() {
        EarliestLatest earliestLatestValues = new EarliestLatest();

        int projectEnd = 10;

        earliestLatestValues
                .calculateLatestAndSlack(new ArrayList<>(), new Duration(1), projectEnd);
    }

    @Test
    public void when_earliestValuesHaveBeenCalculated_calcLatestValuesAndSlack_returns_newUpdatedValues() {
        EarliestLatest earliestLatestValues = new EarliestLatest();

        int projectEnd = 10;

        EarliestLatest newEarliestValues = earliestLatestValues
                .calculateEarliest(new ArrayList<>(), new Duration(1));

        EarliestLatest newLatestValues = newEarliestValues
                .calculateLatestAndSlack(new ArrayList<>(), new Duration(1), projectEnd);

        assertTrue(newLatestValues.latestStart().isDateIsPresent());
        assertTrue(newLatestValues.latestStart().getAsLong() == projectEnd - 1);

        assertTrue(newLatestValues.latestFinish().isDateIsPresent());
        assertTrue(newLatestValues.latestFinish().getAsLong() == projectEnd);

        assertFalse(newEarliestValues.latestStart().isDateIsPresent());
        assertFalse(newEarliestValues.latestFinish().isDateIsPresent());
    }

}
