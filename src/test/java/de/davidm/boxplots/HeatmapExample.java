package de.davidm.boxplots;

import org.apache.commons.math3.util.Pair;

public class HeatmapExample {
    public static void main(String[] args) {
        Plot plot = new Heatmap.HeatmapBuilder(
                Pair.create("IRIS_SEPAL_LENGTH", IrisData.IRIS_SEPAL_LENGTH),
                Pair.create("IRIS_SEPAL_WIDTH", IrisData.IRIS_SEPAL_WIDTH))
                .setSize(50, 20)
                .plotObject();

        plot.printPlot();
    }
}
