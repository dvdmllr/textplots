package de.davidm.textplots;

import org.apache.commons.math3.util.Pair;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class Plot {

    public static final String CHARACTER_COLUMN_DIVISOR = "|";
    public static final String CHARACTER_EMPTY_BIN = " ";
    public static final int DEFAULT_WIDTH = 50;
    public static final int MIN_WIDTH = 20;
    public static final int MAX_WIDTH = 100;
    public static final int DEFAULT_HEIGHT = 20;
    public static final int MIN_HEIGHT = 10;
    public static final int MAX_HEIGHT = 50;

    protected final DecimalFormat df = new DecimalFormat("0.00");

    protected Plot(){}

    /**
     * Plot to command line
     */
    public void printPlot(boolean printLegend){
        System.out.println(plot(printLegend));
    }

    /**
     * Returns the plot as a single String
     */
    public abstract String plot(boolean printLegend);

    /**
     * @param value any given value of the data series
     * @param width width of the plot for direction
     * @param min   global minimum value
     * @param max   global maximum value
     *
     * @return bin for a given value
     */
    protected static int locateBin(double value, int width, Double min, Double max) {
        double span = (max - min);
        double binWidth = span / (1d * width);
        int bin = (int) ((value - min) / binWidth);
        if(value >= max){
            bin = width - 1;
        }
        return bin;
    }

    /**
     * @param value any given value of the data series
     * @param width width of the plot for direction
     * @param min   global minimum value
     * @param max   global maximum value
     *
     * @return 2 bins surrounding given value and assignment percentage for each bin (avoid skew by floor operation)
     */
    protected static List<Pair<Integer, Double>> locateBins(double value, int width, Double min, Double max) {
        double span = (max - min);
        double binWidth = span / (1d * width);
        List<Pair<Integer, Double>>  output = new ArrayList<>();
        double bin = ((value - min) / binWidth);
        if(value < max){
            int bin1 = (int) bin;
            int bin2 = bin1 + 1;
            double bin1Prob = bin - (int) bin;
            double bin2Prob = 1 - bin1Prob;
            output.add(Pair.create(bin1, bin1Prob));
            output.add(Pair.create(bin2, bin2Prob));
        } else {
            output.add(Pair.create((int) bin - 1, 0.5));
            output.add(Pair.create((int) bin - 1, 0.5));
        }
        return output;
    }

    /**
     * @param data array of double data
     *
     * @return the minimum and maximum value in the array in the form of Pair(min, max)
     */
    protected static Pair<Double, Double> getMinimumAndMaximum(double[] data){
        Double min = null, max = null;
        for(double element : data){
            if(min == null || min > element){
                min = element;
            }
            if(max == null || max < element){
                max = element;
            }
        }
        return Pair.create(min, max);
    }

    /**
     * @param data set of data series
     *
     * @return a pair of minimum and maximum values for a list of data series
     */
    protected static Pair<Double, Double> getMinimumAndMaximum(List<Pair<String, double[]>> data) {
        Double globalMin = null, globalMax = null;
        for (Pair<String, double[]> dataSeries : data) {
            Pair<Double, Double> minMax = getMinimumAndMaximum(dataSeries.getSecond());
            if (globalMin == null || globalMin > minMax.getFirst()) {
                globalMin = minMax.getFirst();
            }
            if (globalMax == null || globalMax < minMax.getSecond()) {
                globalMax = minMax.getSecond();
            }
        }
        return Pair.create(globalMin, globalMax);
    }

    /**
     * Checks whether a given bin is out of bounds
     *
     * @param bin      target bin
     * @param minBound minimum bin
     * @param maxBound maximum bin
     *
     * @return true if out of bounds / false otherwise
     */
    protected static boolean isOutOfBounds(int bin, int minBound, int maxBound) {
        return (bin>maxBound||bin<minBound);
    }

}
