#!/usr/bin/python

import sys, os, string, subprocess

for line in sys.stdin:
    if len(line) == 0: continue
    splits = line.split('\t', 2)
    process = subprocess.Popen(["compare", "-metric", "AE", splits[0].strip(), splits[1].strip(), splits[2].strip() + "/diff.tif"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    results = process.communicate()
    print '%s\t%s' % (splits[0].strip(), results[1].strip())