/**
 * Copyright (C) 2013 intelliAd Media GmbH 
 * All rights reserved. 
 * http://www.intelliAd.de 
 */

package de.davidm.textplots;

import org.apache.commons.math3.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class HistogramTest {

    Histogram hst;

    @Before
    public void setup(){
        hst = new Histogram.HistogramBuilder(Pair.create("IRIS_SEPAL_LENGTH", IrisData.IRIS_SEPAL_LENGTH)).setBinNumber(8).plotObject();
    }

    @Test
    public void testStringForBin(){
        assertEquals(Plot.DEFAULT_WIDTH, Histogram.stringForBin(50, 10, Plot.DEFAULT_WIDTH).length());
        assertEquals(50, binLength(Histogram.stringForBin(50, 100, 100)));
        assertEquals(15, binLength(Histogram.stringForBin(50, 100, 30)));
        assertEquals(27, binLength(Histogram.stringForBin(72, 80, 30)));
    }

    @Test
    public void testHistogram(){
        List<Pair<double[], String>> bins = hst.histogram();
        assertEquals(8, bins.size());
        Pair<double[], String> first = bins.get(0);
        Pair<double[], String> second = bins.get(1);
        assertArrayEquals(new double[]{4.3, 4.75}, first.getFirst(), 0.001);
        assertArrayEquals(new double[]{4.75, 5.2}, second.getFirst(), 0.001);

        assertTrue(first.getSecond().startsWith("### "));
        assertTrue(second.getSecond().startsWith("########## "));

        // Check if everything adds up to width
        int count = 0;
        for (Pair<double[], String> bin : bins) {
            count += binLength(bin.getSecond());
        }
        assertEquals(Plot.DEFAULT_WIDTH, count, 1);
    }

    private int binLength(String bin) {
        int count = 0;
        for (char c : bin.toCharArray()) {
            if (String.valueOf(c).equals(Histogram.CHARACTER_HISTOGRAM)) {
                count++;
            }
        }
        return count;
    }
}
