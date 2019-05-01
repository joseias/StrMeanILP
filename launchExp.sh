#!/bin/bash
# $1 path to the folder with all benchmark files
# $2 path to the result folder


JAVA_PATH="/shared/home/rasin/jdk1.8.0_201/bin/"
IN_FOLDER_BENCHMARK=$1
OUT_FOLDER=$2

for file in $(ls $IN_FOLDER_BENCHMARK)
do
#	fileName=$(basename $file)
#	#qsub -p -100 -pe make 7 -j yes -o $2/$fileName.log solvingScript.sh $1/$fileName $2
#	qsub -p -100 -pe make 7 -j yes -o $OUT_FOLDER/$file.log run.sh $JAVA_PATH $IN_FOLDER_BENCHMARK/$file $OUT_FOLDER
	qsub -p -100 -pe make 7 -j yes -o $OUT_FOLDER/$file.log -S /bin/bash /shared/home/rasin/cadenaMedia/StrMeanILP/Exp/run.sh $JAVA_PATH $IN_FOLDER_BENCHMARK/$file $OUT_FOLDER
#	/shared/home/rasin/cadenaMedia/StrMeanILP/Exp/run.sh $JAVA_PATH $IN_FOLDER_BENCHMARK/$file $OUT_FOLDER

done


