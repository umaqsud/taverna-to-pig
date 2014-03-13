#!/usr/bin/python

import sys, os, string

# opj_decompress -i %%jp2infilepath%% -o %%tmpdir%%/$(basename %%jp2infilepath%%).tif

for line in sys.stdin:
    if len(line) == 0: continue
    splits = line.split('\t', 2)
    os.popen("/opt/openjpeg-2.0.0-Linux-i386/bin/opj_decompress -i " + splits[1].strip() + " -o " + splits[2].strip() + "/$(basename " + splits[1].strip() + ").tif")
    tif_file = os.popen("echo -n " + splits[2].strip() + "/$(basename " + splits[1].strip() + ").tif").read()
    print '%s\t%s\t%s' % (splits[0].strip(), splits[1].strip(), tif_file)
