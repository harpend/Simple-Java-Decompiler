package parser.ast;

public class Statement extends astNode {
    String statement;

    public Statement(String statement) {
        this.statement = statement;
    }

    public String toString(String indent) {
        return indent + this.statement;
    }

    @Override
    public String toString() {
        return toString("");
    }
}

