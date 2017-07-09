#! /usr/bin/env python
import sys
import os
from subprocess import Popen

dir = os.path.abspath("..")

compiler = ["java", "-classpath",  "{}:{}/lib/commons-cli.jar:{}/lib/antlr-4.7-complete.jar:".format(dir,dir,dir),  "Fjc"]
interpreter = ["java", "-classpath",  "{}:{}/lib/commons-cli.jar:{}/lib/antlr-4.7-complete.jar:".format(dir,dir,dir),  "Fool"]

def main():
    for fil in sorted(os.listdir('./')):
        if os.path.isfile("./" + fil) and "test" in fil and fil != sys.argv[0] and ".fool" and not ".asm" in fil:
            print("Checking " + fil + " ...")
            proc = Popen(compiler + ["-f", "./" + fil])
            proc.wait()

            if proc.returncode != 0:
                print("Error in file " + fil)
                exit(1)
            
            print("compiled successfully")

            print("Executing " + fil + ".asm ...")
            proc = Popen(interpreter + ["-f", "./" + fil +".asm"])
            proc.wait()

            if proc.returncode != 0:
                print("Error while executing " + fil + ".asm")
                exit(1)


if __name__ == "__main__":
    main()
