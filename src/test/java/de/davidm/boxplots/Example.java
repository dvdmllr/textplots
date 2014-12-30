package de.davidm.boxplots;

import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Example {
    public static void main(String[] args){
        double[] dataSeries1 = {1d, 2d, 5d, 20d, 3d, 22d, 4d, 4d, 2d, 5d};
        double[] dataSeries2 = {11d, 2d, 2d, 20d, 0d, 11d, 5d, 10d, 10d, 12d, 0d, 11d, 5d, 10d, 10d, 12d};
        List<Pair<String, double[]>> data = new ArrayList<>();
        data.add(Pair.create("dataSeries1", dataSeries1));
        data.add(Pair.create("dataSeries2", dataSeries2));

        Boxplots.printPlots(data);
    }
}
