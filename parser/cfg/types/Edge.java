package parser.cfg.types;

public class Edge {
    // To, From
    public BasicBlock to;
    public BasicBlock from;
    public Edge(BasicBlock to, BasicBlock from) {
        this.to = to;
        this.from = from;
    }
}
