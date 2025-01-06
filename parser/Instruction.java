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

    public void flip() {
        switch (this.type) {
            case "if_icmple":
                this.type = "if_icmpgt";
                break;
            case "if_icmpgt":
                this.type = "if_icmple";
                break;
            case "ifle":
                this.type = "ifgt";
                break;
            case "ifge":
                this.type = "iflt";
                break;
            default:
                System.out.println("this instruction cannot be flipped: " + this.type);
                System.exit(1);
        }
    }
}
