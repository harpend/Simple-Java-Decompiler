package parser.cfg;

import java.util.HashSet;
import java.util.Set;

public class IntervalNode {
    public Set<Integer> succs;
    public Set<Integer> preds;
    public Set<Integer> IDs;
    public Integer ID;
    public Integer head;
    
    public IntervalNode(Integer head, Integer ID) {
        this.succs = new HashSet<Integer>();
        this.preds = new HashSet<Integer>();
        this.IDs = new HashSet<Integer>();
        this.ID = ID;
        this.head = head;
    }
}