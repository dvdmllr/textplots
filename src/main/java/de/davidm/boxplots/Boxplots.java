package de.davidm.boxplots;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.Pair;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Boxplots provides a set of static methods to print boxplots to command line or format data to a boxplot string
 */
public class Boxplots {

    public static final String CHARACTER_COLUMN_DIVISOR = "|";
    public static final String CHARACTER_BOXPLOT_MINMAX = "|";
    public static final String CHARACTER_BOXPLOT_MEDIAN = "|";
    public static final String CHARACTER_BOXPLOT_QUARTILE_LOW = "[";
    public static final String CHARACTER_BOXPLOT_QUARTILE_HIGH = "]";
    public static final String CHARACTER_BOXPLOT_WHISKER = "-";
    public static final String CHARACTER_EMPTY_BIN = " ";

    public static final int DEFAULT_WIDTH = 50;
    public static final int MIN_WIDTH = 20;
    public static final int MAX_WIDTH = 100;

    /**
     * Calls printPlots(data, width) with a fixed width of 50 using actual min and max of data for plot display ranges
     * @param data  a list of data series and a name for each in the form of : Pair(name of data series, data)
     */
    public static void printPlots(List<Pair<String, double[]>> data){
        printPlots(data, DEFAULT_WIDTH);
    }

    /**
     * Calls printPlots(data, width) using actual min and max of data for plot display ranges
     * @param data  a list of data series and a name for each in the form of : Pair(name of data series, data)
     * @param width width of plots - attention: additional width is added by names and table formatting
     */
    public static void printPlots(List<Pair<String, double[]>> data, int width){
        printPlots(data, width, null, null);
    }

    /**
     * Calls printPlots(data, width) with a fixed width of 50
     * @param data  a list of data series and a name for each in the form of : Pair(name of data series, data)
     * @param min minimum value shown in plot (optional)
     * @param max maximum value shown in plot (optional)
     */
    public static void printPlots(List<Pair<String, double[]>> data, Double min, Double max){
        printPlots(data, DEFAULT_WIDTH, min, max);
    }

    /**
     * Prints boxplots to the command line for given input
     * @param data a list of data series and a name for each in the form of : Pair(name of data series, data)
     * @param width width of plots - attention: additional width is added by names and table formatting
     * @param min minimum value shown in plot (optional)
     * @param max maximum value shown in plot (optional)
     */
    public static void printPlots(List<Pair<String, double[]>> data, int width, Double min, Double max){
        List<Pair<String, String>> plots = boxPlots(data, width, min, max);
        int maxLength = 0;

        // find max length for name of data series
        for(Pair<String, String> element : plots){
            int length = element.getFirst().length();
            if(length > maxLength) maxLength = length;
        }

        /*
         * Print a line for each plot
         */
        for(Pair<String, String> element : plots){
            String line = "";
            line += element.getFirst();
            // fill blancs
            for(int i = element.getFirst().length() + 1; i <= maxLength; i++){
                line += CHARACTER_EMPTY_BIN;
            }
            // add a character to divide name and plot
            line += CHARACTER_COLUMN_DIVISOR;
            // plot data as boxplot
            line += element.getSecond();
            // add a final character to show that the plot ended
            line += CHARACTER_COLUMN_DIVISOR;
            System.out.println(line);
        }

        /*
         * Add a legend below plots
         */
        Pair<Double, Double> minMax = getMinimumAndMaximum(min, max, data);
        DecimalFormat df = new DecimalFormat("0.00");
        String minString = df.format(minMax.getFirst());
        String maxString = df.format(minMax.getSecond());

        String line = "";
        // fill left
        for(int i = 0; i < maxLength; i++){
            line += CHARACTER_EMPTY_BIN;
        }
        line += CHARACTER_COLUMN_DIVISOR;
        // write the minimum to the beginning of plots
        line += minString;
        // add spaces between min and max
        for(int i = 0; i < width - maxString.length() - minString.length(); i++){
            line += CHARACTER_EMPTY_BIN;
        }
        // write the maximum to the end of plots
        line += maxString;
        line += CHARACTER_COLUMN_DIVISOR;
        System.out.println(line);
    }

    /**
     * @param data a list of data series and a name for each in the form of : Pair(name of data series, data)
     * @param width width of plots
     * @return a list of pairs containing (name of data series, boxplot string)
     */
    public static List<Pair<String, String>> boxPlots(List<Pair<String, double[]>> data, int width,  Double min, Double max){

        // Check for correct width
        Preconditions.checkState(width>=MIN_WIDTH && width <= MAX_WIDTH,
                                 "Width is set to " + width + " but needs to be in " + "[" + MIN_WIDTH + "," + MAX_WIDTH + "]");

        Pair<Double, Double> minMax = getMinimumAndMaximum(min, max, data);
        // Check for correct min and max value settings
        Preconditions.checkState(minMax.getFirst()<=minMax.getSecond(), "Minimum value needs to be smaller than the maximum");

        // Create a string representation of a boxplot for each statistic
        List<Pair<String, String>> output = new ArrayList<>();
        for(Pair<String, DescriptiveStatistics> element : toDescriptiveStats(data)){
            DescriptiveStatistics stats = element.getSecond();
            // Note: this uses a simple approach where the whiskers represent min and max of each statistic
            double minLocal = stats.getMin();
            double maxLocal = stats.getMax();
            double quartileLow = stats.getPercentile(25);
            double quartileHigh = stats.getPercentile(75);
            double median = stats.getPercentile(50);
            // Create string using width bins
            String boxplotString = boxPlotString(minMax.getFirst(), minMax.getSecond(), minLocal, maxLocal, quartileLow, quartileHigh, median, width);
            output.add(Pair.create(element.getFirst(), boxplotString));
        }

        return output;
    }

    /**
     * Converts a list of String, double[] pairs into a list of pairs containing String, DescriptiveStats
     * @param data
     * @return
     */
    private static List<Pair<String, DescriptiveStatistics>> toDescriptiveStats(List<Pair<String, double[]>> data){
        List<Pair<String, DescriptiveStatistics>> output = new ArrayList<>();
        for(Pair<String, double[]> dataSeries : data){
            output.add(Pair.create(dataSeries.getFirst(), new DescriptiveStatistics(dataSeries.getSecond())));
        }
        return output;
    }

    /**
     * @param data set of data series
     * @return a pair of minimum and maximum values for plots
     */
    protected static Pair<Double, Double> getMinimumAndMaximum(Double min, Double max, List<Pair<String, double[]>> data){
        // get global min and max if no external value has been set
        Double globalMin = null, globalMax = null;
        if(min==null||max==null){
            for(Pair<String, double[]> dataSeries : data){
                for(double element : dataSeries.getValue()){
                    if(globalMin == null || globalMin > element){
                        globalMin = element;
                    }
                    if(globalMax == null || globalMax < element){
                        globalMax = element;
                    }
                }
            }
        }
        // return set external values if not null, global min/max otherwise
        return Pair.create(min != null ? min : globalMin, max != null ? max : globalMax);
    }

    /**
     * Create a string representation of a boxplot for given variables
     * @param min global minimum value
     * @param max global maximum value
     * @param minLocal minimum value for this data series
     * @param maxLocal maximum value for this data series
     * @param quartileLow 0.25 quartile for this data series
     * @param quartileHigh 0.75 quartile for this data series
     * @param median 0.5 quartile for this data series
     * @param width width of plots
     * @return
     */
    protected static String boxPlotString(
        double min, double max, double minLocal, double maxLocal, double quartileLow, double quartileHigh, double median, int width) {
        String[] out = new String[width];
        // initially fill
        for(int i = 0; i < out.length; i++){
            out[i] = CHARACTER_EMPTY_BIN;
        }

        // locate bins for visual boundaries
        int minBound = locateBin(min, width, min, max);
        int maxBound = locateBin(max, width, min, max);

        // draw quartiles
        int binQuartileLow = locateBin(quartileLow, width, min, max);
        if(!outOfBounds(binQuartileLow, minBound, maxBound)&&out[binQuartileLow].equals(CHARACTER_EMPTY_BIN)) {
            out[binQuartileLow] = CHARACTER_BOXPLOT_QUARTILE_LOW;
        }
        int binQuartileHigh = locateBin(quartileHigh, width, min, max);
        if(!outOfBounds(binQuartileHigh, minBound, maxBound)&&out[binQuartileHigh].equals(CHARACTER_EMPTY_BIN)) {
            out[binQuartileHigh] = CHARACTER_BOXPLOT_QUARTILE_HIGH;
        }

        // draw median
        int binMedian = locateBin(median, width, min, max);
        if(!outOfBounds(binMedian, minBound, maxBound)&&out[binMedian].equals(CHARACTER_EMPTY_BIN)) out[binMedian] = CHARACTER_BOXPLOT_MEDIAN;

        // fill bins between median and quartiles
        for(int i = binQuartileLow + 1; i < binMedian; i++){
            if(!outOfBounds(i, minBound, maxBound)) out[i] = " ";
        }
        for(int i = binMedian + 1; i < binQuartileHigh; i++){
            if(!outOfBounds(i, minBound, maxBound)) out[i] = " ";
        }

        // draw min/max
        int binMin = locateBin(minLocal, width, min, max);
        if(!outOfBounds(binMin, minBound, maxBound)&&out[binMin].equals(CHARACTER_EMPTY_BIN)) {
            out[binMin] = CHARACTER_BOXPLOT_MINMAX;
        }
        int binMax = locateBin(maxLocal, width, min, max);
        if(!outOfBounds(binMax, minBound, maxBound)&&out[binMax].equals(CHARACTER_EMPTY_BIN)) {
            out[binMax] = CHARACTER_BOXPLOT_MINMAX;
        }

        // fill bins between min/max and quartiles
        for(int i = binMin + 1; i < binQuartileLow; i++){
            if(!outOfBounds(i, minBound, maxBound)) out[i] = CHARACTER_BOXPLOT_WHISKER;
        }
        for(int i = binQuartileHigh + 1; i < binMax; i++){
            if(!outOfBounds(i, minBound, maxBound)) out[i] = CHARACTER_BOXPLOT_WHISKER;
        }

        // create the output string
        String output = "";
        for(String s : out){
            output += s;
        }
        return output;
    }

    private static boolean outOfBounds(int bin, int minBound, int maxBound) {
        return (bin>maxBound||bin<minBound);
    }

    /**
     * @param value any given value of the data series
     * @param width width of the plot
     * @param min global minimum value
     * @param max global maximum value
     * @return bin in plot for a given value
     */
    protected static int locateBin(double value, int width, Double min, Double max) {
        double span = max - min;
        double binWidth = span / (1d * width - 1);
        double bin = Math.floor((value - min) / binWidth);
        return (int) bin;
    }

    /*
     * Run from command line
     */

    @Option(name="-width", usage="width of printed boxplots")
    private int cmdWidth = DEFAULT_WIDTH;

    @Option(name="-data", usage="data to be plotted in the form of {name|value1, value2, ...}{...}", required=true)
    private String cmdData = "";

    @Option(name="-min", usage="maximum value in plot")
    private Double cmdMin = null;

    @Option(name="-max", usage="minimum value in plot")
    private Double cmdMax = null;

    public static void main(String[] args) throws CmdLineException {
        new Boxplots().run(args);
    }

    private void run(String[] args) throws CmdLineException {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
            // transform data string to descriptive stat objects
            List<Pair<String, double[]>> data = parse(cmdData);
            Boxplots.printPlots(data, cmdWidth, cmdMin, cmdMax);
        } catch (CmdLineException e) {
            throw new CmdLineException(parser, "Could not parse given arguments", e);
        }
    }

    /**
     * Parse an input String into a list of data series
     * @param cmdData String representing a list of data series
     * @return
     */
    protected List<Pair<String, double[]>> parse(String cmdData) {
        List<Pair<String, double[]>> output = new ArrayList<>();
        String[] split = cmdData.split("\\}");
        for(String dataSeries : split){
            dataSeries = dataSeries.replaceAll("\\{", "");
            String[] dataSeriesSplit = dataSeries.split("\\|");
            String name = dataSeriesSplit[0];
            double[] data = parseData(dataSeriesSplit[1]);
            output.add(Pair.create(name, data));
        }
        return output;
    }

    /**
     * Parse a string containing values split by a comma into an array of doubles
     * @param doubles input string containing double values split by comma
     * @return a double array of values in string
     */
    protected double[] parseData(String doubles) {
        String[] split = doubles.split("\\,");
        double[] doubleArray = new double[split.length];
        for(int i = 0; i < split.length; i++){
            doubleArray[i] = Double.valueOf(split[i]).doubleValue();
        }
        return doubleArray;
    }
}
