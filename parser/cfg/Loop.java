package parser.cfg;

import java.util.ArrayList;
import java.util.List;

public class Loop {
    public String loopType;
    public BasicBlock header;
    public BasicBlock tail;
    public List<BasicBlock> nodesInLoop;

    public Loop(BasicBlock h, String type) {
        this.loopType = type;
        this.header = h;
        this.nodesInLoop = new ArrayList<>();
    }

    public void stringify() {
        System.out.println("Nodes:");
        for (BasicBlock n : this.nodesInLoop) {
            System.out.print(n.id + " ");
        }
        System.out.println();
    }
    
}
