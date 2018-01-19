CLASSPATH = "lib/spark-core_2.11-2.2.0.jar:lib/spark-tags_2.11-2.2.0.jar:lib/hadoop-common-2.6.5.jar:lib/hadoop-annotations-2.6.5.jar:lib/jackson-annotations-2.6.5.jar"
TARGET = target
SCALA_HOME = /Users/rashmidwaraka/Desktop/MR/scala-2.11.8
SPARK_HOME = /usr/local/Cellar/apache-spark/2.2.0

# 0 - kmeans, 1 - agglomerative for Subproblem1
ALGO = 0

#Data Subset
INPUT1 = input/MillionSongSubset/song_info.csv
#INPUT1 = input/MillionSongSubset/song_info_sample.csv
INPUT2 = input/MillionSongSubset/similar_artists.csv
INPUT3 = input/MillionSongSubset/artist_terms.csv


#Data Fullset
#INPUT1 = input/MillionSongDataset/song_info.csv
#INPUT2 = input/MillionSongDataset/similar_artists.csv
#INPUT3 = input/MillionSongDataset/artist_terms.csv

OUTPUT = output/MillionSongSubset/
#OUTPUT = output/MillionSongDataset/

gunzip:
	gunzip ./input/MillionSongSubset/*
	gunzip ./input/MillionSongDataset/*

gzip:
	-gzip ./input/MillionSongSubset/*;
	 gzip ./input/MillionSongDataset/*

all: clean build run report

allG: clean build runG

allC: clean build runC

clean:
	-rm -rf op1
	-rm -rf target

runG:
	$(SPARK_HOME)/bin/spark-submit --class org.neu.Graphs --master local --driver-memory 6g Clustering.jar $(INPUT1) $(INPUT2) $(INPUT3) $(OUTPUT)

runC:
	$(SPARK_HOME)/bin/spark-submit --class org.neu.Clustering --master local --driver-memory 6g Clustering.jar $(ALGO) $(INPUT1) $(OUTPUT)


run:
	$(SPARK_HOME)/bin/spark-submit --class org.neu.Graphs --master local --driver-memory 6g Clustering.jar $(INPUT1) $(INPUT2) $(INPUT3) $(OUTPUT)
	$(SPARK_HOME)/bin/spark-submit --class org.neu.Clustering --master local --driver-memory 6g Clustering.jar $(ALGO) $(INPUT1) $(OUTPUT)

compile:
	mkdir target
	$(SCALA_HOME)/bin/scalac -classpath $(CLASSPATH) -d $(TARGET) src/main/scala/org/neu/*.scala

build: compile
	jar cvfm Clustering.jar MANIFEST.MF -C target/ .

report:
	Rscript -e "rmarkdown::render('./report/A7_report.Rmd')"