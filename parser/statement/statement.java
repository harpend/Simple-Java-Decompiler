package parser.statement;

public interface statement {
    default boolean isIfStmt() { return false; }
    default boolean isIfElseStmt() { return false; }
    default boolean isDoWhileStmt() { return false; }
    default boolean isWhileStmt() { return false; }
}
