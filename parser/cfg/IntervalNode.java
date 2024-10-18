package parser.cfg;

import java.util.HashSet;
import java.util.Set;

public class IntervalNode {
    Set<Integer> succs;
    Set<Integer> preds;
    Set<Integer> IDs;
    Integer ID;
    Integer head;
    
    public IntervalNode(Integer head, Integer ID) {
        this.succs = new HashSet<Integer>();
        this.preds = new HashSet<Integer>();
        this.IDs = new HashSet<Integer>();
        this.ID = ID;
        this.head = head;
    }
}
