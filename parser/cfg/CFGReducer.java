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
        if (!matchType(bb, BasicBlock.GROUP_END) && (!visited.contains(bb))) {
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
        if (reduceCFG(bb.next, visited) && reduceCFG(bb.branch, visited)) {
            return reduceCB(bb);
        }
        
        return false;
    }

    private static boolean reduceCB(BasicBlock bb) {
        if (matchType(bb.next, BasicBlock.GROUP_END) && (bb.next.predecessors.size() <= 1)) {
            // createIf();
            System.out.println("check");
            bb.next.stringify();
            System.exit(0);
            return true;
        }
        
        System.out.println("check2");
        System.exit(1);
        

        return false;
    }

    private static boolean reduceLoop(BasicBlock bb, HashSet<BasicBlock> visited) {
        return true;
    }

    private static boolean matchType(BasicBlock bb, int type) {
        return (bb.TYPE & type) != 0;
    }
}
