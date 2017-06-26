JFLAGS = -g
<<<<<<< HEAD
DIR := ${CURDIR}
CP = $(DIR):$(DIR)/lib/commons-cli.jar:$(CLASSPATH)
JC = javac -classpath $(CP)
JAVA = java -classpath $(CP)
=======
 JC = javac -classpath "./lib/commons-cli.jar:$(CLASSPATH)"
>>>>>>> a0d64f655c864f3cd01ef979a402527dd013a822
#JC = javac

ifndef GRUN
GRUN = java org.antlr.v4.gui.TestRig
endif


<<<<<<< HEAD
# CLASSES = Fcc.java Test.java
SOURCES = $(wildcard ast/*.java) $(wildcard test/*.java) $(wildcard util/*.java) Fcc.java Test.java
CLASSES = $(SOURCES:.java=.class)
=======
CLASSES = Fcc.java Test.java
#CLASSES = Test.java
>>>>>>> a0d64f655c864f3cd01ef979a402527dd013a822


#
# the default make target entry
#


default: classes


classes: generated $(CLASSES)

.PHONY: clean run test

$(CLASSES): $(SOURCES)
	$(JC) $(SOURCES)

generated:
	$(MAKE) -C parser

clean:
	$(RM) *.class ./parser/*.class ./ast/*.class ./lib/*.class ./util/*.class *.asm ./test/*.class ./test/*.asm
	$(MAKE) clean -C parser

run: classes
	 java -classpath "./lib/commons-cli.jar:$(CLASSPATH)" Fcc -f $(f)
#run: classes
#	java Test $(f)

test: classes
	cd test && python test_suite.py "$(JAVA) Fcc"

go:
	$(MAKE) clean
	$(MAKE)
	echo "\n\n\n\n##########################\n## Starting program...\n##########################\n"
	$(MAKE) run $(f)
