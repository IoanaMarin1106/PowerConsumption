#!/bin/bash
mkdir temp_files

source_cpu_temp_file="/sys/class/thermal/thermal_zone0/temp"
dest_cpu_temp_file="./temp_files/cpu_overall_temp"

source_load_avg="/proc/loadavg"
destination_load_avg="./temp_files/cpu_loadavg"

cores_load="/proc/stat"
destination_cores_load="./temp_files/cores_loadavg"

curr_freq="/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq"
max_freq="/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq"
min_freq="/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq"
dest_cpu_curr_freq="./temp_files/cpu_curr_freq"
dest_cpu_min_freq="./temp_files/cpu_min_freq"
dest_cpu_max_freq="./temp_files/cpu_max_freq"

core_0_frequency="/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq"
core_1_frequency="/sys/devices/system/cpu/cpu1/cpufreq/scaling_cur_freq"
core_2_frequency="/sys/devices/system/cpu/cpu2/cpufreq/scaling_cur_freq"
core_3_frequency="/sys/devices/system/cpu/cpu3/cpufreq/scaling_cur_freq"
core_4_frequency="/sys/devices/system/cpu/cpu4/cpufreq/scaling_cur_freq"
core_5_frequency="/sys/devices/system/cpu/cpu5/cpufreq/scaling_cur_freq"
core_6_frequency="/sys/devices/system/cpu/cpu6/cpufreq/scaling_cur_freq"
core_7_frequency="/sys/devices/system/cpu/cpu7/cpufreq/scaling_cur_freq"

dest_core_0_freq="./temp_files/cpu0_freq"
dest_core_1_freq="./temp_files/cpu1_freq"
dest_core_2_freq="./temp_files/cpu2_freq"
dest_core_3_freq="./temp_files/cpu3_freq"
dest_core_4_freq="./temp_files/cpu4_freq"
dest_core_5_freq="./temp_files/cpu5_freq"
dest_core_6_freq="./temp_files/cpu6_freq"
dest_core_7_freq="./temp_files/cpu7_freq"

# copy temp file from non-readable location to readable location (sdcard)
while [ true ]
do
  cp -f $source_cpu_temp_file $dest_cpu_temp_file
  cp -f $source_load_avg $destination_load_avg
  cp -f $cores_load $destination_cores_load

  if test -f $core_0_frequency; then
    cp -f $core_0_frequency $dest_core_0_freq
  fi

  if test -f $core_1_frequency; then
    cp -f $core_1_frequency $dest_core_1_freq 
  fi

  if test -f $core_2_frequency; then
    cp -f $core_2_frequency $dest_core_2_freq
  fi

  if test -f $core_3_frequency; then
    cp -f $core_3_frequency $dest_core_3_freq 
  fi

  if test -f $core_4_frequency; then
    cp -f $core_4_frequency $dest_core_4_freq 
  fi

  if test -f $core_5_frequency; then
    cp -f $core_5_frequency $dest_core_5_freq 
  fi

  if test -f $core_6_frequency; then
    cp -f $core_6_frequency $dest_core_6_freq 
  fi

  if test -f $core_7_frequency; then
    cp -f $core_7_frequency $dest_core_7_freq
  fi

  if test -f $curr_freq; then
    cp -f $curr_freq $dest_cpu_curr_freq
  fi

  if test -f $min_freq; then
    cp -f $min_freq $dest_cpu_min_freq
  fi

  if test -f $max_freq; then
    if cmp -s $max_freq $dest_cpu_max_freq; then
      printf 'The file "%s" is the same as "%s"\n' "$max_freq" "$dest_cpu_max_freq"
      cp -f $max_freq $dest_cpu_max_freq
    else 
      printf 'Max frequency was changed by the user\n'
    fi
  fi

  sleep 10
done
