package de.davidm.boxplots;

import org.apache.commons.math3.util.Pair;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.util.ArrayList;
import java.util.List;

public class CLI {
    /*
     * Run from command line
     */

    @Option(name="-width", usage="width of printed boxplots")
    private Integer cmdWidth = Plot.DEFAULT_WIDTH;

    @Option(name="-height", usage="height of printed boxplots")
    private Integer cmdHeight= Plot.DEFAULT_HEIGHT;

    @Option(name="-data", usage="data to be plotted in the form of {name|value1, value2, ...}{...}", required=true)
    private String cmdData = "";

    @Option(name="-min", aliases="-minX", usage="maximum value in plot (for X)")
    private Double cmdMin = null;

    @Option(name="-max", aliases="-maxX", usage="minimum value in plot (for X)")
    private Double cmdMax = null;

    @Option(name="-minY", usage="maximum value in plot (for Y)")
    private Double cmdMinY = null;

    @Option(name="-maxY", usage="minimum value in plot (for Y)")
    private Double cmdMaxY = null;

    @Option(name="-type", usage="type of plot {boxplot, heatmap}")
    private String type = "boxplot";

    public static void main(String[] args) throws CmdLineException {
        new CLI().run(args);
    }

    public void run(String[] args) throws CmdLineException {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
            // transform data string to descriptive stat objects
            List<Pair<String, double[]>> data = parse(cmdData);
            Plot plot = null;
            if(type.equals("boxplot")){
                plot = new Boxplot.BoxplotBuilder(data)
                        .setPlotLimits(cmdMin, cmdMax)
                        .setWidth(cmdWidth)
                        .plotObject();
            } else if(type.equals("heatmap")){
                if(data.size()!=2){
                    throw new IllegalArgumentException("Data needs to contain exactly 2 set which will be parsed to X and Y values");
                }
                plot = new Heatmap.HeatmapBuilder(data.get(0), data.get(1))
                        .setPlotLimits(cmdMin, cmdMax, cmdMinY, cmdMaxY)
                        .setSize(cmdWidth, cmdHeight)
                        .plotObject();
            } else {
                throw new IllegalArgumentException("Could not match plot type. Needs to be one of (boxplot, heatmap)");
            }
            plot.printPlot();
        } catch (CmdLineException e) {
            throw new CmdLineException(parser, "Could not parse given arguments", e);
        }
    }

    /**
     * Parse an input String into a list of data series
     *
     * @param cmdData String representing a list of data series
     *
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
     *
     * @param doubles input string containing double values split by comma
     *
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
