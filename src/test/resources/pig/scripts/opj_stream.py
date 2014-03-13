#!/usr/bin/python

import sys, os, string

for line in sys.stdin:
    if len(line) == 0: continue
    splits = line.split('\t', 1)
    os.popen("/opt/openjpeg-2.0.0-Linux-i386/bin/opj_compress -i " + splits[0].strip() + " -o " + splits[1].strip() + "/$(basename " + splits[0].strip() + ").jp2")
    jp2_file = os.popen("echo -n " + splits[1].strip() + "/$(basename " + line.strip() + ").jp2").read()
    print '%s\t%s' % (splits[0].strip(), jp2_file)
