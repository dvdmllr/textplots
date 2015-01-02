package de.davidm.textplots;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Heatmap extends Plot {

    public static final String CHARACTER_NO_DENSITY = " ";
    public static final String CHARACTER_MEDIUM_DENSITY = "·";
    public static final String CHARACTER_HIGH_DENSITY = "+";
    public static final String CHARACTER_HIGHEST_DENSITY = "#";

    public static final int BOUNDARY_NO_DENSITY = 10;
    public static final int BOUNDARY_MEDIUM_DENSITY = 50;
    public static final int BOUNDARY_HIGH_DENSITY = 80;

    private final Pair<String, double[]> X;
    private final Pair<String, double[]> Y;
    private final int width, height;
    private final Double minX, maxX, minY, maxY;
    private final boolean smoothing;

    private Heatmap(HeatmapBuilder heatmapBuilder) {
        this.X = heatmapBuilder.x;
        this.Y = heatmapBuilder.y;
        this.width = heatmapBuilder.width;
        this.height = heatmapBuilder.height;
        this.smoothing = heatmapBuilder.smoothing;

        // Set min and max values X
        Pair<Double, Double> minMaxData = getMinimumAndMaximum(X.getSecond());
        minX = heatmapBuilder.minX != null ? heatmapBuilder.minX : minMaxData.getFirst();
        maxX = heatmapBuilder.maxX != null ? heatmapBuilder.maxX : minMaxData.getSecond();
        // Check for correct min and max value settings
        Preconditions.checkState(minX<=maxX, "Minimum value for X needs to be smaller than the maximum");

        // Set min and max values Y
        Pair<Double, Double> minMaxDataY = getMinimumAndMaximum(Y.getSecond());
        minY = heatmapBuilder.minY != null ? heatmapBuilder.minY : minMaxDataY.getFirst();
        maxY = heatmapBuilder.maxY != null ? heatmapBuilder.maxY : minMaxDataY.getSecond();
        // Check for correct min and max value settings
        Preconditions.checkState(minY<=maxY, "Minimum value for X needs to be smaller than the maximum");
    }

    /**
     * Construct a Heatmap
     */
    public static class HeatmapBuilder{
        Pair<String, double[]> x;
        Pair<String, double[]> y;
        Double minX = null, minY = null;
        Double maxX = null, maxY = null;
        int width = DEFAULT_WIDTH;
        int height = DEFAULT_HEIGHT;
        boolean smoothing = true;

        /**
         * Construct a Heatmap
         * @param X a String, Double pair representing a data series name and an array of values for X axis
         * @param Y a String, Double pair representing a data series name and an array of values for Y axis
         */
        public HeatmapBuilder(Pair<String, double[]> X, Pair<String, double[]> Y){

            Preconditions.checkArgument(X.getSecond().length == Y.getSecond().length,
                    "Input vectors need to be of the same length. Currently X=" + X.getSecond().length + " and Y=" + Y.getSecond().length);
            this.x = X;
            this.y = Y;
        }

        /**
         * @param width width of plots - attention: additional width is added by names and table
         * @param height height of plots - attention: additional width is added by names and table formatting
         * @return
         */
        public HeatmapBuilder setSize(int width, int height){
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * Set minimum and maximum value shown in plots
         * @param minX minimum x value shown in printPlot (optional)
         * @param maxX maximum x value shown in printPlot (optional)
         * @param minY minimum y value shown in printPlot (optional)
         * @param maxY maximum y value shown in printPlot (optional)
         */
        public HeatmapBuilder setPlotLimits(Double minX, Double maxX, Double minY, Double maxY){
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
            return this;
        }

        /**
         * @param smoothing if true output will be smoothed to better fit to limited amount of bins in command line
         * @return
         */
        public HeatmapBuilder setSmoothing(boolean smoothing){
            this.smoothing = smoothing;
            return this;
        }

        /**
         * @return a Heatmap object with user settings
         */
        public Heatmap plotObject(){
            // Check for correct width
            Preconditions.checkState(width >= MIN_WIDTH && width <= MAX_WIDTH,
                    "Width is set to " + width + " but needs to be in " + "[" + MIN_WIDTH + "," + MAX_WIDTH + "]");
            // Check for correct height
            Preconditions.checkState(height >= MIN_HEIGHT && height <= MAX_HEIGHT,
                    "Height is set to " + height + " but needs to be in " + "[" + MIN_HEIGHT + "," + MAX_HEIGHT + "]");
            return new Heatmap(this);
        }
    }

    /**
     * @return the Heatmap in a single String
     */
    @Override
    public String plot(boolean printLegend){
        String out = "";

        String[] plot = heatmapString(X.getSecond(), Y.getSecond(), width, height, minX, maxX, minY, maxY, smoothing);

        if(printLegend) {

            /*
             * Add a legend for Y to the left
             */
            out = Y.getFirst();
            int leftSize = out.length();
            String minYString = df.format(minY.doubleValue());
            String maxYString = df.format(maxY.doubleValue());
            int maxLengthY = Math.max(minYString.length(), maxYString.length());

            df.format(maxY.doubleValue());
            for (int i = 0; i < plot.length; i++) {
                // Add strings to the left if we are not in first line
                if (i > 0) {
                    for (int j = 0; j < leftSize; j++) {
                        out += CHARACTER_EMPTY_BIN;
                    }
                }
                out += CHARACTER_COLUMN_DIVISOR;
                // Show min and max values for first and last line or fill otherwise
                if (i == 0) {
                    out += maxYString;
                    for (int j = 0; j < (maxLengthY - maxYString.length()); j++) {
                        out += CHARACTER_EMPTY_BIN;
                    }
                } else if (i == plot.length - 1) {
                    out += minYString;
                    for (int j = 0; j < (maxLengthY - minYString.length()); j++) {
                        out += CHARACTER_EMPTY_BIN;
                    }
                } else {
                    for (int j = 0; j < maxLengthY; j++) {
                        out += CHARACTER_EMPTY_BIN;
                    }
                }
                out += CHARACTER_COLUMN_DIVISOR;
                // Add actual plot row
                out += plot[i] + CHARACTER_COLUMN_DIVISOR + "\n";
            }

            /*
             * Add a legend for X below
             */
            String minXString = df.format(minX.doubleValue());
            String maxXString = df.format(maxX.doubleValue());
            for (int j = 0; j < leftSize + maxLengthY + 1; j++) {
                out += CHARACTER_EMPTY_BIN;
            }
            out += CHARACTER_COLUMN_DIVISOR;
            out += minXString;
            for (int j = 0; j < width - (minXString.length() + maxXString.length()); j++) {
                out += CHARACTER_EMPTY_BIN;
            }
            out += maxXString;
            out += CHARACTER_COLUMN_DIVISOR + "\n";

            // Add name of data series
            int nameLength = X.getFirst().length();
            for (int j = 0; j < maxLengthY + 1 + Y.getFirst().length(); j++) {
                out += CHARACTER_EMPTY_BIN;
            }
            out += CHARACTER_COLUMN_DIVISOR;
            for (int j = 0; j < width - nameLength; j++) {
                out += CHARACTER_EMPTY_BIN;
            }
            out += X.getFirst();
            out += CHARACTER_COLUMN_DIVISOR + "\n";
        } else {
            for(String line : plot){
                out += line + "\n";
            }
        }

        return out;
    }

    /**
     * Create a string representation of a Heatmap for given variables with one row per line in a String[]
     * @param X input X variables
     * @param Y input Y variables
     * @param width width of plot
     * @param height height of plot
     * @param minX x axis visual minimum value
     * @param maxX x axis visual maximum value
     * @param minY y axis visual minimum value
     * @param maxY y axis visual maximum value
     * @param smoothing if true output values will be smoothed
     * @return a Heatmap as String where each element equals a line of the map
     */
    protected static String[] heatmapString(
        double[] X, double[] Y, int width, int height, Double minX, Double maxX, Double minY, Double maxY, boolean smoothing) {

        // Locate visual boundaries
        Pair<Double, Double> minMaxX = getMinimumAndMaximum(X);
        Pair<Double, Double> minMaxY = getMinimumAndMaximum(Y);
        if(minX==null) minX=minMaxX.getFirst();
        if(minY==null) minY=minMaxY.getFirst();
        if(maxX==null) maxX=minMaxX.getSecond();
        if(maxY==null) maxY=minMaxY.getSecond();

        // Locate bins for visual boundaries
        int minBoundX = locateBin(minX, width, minX, maxX);
        int maxBoundX = locateBin(maxX, width, minX, maxX);
        int minBoundY = locateBin(minY, height, minY, maxY);
        int maxBoundY = locateBin(maxY, height, minY, maxY);

        // Fill outputdata 2d-array counting occurrences of xy-values
        double[][] outputData = new double[width][height]; // keep track of data in bins
        // Fill with 0s
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                outputData[x][y] = 0d;
            }
        }
        // Fill with values
        for (int x = 0; x < X.length; x++) {
            int y = x;
            // Count the occurrences
            if (smoothing) {
                // Distributed over two bins to avoid skew introduced by floor operation
                List<Pair<Integer, Double>> binsX = locateBins(X[x], width, minX, maxX);
                List<Pair<Integer, Double>> binsY = locateBins(Y[y], height, minY, maxY);
                for (int i = 0; i < binsX.size(); i++) {
                    int binX = binsX.get(i).getFirst();
                    int binY = binsY.get(i).getFirst();
                    double valueX = binsX.get(i).getSecond();
                    double valueY = binsY.get(i).getSecond();
                    double value = (valueX + valueY) / (double) binsX.size();
                    if (!isOutOfBounds(binX, minBoundX, maxBoundX) && !isOutOfBounds(binY, minBoundY, maxBoundY)) {
                        outputData[binX][height - binY - 1] += value;
                    }
                }
            } else {
                // Distributed over two bins to avoid skew introduced by floor operation
                int binX = locateBin(X[x], width, minX, maxX);
                int binY = locateBin(Y[y], height, minY, maxY);
                if (!isOutOfBounds(binX, minBoundX, maxBoundX) && !isOutOfBounds(binY, minBoundY, maxBoundY)) {
                    outputData[binX][height - binY - 1] += 1;
                }
            }
        }


        // Create density mapping
        List<Double> density = new ArrayList<>();
        for (double[] data : outputData) {
            for (double value : data) {
                if (value > 0) {
                    density.add(value);
                }
            }
        }
        DescriptiveStatistics densityStats = new DescriptiveStatistics(ArrayUtils.toPrimitive(density.toArray(new Double[] { })));

        // Generate output
        String[][] output = new String[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if(outputData[x][y]==0) {
                    output[x][y] = CHARACTER_NO_DENSITY;
                } else {
                    output[x][y] = matchDensityChar(densityStats, outputData[x][y]);
                }
            }
        }

        // Build results
        String[] out = new String[height];

        for(int wY = 0; wY < height; wY++){
            String line = "";
            for(int wX = 0; wX < width; wX++){
                line += output[wX][wY];
            }
            out[wY] = line;
        }

        return out;
    }

    /**
     * @param densityStats an object containing information on the distribution of bin occurrences
     * @param value value which is matched with an appropriate density representing character
     * @return a character that resembles the density for the input value given a distribution of values
     */
    private static String matchDensityChar(DescriptiveStatistics densityStats, double value) {
        if(value < densityStats.getPercentile(BOUNDARY_NO_DENSITY)){
            return CHARACTER_NO_DENSITY;
        } else if(value < densityStats.getPercentile(BOUNDARY_MEDIUM_DENSITY)){
            return CHARACTER_MEDIUM_DENSITY;
        } else if (value < densityStats.getPercentile(BOUNDARY_HIGH_DENSITY)){
            return CHARACTER_HIGH_DENSITY;
        } else {
            return CHARACTER_HIGHEST_DENSITY;
        }
    }

}
