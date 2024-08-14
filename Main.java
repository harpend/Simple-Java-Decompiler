
public class Main {
    public static void main(String[] args) {
        System.out.println("SJD starting...");
        String path = "./tests/test.class";
        ClassParser cp = new ClassParser();
        ClassDeclaration cd = cp.ParseClass();
        System.out.println(cd.toString());
    }
}