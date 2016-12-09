#!/bin/bash
ps aux | grep sus-openehr-builder | awk '{print $2}' | xargs kill -9
ps aux | grep iostat | awk '{print $2}' | xargs kill -9
ps aux | grep collectl | awk '{print $2}' | xargs kill -9
rm -rf out_*
rm -rf iostat.load
rm -rf collectl.*
rm -rf log.er*
exit 0
