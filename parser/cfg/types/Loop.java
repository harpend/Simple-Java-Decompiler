package parser.cfg.types;

import java.util.HashSet;

public class Loop {
    public String loopType;
    public BasicBlock header;
    public BasicBlock terminator;
    public HashSet<BasicBlock> nodesInLoop;
    public boolean isReducible = false;
    public Loop parentLoop = null;

    public Loop(BasicBlock header, String type) {
        this.loopType = type;
        this.header = header;
        this.nodesInLoop = new HashSet<>();
        nodesInLoop.add(header);
        this.terminator = header;
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
