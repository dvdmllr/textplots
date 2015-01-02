package de.davidm.textplots;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BoxplotTest {
    @Test
    public void testBoxPlotString(){
        double min = 0d;
        double max = 22d;
        double minLocal = 0d;
        double maxLocal = 20d;
        double qLow = 2.75;
        double qHigh = 11d;
        double qMed = 10d;
        int width = 50;
        String boxplot = Boxplot.boxplotString(min, max, minLocal, maxLocal, qLow, qHigh, qMed, width);
        String boxplotExpected = "|-----[###############|##]-------------------|    ";
        assertEquals(boxplotExpected, boxplot);
    }
}
