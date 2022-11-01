#!/bin/bash
PATH=/usr/bin:/sbin:/usr/local/bin; export PATH

pwd

echo "Argument: $1"
MYROOT=/home/holymoly/JavaProcessor
cd $MYROOT
java -cp $MYROOT/Processor.jar folyoz.processor.independent.CheckAndRunJAR "$1"