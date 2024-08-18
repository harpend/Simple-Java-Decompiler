package parser.ast;

import java.util.List;
import java.util.Stack;


public class Subroutine extends astNode {
    String accessFlags, type, name;
    List<Parameter> params;
    public Stack<String> finalStack = new Stack<String>();
    Stack<String> outputStack = new Stack<String>();

    public Subroutine(String flags, String type, String name, List<Parameter> params) {
        this.accessFlags = flags;
        this.type = type;
        this.name = name;
        this.params = params;
    }

    @Override
    public String toString(String indent) {
        StringBuilder s = new StringBuilder();
        s.append(indent).append(this.accessFlags).append(" ").append(this.type).append(" ").append(this.name).append("(");
        for (int i = 0; i < this.params.size(); i++) {
            s.append(this.params.get(i).toString());
            if (i < this.params.size() - 1) {
                s.append(", ");
            }
        }
        s.append(") {\n");
        
        String statIndent = indent + "\t";
        while (!finalStack.isEmpty()) {
            outputStack.push(finalStack.pop());
        }
        while(!outputStack.isEmpty()) {
            s.append(statIndent).append(outputStack.pop()).append("\n");
        }

        s.append(indent).append("}");
        return s.toString();
    }

    @Override
    public String toString() {
        return toString("");
    }
}
