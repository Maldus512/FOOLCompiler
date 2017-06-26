#! /usr/bin/env python
import sys
import os
from subprocess import Popen
import re

def main():
    if len(sys.argv) < 2:
        print("No compiler specified")
        exit(1)

    comp = sys.argv[1]
    tmp = comp.split("'")
    res = []

    for piece in tmp[0].split(" "):
        if piece:
            res.append(piece)

    res.append(tmp[1])

    for piece in tmp[2].split(" "):
        if piece:
            res.append(piece)

    comp = res

    for fil in sorted(os.listdir('.')):
        if os.path.isfile(fil) and "test" in fil and fil != sys.argv[0] and not "asm" in fil:
            print("Checking "+fil+" ...")
            proc = Popen(comp + ["-c", "-f", fil])
            proc.wait()

            if proc.returncode != 0:
                print("Error in file " + fil)
                exit(1)



if __name__ == "__main__":
    main()