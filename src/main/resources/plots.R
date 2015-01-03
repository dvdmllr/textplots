library(ggplot2)
library(reshape2)
# boxplot
df <- data.frame(iris$Sepal.Width, iris$Sepal.Length) 
md <- melt(df, variables=(c("iris.Sepal.Width", "iris.Sepal.Length")))
qplot(factor(0),value,data=md, geom='boxplot', color=variable) + 
  xlab("") + 
  coord_flip() 
# histogram
ggplot(iris, aes(x=Sepal.Length)) +
  geom_histogram(colour="black", fill="white", breaks=seq(4.3,7.9,0.45),right=TRUE) 
# scatterplot
ggplot(iris, aes(x=Sepal.Length, y=Sepal.Width)) + 
  geom_point(shape=3)