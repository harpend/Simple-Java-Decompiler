import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        String path = "./tests/test.class";
        ClassReader classReader = new ClassReader();
        classReader.readClass(path);

    }
}