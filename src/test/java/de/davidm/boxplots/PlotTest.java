package de.davidm.boxplots;

import org.apache.commons.math3.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PlotTest {

    List<Pair<String, double[]>> data;

    @Before
    public void setup(){
        data = new ArrayList<>();
        data.add(Pair.create("IRIS_SEPAL_LENGTH", IrisData.IRIS_SEPAL_LENGTH));
        data.add(Pair.create("IRIS_SEPAL_WIDTH", IrisData.IRIS_SEPAL_WIDTH));
    }

    @Test
    public void testLocateBin(){
        // complete test if (min, max) < width
        assertEquals(0, Plot.locateBin(0d, 10, 0d, 7d));
        assertEquals(1, Plot.locateBin(1d, 10, 0d, 7d));
        assertEquals(2, Plot.locateBin(2d, 10, 0d, 7d));
        assertEquals(3, Plot.locateBin(3d, 10, 0d, 7d));
        assertEquals(5, Plot.locateBin(4d, 10, 0d, 7d));
        assertEquals(6, Plot.locateBin(5d, 10, 0d, 7d));
        assertEquals(7, Plot.locateBin(6d, 10, 0d, 7d));
        assertEquals(9, Plot.locateBin(7d, 10, 0d, 7d));

        // complete test if (min, max) > width
        assertEquals(0, Plot.locateBin(0d, 5, 0d, 7d));
        assertEquals(0, Plot.locateBin(1d, 5, 0d, 7d));
        assertEquals(1, Plot.locateBin(2d, 5, 0d, 7d));
        assertEquals(1, Plot.locateBin(3d, 5, 0d, 7d));
        assertEquals(2, Plot.locateBin(4d, 5, 0d, 7d));
        assertEquals(2, Plot.locateBin(5d, 5, 0d, 7d));
        assertEquals(3, Plot.locateBin(6d, 5, 0d, 7d));
        assertEquals(4, Plot.locateBin(7d, 5, 0d, 7d));

        // complete test with negative data
        assertEquals(4, Plot.locateBin(0d, 5, -7d, 0d));
        assertEquals(3, Plot.locateBin(-1d, 5, -7d, 0d));
        assertEquals(2, Plot.locateBin(-2d, 5, -7d, 0d));
        assertEquals(2, Plot.locateBin(-3d, 5, -7d, 0d));
        assertEquals(1, Plot.locateBin(-4d, 5, -7d, 0d));
        assertEquals(1, Plot.locateBin(-5d, 5, -7d, 0d));
        assertEquals(0, Plot.locateBin(-6d, 5, -7d, 0d));
        assertEquals(0, Plot.locateBin(-7d, 5, -7d, 0d));
    }

    @Test
    public void testMinMax(){
        Pair<Double, Double> minMax = Plot.getMinimumAndMaximum(data);
        assertEquals(2.0, minMax.getFirst(), 0.0001);
        assertEquals(7.9, minMax.getSecond(), 0.0001);
    }

}
