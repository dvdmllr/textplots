package de.davidm.textplots;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HeatmapTest {
    @Test
    public void testHeatmapStringArray(){
        String[] plot = Heatmap.heatmap(IrisData.IRIS_SEPAL_LENGTH, IrisData.IRIS_SEPAL_WIDTH, 50, 20, null, null, null, null);
        assertEquals(20, plot.length);
        assertEquals("░▒░░▒░▒░▒▒▒░▒░▒░▒▒░▒▒░░▒▒▒░▒▒▒░░▒░▒▒░░░▒░░░░░░▒░░░", plot[0]);
        assertEquals("░░░░░░░▒▒▒▒▒░░░▒▒▒▒▒▒▒░▒▒▒▒▒▒▒▒░▒▒░▒░░░░░░░░░░░░░░", plot[18]);
        assertEquals("░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░", plot[19]);
    }
}
