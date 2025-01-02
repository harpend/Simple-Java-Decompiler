import parser.ClassParser;
import parser.helpers.CPParser;

public class Main {
    public static void main(String[] args) {
        CPParser constpParser = new CPParser("Ljava/util/HashMap<Ljava/util/HashMap<Ljava/lang/Integer;Ljava/lang/Integer;>;Ljava/lang/Integer;>;");
        constpParser.parse();
        System.out.println(constpParser.getType());
        System.exit(1);
        System.out.println("SJD starting...");
        ClassParser cp = new ClassParser();
        parser.ast.ClassDeclaration cd = cp.ParseClass();
        System.out.println(cd.toString());
    }
}