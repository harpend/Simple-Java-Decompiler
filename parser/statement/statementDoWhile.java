package parser.statement;

public class StatementDoWhile implements Statement {
    protected Statement statements;

    public StatementDoWhile(Statement stmts) {
        this.statements = stmts;
    }
    
    @Override
    public boolean isDoWhileStmt() { return true; }
}
