package parser.statement;

public class StatementIf implements Statement {
    protected Statement statements;
    public StatementIf(Statement stmts) {
        this.statements = stmts;
    }
    
    @Override
    public boolean isIfStmt() { return true; }
}
