package de.davidm.boxplots;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Histogram extends Plot {
    public static final int DEFAULT_NR_BINS = 10;
    public static final String CHARACTER_HISTOGRAM = "▒";
    public static final String LEGEND_INTERVAL_LEFTBORDER = "[";
    public static final String LEGEND_INTERVAL_DIVISOR = ",";
    public static final String LEGEND_INTERVAL_RIGHTBORDER = ")";

    private final Pair<String, double[]> data;
    private final int width;
    private final boolean legend;
    private final int bins;

    private Histogram(HistogramBuilder histogramBuilder){
        this.data = histogramBuilder.data;
        this.width = histogramBuilder.width;
        this.legend = histogramBuilder.legend;
        this.bins = histogramBuilder.bins;
    }

    public static class HistogramBuilder {

        Pair<String, double[]> data;
        int bins = DEFAULT_NR_BINS;
        int width = DEFAULT_WIDTH;
        boolean legend = true;

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
         * @param enable true to show legend, false otherwise
         */
        public HistogramBuilder enableLegend(boolean enable){
            this.legend = enable;
            return this;
        }

        /**
         * @return a Plot object with user settings
         */
        public Plot plotObject(){
            // Check for correct width
            Preconditions.checkState(width >= MIN_WIDTH && width <= MAX_WIDTH,
                    "Width is set to " + width + " but needs to be in " + "[" + MIN_WIDTH + "," + MAX_WIDTH + "]");
            return new Histogram(this);
        }

    }

    /**
     * Print the Histogram to command line
     */
    @Override
    public void printPlot() {
        // Create a distribution of data for bins number of bins
        EmpiricalDistribution dist = new EmpiricalDistribution(bins);
        dist.load(data.getSecond());

        // Create the histogram out of given distribution
        List<Pair<double[], String>> plot = histogram(dist);
        List<SummaryStatistics> bounds = dist.getBinStats();
        long N = dist.getSampleStats().getN();

        // Get the length of the longest bin interval string
        int maxLengthLeft = 0;
        if(legend) {
            for(Pair<double[], String> row : plot){
                int length = intervalString(row.getFirst()[0], row.getFirst()[1]).length();
                maxLengthLeft = Math.max(maxLengthLeft, length);
            }
        }

        // Print rows
        int rowCount = 0;
        for (Pair<double[], String> row : plot) {
            String line = "";

            // Add a legend to the left
            if (legend) {
                String interval = intervalString(row.getFirst()[0], row.getFirst()[1]);
                line += interval;
                // Fill in case of smaller interval strings
                for (int i = interval.length(); i < maxLengthLeft; i++) {
                    line += CHARACTER_EMPTY_BIN;
                }
                line += CHARACTER_COLUMN_DIVISOR;
            }
            line += row.getSecond();

            // Add bin count to the right
            if (legend) {
                line += CHARACTER_COLUMN_DIVISOR;
                line += "n=";
                long n = bounds.get(rowCount).getN();
                // Add spaces for right-alignment
                for (int i = String.valueOf(n).length(); i < String.valueOf(N).length(); i++) {
                    line += CHARACTER_EMPTY_BIN;
                }
                line += n;
            }
            System.out.println(line);
            rowCount++;
        }

        // Write x axis labelling under plot
        if(legend) {
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
            System.out.println(line);
        }
    }

    /**
     * @param left left value for interval (inclusive)
     * @param right right value for interval (exclusive)
     * @return a string representation of an interval for a bin in the histogram
     */
    private String intervalString(double left, double right){
        return (LEGEND_INTERVAL_LEFTBORDER +
                df.format(left) +
                LEGEND_INTERVAL_DIVISOR +
                df.format(right) +
                LEGEND_INTERVAL_RIGHTBORDER);
    }

    /**
     * @param dist empirical distribution of data in bins
     * @return a list containing each bin of the histogram as a Pair of (bin boundaries, bin plot (as String))
     */
    private List<Pair<double[], String>> histogram(EmpiricalDistribution dist) {
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
    protected String stringForBin(long n, long N, int width) {
        String output = "";
        double value = (n / (double) N) * 100d;
        int maxBin = locateBin(value, width, 0d, 100d);
        for(int i = 0; i < width; i++){
           output += i < maxBin ? CHARACTER_HISTOGRAM : CHARACTER_EMPTY_BIN;
        }
        return output;
    }
}
