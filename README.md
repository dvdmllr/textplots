# Boxplots
Boxplots is a tiny Java library that creates simple ascii-boxplots for input datasets. Build to quickly print data distribution to logging output from code or inspect data from command line.

## Usage
### From Code

```java
double[] dataSeries1 = {1d, 2d, 5d, 20d, 3d, 22d, 4d, 4d, 2d, 5d};
double[] dataSeries2 = {11d, 2d, 2d, 20d, 0d, 11d, 5d, 10d, 10d, 12d, 0d, 11d, 5d, 10d, 10d, 12d};
List<Pair<String, double[]>> data = new ArrayList<>();
data.add(Pair.create("dataSeries1", dataSeries1));
data.add(Pair.create("dataSeries2", dataSeries2));

Boxplots.printPlots(data);
```

This will produce the following output to command line:

```
dataSeries1|  |-[   |          ]-----------------------------||
dataSeries2||-----[               | ]-------------------|     |
           |0,00                                         22,00|
```

### From Command Line
mvn:install will produce an executable jar in the target folder. The program only expects a string representation of a data series as input object. Running on custom data is as easy as:

```
java -jar boxplots-1.0.jar -min 0 -max 20 -data '{series1|1,2,1,2,3,3,4,5,8,2,1}{series2|1,2,1,9,3,7,4,15,8,2,1}'
```

Each data series is enclosed with curly brackets and contains a name and the data points divided by a pipe ("|"). Data is split by a comma (","). Future versions will include parsing of csv files to allow for handling of larger data sets. Setting optional min and max parameters will visually restrict / expand the graph to given range.

## Next Steps
This library will be completed as I see the need for use in other personal projects. Ideas include

* An improved CLI which reads CSV and other data formats
* Print Tukey boxplots (different treatment of Whiskers and plotting of outliers)
* Improve the legend by showing meaningful values between the min and max
* Allow customized formatting
* Plot not only boxplots, but also histograms/scatterplots/... to command line

Feel free to contribute!
