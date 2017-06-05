JFLAGS = -g
JC = javac


ANTLR = antlr4

GEN = {}Lexer.java {}Parser.java {}Visitor.java {}BaseVisitor.java
FOOLGEN = $(subst {},FOOL, $(GEN))

ifndef TEST
SVMGEN = $(subst {},SVM, $(GEN))
VM = ExecuteVM.java
endif

CLASSES = $(subst .java,.class, $(FOOLGEN)) $(subst .java,.class, $(SVMGEN))

ifndef PACK
PACKAGING = -package parser
endif


default:  $(CLASSES)


$(CLASSES): $(FOOLGEN) $(SVMGEN) $(VM)
	$(JC) $?

$(FOOLGEN):  FOOL.g4
	antlr4 -no-listener FOOL.g4 -visitor $(PACKAGING)

$(SVMGEN):  SVM.g4
	antlr4  -no-listener SVM.g4 -visitor $(PACKAGING)



# RM is a predefined macro in make (RM = rm -f)
#

clean:
	$(RM) *.class *.tokens
	$(RM) $(subst {},FOOL, $(GEN))
	$(RM) $(subst {},SVM, $(GEN))
