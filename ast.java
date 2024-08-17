
import java.util.List;
import java.util.Stack;

abstract class astNode {
    public abstract String toString(String indent);
}

class ClassDeclaration extends astNode {
    String accessFlags, name;
    List<Subroutine> subroutines;

    public ClassDeclaration(String flags, String name, List<Subroutine> subroutines) {
        this.accessFlags = flags;
        this.name = name;
        this.subroutines = subroutines;
    }

    public String toString(String indent) {
        StringBuilder s = new StringBuilder();
        s.append(indent).append(this.accessFlags).append(" ").append("class ").append(this.name).append(" {\n");
        String subIndent = indent + "\t";
        for (Subroutine sub : this.subroutines) {
            s.append(sub.toString(subIndent)).append("\n");
        }
        s.append(indent).append("}\n");
        return s.toString();
    }

    public String toString() {
        return toString("");
    }
}

class Subroutine extends astNode {
    String accessFlags, type, name;
    List<Parameter> params;
    Stack<String> finalStack = new Stack<String>();
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

class Parameter extends astNode {
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

class Statement extends astNode {
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
