import parser.ClassParser;

public class Main {
    public static void main(String[] args) {
        System.out.println("SJD starting...");
        ClassParser cp = new ClassParser();
        parser.ast.ClassDeclaration cd = cp.ParseClass();
        System.out.println(cd.toString());
    }
}