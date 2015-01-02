package de.davidm.textplots;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Boxplots provides a set of static methods to print boxplots to command line or format data to a boxplot string
 */
public class Boxplot extends Plot {

    public static final String CHARACTER_BOXPLOT_MINMAX = "|";
    public static final String CHARACTER_BOXPLOT_MEDIAN = "|";
    public static final String CHARACTER_BOXPLOT_QUARTILE_LOW = "[";
    public static final String CHARACTER_BOXPLOT_QUARTILE_HIGH = "]";
    public static final String CHARACTER_BOXPLOT_WHISKER = "-";
    public static final String CHARACTER_BOXPLOT_BODY_FILL = "░";

    private final List<Pair<String, double[]>> data;
    private final int width;
    private final Double min;
    private final Double max;
    private final boolean legend;

    private Boxplot(BoxplotBuilder boxplotBuilder) {
        this.data = boxplotBuilder.data;
        this.width = boxplotBuilder.width;
        this.legend = boxplotBuilder.legend;

        // Set min and max values
        Pair<Double, Double> minMaxData = getMinimumAndMaximum(data);
        min = boxplotBuilder.min != null ? boxplotBuilder.min : minMaxData.getFirst();
        max = boxplotBuilder.max != null ? boxplotBuilder.max : minMaxData.getSecond();
        // Check for correct min and max value settings
        Preconditions.checkState(min<=max, "Minimum value needs to be smaller than the maximum");
    }

    /**
     * Construct a Boxplot
     */
    public static class BoxplotBuilder {
        List<Pair<String, double[]>> data;
        int width = DEFAULT_WIDTH;
        Double min = null;
        Double max = null;
        boolean legend = true;

        /**
         * Construct a Boxplot
         * @param data a list of data series and a name for each in the form of : Pair(name of data series, data)
         */
        public BoxplotBuilder(List<Pair<String, double[]>> data){
           this.data = data;
        }

        /**
         * @param width width of plots - attention: additional width is added by names and table formatting
         * @return
         */
        public BoxplotBuilder setWidth(int width){
            this.width = width;
            return this;
        }

        /**
         * Set minimum and maximum value shown in plots
         * @param min minimum value shown in plot (optional)
         * @param max maximum value shown in plot (optional)
         */
        public BoxplotBuilder setPlotLimits(Double min, Double max){
            this.min = min;
            this.max = max;
            return this;
        }

        /**
         * @param enable true to show legend, false otherwise
         */
        public BoxplotBuilder enableLegend(boolean enable){
            this.legend = enable;
            return this;
        }

        /**
         * @return a Plot object with user settings
         */
        public Plot plotObject(){
            // Check for correct width
            Preconditions.checkState(width>=MIN_WIDTH && width <=MAX_WIDTH,
                    "Width is set to " + width + " but needs to be in " + "[" + MIN_WIDTH + "," + MAX_WIDTH + "]");
            return new Boxplot(this);
        }
    }

    /**
     * Print the Boxplots to command line
     */
    @Override
    public void printPlot(){
        List<Pair<String, String>> plots = boxPlots();

        if(legend) {
            int maxLength = 0;

            // Find max length for name of data series
            for (Pair<String, String> element : plots) {
                int length = element.getFirst().length();
                if (length > maxLength) maxLength = length;
            }

            /*
             * Print a line for each printPlot
             */
            for (Pair<String, String> element : plots) {
                String line = "";
                line += element.getFirst();
                // fill blancs
                for (int i = element.getFirst().length() + 1; i <= maxLength; i++) {
                    line += CHARACTER_EMPTY_BIN;
                }
                // Add a character to divide name and plot
                line += CHARACTER_COLUMN_DIVISOR;
                // Plot data as boxplot
                line += element.getSecond();
                // Add a final character to show that the plot ended
                line += CHARACTER_COLUMN_DIVISOR;
                System.out.println(line);
            }

            /*
             * Add a legend below plots
             */
            String minString = df.format(min);
            String maxString = df.format(max);

            String line = "";
            // Fill left
            for (int i = 0; i < maxLength; i++) {
                line += CHARACTER_EMPTY_BIN;
            }
            line += CHARACTER_COLUMN_DIVISOR;
            // Write the minimum to the beginning of plots
            line += minString;
            // Add spaces between min and max
            for (int i = 0; i < width - maxString.length() - minString.length(); i++) {
                line += CHARACTER_EMPTY_BIN;
            }
            // Write the maximum to the end of plots
            line += maxString;
            line += CHARACTER_COLUMN_DIVISOR;
            System.out.println(line);
        } else {
            String out = "";
            for(Pair<String, String> line : plots){
                out += line.getSecond() + "\n";
            }
            System.out.println(out);
        }
    }

    /**
     * @return a list of pairs containing (name of data series, boxplot string)
     */
    protected List<Pair<String, String>> boxPlots(){
        // Create a string representation of a boxplot for each statistic
        List<Pair<String, String>> output = new ArrayList<>();
        for(Pair<String,  double[]> element : data){
            DescriptiveStatistics stats = new DescriptiveStatistics(element.getSecond());
            // Note: this uses a simple approach where the whiskers represent min and max of each statistic
            double minLocal = stats.getMin();
            double maxLocal = stats.getMax();
            double quartileLow = stats.getPercentile(25);
            double quartileHigh = stats.getPercentile(75);
            double median = stats.getPercentile(50);
            // Create string using width bins
            String boxplotString = boxPlotString(min, max, minLocal, maxLocal, quartileLow, quartileHigh, median, width);
            output.add(Pair.create(element.getFirst(), boxplotString));
        }

        return output;
    }

    /**
     * Create a string representation of a Boxplot for given variables
     * @param min visual minimum value
     * @param max visual maximum value
     * @param minLocal actual minimum value for this data series
     * @param maxLocal actual maximum value for this data series
     * @param quartileLow 0.25 quartile for this data series
     * @param quartileHigh 0.75 quartile for this data series
     * @param median 0.5 quartile for this data series
     * @param width width of plots
     * @return a Boxplot as String
     */
    protected static String boxPlotString(
        double min, double max, double minLocal, double maxLocal, double quartileLow, double quartileHigh, double median, int width) {
        String[] out = new String[width];
        // Initially fill
        for(int i = 0; i < out.length; i++){
            out[i] = CHARACTER_EMPTY_BIN;
        }

        // Locate bins for visual boundaries
        int minBound = locateBin(min, width, min, max);
        int maxBound = locateBin(max, width, min, max);

        // Draw quartiles
        int binQuartileLow = locateBin(quartileLow, width, min, max);
        if(!isOutOfBounds(binQuartileLow, minBound, maxBound)&&out[binQuartileLow].equals(CHARACTER_EMPTY_BIN)) {
            out[binQuartileLow] = CHARACTER_BOXPLOT_QUARTILE_LOW;
        }
        int binQuartileHigh = locateBin(quartileHigh, width, min, max);
        if(!isOutOfBounds(binQuartileHigh, minBound, maxBound)&&out[binQuartileHigh].equals(CHARACTER_EMPTY_BIN)) {
            out[binQuartileHigh] = CHARACTER_BOXPLOT_QUARTILE_HIGH;
        }

        // Draw median
        int binMedian = locateBin(median, width, min, max);
        if(!isOutOfBounds(binMedian, minBound, maxBound)&&out[binMedian].equals(CHARACTER_EMPTY_BIN)) out[binMedian] = CHARACTER_BOXPLOT_MEDIAN;

        // Fill bins between median and quartiles
        for(int i = binQuartileLow + 1; i < binMedian; i++){
            if(!isOutOfBounds(i, minBound, maxBound)) out[i] = CHARACTER_BOXPLOT_BODY_FILL;
        }
        for(int i = binMedian + 1; i < binQuartileHigh; i++){
            if(!isOutOfBounds(i, minBound, maxBound)) out[i] = CHARACTER_BOXPLOT_BODY_FILL;
        }

        // Draw min/max
        int binMin = locateBin(minLocal, width, min, max);
        if(!isOutOfBounds(binMin, minBound, maxBound)&&out[binMin].equals(CHARACTER_EMPTY_BIN)) {
            out[binMin] = CHARACTER_BOXPLOT_MINMAX;
        }
        int binMax = locateBin(maxLocal, width, min, max);
        if(!isOutOfBounds(binMax, minBound, maxBound)&&out[binMax].equals(CHARACTER_EMPTY_BIN)) {
            out[binMax] = CHARACTER_BOXPLOT_MINMAX;
        }

        // Fill bins between min/max and quartiles
        for(int i = binMin + 1; i < binQuartileLow; i++){
            if(!isOutOfBounds(i, minBound, maxBound)) out[i] = CHARACTER_BOXPLOT_WHISKER;
        }
        for(int i = binQuartileHigh + 1; i < binMax; i++){
            if(!isOutOfBounds(i, minBound, maxBound)) out[i] = CHARACTER_BOXPLOT_WHISKER;
        }

        // Create the output string
        String output = "";
        for(String s : out){
            output += s;
        }
        return output;
    }

}
