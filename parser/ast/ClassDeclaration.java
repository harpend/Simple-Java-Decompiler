package parser.ast;

import java.util.Dictionary;
import java.util.List;
import parser.ClassReader;
import parser.helpers.CPParser;

public class ClassDeclaration extends astNode {
    String accessFlags, name;
    List<Subroutine> subroutines;
    List<Dictionary<String, Object>> attributes;
    ClassReader cr;

    public ClassDeclaration(String flags, String name, List<Subroutine> subroutines, ClassReader cr) {
        this.accessFlags = flags;
        this.name = name;
        this.subroutines = subroutines;
        this.cr = cr;
    }

    @Override
    public String toString(String indent) {
        StringBuilder s = new StringBuilder();
        s.append(indent).append(this.accessFlags).append(" ").append("class ").append(this.name).append(" {\n");
        String subIndent = indent + "\t";
        
        for (Dictionary<String,Object> field : this.cr.fields) {
            List<Dictionary<String, Object>> attrs = (List<Dictionary<String, Object>>)field.get("attributes");
            String type = "";
            int nameIndex = (Integer)field.get("name_index");
            Dictionary<String, String> cpEntry = this.cr.constantPool.get(nameIndex-1);
            String name = cpEntry.get("bytes");
            List<String> accessFlags = (List<String>)field.get("access_flags");
            // cpEntry = this.cr.constantPool.get(signature-1);
            // type = cpEntry.get("bytes");
            for (Dictionary<String, Object> attr : attrs) {
                if (attr.get("signature_index") == null) {
                    continue;
                }
    
                // signatrure stores the type of a field
                int signature = (int)attr.get("signature_index");
                cpEntry = this.cr.constantPool.get(signature-1);
                type = cpEntry.get("bytes");
                CPParser cpp = new CPParser(type);
                cpp.parse();
                type = cpp.getType();
            }

            String flags = String.join(" ", this.cr.accessFlags);
            s.append(subIndent + flags + " " + type + " " + name + ";\n");
        }

        for (Subroutine sub : this.subroutines) {
            s.append(sub.toString(subIndent)).append("\n");
        }
        s.append(indent).append("}\n");
        return s.toString();
    }

    @Override
    public String toString() {
        return toString("");
    }
}