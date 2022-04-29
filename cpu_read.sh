#!/bin/bash
mkdir temp_files

source_cpu_temp_file="/sys/devices/virtual/thermal/thermal_zone0/temp"
dest_cpu_temp_file="./temp_files/cpu_overall_temp"

source_load_avg="/proc/loadavg"
destination_load_avg="./temp_files/cpu_loadavg"

curr_freq="/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq"
max_freq="/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq"
min_freq="/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq"
dest_cpu_curr_freq="./temp_files/cpu_curr_freq"
dest_cpu_min_freq="./temp_files/cpu_min_freq"
dest_cpu_max_freq="./temp_files/cpu_max_freq"

# copy temp file from non-readable location to readable location (sdcard)
while [ true ]
do
  cp -f $source_cpu_temp_file $dest_cpu_temp_file
  cp -f $source_load_avg $destination_load_avg
  cp -f $curr_freq $dest_cpu_curr_freq
  cp -f $min_freq $dest_cpu_min_freq
  cp -f $max_freq $dest_cpu_max_freq
  sleep 10
done
