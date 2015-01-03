library(ggplot2)
library(reshape2)
# boxplot
df <- data.frame(iris$Sepal.Width, iris$Sepal.Length) 
md <- melt(df, variables=(c("iris.Sepal.Width", "iris.Sepal.Length")))
qplot(factor(0),value,data=md, geom='boxplot', color=variable) + xlab("") + coord_flip() 
# histogram
ggplot(iris, aes(x=Sepal.Length)) + geom_histogram(binwidth=.45, colour="black", fill="white") + scale_x_reverse() + coord_flip() 
# scatterplot
ggplot(iris, aes(x=Sepal.Length, y=Sepal.Width)) + geom_point(shape=3)  
