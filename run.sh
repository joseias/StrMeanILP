#!/bin/bash
#source ~/.bashrc
# Warning: Add an space after each command line if there are \r
#
# It receives  parameters:
# $1 path to java JDK   -> for instace: /home/user1/Programs/jdk/bin/java
# $2 full path to the benchmark file
# $3 full path to the folder to output results
#
export GUROBI_HOME="/opt/gurobi751/linux64"
export PATH="${PATH}:${GUROBI_HOME}/bin"
export  LD_LIBRARY_PATH="${LD_LIBRARY_PATH}:${GUROBI_HOME}/lib"
export  GRB_LICENSE_FILE="${GUROBI_HOME}/bin/gurobi.lic"

LIB_GRB="/opt/gurobi751/linux64/lib/*"
LIB_THIRD_PARTY="/shared/home/rasin/cadenaMedia/StrMeanILP/Exp/lib/*"
LIB_STRMEAN="/shared/home/rasin/cadenaMedia/StrMeanILP/Exp/dist/*"


# Other options
# -verbose
# -om $1.lp
JAVA_PATH=$1
IN_FILE=$2
OUT_FOLDER=$3
TO=3600

if [ ! -d $OUT_FOLDER ]; then
mkdir $OUT_FOLDER
fi

F="$(basename -- $IN_FILE)"
EXT="${F#*.}"
FN="$(basename $F .$EXT)"


TMP_FOLDER="/tmp/$$.tmp"
TMP_OUT_FOLDER_BENCHMARK=$TMP_FOLDER/$FN

rm -r -f $TMP_FOLDER
mkdir -p $TMP_FOLDER

cp $IN_FILE $TMP_FOLDER
mkdir $TMP_OUT_FOLDER_BENCHMARK

TMP_IN=$TMP_FOLDER/$F
TMP_OUT_FILE=$TMP_OUT_FOLDER_BENCHMARK"/"$FN".out"

JAVA_CLASS_PATH=$LIB_GRB:$LIB_THIRD_PARTY:$LIB_STRMEAN:

MAX_MEM="4G"
timeout -s SIGINT $TO $JAVA_PATH/java -Xmx$MAX_MEM -cp $JAVA_CLASS_PATH optimization.ILP_StrMean -in $TMP_IN -ops $TMP_OUT_FILE -opm 

IFS=$'\n'
for line in $(cat $TMP_OUT_FILE); do
OBJ=$(echo $line|awk -F "-" '{print $1}'|awk -F ":" '{print $2}')
TIME=$(echo $line|awk -F "-" '{print $2}'|awk -F ":" '{print $2}')
STRMEAN=$(echo $line|awk -F "-" '{print $3}'|awk -F ":" '{print $2}')
echo $STRMEAN>$TMP_OUT_FOLDER_BENCHMARK/$FN"-"$OBJ"-"$TIME".sol"
done

rm $TMP_OUT_FILE
cp -r $TMP_OUT_FOLDER_BENCHMARK/ $OUT_FOLDER
rm -r $TMP_FOLDER
