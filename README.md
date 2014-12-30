# Boxplots
Boxplots is a tiny Java library that creates simple ascii-graphs for input datasets. Build to quickly print data distribution to logging output from code or inspect data from command line.

## Usage
### From Code

**Boxplots**

```java
List<Pair<String, double[]>> data = new ArrayList<>();
data.add(Pair.create("IRIS_SEPAL_LENGTH", IrisData.IRIS_SEPAL_LENGTH));
data.add(Pair.create("IRIS_SEPAL_WIDTH",  IrisData.IRIS_SEPAL_WIDTH));

Plot plot = new Boxplot.BoxplotBuilder(data).plotObject();
plot.printPlot();
```

This will produce the following output to command line:

```
IRIS_SEPAL_LENGTH|                   |-----[░░░░░|░░░░]------------||
IRIS_SEPAL_WIDTH ||-----[░|░]--------|                              |
                 |2.00                                          7.90|
```

**Heatmaps** (experimental)

Additionally the library allows to print heatmaps to command line. Here is an example:

```java
Plot plot = new Heatmap.HeatmapBuilder(
                Pair.create("IRIS_SEPAL_LENGTH", IrisData.IRIS_SEPAL_LENGTH),
                Pair.create("IRIS_SEPAL_WIDTH", IrisData.IRIS_SEPAL_WIDTH))
                .setSize(50, 20)
                .plotObject();

plot.printPlot();
```

This results in the following output (note that github markdown has not the best display here):

```
IRIS_SEPAL_WIDTH|4.40|░▒░░▒░▒░▒▒▒░▒░▒░▒▒░▒▒░░▒▒▒░▒▒▒░░▒░▒▒░░░▒░░░░░░▒░░░|
                |    |▒▒░░▒▒▒▒▒▓▒▒▒░▒▒▒▒▒▓▒▒░▒▒▒░▓▒▒▒▒▒▒▒▒░░▒▒░░░░░░▒░░▒|
                |    |▒▒▒▒▓▒▒▒▓▓▓▓▒▒▒▓▓▓▒▓▓▓▒▓▓▓▒▓▓▓▒▒▓▓▒▓▒▒▒▒▒▒▒░░▒▒▒░▒|
                |    |▒▓▒▒▓▓▓▒▓█▓▓▓▒▒▓▓▓▓█▓▓▒▓▓▓▒█▓▓▓▒▓▓▓▓▒▒▒▓▒▒▒░░▒▓▒░▒|
                |    |▒▒▓▒▒▓▓█▓███▒▓▒█▓██▓██▓▓██▓▓███▒▓█▒▓▓▒▒▒▓▒▒▒░▒▒▓░▒|
                |    |▒▓▒▒▓▓▓▓▓██▓▓▓▓▓▓▓▓██▓▒▓▓▓▓██▓▓▒▓▓▓▓▓▒▒▓▒▒▒▒▒▒▓▒░▒|
                |    |▒▓▓▒█▓▓▓█████▓▓███▓███▓███▓███▓▓██▓█▓▒▒▓▓▒▒▒▒▒▓▓░▒|
                |    |▓█▓▒█████████▓████████▓███▓████▓████▓▒▓█▓▒▓▒▒▒█▓░▓|
                |    |▒▓█▓▓███████▓█▓████████████████▒██▓██▓▒▓█▓▒▓▒▓▓█░▒|
                |    |▒▓▓▒▓▓▓█████▓▓▓███████▓███▓████▓██▓█▓▒▒▓▓▒▒▒▒▒▓▓░▒|
                |    |▒▓▓▒▓▓▓█▓███▓▓▓█▓█████▓▓██▓████▒▓█▓▓▓▒▒▓▓▒▒▒▒▒▓▓░▒|
                |    |▓▓▓▒█▓█▓███▓█▓▓▓██▓██▓▒███▓███▓▓█▓██▓▒▓▓▓▒▓░▒▒█▒░▓|
                |    |▒▒▓▒▒▓▓█▒███▒▓▒█▒██▓▓█▓▒▓██▓███▒▓█▒▓▓▒░▒▓▒░▒░▒▒▓░▒|
                |    |▒▒▒▒▒▒▒▒▓▓▓▓▒▒▒▓▓▓▓▓▓▓▒▓▓▓▒▓▓▓▓▒▓▓▒▒▒▒▒▒▒▒▒░░▒▒▒░▒|
                |    |▒▓▒░▓▒▓▒▓█▓▓▓▒▓▓▓▓▒█▓▓▒▓▓▓▒█▓▓▒▒▓▓▓▓▒░▒▓▒▒▒░▒▒▓▒░▒|
                |    |▒▒▒▒▒▒▒▓▓▓▓▓▒▒▒▓▓▓▓▓▓▓▒▓▓▓▓▓▓▓▓▒▓▓▒▒▒▒▒▒▒▒▒░░▒▒▒░▒|
                |    |░▒▒░▒▒▒▒▒▒▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒░▒▒▒▒▒░░▒▒░░░░░▒▒░░|
                |    |░▒▒░▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒░▒▒▒▒▒░░▒▒░░░░░▒░░░|
                |    |░░░░░░░▒▒▒▒▒░░░▒▒▒▒▒▒▒░▒▒▒▒▒▒▒▒░▒▒░▒░░░░░░░░░░░░░░|
                |2.00|░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░|
                     |4.30                                          7.90|
                     |                                 IRIS_SEPAL_LENGTH|
```

### From Command Line
mvn:install will produce an executable jar in the target folder which can be called with the following arguments:

| Argument | Usage | Default | Required |
| --- | --- | --- | --- |
| -data | _input data string, see below for an example_ | | *X* |
| -width | _width of plot_ | 50 (chars) | |
| -height | _height of plot (for heatmaps only)_ | 20 (chars) | |
| -min | _lower visible boundary of (x) axis_ | minimum value in data | |
| -max | _upper visible boundary of (x) axis_ | maximum value in data | |
| -minY | _lower visible boundary of (y) axis_ | minimum value in data | |
| -maxY | _upper visible boundary of (y) axis_ | maximum value in data | |
| -type | _type of plot (boxplot or heatmap)_ | boxplot | |

Here is an example:

```
java -jar boxplots-1.0.jar -min 0 -max 20
-data '{series1|1,2,1,2,3,3,4,5,8,2,1}{series2|1,2,1,9,3,7,4,15,8,2,1}'
```

Each data series is enclosed with curly brackets and contains a name and the data points divided by a pipe ("|"). Data is split by a comma (","). Future versions will include parsing of csv files to allow for handling of larger data sets. Setting optional min and max parameters will visually restrict / expand the graph to given range.


## Next Steps
This library will be completed as I see the need for use in other personal projects. Ideas include

* An improved CLI which reads CSV and other data formats
* Tukey boxplots (different treatment of Whiskers and plotting of outliers)
* Improve the legend by showing meaningful values between the min and max
* Allow customized formatting
* Add histograms
* Add colors

Feel free to contribute!
