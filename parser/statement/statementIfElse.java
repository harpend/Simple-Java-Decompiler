package parser.statement;

public class StatementIfElse extends StatementIf {
    protected Statement elseStatements;
    public StatementIfElse(Statement ifStmt, Statement elseStmts) {
        super(ifStmt);
        this.elseStatements = elseStmts;
    }
    
    @Override
    public boolean isIfElseStmt() { return true; }
}
