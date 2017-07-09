JFLAGS = -g
DIR := ${CURDIR}
CP = '$(DIR):$(DIR)/lib/commons-cli.jar:$(DIR)/lib/antlr-4.7-complete.jar:'
JC = javac -g -classpath $(CP)
JAVA = java -classpath $(CP)

GRUN = java -cp ./lib/antlr-4.7-complete.jar org.antlr.v4.gui.TestRig


SOURCES = $(wildcard ast/*.java) $(wildcard ast/types/*.java) $(wildcard util/*.java) Fjc.java Fool.java
CLASSES = $(SOURCES:.java=.class)


default: classes

classes: generated $(CLASSES)

.PHONY: clean run test debug

$(CLASSES): $(SOURCES)
	$(JC) $(SOURCES)

generated:
	$(MAKE) -C parser

clean:
	$(RM)  $(CLASSES) ./test/*.asm *.class ./util/*.class
	$(MAKE) clean -C parser

run: classes
	 java -classpath $(CP) Fjc -f $(f) -d

test: classes
	cd test && python test_suite.py

go:
	$(MAKE) clean
	$(MAKE)
	echo "\n\n\n\n##########################\n## Starting program...\n##########################\n"
	$(MAKE) run $(f)

debug:
	jdb -classpath $(CP) Fjc