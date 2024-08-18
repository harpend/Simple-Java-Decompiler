package parser.ast;

public class Parameter extends astNode {
    String type, name;

    public Parameter(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String toString(String indent) {
        return indent + this.type + " " + this.name;
    }

    @Override
    public String toString() {
        return toString("");
    }
}
