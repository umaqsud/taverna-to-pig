#!/usr/bin/python

import sys, os, string

for line in sys.stdin:
    if len(line) == 0: continue
    splits = line.split('\t', 1)
    new_lines = os.popen("/opt/openplanets-jpylyzer-5fe25bd/jpylyzer/jpylyzer.py " + splits[1]).readlines()
    striped_lines = [x.strip() for x in new_lines]	
    print '%s\t%s\t%s' % (splits[0].strip(), splits[1].strip(), ' '.join(striped_lines))
