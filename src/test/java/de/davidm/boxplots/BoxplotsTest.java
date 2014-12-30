package de.davidm.boxplots;

import org.apache.commons.math3.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BoxplotsTest {

    double[] dataSeries1;
    double[] dataSeries2;
    List<Pair<String, double[]>> data;

    @Before
    public void setup(){
        dataSeries1 = new double[]{1d, 2d, 5d, 20d, 3d, 22d, 4d, 4d, 2d, 5d};
        dataSeries2 = new double[]{11d, 2d, 2d, 20d, 0d, 11d, 5d, 10d, 10d, 12d, 0d, 11d, 5d, 10d, 10d, 12d};

        data = new ArrayList<>();
        data.add(Pair.create("dataSeries1", dataSeries1));
        data.add(Pair.create("dataSeries2", dataSeries2));
    }

    @Test
    public void testMinMax(){
        Pair<Double, Double> minmax = Boxplots.getMinimumAndMaximum(null, null, data);
        assertEquals(0d, minmax.getFirst(), 0.0001);
        assertEquals(22d, minmax.getSecond(), 0.0001);

        minmax = Boxplots.getMinimumAndMaximum(2d, 5d, data);
        assertEquals(2d, minmax.getFirst(), 0.0001);
        assertEquals(5d, minmax.getSecond(), 0.0001);
    }

    @Test
    public void testLocateBin(){
        // complete test if (min, max) < width
        assertEquals(0, Boxplots.locateBin(0d, 10, 0d, 7d));
        assertEquals(1, Boxplots.locateBin(1d, 10, 0d, 7d));
        assertEquals(2, Boxplots.locateBin(2d, 10, 0d, 7d));
        assertEquals(3, Boxplots.locateBin(3d, 10, 0d, 7d));
        assertEquals(5, Boxplots.locateBin(4d, 10, 0d, 7d));
        assertEquals(6, Boxplots.locateBin(5d, 10, 0d, 7d));
        assertEquals(7, Boxplots.locateBin(6d, 10, 0d, 7d));
        assertEquals(9, Boxplots.locateBin(7d, 10, 0d, 7d));

        // complete test if (min, max) > width
        assertEquals(0, Boxplots.locateBin(0d, 5, 0d, 7d));
        assertEquals(0, Boxplots.locateBin(1d, 5, 0d, 7d));
        assertEquals(1, Boxplots.locateBin(2d, 5, 0d, 7d));
        assertEquals(1, Boxplots.locateBin(3d, 5, 0d, 7d));
        assertEquals(2, Boxplots.locateBin(4d, 5, 0d, 7d));
        assertEquals(2, Boxplots.locateBin(5d, 5, 0d, 7d));
        assertEquals(3, Boxplots.locateBin(6d, 5, 0d, 7d));
        assertEquals(4, Boxplots.locateBin(7d, 5, 0d, 7d));

        // complete test with negative data
        assertEquals(4, Boxplots.locateBin(0d, 5, -7d,  0d));
        assertEquals(3, Boxplots.locateBin(-1d, 5, -7d,  0d));
        assertEquals(2, Boxplots.locateBin(-2d, 5, -7d,  0d));
        assertEquals(2, Boxplots.locateBin(-3d, 5, -7d,  0d));
        assertEquals(1, Boxplots.locateBin(-4d, 5, -7d,  0d));
        assertEquals(1, Boxplots.locateBin(-5d, 5, -7d,  0d));
        assertEquals(0, Boxplots.locateBin(-6d, 5, -7d,  0d));
        assertEquals(0, Boxplots.locateBin(-7d, 5, -7d,  0d));
    }

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
        String boxplot = Boxplots.boxPlotString(min, max, minLocal, maxLocal, qLow, qHigh, qMed, width);
        String boxplotExpected = "|-----[               | ]-------------------|     ";
        assertEquals(boxplotExpected, boxplot);
    }
}
