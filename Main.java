
public class Main {
    public static void main(String[] args) {
        String path = "./tests/test.class";
        ClassReader classReader = new ClassReader();
        classReader.ReadClass(path);

    }
}