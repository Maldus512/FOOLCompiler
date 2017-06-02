JFLAGS = -g
JC = javac


CLASSES = Test.java 

ANTLR = antlr4


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
