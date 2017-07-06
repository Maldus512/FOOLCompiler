package parser;

import java.util.HashMap;;

public class ExecuteVM {
    
    public static final int CODESIZE = 10000;
    public static final int MEMSIZE = 1000;

    private HashMap<Integer, Integer> heapReferences = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> garbageCollector = new HashMap<Integer, Integer>();

    private boolean debug = false;
    
    private int[] code;
    private int[] memory = new int[MEMSIZE];
    
    private int ip = 0;
    private int sp = MEMSIZE;
    
    private int hp = 0;       
    private int fp = MEMSIZE; 
    private int ra;           
    private int rv;
    
    public ExecuteVM(int[] code) {
      this.code = code;
    }

    public ExecuteVM(int[] code, int flags) {
      this.code = code;
      if ((flags & 1) >0) {
        debug = true;
      }
    }
    
    public void cpu() {
      while ( true ) {
        int bytecode = code[ip++]; // fetch
        int v1,v2;
        int address;
        switch ( bytecode ) {
          case SVMParser.PUSH:
            push( code[ip++] );
            if (sp < hp) {
              //STACKOVERFLOW
              System.out.println("STACK OVERFLOW");
              System.exit(1);
            }
            break;
          case SVMParser.POP:
            pop();
            break;
          case SVMParser.ADD :
            v1=pop();
            v2=pop();
            push(v2 + v1);
            break;
          case SVMParser.MULT :
            v1=pop();
            v2=pop();
            push(v2 * v1);
            break;
          case SVMParser.DIV :
            v1=pop();
            v2=pop();
            push(v2 / v1);
            break;
          case SVMParser.SUB :
            v1=pop();
            v2=pop();
            push(v2 - v1);
            break;
          case SVMParser.STOREW : //
            address = pop();
            memory[address] = pop();    
            break;
          case SVMParser.LOADW : //
            push(memory[pop()]);
            break;
          case SVMParser.BRANCH : 
            address = code[ip];
            ip = address;
            break;
          case SVMParser.BRANCHEQ : //
            address = code[ip++];
            v1=pop();
            v2=pop();
            if (v2 == v1) ip = address;
            break;
          case SVMParser.BRANCHLESSEQ :
            address = code[ip++];
            v1=pop();
            v2=pop();
            if (v2 <= v1) ip = address;
            break;
          case SVMParser.JS : //
            address = pop();
            ra = ip;
            ip = address;
            break;
         case SVMParser.STORERA : //
            ra=pop();
            break;
         case SVMParser.LOADRA : //
            push(ra);
            break;
         case SVMParser.STORERV : //
            rv=pop();
            break;
         case SVMParser.LOADRV : //
            push(rv);
            break;
         case SVMParser.LOADFP : //
            push(fp);
            break;
         case SVMParser.STOREFP : //
            fp=pop();
            break;
         case SVMParser.COPYFP : //
            fp=sp;
            break;
         case SVMParser.STOREHP : //
            hp=pop();
            break;
         case SVMParser.LOADHP : //
            push(hp);
            break;
         case SVMParser.MALL : //
            int size = code[ip++];
            push(hp);
            hp += size;
            if (hp > sp) {
              //HEAPOVERFLOW
              System.out.println("OUT OF MEMORY.");
              System.exit(1);
            }
            if (debug) {
              System.out.println("Allocated " + size + " words at " + (hp-size) + ".");
            }
            heapReferences.put(sp, hp-size);
            garbageCollector.put(hp-size, 1);
            break;
         case SVMParser.PRINT :
            System.out.println((sp<MEMSIZE)?memory[sp]:"Empty stack!");
            break;
         case SVMParser.HALT :
            //dumpHeap();
            return;
        }
      }
    } 

    private void dumpHeap() {
      for (int i = hp-1; i >= 0; i--) {
        System.out.println(i + " : " + memory[i]);
      }
    }
    
    private int pop() {
      if (heapReferences.get(sp) != null) {
        Integer refCount = garbageCollector.get(memory[sp]);
        refCount--;
        if (refCount > 0) {
          garbageCollector.put(memory[sp], refCount);
        } else {
          garbageCollector.put(memory[sp],null);
        }
      }
      heapReferences.put(sp, null);
      return memory[sp++];
    }
    
    private void push(int v) {
      memory[--sp] = v;
    }
}