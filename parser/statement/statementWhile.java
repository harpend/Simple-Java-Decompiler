package parser.statement;

public class StatementWhile implements Statement {
    protected Statement statements;

    public StatementWhile(Statement stmts) {
        this.statements = stmts;
    }

    @Override
    public boolean isWhileStmt() { return true; }
}
