package parser;

import java.util.HashMap;
import java.util.Map;

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
	private int rv = -1;

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
				if (v1 == 0) {
					System.out.println("Division by zero detected.");
					System.exit(1);
				}
				push(v2 / v1);
				break;
			case SVMParser.SUB:
				v1 = pop();
				v2 = pop();
				push(v2 - v1);
				break;
			case SVMParser.STOREW: //
				address = pop();
				int content;
				if (heapReferences.get(sp) != null) {
					content = popNoGC();
					heapReferences.put(address, content);
				} else {
					content = popNoGC();
				}
				//if (addReference(content)) {
				// heapReferences.put(address, content);
				//}
				memory[address] = content;
				break;
			case SVMParser.LOADW: //
				int add = pop();
				push(memory[add]);
				addRefBySP(add);
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
			case SVMParser.BRANCHGREATEQ:
				address = code[ip++];
				v1 = pop();
				v2 = pop();
				if (v2 >= v1)
					ip = address;
				break;
			case SVMParser.BRANCHLESS:
				address = code[ip++];
				v1 = pop();
				v2 = pop();
				if (v2 < v1)
					ip = address;
				break;
			case SVMParser.BRANCHGREAT:
				address = code[ip++];
				v1 = pop();
				v2 = pop();
				if (v2 > v1)
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
				heapReferences.remove(sp);
				rv = popNoGC();
				break;
			case SVMParser.LOADRV: //
				push(rv);
				heapReferences.put(sp, rv);
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
				pop();
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
			System.out.println("Block of size " + entry.getValue().blockSize + " at address " + entry.getKey() + ".");
		}
	}

	private int malloc(int size) {
		int newRef = 0;
		HeapBlock block = null;
		if (garbageCollector.size() == 0) {
			block = new HeapBlock(1, size, newRef);
		}
		while (block == null) {
			boolean room = true;
			for (Map.Entry<Integer, HeapBlock> entry : garbageCollector.entrySet()) {
				int ref = entry.getKey();
				if ((newRef == ref) || (newRef < ref && newRef + size > ref && newRef + size < sp)) {
					newRef = ref + entry.getValue().blockSize;
					room = false;
					break;
				}
			}
			if (room) {
				block = new HeapBlock(1, size, newRef);
				if (newRef + size >= sp) {
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

	private int popNoGC() {
		return memory[sp++];
	}

	private boolean addRefBySP(int add) {
		if (heapReferences.get(add) != null) {
			if (addReference(heapReferences.get(add))) {
				heapReferences.put(sp, heapReferences.get(add));
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean addReference(int ref) {
		HeapBlock block = garbageCollector.get(ref);
		if (block == null) {
			return false;
		}
		block.refCount++;
		garbageCollector.put(ref, block);
		return true;
	}

	private boolean removeReference(int ref) {
		int max;
		HeapBlock block = garbageCollector.get(ref);
		if (block == null) {
			return false;
		}
		max = ref + block.blockSize;
		block.refCount--;
		if (block.refCount > 0) {
			garbageCollector.put(ref, block);
		} else {
			if (max == hp) {
				hp -= block.blockSize;
			}
			//garbageCollector.remove(ref);
			for (int i = block.address; i < block.address + block.blockSize; i++) {
				if (heapReferences.get(i) != null) {
					removeReference(memory[i]);
					heapReferences.remove(i);
				}
			}
			garbageCollector.remove(block.address);

			if (debug == true) {
				System.out.println("Deallocated block at " + ref + " of size " + block.blockSize + ".");
			}
		}
		return true;
	}

	private int pop() {
		if (heapReferences.get(sp) != null) {
			heapReferences.remove(sp);
			if (!removeReference(memory[sp])) {
				return memory[sp++];
			}
		}
		return memory[sp++];
	}

	private void push(int v) {
		memory[--sp] = v;
	}

	private class HeapBlock {
		public int refCount;
		public int blockSize;
		public int address;

		public HeapBlock(int count, int size, int add) {
			refCount = count;
			blockSize = size;
			address = add;
		}
	}
}