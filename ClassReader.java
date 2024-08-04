import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import jdk.jfr.Unsigned;

public class ClassReader {
    private int magicNum;
    private int minorVer;
    private int majorVer;
    private int constantPoolCount;
    private int thisClass;
    private int superClass;
    private int interfacesCount;
    private FileInputStream fis;

    public List<Dictionary<String, String>> constantPool; 
    public List<String> accessFlags;
    public List<Integer> interfaces;
    public List<Dictionary<String, Object>> fields;
    public List<Dictionary<String, Object>> methods;
    public List<Dictionary<String, Object>> attributes;

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
    
    public void readConstantPool(String path) throws IOException{
            fis = new FileInputStream(path);
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
                        System.out.println("Undefined CP type");
                        break;
                }
                System.out.print(i+1);
                System.out.println(element);

            }
            this.constantPool = list;

            

        
    }

    private void readInterfaces() throws IOException {
        // thisClass and superClass refer to the entries in the constant pool
        byte[] accessFlagsBytes = new byte[2];
        byte[] thisClassBytes = new byte[2];
        byte[] superClassBytes = new byte[2];
        byte[] interfacesCountBytes = new byte[2];

        fis.read(accessFlagsBytes);
        fis.read(thisClassBytes);
        fis.read(superClassBytes);
        fis.read(interfacesCountBytes);

        int flags = ByteBuffer.wrap(accessFlagsBytes).getShort();   
        this.thisClass = ByteBuffer.wrap(thisClassBytes).getShort();   
        this.superClass = ByteBuffer.wrap(superClassBytes).getShort();   
        this.interfacesCount = ByteBuffer.wrap(interfacesCountBytes).getShort();  

        this.accessFlags = new ArrayList<String> ();
        
        this.accessFlags = calcAccessFlags(flags);

        this.interfaces = new ArrayList<Integer>(this.interfacesCount);
        for (int i = 0; i < this.interfacesCount; i++) {
            byte[] interBytes = new byte[2];
            fis.read(interBytes);
            int interfac = ByteBuffer.wrap(interBytes).getShort();
            this.interfaces.add(interfac);
        }

        if (!this.interfaces.isEmpty()) {
            System.out.println("\ninterfaces:\n");
            System.out.println(this.interfaces);
        }
    }

    private void readFields() throws IOException {
        byte[] fieldsCountBytes = new byte[2];

        fis.read(fieldsCountBytes);

        int fieldsCount = ByteBuffer.wrap(fieldsCountBytes).getShort();
        List<Dictionary<String, Object>> list = new ArrayList<Dictionary<String, Object>> (); 

        byte[] accessFlagsBytes = new byte[2];
        byte[] nameIndexBytes = new byte[2];
        byte[] descriptorIndexBytes = new byte[2];
        byte[] attributesCountBytes = new byte[2];
        for (int i = 0; i < fieldsCount; i++) {
            Dictionary<String, Object> element = new Hashtable<>();
            fis.read(accessFlagsBytes);
            fis.read(nameIndexBytes);
            fis.read(descriptorIndexBytes);
            fis.read(attributesCountBytes);

            int flags = ByteBuffer.wrap(accessFlagsBytes).getShort();
            int nameIndex = ByteBuffer.wrap(nameIndexBytes).getShort();
            int descriptorIndex = ByteBuffer.wrap(descriptorIndexBytes).getShort();
            int attributesCount = ByteBuffer.wrap(attributesCountBytes).getShort();
            
            List<String> accessFlags = calcAccessFlags(flags);

            element.put("access_flags", accessFlags);
            element.put("name_index", nameIndex);
            element.put("descriptor_index", descriptorIndex);
            element.put("attributes_count", attributesCount);

            List<Dictionary<String, Object>> attrList = parseAttr(attributesCount);

            element.put("attributes", attrList);
            list.add(element);

            element.put("attributes", attrList);
            list.add(element);
        }

        this.fields = list;
        if (!list.isEmpty()) {
            System.out.println("\nfields:\n");
            System.out.println(list);
        }
    }

    private void readMethods() throws IOException {
        byte[] methodsCountBytes = new byte[2];

        fis.read(methodsCountBytes);

        int methodsCount = ByteBuffer.wrap(methodsCountBytes).getShort();

        List<Dictionary<String, Object>> list = new ArrayList<Dictionary<String, Object>> (); 

        byte[] accessFlagsBytes = new byte[2];
        byte[] nameIndexBytes = new byte[2];
        byte[] descriptorIndexBytes = new byte[2];
        byte[] attributesCountBytes = new byte[2];
        for (int i = 0; i < methodsCount; i++) {
            Dictionary<String, Object> element = new Hashtable<>();
            fis.read(accessFlagsBytes);
            fis.read(nameIndexBytes);
            fis.read(descriptorIndexBytes);
            fis.read(attributesCountBytes);

            int flags = ByteBuffer.wrap(accessFlagsBytes).getShort();
            int nameIndex = ByteBuffer.wrap(nameIndexBytes).getShort();
            int descriptorIndex = ByteBuffer.wrap(descriptorIndexBytes).getShort();
            int attributesCount = ByteBuffer.wrap(attributesCountBytes).getShort();
            
            List<String> accessFlags = calcAccessFlags(flags);

            element.put("access_flags", accessFlags);
            element.put("name_index", nameIndex);
            element.put("descriptor_index", descriptorIndex);
            element.put("attributes_count", attributesCount);

            List<Dictionary<String, Object>> attrList = parseAttr(attributesCount);

            element.put("attributes", attrList);
            list.add(element);
        }

        this.methods = list;
        if (!list.isEmpty()) {
            System.out.println("\nmethods:\n");
            System.out.println(list);
        }
    }

    private void readAttributes() throws IOException {
        byte[] attributesCountBytes = new byte[2];

        fis.read(attributesCountBytes);

        int attributesCount = ByteBuffer.wrap(attributesCountBytes).getShort();

        this.attributes = parseAttr(attributesCount);
        if (!this.attributes.isEmpty()) {
            System.out.println("\nattributes:\n");
            System.out.println(this.attributes);
        }
    }

    private List<String> calcAccessFlags(int flags) {
        List<String> afList = new ArrayList<String>();
        if ((flags & 0x0001) != 0) {
            afList.add("public");
        }
        if ((flags & 0x0002) != 0) {
            afList.add("private");
        }
        if ((flags & 0x0004) != 0) {
            afList.add("protected");
        }
        if ((flags & 0x0008) != 0) {
            afList.add("static");
        }
        if ((flags & 0x0010) != 0) {
            afList.add("final");
        }
        if ((flags & 0x0020) != 0) {
            afList.add("super");
        }
        if ((flags & 0x0040) != 0) {
            afList.add("bridge");
        }
        if ((flags & 0x0080) != 0) {
            afList.add("varargs");
        }
        if ((flags & 0x0100) != 0) {
            afList.add("native");
        }
        if ((flags & 0x0200) != 0) {
            afList.add("interface");
        }
        if ((flags & 0x0400) != 0) {
            afList.add("abstract");
        }
        if ((flags & 0x0800) != 0) {
            afList.add("strict");
        }
        if ((flags & 0x1000) != 0) {
            afList.add("synthetic");
        }
        if ((flags & 0x2000) != 0) {
            afList.add("annotation");
        }
        if ((flags & 0x4000) != 0) {
            afList.add("enum");
        }

        return afList;
    }

    private List<Dictionary<String, Object>> parseAttr(int attrCount) throws IOException {
        List<Dictionary<String, Object>> attrList = new ArrayList<Dictionary<String, Object>> (); 
        byte[] attributeNameIndexBytes = new byte[2];
        byte[] attributeLengthBytes = new byte[4];
        for (int j = 0; j < attrCount; j++) {
            Dictionary<String, Object> el = new Hashtable<>();
            fis.read(attributeNameIndexBytes);
            fis.read(attributeLengthBytes);

            int attributeNameIndex = ByteBuffer.wrap(attributeNameIndexBytes).getShort();
            int attributeLength = ByteBuffer.wrap(attributeLengthBytes).getInt();

            byte[] infoBytes = new byte[attributeLength];
            fis.read(infoBytes);

            el.put("attribute_name_index", attributeNameIndex);
            el.put("attribute_length", attributeLength);
            el.put("info", infoBytes);
            attrList.add(el);
        }

        return attrList;
    }

    public void ReadClass(String path) {
        try {
            System.out.println("Reading class file...");
            readConstantPool(path);
            readInterfaces();
            readFields();
            readMethods();
            readAttributes();

            System.out.println("\nmethods names:\n");
            for (int i = 0; i < this.methods.size(); i++) {
               Dictionary<String, Object> method = this.methods.get(i);
               int nameIndex = (int)method.get("name_index");
               Dictionary<String, String> cpEntry = this.constantPool.get(nameIndex - 1);
               System.out.println(cpEntry.get("bytes"));
               List<Dictionary<String, Object>> attrs = (List<Dictionary<String, Object>>) method.get("attributes");
               for (int j = 0; j < attrs.size(); j++) {
                    Dictionary<String, Object> attr = attrs.get(j);
                    int attributeNameIndex = (int) attr.get("attribute_name_index");
                    cpEntry = this.constantPool.get(attributeNameIndex - 1);
                    if (Arrays.equals(cpEntry.get("bytes").getBytes(StandardCharsets.UTF_8), "Code".getBytes(StandardCharsets.UTF_8))) {
                        byte[] code = (byte[]) attr.get("info");
                        System.out.print("\t");
                        for (byte b : code) {
                            System.out.print(String.format("0x%02X ", b));
                        }
                        System.out.println();
                    }
               }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
