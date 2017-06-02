JFLAGS = -g
JC = javac


CLASSES = Test.java 

ANTLR = antlr4

GEN = {}BaseVisitor.java {}Lexer.java {}Parser.java {}Visitor.java {}BaseVisitor.java
FOOLGEN = $(subst {},./parser/FOOL, $(GEN))
SVMGEN = $(subst {},./parser/SVM, $(GEN))

#
# the default make target entry
#


default: classes


classes: generated $(CLASSES:.java=.class)


.PHONY: clean run


$(CLASSES:.java=.class):
	$(JC) $(CLASSES)

generated:
	$(MAKE) -C parser

clean:
	$(RM) *.class ./parser/*.class ./ast/*.class ./lib/*.class ./util/*.class *.asm
	$(MAKE) clean -C parser

run:
	java Test
