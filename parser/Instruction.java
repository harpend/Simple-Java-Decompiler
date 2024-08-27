package parser;
public class Instruction {
    public String type;
    public int index1;
    public int index2;

    public Instruction(String t, int i, int j) {
        this.index1 = i;
        this.index2 = j;
        this.type = t;
    }
}
