package parser;

import java.util.HashMap;
import java.util.Map;

import com.sun.javafx.geom.AreaOp.AddOp;;

public class ExecuteVM {

  public static final int CODESIZE = 10000;
  public static final int MEMSIZE = 1000;
  

  private HashMap<Integer, Integer> heapReferences = new HashMap<Integer, Integer>();
  private HashMap<Integer, HeapBlock> garbageCollector = new HashMap<Integer, HeapBlock>();

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
    if ((flags & 1) > 0) {
      debug = true;
    }
  }

  public void cpu() {
    while (true) {
      int bytecode = code[ip++]; // fetch
      int v1, v2;
      int address;
      switch (bytecode) {
      case SVMParser.PUSH:
        push(code[ip++]);
        if (sp < hp) {
          //STACKOVERFLOW
          System.out.println("STACK OVERFLOW");
          System.exit(1);
        }
        break;
      case SVMParser.POP:
        pop();
        break;
      case SVMParser.ADD:
        v1 = pop();
        v2 = pop();
        push(v2 + v1);
        break;
      case SVMParser.MULT:
        v1 = pop();
        v2 = pop();
        push(v2 * v1);
        break;
      case SVMParser.DIV:
        v1 = pop();
        v2 = pop();
        push(v2 / v1);
        break;
      case SVMParser.SUB:
        v1 = pop();
        v2 = pop();
        push(v2 - v1);
        break;
      case SVMParser.STOREW: //
        address = pop();
        memory[address] = pop();
        break;
      case SVMParser.LOADW: //
        push(memory[pop()]);
        break;
      case SVMParser.BRANCH:
        address = code[ip];
        ip = address;
        break;
      case SVMParser.BRANCHEQ: //
        address = code[ip++];
        v1 = pop();
        v2 = pop();
        if (v2 == v1)
          ip = address;
        break;
      case SVMParser.BRANCHLESSEQ:
        address = code[ip++];
        v1 = pop();
        v2 = pop();
        if (v2 <= v1)
          ip = address;
        break;
      case SVMParser.JS: //
        address = pop();
        ra = ip;
        ip = address;
        break;
      case SVMParser.STORERA: //
        ra = pop();
        break;
      case SVMParser.LOADRA: //
        push(ra);
        break;
      case SVMParser.STORERV: //
        rv = pop();
        break;
      case SVMParser.LOADRV: //
        push(rv);
        break;
      case SVMParser.LOADFP: //
        push(fp);
        break;
      case SVMParser.STOREFP: //
        fp = pop();
        break;
      case SVMParser.COPYFP: //
        fp = sp;
        break;
      case SVMParser.STOREHP: //
        hp = pop();
        break;
      case SVMParser.LOADHP: //
        push(hp);
        break;
      case SVMParser.MALL: //
        int ref = malloc(code[ip++]);
        push(ref);
        heapReferences.put(sp, ref);
        break;
      case SVMParser.PRINT:
        System.out.println((sp < MEMSIZE) ? memory[sp] : "Empty stack!");
        //pop();
        break;
      case SVMParser.HALT:
        if (debug == true) {
          dumpHeap();
        }
        return;
      }
    }
  }

  private void dumpHeap() {
    for (Map.Entry<Integer, HeapBlock> entry : garbageCollector.entrySet()) {
      System.out.println("Block of size "+entry.getValue().blockSize + " at address " + entry.getKey() + ".");
    }
  }

  private int malloc(int size) {
    int oldhp = hp;
    int newRef = 0;
    HeapBlock block = null;
    if (garbageCollector.size() == 0) {
      block = new HeapBlock(1, size);
    }
    while (block == null) {
      boolean room = true;
      for (Map.Entry<Integer, HeapBlock> entry : garbageCollector.entrySet()) {
        int ref = entry.getKey();
        if ((newRef == ref ) || 
                    (newRef < ref && newRef+size > ref && newRef+size < sp)) {
          newRef = ref + entry.getValue().blockSize;
          room = false;
          break;
        }
      }
      if (room) {
        block = new HeapBlock(1, size);
        if (newRef+size >= sp) {
          //HEAPOVERFLOW
          System.out.println("OUT OF MEMORY.");
          System.exit(1);
        }
      }
    }
    if (newRef + size > hp) {
      hp = newRef + size;
    }

    if (debug) {
      System.out.println("Allocated " + size + " words at " + newRef + ".");
    }

    garbageCollector.put(newRef, block);
    return newRef;
  }

  private int pop() {
    int max;
    if (heapReferences.get(sp) != null) {
      HeapBlock block = garbageCollector.get(memory[sp]);
      if (block == null) {
        return memory[sp++];
      }
      max = memory[sp] + block.blockSize;
      block.refCount--;
      if (block.refCount > 0) {
        garbageCollector.put(memory[sp], block);
      } else {
        garbageCollector.remove(memory[sp]);
        if (max == hp) {
          hp-= block.blockSize;
        }
      }
      heapReferences.remove(sp);
    }
    return memory[sp++];
  }

  private void push(int v) {
    memory[--sp] = v;
  }

  private class HeapBlock {
    public int refCount;
    public int blockSize;

    public HeapBlock(int count, int size) {
      refCount = count;
      blockSize = size;
    }
  }
}