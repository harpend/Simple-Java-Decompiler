
public class Main {
    public static void main(String[] args) {
        System.out.println("SJD starting...");
        String path = "./tests/test.class";
        ClassReader classReader = new ClassReader();
        classReader.ReadClass(path);

    }
}