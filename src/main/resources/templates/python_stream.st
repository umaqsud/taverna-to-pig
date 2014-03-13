#!/usr/bin/python

import sys, os, string

for line in sys.stdin:
    if len(line) == 0: continue
    new_lines = os.popen("<command>" + line).readlines()
    striped_lines = [x.strip() for x in new_lines]	
    print '%s' % (' '.join(striped_lines))