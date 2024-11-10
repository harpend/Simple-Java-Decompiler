package parser.cfg;

import java.util.ArrayList;
import java.util.List;

public class Loop {
    public String loopType;
    public Edge backEdge;
    public List<BasicBlock> nodesInLoop;

    public Loop(Edge e, String type) {
        this.loopType = type;
        this.backEdge = e;
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
