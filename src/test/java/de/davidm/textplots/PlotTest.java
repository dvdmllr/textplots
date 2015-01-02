package de.davidm.textplots;

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
        // Complete test if (min, max) < width
        assertEquals(0, Plot.locateBin(0d, 10, 0d, 7d));
        assertEquals(1, Plot.locateBin(1d, 10, 0d, 7d));
        assertEquals(2, Plot.locateBin(2d, 10, 0d, 7d));
        assertEquals(4, Plot.locateBin(3d, 10, 0d, 7d));
        assertEquals(5, Plot.locateBin(4d, 10, 0d, 7d));
        assertEquals(7, Plot.locateBin(5d, 10, 0d, 7d));
        assertEquals(8, Plot.locateBin(6d, 10, 0d, 7d));
        assertEquals(9, Plot.locateBin(7d, 10, 0d, 7d));

        // Complete test if (min, max) > width
        assertEquals(0, Plot.locateBin(0d, 5, 0d, 7d));
        assertEquals(0, Plot.locateBin(1d, 5, 0d, 7d));
        assertEquals(1, Plot.locateBin(2d, 5, 0d, 7d));
        assertEquals(2, Plot.locateBin(3d, 5, 0d, 7d));
        assertEquals(2, Plot.locateBin(4d, 5, 0d, 7d));
        assertEquals(3, Plot.locateBin(5d, 5, 0d, 7d));
        assertEquals(4, Plot.locateBin(6d, 5, 0d, 7d));
        assertEquals(4, Plot.locateBin(7d, 5, 0d, 7d));

        // Complete test with negative data
        assertEquals(4, Plot.locateBin(0d, 5, -7d, 0d));
        assertEquals(4, Plot.locateBin(-1d, 5, -7d, 0d));
        assertEquals(3, Plot.locateBin(-2d, 5, -7d, 0d));
        assertEquals(2, Plot.locateBin(-3d, 5, -7d, 0d));
        assertEquals(2, Plot.locateBin(-4d, 5, -7d, 0d));
        assertEquals(1, Plot.locateBin(-5d, 5, -7d, 0d));
        assertEquals(0, Plot.locateBin(-6d, 5, -7d, 0d));
        assertEquals(0, Plot.locateBin(-7d, 5, -7d, 0d));

        // Further tests
        assertEquals(0, Plot.locateBin(25, 4, 1d, 100d));
        assertEquals(1, Plot.locateBin(26, 4, 1d, 100d));
        assertEquals(1, Plot.locateBin(50, 4, 1d, 100d));
        assertEquals(2, Plot.locateBin(51, 4, 1d, 100d));
    }

    @Test
    public void testLocateBins(){
        List<Pair<Integer, Double>> bins1 = Plot.locateBins(3d, 5, 0d, 7d);
        List<Pair<Integer, Double>> bins2 = Plot.locateBins(4d, 5, 0d, 7d);
        assertEquals(2, (int) bins1.get(0).getFirst());
        assertEquals(2, (int) bins2.get(0).getFirst());
        assertEquals(3, (int) bins1.get(1).getFirst());
        assertEquals(3, (int) bins2.get(1).getFirst());
        assertEquals(0.143, bins1.get(0).getSecond(), 0.01);  // 3 / (7 / 5 = 1.4) = 2.143 - 2 = 0.143
        assertEquals(0.857, bins2.get(0).getSecond(), 0.01);
        assertEquals(0.857, bins1.get(1).getSecond(), 0.01);
        assertEquals(0.143, bins2.get(1).getSecond(), 0.01);
    }

    @Test
    public void testMinMax(){
        Pair<Double, Double> minMax = Plot.getMinimumAndMaximum(data);
        assertEquals(2.0, minMax.getFirst(), 0.0001);
        assertEquals(7.9, minMax.getSecond(), 0.0001);
    }

}
