import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

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

            byte[] tag = new byte[1];
            List<Dictionary<String, String>> list = new ArrayList<Dictionary<String, String>> (); 
            for (int i = 0; i<(constantPoolCount-1); i++){
                Dictionary<String, String> element = new Hashtable<>();
                fis.read(tag);
                switch (tag[0]) {
                    case CONSTANT_Class:
                        // u1 tag, u2 name_index
                        byte[] nameIndexBytes = new byte[2];
                        fis.read(nameIndexBytes);
                        String nameIndex = Short.toString(ByteBuffer.wrap(nameIndexBytes).getShort());
                        element.put("tag", "CONSTANT_Class");
                        element.put("name_index", nameIndex);
                        list.add(element);               
                        break;
                    case CONSTANT_Fieldref:
                        // u1 tag, u2 class_index, u2 name_amd_type_index
                        byte[] classIndexBytes = new byte[2];
                        byte[] nameAndTypeIndexBytes = new byte[2];
                        fis.read(classIndexBytes);
                        fis.read(nameAndTypeIndexBytes);
                        String classIndex = Short.toString(ByteBuffer.wrap(classIndexBytes).getShort());
                        String nameAndTypeIndex = Short.toString(ByteBuffer.wrap(nameAndTypeIndexBytes).getShort());
                        element.put("tag", "CONSTANT_Fieldref");
                        element.put("class_index", classIndex);
                        element.put("name_and_type_index", nameAndTypeIndex);
                        list.add(element);
                        break;
                    case CONSTANT_Double:
                        // u1 tag, u4 high_bytes, u4 low_bytes
                        byte[] highBytesBytes = new byte[4];
                        byte[] lowBytesBytes = new byte[4];
                        fis.read(highBytesBytes);
                        fis.read(lowBytesBytes);
                        String highBytes = Integer.toString(ByteBuffer.wrap(highBytesBytes).getInt());
                        String lowBytes = Integer.toString(ByteBuffer.wrap(lowBytesBytes).getInt());
                        element.put("tag", "CONSTANT_Double");
                        element.put("high_bytes", highBytes);
                        element.put("low_bytes", lowBytes);
                        list.add(element);
                        break;
                    case CONSTANT_Float:
                        // u1 tag, u4 bytes
                        byte[] bytesBytes2 = new byte[4];
                        fis.read(bytesBytes2);
                        String bytes2 = Integer.toString(ByteBuffer.wrap(bytesBytes2).getInt());
                        element.put("tag", "CONSTANT_Float");
                        element.put("bytes", bytes2);
                        list.add(element);                    
                        break;
                    case CONSTANT_Integer:
                        // u1 tag, u4 bytes
                        byte[] bytesBytes = new byte[4];
                        fis.read(bytesBytes);
                        String bytes = Integer.toString(ByteBuffer.wrap(bytesBytes).getInt());
                        element.put("tag", "CONSTANT_Integer");
                        element.put("bytes", bytes);
                        list.add(element);
                        break;
                    case CONSTANT_InterfaceMethodref:
                        // u1 tag, u2 class_index, u2 name_amd_type_index
                        byte[] classIndexBytes3 = new byte[2];
                        byte[] nameAndTypeIndexBytes3 = new byte[2];
                        fis.read(classIndexBytes3);
                        fis.read(nameAndTypeIndexBytes3);
                        String classIndex3 = Short.toString(ByteBuffer.wrap(classIndexBytes3).getShort());
                        String nameAndTypeIndex3 = Short.toString(ByteBuffer.wrap(nameAndTypeIndexBytes3).getShort());
                        element.put("tag", "CONSTANT_InterfaceMethodref");
                        element.put("class_index", classIndex3);
                        element.put("name_and_type_index", nameAndTypeIndex3);
                        list.add(element);  
                        break;
                    case CONSTANT_InvokeDynamic:
                        // u1 tag, u2 bootstrap_method_attr_index, u2 name_and_type_index
                        byte[] bootstrapMethodAtrrIndexBytes = new byte[2];
                        byte[] nameAndTypeIndexBytes4 = new byte[2];
                        fis.read(bootstrapMethodAtrrIndexBytes);
                        fis.read(nameAndTypeIndexBytes4);
                        String bootstrapMethodAttrIndex = Short.toString(ByteBuffer.wrap(bootstrapMethodAtrrIndexBytes).getShort());
                        String nameAndTypeIndex4 = Short.toString(ByteBuffer.wrap(nameAndTypeIndexBytes4).getShort());
                        element.put("tag", "CONSTANT_InvokeDyanmic");
                        element.put("bootstrap_method_attr_index", bootstrapMethodAttrIndex);
                        element.put("name_and_type_index", nameAndTypeIndex4);
                        list.add(element);
                        break;
                    case CONSTANT_Long:
                        // u1 tag, u4 high_bytes, u4 low_bytes
                        byte[] highBytesBytes2 = new byte[4];
                        byte[] lowBytesBytes2 = new byte[4];
                        fis.read(highBytesBytes2);
                        fis.read(lowBytesBytes2);
                        String highBytes2 = Integer.toString(ByteBuffer.wrap(highBytesBytes2).getInt());
                        String lowBytes2 = Integer.toString(ByteBuffer.wrap(lowBytesBytes2).getInt());
                        element.put("tag", "CONSTANT_Long");
                        element.put("high_bytes", highBytes2);
                        element.put("low_bytes", lowBytes2);
                        list.add(element);                    
                        break;
                    case CONSTANT_MethodHandle:
                        // u1 tag, u1 reference_kind, u2 reference_index
                        byte[] referenceKindBytes = new byte[1];
                        byte[] referenceIndexBytes = new byte[2];
                        fis.read(referenceKindBytes);
                        fis.read(referenceIndexBytes);
                        String referenceKind = Byte.toString(ByteBuffer.wrap(referenceKindBytes).get());
                        String referenceIndex = Short.toString(ByteBuffer.wrap(referenceIndexBytes).getShort());
                        element.put("tag", "CONSTANT_MethodHandle");
                        element.put("referenceKind", referenceKind);
                        element.put("reference_index", referenceIndex);
                        list.add(element);
                        break;
                    case CONSTANT_MethodType:
                        // u1 tag, u2 descriptor_index
                        byte[] descriptorIndexBytes2 = new byte[2];
                        fis.read(descriptorIndexBytes2);
                        String descriptorIndex2 = Short.toString(ByteBuffer.wrap(descriptorIndexBytes2).getShort());
                        element.put("tag", "CONSTANT_MethodType");
                        element.put("descriptor_index", descriptorIndex2);
                        list.add(element);
                        break;
                    case CONSTANT_Methodref:
                        // u1 tag, u2 class_index, u2 name_amd_type_index
                        byte[] classIndexBytes2 = new byte[2];
                        byte[] nameAndTypeIndexBytes2 = new byte[2];
                        fis.read(classIndexBytes2);
                        fis.read(nameAndTypeIndexBytes2);
                        String classIndex2 = Short.toString(ByteBuffer.wrap(classIndexBytes2).getShort());
                        String nameAndTypeIndex2 = Short.toString(ByteBuffer.wrap(nameAndTypeIndexBytes2).getShort());
                        element.put("tag", "CONSTANT_Methodref");
                        element.put("class_index", classIndex2);
                        element.put("name_and_type_index", nameAndTypeIndex2);
                        list.add(element);                    
                        break;
                    case CONSTANT_NameAndType:
                        // u1 tag, u2 name_index, u2 descriptor_index
                        byte[] nameIndexBytes2 = new byte[2];
                        byte[] descriptorIndexBytes = new byte[2];
                        fis.read(nameIndexBytes2);
                        fis.read(descriptorIndexBytes);
                        String nameIndex2 = Short.toString(ByteBuffer.wrap(nameIndexBytes2).getShort());
                        String descriptorIndex = Short.toString(ByteBuffer.wrap(descriptorIndexBytes).getShort());
                        element.put("tag", "CONSTANT_NameAndType");
                        element.put("name_index", nameIndex2);
                        element.put("descriptor_index", descriptorIndex);
                        list.add(element);                     
                        break;
                    case CONSTANT_String:
                        // u1 tag, u2 string_index
                        byte[] stringIndexBytes = new byte[2];
                        fis.read(stringIndexBytes);
                        String stringIndex = Short.toString(ByteBuffer.wrap(stringIndexBytes).getShort());
                        element.put("tag", "CONSTANT_String");
                        element.put("string_index", stringIndex);
                        list.add(element);
                        break;
                    case CONSTANT_Utf8:
                        // u1 tag, u2 length, u1 bytes[length]
                        byte[] lengthBytes = new byte[2];
                        fis.read(lengthBytes);
                        Short lengthShort = ByteBuffer.wrap(lengthBytes).getShort();
                        byte[] bytesBytes3 = new byte[lengthShort];
                        fis.read(bytesBytes3);
                        String bytes3 = new String(bytesBytes3, StandardCharsets.UTF_8);
                        String length = String.valueOf(lengthShort);
                        element.put("tag", "CONSTANT_Utf8");
                        element.put("length", length);
                        element.put("bytes", bytes3);
                        list.add(element);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }
}
