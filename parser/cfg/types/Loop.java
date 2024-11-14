package parser.cfg.types;

import java.util.ArrayList;
import java.util.List;

public class Loop {
    public String loopType;
    public BasicBlock header;
    public List<BasicBlock> nodesInLoop;
    public boolean isReducible = false;
    public Loop parentLoop = null;

    public Loop(BasicBlock header, String type) {
        this.loopType = type;
        this.header = header;
        this.nodesInLoop = new ArrayList<>();
        nodesInLoop.add(header);
    }

    public void stringify() {
        System.out.println("Header:");
        System.out.println(header.id);
        System.out.println("Nodes:");
        for (BasicBlock n : this.nodesInLoop) {
            System.out.print(n.id + " ");
        }
        System.out.println();
    }
    
}
