package parser.cfg;

import java.util.ArrayList;
import java.util.List;

public class Loop {
    public String loopType;
    public BasicBlock header;
    public List<BasicBlock> nodesInLoop;

    public Loop(BasicBlock h, String type) {
        this.loopType = type;
        this.header = h;
        this.nodesInLoop = new ArrayList<>();
    }
    
}
