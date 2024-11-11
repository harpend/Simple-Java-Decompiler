package parser.cfg;

import java.util.ArrayList;
import java.util.List;

public class UnionFind {
    public List<BasicBlock> parent;
    public UnionFind() {
        this.parent = new ArrayList<>();
    }

    public BasicBlock find(BasicBlock bb) {
        if (this.parent.get(bb.id) == bb) {
            return bb;
        }

        this.parent.set(bb.id, find(this.parent.get(bb.id)));
        return this.parent.get(bb.id);
    }

    public void union(BasicBlock b1, BasicBlock b2) {
        BasicBlock rep1 = find(b1);
        BasicBlock rep2 = find(b2);
        this.parent.set(rep1.id, rep2);
    }
}
