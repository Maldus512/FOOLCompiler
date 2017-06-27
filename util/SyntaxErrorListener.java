package util;

import org.antlr.v4.runtime.*;
import java.util.*;

public class SyntaxErrorListener extends BaseErrorListener {
    public int errors = 0;
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
            String msg, RecognitionException e) {
        List<String> stack = ((Parser) recognizer).getRuleInvocationStack();
        Collections.reverse(stack);
        System.err.println("rule stack: " + stack);
        System.err.println("line " + line + ":" + charPositionInLine + " at " + offendingSymbol + ": " + msg);
        errors++;
    }

    static class SyntaxErrorException extends Exception {
      // Parameterless Constructor
      public SyntaxErrorException() {}

      // Constructor that accepts a message
      public SyntaxErrorException(String message) {
         super(message);
      }
 }
}