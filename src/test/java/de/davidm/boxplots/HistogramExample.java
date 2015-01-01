package de.davidm.boxplots;

import org.apache.commons.math3.util.Pair;

public class HistogramExample {
    public static void main(String[] args) {
        Plot plot = new Histogram.HistogramBuilder(
                Pair.create("IRIS_SEPAL_LENGTH", IrisData.IRIS_SEPAL_LENGTH))
                .setBinNumber(8)
                .plotObject();

        plot.printPlot();
    }
}
