#!/bin/bash
mkdir user_data
touch "./user_data/cpu_min_freq.txt"
touch "./user_data/cpu_max_freq.txt"
touch "./user_data/cpu_curr_freq.txt"
touch "./user_data/cpu_governor.txt"

# source files -> where the user is going to write its values
source_cpu_min_freq="./user_data/cpu_min_freq.txt"
source_cpu_max_freq="./user_data/cpu_max_freq.txt"
source_cpu_curr_freq="./user_data/cpu_curr_freq.txt"
source_cpu_governor="./user_data/cpu_governor.txt"

chmod 777 $source_cpu_curr_freq
chmod 777 $source_cpu_min_freq
chmod 777 $source_cpu_max_freq
chmod 777 $source_cpu_governor

# dest files -> files that will be modified
dest_cpu_min_freq="/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq"
dest_cpu_max_freq="/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq"
dest_cpu_curr_freq="/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq"
dest_cpu_governor="/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor"

# copy temp file from non-readable location to readable location (sdcard)
while [ true ]
do
	if test -f $dest_cpu_min_freq; then
		if cmp -s $dest_cpu_min_freq $source_cpu_min_freq; then
			printf 'The file "%s" is the same as "%s"\n' "$dest_cpu_min_freq" "$source_cpu_min_freq"
		else 
			cp -f $source_cpu_min_freq $dest_cpu_min_freq
		fi
	fi

	if test -f $dest_cpu_max_freq; then
		if cmp -s $dest_cpu_max_freq $source_cpu_max_freq; then
			printf 'The file "%s" is the same as "%s"\n' "$dest_cpu_max_freq" "$source_cpu_max_freq"
		else 
			cp -f $source_cpu_max_freq $dest_cpu_max_freq
		fi
	fi

	if test -f $dest_cpu_curr_freq; then
		if cmp -s $dest_cpu_curr_freq $source_cpu_curr_freq; then
			printf 'The file "%s" is the same as "%s"\n' "$dest_cpu_curr_freq" "$source_cpu_curr_freq"
		else
			cp -f $source_cpu_curr_freq $dest_cpu_curr_freq
		fi
	fi

	if test -f $dest_cpu_governor; then
		if cmp -s $dest_cpu_governor $source_cpu_governor; then
			printf 'The file "%s" is the same as "%s"\n' "$dest_cpu_governor" "$source_cpu_governor"
		else
			cp -f $source_cpu_governor $dest_cpu_governor
		fi
	fi

	sleep 10
done