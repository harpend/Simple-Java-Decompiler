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
    public Edge loopEdge;
    public int header;
    public String type;
    public boolean isHeader = false;
    public boolean isLatch = false;
    public BasicBlock branch;
    public BasicBlock next;

    public BasicBlock(Instruction l, int id) {
        this.leader = l;
        this.instructions = new ArrayList<>();
        this.successors = new HashSet<>();
        this.predecessors = new HashSet<>();
        this.instructions.add(l);
        this.dfspPos = 0;
        this.loopEdge = null;
        this.id = id;
        this.header = 0;
        this.type = "Non-Header";
        this.branch = null;
        this.next = null;
        this.TYPE = 0;
    }

    public void addInstruction(Instruction i) {
        this.instructions.add(i);
    }

    public void stringify() {
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
}
