package parser;
public class Instruction {
    public String type;
    public int line;
    public int index1;
    public int index2;

    public Instruction(int l, String t, int i, int j) {
        this.index1 = i;
        this.line = l;
        this.index2 = j;
        this.type = t;
    }
}
