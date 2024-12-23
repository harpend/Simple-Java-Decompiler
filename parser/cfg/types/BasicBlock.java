package parser.cfg.types;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import parser.Instruction;

public class BasicBlock {
    public static final int TYPE_DELETED                         = 0;
    public static final int TYPE_START                           = 1;
    public static final int TYPE_END                             = (1 << 1);
    public static final int TYPE_STATEMENTS                      = (1 << 2);
    public static final int TYPE_STAT                      = (1 << 3);
    public static final int TYPE_RETURN                          = (1 << 4);
    public static final int TYPE_RETURN_VALUE                    = (1 << 5);
    public static final int TYPE_RET                             = (1 << 6);
    public static final int TYPE_CONDITIONAL_BRANCH              = (1 << 7);
    public static final int TYPE_IF                              = (1 << 8);
    public static final int TYPE_IF_ELSE                         = (1 << 9);
    public static final int TYPE_CONDITION                       = (1 << 10);
    public static final int TYPE_CONDITION_OR                    = (1 << 11);
    public static final int TYPE_CONDITION_AND                   = (1 << 12);
    public static final int TYPE_LOOP                            = (1 << 13);
    public static final int TYPE_LOOP_START                      = (1 << 14);
    public static final int TYPE_LOOP_CONTINUE                   = (1 << 15);
    public static final int TYPE_LOOP_END                        = (1 << 16);
    public static final int TYPE_GOTO                            = (1 << 17);
    public static final int TYPE_INFINITE_GOTO                   = (1 << 18);
    public static final int TYPE_JUMP                            = (1 << 19);

    // public static final int GROUP_SINGLE_SUCCESSOR  = TYPE_START|TYPE_STATEMENTS|TYPE_LOOP|TYPE_IF|TYPE_IF_ELSE|TYPE_GOTO;
    // public static final int GROUP_SYNTHETIC         = TYPE_START|TYPE_END|TYPE_CONDITIONAL_BRANCH|TYPE_RET|TYPE_GOTO|TYPE_JUMP;
    // public static final int GROUP_CODE              = TYPE_STATEMENTS|TYPE_THROW|TYPE_RETURN|TYPE_RETURN_VALUE|TYPE_CONDITIONAL_BRANCH|TYPE_RET|TYPE_GOTO|TYPE_INFINITE_GOTO|TYPE_CONDITION;
    public static final int GROUP_END               = TYPE_END|TYPE_RETURN|TYPE_RETURN_VALUE|TYPE_RET|TYPE_LOOP_START|TYPE_LOOP_CONTINUE|TYPE_LOOP_END|TYPE_INFINITE_GOTO|TYPE_JUMP;
    // public static final int GROUP_CONDITION         = TYPE_CONDITION|TYPE_CONDITION_OR|TYPE_CONDITION_AND;

    public static final BasicBlock END = new BasicBlock(TYPE_END);

    public Instruction leader;
    public Instruction terminator;
    public boolean visited;
    public int id; 
    public int TYPE;
    public List<Instruction> instructions;
    public Set<BasicBlock> successors;
    public Set<BasicBlock> predecessors;
    public BitSet dominators;
    public int dfspPos;
    public int header;
    public String type;
    public boolean isHeader = false;
    public boolean isLatch = false;
    public BasicBlock branch;
    public BasicBlock next;
    public List<BasicBlock> subNodes;

    public BasicBlock(Instruction l, int id) {
        this.leader = l;
        this.instructions = new ArrayList<>();
        this.successors = new HashSet<>();
        this.predecessors = new HashSet<>();
        this.instructions.add(l);
        this.dfspPos = 0;
        this.id = id;
        this.header = 0;
        this.type = "Non-Header";
        this.branch = null;
        this.next = null;
        this.subNodes = new ArrayList<>();
        this.TYPE = 0;
    }

    public BasicBlock(BasicBlock bb, int id) {
        this.leader = null;
        this.instructions = bb.instructions;
        this.successors = bb.successors;
        this.predecessors = bb.predecessors;
        this.dfspPos = 0;
        this.id = id;
        this.header = bb.header;
        this.type = "Non-Header";
        this.branch = bb.branch;
        this.next = bb.next;
        this.TYPE = bb.TYPE;
        this.subNodes = new ArrayList<>();
    }

    public BasicBlock(Loop l, int id) {
        this.leader = l.header.leader;
        this.instructions = new ArrayList<>();
        for (BasicBlock bb : l.nodesInLoop) {
            this.instructions.addAll(bb.instructions);
        }

        this.successors = l.terminator.successors;
        this.predecessors = l.header.predecessors;
        this.dfspPos = 0;
        this.id = id;
        this.header = id;
        this.type = "Loop";
        this.next = this.branch = END;
        this.TYPE = TYPE_LOOP;
        this.subNodes = new ArrayList<>();
    }

    public BasicBlock(int type, int id) {
        this.TYPE = type;
        this.id = id;
        this.instructions = new ArrayList<>();
        this.successors = new HashSet<>();
        this.predecessors = new HashSet<>();
        this.dfspPos = 0;
        this.header = 0;
        this.type = "Non-Header";
        this.branch = null;
        this.next = null;
        this.subNodes = new ArrayList<>();
    }

    public BasicBlock(int type) {
        this.TYPE = type;
    }

    public void addInstruction(Instruction i) {
        this.instructions.add(i);
    }

    public boolean matchType(int type) {
        return (this.TYPE & type) != 0;
    }

    public void stringify() {
        if (matchType(TYPE_LOOP)) {
            stringifyLoop();
            return;
        } else if (matchType(TYPE_STATEMENTS)) {
            stringifyStats();
            return;
        } else if (matchType(TYPE_IF) || matchType(TYPE_IF_ELSE)) {
            stringifyIf();
            return;
        }
        for (Instruction i : this.instructions) {
            System.out.println("\t" + i.line + " " + i.type + " " + i.index1 + " " + i.index2);
        }

        System.out.println("Predecessors:");
        for (BasicBlock pred : this.predecessors) {
            System.out.print(pred.id + " ");
        }
        System.out.println();

        System.out.println();
        System.out.println("Successors:");
        for (BasicBlock succ : this.successors) {
            System.out.print(succ.id + " ");
        }
        System.out.println();

        System.out.println();
    }

    private void stringifyLoop() {
        System.out.println("LOOP:");
        for (BasicBlock basicBlock : this.subNodes) {
            System.out.println(basicBlock.id);
        }

        
        System.out.println("Predecessors:");
        for (BasicBlock pred : this.predecessors) {
            System.out.print(pred.id + " ");
        }
        System.out.println();

        System.out.println();
        System.out.println("Successors:");
        for (BasicBlock succ : this.successors) {
            System.out.print(succ.id + " ");
        }
        
        System.out.println();
        System.out.println("LOOP END");
        System.out.println();
    }

    private void stringifyStats() {
        System.out.println("STATS:");
        for (BasicBlock basicBlock : this.subNodes) {
            System.out.println(basicBlock.id);
        }

        
        System.out.println("Predecessors:");
        for (BasicBlock pred : this.predecessors) {
            System.out.print(pred.id + " ");
        }
        System.out.println();

        System.out.println();
        System.out.println("Successors:");
        for (BasicBlock succ : this.successors) {
            System.out.print(succ.id + " ");
        }
        
        System.out.println();
        System.out.println("STATS END");
        System.out.println();
    }

    private void stringifyIf() {
        System.out.println("IF: ");
        for (BasicBlock basicBlock : this.subNodes) {
            System.out.println(basicBlock.id);
        }
        
        System.out.println("Predecessors:");
        for (BasicBlock pred : this.predecessors) {
            System.out.print(pred.id + " ");
        }
        System.out.println();

        System.out.println();
        System.out.println("Successors:");
        for (BasicBlock succ : this.successors) {
            System.out.print(succ.id + " ");
        }
        
        System.out.println();
        System.out.println("IF END");
        System.out.println();
    }

    // public void replace(BasicBlock prev, BasicBlock post) {
    //     if (this.next == prev) {
    //         this.next = post;
    //     }

    //     if (this.branch == prev) {
    //         this.branch = post;
    //     }

    //     if (this.sub1 == prev) {
    //         this.sub1 = post;
    //     }

    //     if (this.sub2 == prev) {
    //         this.sub2 = post;
    //     }

    //     if (this.predecessors.contains(prev)) {
    //         this.predecessors.remove(prev);
    //         this.predecessors.add(post);
    //     }

    //     if (this.successors.contains(prev)) {
    //         this.successors.remove(prev);
    //         this.successors.add(post);
    //     }
    // }

    // public void replacePred(Set<BasicBlock> prev, BasicBlock post) {
    //     if (prev.contains(this.next)) {
    //         this.next = post;
    //     }

    //     if (prev.contains(this.branch)) {
    //         this.branch = post;
    //     }

    //     if (prev.contains(this.sub1)) {
    //         this.sub1 = post;
    //     }

    //     if (prev.contains(this.sub2)) {
    //         this.sub2 = post;
    //     }

    //     this.predecessors.removeAll(prev);
    //     this.predecessors.add(post);
    // }
}
