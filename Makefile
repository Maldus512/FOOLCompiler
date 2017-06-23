JFLAGS = -g
JC = javac -classpath "./lib/commons-cli-1.4.jar:."
# JC = javac

ifndef GRUN
GRUN = java org.antlr.v4.gui.TestRig
endif

DIR := ${CURDIR}

CLASSES = Fcc.java Test.java
# CLASSES = Test.java


#
# the default make target entry
#


default: classes


classes: generated $(CLASSES:.java=.class)


.PHONY: clean run test


$(CLASSES:.java=.class):
	$(JC) $(CLASSES)

generated:
	$(MAKE) -C parser

clean:
	$(RM) *.class ./parser/*.class ./ast/*.class ./lib/*.class ./util/*.class *.asm ./test/*.class ./test/*.asm
	$(MAKE) clean -C parser

run: classes
	java Fcc -f $(f)
# run: classes
	# java Test $(f)

test: classes
	cd test && python test_suite.py "java -classpath $(DIR):$(CLASSPATH) Fcc"

go:
	$(MAKE) clean
	$(MAKE)
	echo "\n\n\n\n##########################\n## Starting program...\n##########################\n"
	$(MAKE) run $(f)
