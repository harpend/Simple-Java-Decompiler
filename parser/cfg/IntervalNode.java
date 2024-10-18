package parser.cfg;

import java.util.HashSet;
import java.util.Set;

public class IntervalNode {
    Set<Integer> succs;
    Set<Integer> preds;
    Set<Integer> bbIds;
    
    public IntervalNode(Set<Integer> bbIDs) {
        this.succs = new HashSet<Integer>();
        this.preds = new HashSet<Integer>();
        this.bbIds = bbIDs;
    }

    public void LinkIntervalNode(Set<Integer> succs, Set<Integer> preds) {
        this.succs = succs;
        this.preds = preds;
    }
}
