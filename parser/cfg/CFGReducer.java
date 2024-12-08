package parser.cfg;

import java.util.HashSet;
import parser.cfg.types.BasicBlock;

public class CFGReducer {
    public static boolean reduceCFG(ControlFlowGraph cfg) {
        BasicBlock head = cfg.head;
        HashSet<BasicBlock> visited = new HashSet(cfg.bbList.size());
        return reduceCFG(head, visited);
    }

    public static boolean reduceCFG(BasicBlock bb, HashSet<BasicBlock> visited) {
        if ((bb.TYPE & BasicBlock.GROUP_END) == 0 && (!visited.contains(bb))) {
            visited.add(bb);
            switch (bb.TYPE) {
                case BasicBlock.TYPE_STATEMENTS:
                case BasicBlock.TYPE_IF:
                case BasicBlock.TYPE_IF_ELSE:
                    return reduceCFG(bb.next, visited);
                case BasicBlock.TYPE_CONDITIONAL_BRANCH:
                    return reduceCB(bb, visited);
                case BasicBlock.TYPE_LOOP:
                    return reduceLoop(bb, visited);
                default:
                    throw new AssertionError();
            }
        }
        return true;
    }

    private static boolean reduceCB(BasicBlock bb, HashSet<BasicBlock> visited) {
        return true;
    }

    private static boolean reduceLoop(BasicBlock bb, HashSet<BasicBlock> visited) {
        return true;
    }
}
