JFLAGS = -g
DIR := ${CURDIR}
CP = '$(DIR):$(DIR)/lib/commons-cli.jar:$(DIR)/lib/antlr-4.7-complete.jar:'
JC = javac -g -classpath $(CP)
JAVA = java -classpath $(CP)
#JC = javac

GRUN = java -cp ./lib/antlr-4.7-complete.jar org.antlr.v4.gui.TestRig


# CLASSES = Fcc.java Test.java
SOURCES = $(wildcard ast/*.java) $(wildcard ast/types/*.java) $(wildcard test/*.java) $(wildcard util/*.java) Fcc.java Fool.java
CLASSES = $(SOURCES:.java=.class)


#
# the default make target entry
#


default: classes


classes: generated $(CLASSES)

.PHONY: clean run test debug

$(CLASSES): $(SOURCES)
	$(JC) $(SOURCES)

generated:
	$(MAKE) -C parser

clean:
	$(RM)  $(CLASSES) ./test/*.asm *.class
	$(MAKE) clean -C parser

run: classes
	 java -classpath $(CP) Fcc -f $(f) -d
#run: classes
#	java Test $(f)

test: classes
	cd test && python test_suite.py "$(JAVA) Fcc"

go:
	$(MAKE) clean
	$(MAKE)
	echo "\n\n\n\n##########################\n## Starting program...\n##########################\n"
	$(MAKE) run $(f)

debug:
	jdb -classpath $(CP) Fcc