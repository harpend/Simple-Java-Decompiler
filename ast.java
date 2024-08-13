
import java.util.List;

abstract class astNode {
    
}

class ClassDeclaration extends astNode {
    String accessFlags, name;
    List<Subroutine> subroutines;

    public ClassDeclaration(String flags, String name, List<Subroutine> subroutines) {
        this.accessFlags = flags;
        this.name = name;
        this.subroutines = subroutines;
    }
}

class Subroutine extends astNode {
    String accessFlags, type, name;
    List<Parameter> params;
    List<Instruction> instructions;

    public Subroutine(String flags, String type, String name, List<Parameter> params, List<Instruction> instructions) {
        this.accessFlags = flags;
        this.type = type;
        this.name = name;
        this.params = params;
        this.instructions = instructions;
    }
}

class Parameter extends astNode {
    String type, name;

    public Parameter(String type, String name) {
        this.type = type;
        this.name = name;
    }
}

class Statement extends astNode {
    String statement;

    public Statement(String statement) {
        this.statement = statement;
    }
}
