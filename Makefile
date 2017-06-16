JFLAGS = -g
JC = javac

GRUN = java org.antlr.v4.gui.TestRig

# Maldo: definisci la tua variabile di ambiente

CLASSES = Test.java 


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
	$(RM) *.class ./parser/*.class ./ast/*.class ./lib/*.class ./util/*.class *.asm
	$(MAKE) clean -C parser

run:
	java Test $(f)

test:
	$(MAKE) clean -C parser
	$(MAKE) -C parser PACK=NONE TEST=TRUE
	cp $(TEST) parser/
	cd parser && $(GRUN) FOOL prog $(TEST) -gui
	rm parser/$(TEST)
	$(MAKE) clean -C parser

go:
	$(MAKE) clean
	$(MAKE)
	echo "\n\n\n\n##########################\n## Starting program...\n##########################\n"
	$(MAKE) run $(f)
