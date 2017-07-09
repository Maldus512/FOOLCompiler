#! /usr/bin/env python
import sys
import os
from subprocess import *
import json

dir = os.path.abspath("..")

compiler = ["java", "-classpath",  "{}:{}/lib/commons-cli.jar:{}/lib/antlr-4.7-complete.jar:".format(dir,dir,dir),  "Fjc"]
interpreter = ["java", "-classpath",  "{}:{}/lib/commons-cli.jar:{}/lib/antlr-4.7-complete.jar:".format(dir,dir,dir),  "Fool"]

def main():
    with open("to_test.json", "r") as f:
        tests = json.load(f)
    for el in tests:
        fil = el['name']
        res = el['output']
        print("#################################################")
        print("Checking " + fil + " ...")
        proc = Popen(compiler + ["-f", "./" + fil])
        proc.wait()
        
        if proc.returncode != 0:
            print("Error in file " + fil)
            exit(1)
        
        print("compiled successfully")

        print("Executing " + fil + ".asm ...")
        proc = Popen(interpreter + ["-f", "./" + fil +".asm"], stdin=PIPE, stdout=PIPE)
        out = proc.communicate()[0]
        out = out.decode().strip('\n')
        print(out)

        if out != res:
            print("Unexpected output. Correct result should have been " + res)
            exit(1)

        if proc.returncode != 0:
            print("Error while executing " + fil + ".asm")
            exit(1)


if __name__ == "__main__":
    main()
