package parser.ast;

import java.util.List;

public class ClassDeclaration extends astNode {
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