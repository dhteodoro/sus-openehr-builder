#!/bin/bash

usage(){
 echo "Usage: $0 [-composition -version -ehr] [-json -xml] [-aih -apac] patient_dir output_dir"
 exit 1
}

JAVA_OPS=""

# define is_file_exits function 
# $f -> store argument passed to the script
is_dir_exits(){
 local d="$1"
 [[ -d "$d" ]] && return 0 || return 1
}

EHR_OBJ=$1
OUT_TYPE=$2
COM_TYPE=$3
PAT_DIR=$4
OUT_DIR=$5

if [ "$EHR_OBJ" = "-ehr" ]; then
 EHR_OBJ="-t ehr"
elif [ "$EHR_OBJ" = "-composition" ]; then
 EHR_OBJ="-t composition"
elif [ "$EHR_OBJ" = "-version" ]; then
 EHR_OBJ="-t version"
else
 usage
fi



if [ "$OUT_TYPE" != "-json" ]; then
 OUT_TYPE="-f json"
elif [ "$OUT_TYPE" != "-xml" ]; then
 OUT_TYPE="-f xml"
else
 usage
fi

if [ "$COM_TYPE" = "-apac" ]; then
 COM_TYPE=""
elif [ "$COM_TYPE" = "-aih" ]; then
 COM_TYPE="--aih"
else
 usage
fi

# invoke  usage
# call usage() function if arguments not supplied
[[ $# -ne 5 ]] && usage

# check if directories exist
if ! ( is_dir_exits "$PAT_DIR" ); then
 echo "Directory $PAT_DIR not found"
 exit 1
fi

if ! ( is_dir_exits "$OUT_DIR" ); then
 echo "Directory $OUT_DIR not found"
 exit 1
fi

# init
# look for empty dir 
if [ "$(ls -A $PAT_DIR)" ]; then
 echo "Processing files..."
 COUNT=1
 for f in `ls $PAT_DIR`; do

  # save the output per process on diferent directories
  if [ ! -d "$OUT_DIR/$COUNT" ]; then
   mkdir -p "$OUT_DIR/$COUNT"
  fi
  
  # do the job
  java "$JAVA_OPS" -cp uber-sus-openehr-builder-1.0.1-SNAPSHOT.jar br.uerj.lampada.openehr.susbuilder.EHRGenerator "$EHR_OBJ" "$COM_TYPE" "$OUT_TYPE" -p "$PAT_DIR/$f" -o "$OUT_DIR/$COUNT" &

  COUNT=$(($COUNT + 1))
 done
else
 echo "$PAT_DIR is empty"
 exit 1
fi
