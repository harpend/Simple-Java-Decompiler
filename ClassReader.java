import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class ClassReader {
    private int magicNum;
    private int minorVer;
    private int majorVer;
    private int constantPoolCount;

    static final int CONSTANT_Class = 7;
    static final int CONSTANT_Fieldref = 9;
    static final int CONSTANT_Methodref = 10;
    static final int CONSTANT_InterfaceMethodref = 11;
    static final int CONSTANT_String = 8;
    static final int CONSTANT_Integer =	3;
    static final int CONSTANT_Float = 4;
    static final int CONSTANT_Long = 5;
    static final int CONSTANT_Double = 6;
    static final int CONSTANT_NameAndType =	12;
    static final int CONSTANT_Utf8 = 1;
    static final int CONSTANT_MethodHandle = 15;
    static final int CONSTANT_MethodType = 16;
    static final int CONSTANT_InvokeDynamic = 18;
    
    public void readClass(String path){
        try (FileInputStream fis = new FileInputStream(path)){
            byte[] magicNumBytes = new byte[4];
            byte[] minorVerBytes = new byte[2];
            byte[] majorVerBytes = new byte[2];
            byte[] constantPoolCountBytes = new byte[2];
            
            fis.read(magicNumBytes);
            fis.read(minorVerBytes);
            fis.read(majorVerBytes);
            fis.read(constantPoolCountBytes);

            this.magicNum = ByteBuffer.wrap(magicNumBytes).getInt();
            this.minorVer = ByteBuffer.wrap(minorVerBytes).getShort();
            this.majorVer = ByteBuffer.wrap(majorVerBytes).getShort();
            this.constantPoolCount = ByteBuffer.wrap(constantPoolCountBytes).getShort();

            for (int i = 0; i<(constantPoolCount-1); i++){

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }
}
