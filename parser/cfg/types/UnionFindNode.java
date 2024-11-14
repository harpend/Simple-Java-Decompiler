package parser.cfg.types;

public class UnionFindNode {
    private UnionFindNode parent; 
    private BasicBlock bb;        
    private Loop loop;      
    private int dfsNumber;       

    public UnionFindNode(BasicBlock bb, int dfsNumber) {
        this.parent = this;      
        this.bb = bb;
        this.dfsNumber = dfsNumber;
        this.loop = null;        
    }

    public UnionFindNode findSet() {
        UnionFindNode node = this;
        while (node != node.parent) {
            node = node.parent;
        }
       
        UnionFindNode root = node;
        node = this;
        while (node != node.parent) {
            UnionFindNode temp = node.parent;
            node.parent = root;
            node = temp;
        }
        return root;
    }

    public void union(UnionFindNode other) {
        UnionFindNode rootThis = this.findSet();
        UnionFindNode rootOther = other.findSet();
        rootThis.parent = rootOther;
    }

    // Getters and setters for optional usage
    public BasicBlock getBasicBlock() {
        return bb;
    }

    public void setBasicBlock(BasicBlock bb) {
        this.bb = bb;
    }

    public Loop getLoop() {
        return loop;
    }

    public void setLoop(Loop loop) {
        this.loop = loop;
    }

    public int getDfsNumber() {
        return dfsNumber;
    }

    public void setDfsNumber(int dfsNumber) {
        this.dfsNumber = dfsNumber;
    }
}
