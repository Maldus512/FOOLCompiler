JFLAGS = -g
JC = javac

ANTLR = java -Xmx500M org.antlr.v4.Tool

UNAME = $(shell uname)
ifeq ($(UNAME), Darwin)  # Mac OS X
ANTLR = java -Xmx500M org.antlr.v4.Tool
endif


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
	java Test

test:
	$(MAKE) clean -C parser
	$(MAKE) -C parser PACK=NONE TEST=TRUE
	cp $(TEST) parser/
	cd parser && grun FOOL prog $(TEST) -gui
	rm parser/$(TEST)
	$(MAKE) clean -C parser
