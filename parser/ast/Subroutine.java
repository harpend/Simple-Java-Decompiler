package parser.ast;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;


public class Subroutine extends astNode {
    public String accessFlags, type, name;
    public Map<Integer, Object> variablesMap;
    List<Parameter> params;
    public Stack<String> finalStack = new Stack<String>();
    Stack<String> outputStack = new Stack<String>();
    public int localCount;

    public Subroutine(String flags, String type, String name, int localCount) {
        this.accessFlags = flags;
        this.type = type;
        this.name = name;
        this.variablesMap = new HashMap<>();
        this.localCount = 0;
    }

    public void setParams(List<Parameter> params) {
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
            String temp = outputStack.pop().toString(); 
            if (temp.contains("}")) {
                statIndent = statIndent.substring(0, statIndent.length() - 1);
            } 
            
            s.append(statIndent).append(temp).append("\n");
            if (temp.contains("{")) {
                statIndent = statIndent + "\t";
            }
        }

        s.append(indent).append("}");
        return s.toString();
    }

    @Override
    public String toString() {
        return toString("");
    }
}
