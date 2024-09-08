package parser.cfg;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import parser.Instruction;

public class ControlFlowGraph {
    private Dictionary<String, Object> method;
    private List<Instruction> instructions;
    private List<BasicBlock> bbList;
    private BasicBlock curBB;

    public ControlFlowGraph(Dictionary<String, Object> method) {
        this.method = method;
        this.instructions = new ArrayList<Instruction>();
        this.bbList = new ArrayList<BasicBlock>();
        this.curBB = new BasicBlock(this, 0, "START", 0, 0, null);
        this.bbList.add(this.curBB);
    }

    public Instruction addInstruction(Instruction i, boolean cfChange) {
        this.instructions.add(i);
        if (cfChange) {
            // new basic block index can be calculated from length of bbList
            // curBB can now point to the offset of the current instruction
            // then toOffset is the last instruction of the curBB offset
            // replace curBB with new BB
        } else {
            this.curBB.addInstruction(i);
        }

        return i;
    }

}
