package de.davidm.textplots;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Histogram extends Plot {
    public static final int DEFAULT_NR_BINS = 10;
    public static final String CHARACTER_HISTOGRAM = "#";
    public static final String LEGEND_INTERVAL_LEFTBORDER_FIRST = "[";
    public static final String LEGEND_INTERVAL_LEFTBORDER = "(";
    public static final String LEGEND_INTERVAL_DIVISOR = ",";
    public static final String LEGEND_INTERVAL_RIGHTBORDER = "]";

    private final Pair<String, double[]> data;
    private final EmpiricalDistribution dist;
    private final int width;
    private final int bins;

    private Histogram(HistogramBuilder histogramBuilder){
        this.data = histogramBuilder.data;
        this.width = histogramBuilder.width;
        this.bins = histogramBuilder.bins;

        // Create a distribution of data for bins number of bins
        dist = new EmpiricalDistribution(bins);
        dist.load(data.getSecond());
    }

    public static class HistogramBuilder {

        Pair<String, double[]> data;
        int bins = DEFAULT_NR_BINS;
        int width = DEFAULT_WIDTH;

        public HistogramBuilder(Pair<String, double[]> data){
            this.data = data;
        }

        /**
         * @param bins number of bins in histogram
         * @return
         */
        public HistogramBuilder setBinNumber(int bins){
            this.bins = bins;
            return this;
        }

        /**
         * @param width width of plots - attention: additional width is added by names and table formatting
         * @return
         */
        public HistogramBuilder setWidth(int width){
            this.width = width;
            return this;
        }

        /**
         * @return a Histogram object with user settings
         */
        public Histogram plotObject(){
            // Check for correct width
            Preconditions.checkState(width >= MIN_WIDTH && width <= MAX_WIDTH,
                    "Width is set to " + width + " but needs to be in " + "[" + MIN_WIDTH + "," + MAX_WIDTH + "]");
            return new Histogram(this);
        }
    }

    /**
     * Print the Histogram to command line
     *
     * @param printLegend switch legend on/off
     */
    @Override
    public String plot(boolean printLegend) {
        String out = "";

        // Create the histogram out of given distribution
        List<Pair<double[], String>> plot = histogram();
        List<SummaryStatistics> bounds = dist.getBinStats();
        long N = dist.getSampleStats().getN();

        // Get the length of the longest bin interval string
        int maxLengthLeft = 0;
        if(printLegend) {
            for(Pair<double[], String> row : plot){
                int length1 = intervalString(false, row.getFirst()[0], row.getFirst()[1]).length();
                int length2 = intervalString(true, row.getFirst()[0], row.getFirst()[1]).length();
                maxLengthLeft = Math.max(maxLengthLeft, Math.max(length1, length2));
            }
        }

        // Add rows
        int rowCount = 0;
        for (Pair<double[], String> row : plot) {
            String line = "";

            // Add a legend to the left
            if (printLegend) {
                String interval = intervalString(rowCount == 0 ? true : false, row.getFirst()[0], row.getFirst()[1]);
                line += interval;
                // Fill in case of smaller interval strings
                for (int i = interval.length(); i < maxLengthLeft; i++) {
                    line += CHARACTER_EMPTY_BIN;
                }
                line += CHARACTER_COLUMN_DIVISOR;
            }
            line += row.getSecond();

            // Add bin count to the right
            if (printLegend) {
                line += CHARACTER_COLUMN_DIVISOR;
                line += "n=";
                long n = bounds.get(rowCount).getN();
                // Add spaces for right-alignment
                for (int i = String.valueOf(n).length(); i < String.valueOf(N).length(); i++) {
                    line += CHARACTER_EMPTY_BIN;
                }
                line += n;
            }
            out += line + "\n";
            rowCount++;
        }

        // Write x axis labelling under plot
        if(printLegend) {
            String line = "";
            for (int i = 0; i < maxLengthLeft; i++) {
               line += CHARACTER_EMPTY_BIN;
            }
            line += CHARACTER_COLUMN_DIVISOR;
            line += "0%";
            for (int i = 0; i < width - 6; i++) {
                line += CHARACTER_EMPTY_BIN;
            }
            line +=  "100%";
            line += CHARACTER_COLUMN_DIVISOR;
            out += line + "\n";
        }

        return out;
    }

    /**
     * @param left left value for interval (inclusive)
     * @param right right value for interval (exclusive)
     *
     * @return a string representation of an interval for a bin in the histogram
     */
    protected String intervalString(boolean first, double left, double right){
        return (
                (first ? LEGEND_INTERVAL_LEFTBORDER_FIRST : LEGEND_INTERVAL_LEFTBORDER) +
                df.format(left) +
                LEGEND_INTERVAL_DIVISOR +
                df.format(right) +
                LEGEND_INTERVAL_RIGHTBORDER);
    }

    /**
     * @return a list containing each bin of the histogram as a pair of (bin boundaries, bin plot (as String))
     */
    public List<Pair<double[], String>> histogram() {
        List<Pair<double[], String>> histogram = new ArrayList<>();

        double min = dist.getSampleStats().getMin();
        long N = dist.getSampleStats().getN();
        int i = 0;
        double[] bounds = dist.getUpperBounds();
        for (SummaryStatistics binStats : dist.getBinStats()) {
            long n = binStats.getN();
            double[] binBounds = {
                    i == 0 ? min : bounds[i - 1],
                    bounds[i]
            };
            i++;
            // convert count for bin to String
            String binString = stringForBin(n, N, width);
            histogram.add(Pair.create(binBounds, binString));
        }

        return histogram;
    }

    /**
     * @param n count for bin
     * @param N total count (over all bins)
     * @param width total width of the plot
     * @return a String representation of the counts in a bin adjusted to width
     */
    protected static String stringForBin(long n, long N, int width) {
        /*
         TODO check - might be not correct
         */
        String output = "";
        int maxBin = locateBin(n, width, 0d, (double) N);
        for(int i = 0; i < width; i++){
           output += i < maxBin ? CHARACTER_HISTOGRAM : CHARACTER_EMPTY_BIN;
        }
        return output;
    }
}
