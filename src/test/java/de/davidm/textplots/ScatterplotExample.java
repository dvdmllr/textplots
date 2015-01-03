package de.davidm.textplots;

import org.apache.commons.math3.util.Pair;

public class ScatterplotExample {
    public static void main(String[] args) {
        Plot plot = new Scatterplot.ScatterplotBuilder(
                Pair.create("IRIS_SEPAL_LENGTH", IrisData.IRIS_SEPAL_LENGTH),
                Pair.create("IRIS_SEPAL_WIDTH", IrisData.IRIS_SEPAL_WIDTH))
                .setSize(50, 20)
                .setSmoothing(true)
                .plotObject();

        plot.printPlot(true);
    }
}
