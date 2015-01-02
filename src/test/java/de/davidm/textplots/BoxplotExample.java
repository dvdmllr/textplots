package de.davidm.textplots;

import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class BoxplotExample {
    public static void main(String[] args){
        List<Pair<String, double[]>> data = new ArrayList<>();
        data.add(Pair.create("IRIS_SEPAL_LENGTH", IrisData.IRIS_SEPAL_LENGTH));
        data.add(Pair.create("IRIS_SEPAL_WIDTH",  IrisData.IRIS_SEPAL_WIDTH));

        Plot plot = new Boxplot.BoxplotBuilder(data).plotObject();
        plot.printPlot(true);
    }
}
