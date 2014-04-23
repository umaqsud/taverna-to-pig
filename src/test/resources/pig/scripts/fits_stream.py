#!/usr/bin/python

import sys, os, string

for line in sys.stdin:
    if len(line) == 0: continue
    new_lines = os.popen("/Users/umar/Downloads/fits-0.6.1/fits.sh -i " + line).readlines()
    striped_lines = [x.strip() for x in new_lines]	
    print '%s\t%s' % (line.strip(), ' '.join(striped_lines))
