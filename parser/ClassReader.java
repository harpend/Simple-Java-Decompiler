package parser;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import jdk.jfr.Unsigned;
import parser.ClassReader.ParamType;
import parser.cfg.ControlFlowGraph;

public class ClassReader {
    private int magicNum;
    private int minorVer;
    private int majorVer;
    private int constantPoolCount;
    public int thisClass;
    public int methodsCount;
    public int offset = 0;
    private int superClass;
    private int interfacesCount;
    private FileInputStream fis;

    public List<Dictionary<String, String>> constantPool; 
    public List<String> accessFlags;
    public List<Integer> interfaces;
    public List<Dictionary<String, Object>> fields;
    public List<Dictionary<String, Object>> methods;
    public List<Dictionary<String, Object>> attributes;
    public Dictionary<String, Object> curMethod;

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

            this.magicNum = getInt();
            this.minorVer = getShort();
            this.majorVer = getShort();
            this.constantPoolCount = getShort();
            byte[] tag = new byte[1];
            this.offset++;
            List<Dictionary<String, String>> list = new ArrayList<Dictionary<String, String>> (); 
            for (int i = 0; i<(constantPoolCount-1); i++){
                Dictionary<String, String> element = new Hashtable<>();
                fis.read(tag);
                switch (tag[0]) {
                    case CONSTANT_Class:
                        // u1 tag, u2 name_index
                        String nameIndex = Integer.toString(getShort());
                        element.put("tag", "CONSTANT_Class");
                        element.put("name_index", nameIndex);
                        list.add(element); 
                        System.out.print(i+1);
                        System.out.println(element);              
                        break;
                    case CONSTANT_Fieldref:
                        // u1 tag, u2 class_index, u2 name_amd_type_index
                        String classIndex = Integer.toString(getShort());
                        String nameAndTypeIndex = Integer.toString(getShort());
                        element.put("tag", "CONSTANT_Fieldref");
                        element.put("class_index", classIndex);
                        element.put("name_and_type_index", nameAndTypeIndex);
                        list.add(element);
                        System.out.print(i+1);
                        System.out.println(element);
                        break;
                    case CONSTANT_Double:
                        // u1 tag, u4 high_bytes, u4 low_bytes
                        String highBytes = Integer.toString(getInt());
                        String lowBytes = Integer.toString(getInt());
                        element.put("tag", "CONSTANT_Double");
                        element.put("high_bytes", highBytes);
                        element.put("low_bytes", lowBytes);
                        list.add(element);
                        System.out.print(i+1);
                        System.out.println(element);
                        i++;
                        break;
                    case CONSTANT_Float:
                        // u1 tag, u4 bytes
                        String bytes2 = Integer.toString(getInt());
                        element.put("tag", "CONSTANT_Float");
                        element.put("bytes", bytes2);
                        list.add(element);
                        System.out.print(i+1);
                        System.out.println(element);                    
                        break;
                    case CONSTANT_Integer:
                        // u1 tag, u4 bytes
                        String bytes = Integer.toString(getInt());
                        element.put("tag", "CONSTANT_Integer");
                        element.put("bytes", bytes);
                        list.add(element);
                        System.out.print(i+1);
                        System.out.println(element);
                        break;
                    case CONSTANT_InterfaceMethodref:
                        // u1 tag, u2 class_index, u2 name_amd_type_index
                        String classIndex3 = Integer.toString(getShort());
                        String nameAndTypeIndex3 = Integer.toString(getShort());
                        element.put("tag", "CONSTANT_InterfaceMethodref");
                        element.put("class_index", classIndex3);
                        element.put("name_and_type_index", nameAndTypeIndex3);
                        list.add(element);  
                        System.out.print(i+1);
                        System.out.println(element);
                        break;
                    case CONSTANT_InvokeDynamic:
                        // u1 tag, u2 bootstrap_method_attr_index, u2 name_and_type_index
                        String bootstrapMethodAttrIndex = Integer.toString(getShort());
                        String nameAndTypeIndex4 = Integer.toString(getShort());
                        element.put("tag", "CONSTANT_InvokeDyanmic");
                        element.put("bootstrap_method_attr_index", bootstrapMethodAttrIndex);
                        element.put("name_and_type_index", nameAndTypeIndex4);
                        list.add(element);
                        System.out.print(i+1);
                        System.out.println(element);
                        break;
                    case CONSTANT_Long:
                        // u1 tag, u4 high_bytes, u4 low_bytes
                        String highBytes2 = Integer.toString(getInt());
                        String lowBytes2 = Integer.toString(getInt());
                        element.put("tag", "CONSTANT_Long");
                        element.put("high_bytes", highBytes2);
                        element.put("low_bytes", lowBytes2);
                        list.add(element); 
                        System.out.print(i+1);
                        System.out.println(element);
                        i++;                   
                        break;
                    case CONSTANT_MethodHandle:
                        // u1 tag, u1 reference_kind, u2 reference_index
                        byte[] referenceKindBytes = new byte[1];
                        fis.read(referenceKindBytes);
                        this.offset++;
                        String referenceKind = Byte.toString(ByteBuffer.wrap(referenceKindBytes).get());
                        String referenceIndex = Integer.toString(getShort());
                        element.put("tag", "CONSTANT_MethodHandle");
                        element.put("referenceKind", referenceKind);
                        element.put("reference_index", referenceIndex);
                        list.add(element);
                        System.out.print(i+1);
                        System.out.println(element);
                        break;
                    case CONSTANT_MethodType:
                        // u1 tag, u2 descriptor_index
                        String descriptorIndex2 = Integer.toString(getShort());
                        element.put("tag", "CONSTANT_MethodType");
                        element.put("descriptor_index", descriptorIndex2);
                        list.add(element);
                        System.out.print(i+1);
                        System.out.println(element);
                        break;
                    case CONSTANT_Methodref:
                        // u1 tag, u2 class_index, u2 name_amd_type_index
                        String classIndex2 = Integer.toString(getShort());
                        String nameAndTypeIndex2 = Integer.toString(getShort());
                        element.put("tag", "CONSTANT_Methodref");
                        element.put("class_index", classIndex2);
                        element.put("name_and_type_index", nameAndTypeIndex2);
                        list.add(element);    
                        System.out.print(i+1);
                        System.out.println(element);                
                        break;
                    case CONSTANT_NameAndType:
                        // u1 tag, u2 name_index, u2 descriptor_index
                        String nameIndex2 = Integer.toString(getShort());
                        String descriptorIndex = Integer.toString(getShort());
                        element.put("tag", "CONSTANT_NameAndType");
                        element.put("name_index", nameIndex2);
                        element.put("descriptor_index", descriptorIndex);
                        list.add(element);
                        System.out.print(i+1);
                        System.out.println(element);                     
                        break;
                    case CONSTANT_String:
                        // u1 tag, u2 string_index
                        String stringIndex = Integer.toString(getShort());
                        element.put("tag", "CONSTANT_String");
                        element.put("string_index", stringIndex);
                        list.add(element);
                        System.out.print(i+1);
                        System.out.println(element);
                        break;
                    case CONSTANT_Utf8:
                        // u1 tag, u2 length, u1 bytes[length]
                        int lengthShort = getShort();
                        byte[] bytesBytes3 = new byte[lengthShort];
                        fis.read(bytesBytes3);
                        this.offset++;
                        String bytes3 = new String(bytesBytes3, StandardCharsets.UTF_8);
                        String length = String.valueOf(lengthShort);
                        element.put("tag", "CONSTANT_Utf8");
                        element.put("length", length);
                        element.put("bytes", bytes3);
                        list.add(element);
                        System.out.print(i+1);
                        System.out.println(element);
                        break;
                    default:
                        System.out.println("Undefined CP type");
                        System.out.println(tag[0]);
                        System.exit(1);
                        break;
                }
            }
            this.constantPool = list;

            

        
    }

    private void readInterfaces() throws IOException {
        int flags = getShort();   
        this.thisClass = getShort();   
        this.superClass = getShort();   
        this.interfacesCount = getShort();  

        this.accessFlags = new ArrayList<String> ();
        
        this.accessFlags = calcAccessFlags(flags);

        this.interfaces = new ArrayList<Integer>(this.interfacesCount);
        for (int i = 0; i < this.interfacesCount; i++) {
            int interfac = getShort();
            this.interfaces.add(interfac);
        }

        if (!this.interfaces.isEmpty()) {
            System.out.println("\ninterfaces:\n");
            System.out.println(this.interfaces);
        }
    }

    private void readFields() throws IOException {
        int fieldsCount = getShort();
        List<Dictionary<String, Object>> list = new ArrayList<Dictionary<String, Object>> (); 

        for (int i = 0; i < fieldsCount; i++) {
            Dictionary<String, Object> element = new Hashtable<>();
 
            int flags = getShort();
            int nameIndex = getShort();
            int descriptorIndex = getShort();
            int attributesCount = getShort();
            
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
        System.out.println("fields parsed...");
    }

    private void readMethods() throws IOException {
        this.methodsCount = getShort();

        List<Dictionary<String, Object>> list = new ArrayList<Dictionary<String, Object>> (); 
        for (int i = 0; i < this.methodsCount; i++) {
            this.curMethod = new Hashtable<>();

            int flags = getShort();
            int nameIndex = getShort();
            int descriptorIndex = getShort();
            int attributesCount = getShort();
            
            List<String> accessFlags = calcAccessFlags(flags);

            curMethod.put("access_flags", accessFlags);
            curMethod.put("name_index", nameIndex);
            curMethod.put("descriptor_index", descriptorIndex);
            curMethod.put("attributes_count", attributesCount);

            if (attributesCount != 0) {
                List<Dictionary<String, Object>> attrList = parseAttr(attributesCount);
                curMethod.put("attributes", attrList);
            }

            list.add(curMethod);
        }

        this.methods = list;
        System.out.println("methods parsed...");
    }

    private void readAttributes() throws IOException {
        int attributesCount = getShort();
        this.attributes = parseAttr(attributesCount);
        System.out.println("attributes parsed...");
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
        for (int j = 0; j < attrCount; j++) {
            Dictionary<String, Object> el = new Hashtable<>();

            int attributeNameIndex = getShort();
            int attributeLength = getInt();
          
            Dictionary<String, Object> codeInfo = null;
            Dictionary<String, Object> lineNumberTableInfo = null;
            Dictionary<String, Object> stackMapTable = null;
            Dictionary<String, Object> sourceFileInfo = null;
            if (Arrays.equals(resolveNameIndex(attributeNameIndex), "Code".getBytes(StandardCharsets.UTF_8))) {
                codeInfo = parseCode();
                el.put("info", codeInfo);
            }
            else if (Arrays.equals(resolveNameIndex(attributeNameIndex), "LineNumberTable".getBytes(StandardCharsets.UTF_8))) {
                lineNumberTableInfo = parseLineNumberTable();
                el.put("info", lineNumberTableInfo);
            }
            else if (Arrays.equals(resolveNameIndex(attributeNameIndex), "StackMapTable".getBytes(StandardCharsets.UTF_8))) {
                stackMapTable = parseStackMapTable();
                el.put("info", stackMapTable);
            }
            else if (Arrays.equals(resolveNameIndex(attributeNameIndex), "SourceFile".getBytes(StandardCharsets.UTF_8))) {
                int sfIndex = getShort();
                el.put("sourcefile_index", sfIndex);
            }
            else {
                System.out.println("attribute type not implemented");
                System.out.println(new String(resolveNameIndex(attributeNameIndex), StandardCharsets.UTF_8));
                System.exit(1);
            }

            el.put("attribute_name_index", attributeNameIndex);
            el.put("attribute_length", attributeLength);
            attrList.add(el);
        }

        return attrList;
    }

    private byte[] resolveNameIndex(int entry) {
        Dictionary<String, String> cpEntry = this.constantPool.get(entry - 1);
        return cpEntry.get("bytes").getBytes();
    }

    private Dictionary<String, Object> parseStackMapTable() throws IOException {
        Dictionary<String, Object> stackMapTableAttr = new Hashtable<>();

        int lineNumberTableLength = getShort();
        List<Dictionary<String, Object>> stackMapFrame = new ArrayList<>();
        
        for (int i = 0; i < lineNumberTableLength; i++) {
            Dictionary<String, Object> stackMapEntry = new Hashtable<>();
            int frameType = getByte();
            if (0 <= frameType && frameType <= 63) {
                stackMapEntry.put("frame_type", frameType);
                stackMapFrame.add(stackMapEntry);
                continue;
            }
            int offsetDelta = getShort();
            stackMapEntry.put("offset_delta", offsetDelta);
            List<Integer> frameElems = new ArrayList<>();

            switch (frameType) {
                case 248:
                case 249:
                case 250:  
                    break;
                case 252:
                case 253:
                case 254:
                    for (int j = 0; j < (frameType-251); j++) {
                        int varInfoTag = getByte();
                        if (!(varInfoTag < 7)) {
                            System.out.println("not supported varinfotag");
                            System.exit(1);
                        }   

                        frameElems.add(varInfoTag);
                    }

                    stackMapEntry.put("verification_type_info", frameElems);
                    break;
                default:
                    System.out.println("unsupported stack map table type");
                    System.out.println(frameType);
                    System.exit(1);
            }

            stackMapFrame.add(stackMapEntry);
        }

        stackMapTableAttr.put("number_of_entries", lineNumberTableLength);
        stackMapTableAttr.put("stack_map_frame", stackMapFrame);

        return stackMapTableAttr;
    }

    private Dictionary<String, Object> parseLineNumberTable() throws IOException {
            Dictionary<String, Object> lineNumberTableAttr = new Hashtable<>();

            int lineNumberTableLength = getShort();

            List<Dictionary<String, Integer>> lineNumberTable = new ArrayList<>();

            for (int i = 0; i < lineNumberTableLength; i++) {
                Dictionary<String, Integer> lineNumberEntry = new Hashtable<>();

                int startPc = getShort();
                int lineNumber = getShort();

                lineNumberEntry.put("start_pc", startPc);
                lineNumberEntry.put("line_number", lineNumber);

                lineNumberTable.add(lineNumberEntry);
            }

            lineNumberTableAttr.put("line_number_table_length", lineNumberTableLength);
            lineNumberTableAttr.put("line_number_table", lineNumberTable);

            return lineNumberTableAttr;
    }

    private Dictionary<String, Object> parseCode() throws IOException {
        Dictionary<String, Object> codeDict = new Hashtable<>();

        boolean wide = false;
        ControlFlowGraph cfg = new ControlFlowGraph(curMethod);

        int maxStack = getShort();
        int maxLocals = getShort();
        int codeLength = getInt();

        byte[] codeBytes = new byte[codeLength];

        fis.read(codeBytes);
        this.offset+=codeLength;

        List<Instruction> codeEl = new ArrayList<Instruction>();
        byte[] b1 = new byte[1];
        byte[] b2 = new byte[2];
        int b1int;
        int b2int;
        int pc = 0;
        for (int i = 0; i < codeLength; i++) {
            byte b = codeBytes[i];
            switch (b) {
            case (byte)0x00:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "nop", 0, 0), false));
                break;
            case (byte)0x01:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "aconst_null", 0, 0), false));
                break;
            case (byte)0x02:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "iconst_m1", 0, 0), false));
                break;
                case (byte)0x03:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "iconst_0", 0, 0), false));
                break;
                case (byte)0x04:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "iconst_1", 0, 0), false));
                break;
                case (byte)0x05:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "iconst_2", 0, 0), false));
                break;
                case (byte)0x06:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "iconst_3", 0, 0), false));
                break;
                case (byte)0x07:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "iconst_4", 0, 0), false));
                break;
                case (byte)0x08:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "iconst_5", 0, 0), false));
                break;
                case (byte)0x09:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "lconst_0", 0, 0), false));
                break;
                case (byte)0x0A:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "lconst_1", 0, 0), false));
                break;
                case (byte)0x0D:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "fconst_2", 0, 0), false));
                break;
                case (byte)0x0E:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "dconst_0", 0, 0), false));
                break;
                case (byte)0x0F:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "dconst_1", 0, 0), false));
                break;
                case (byte)0x10:
                b1[0] = codeBytes[++i];
                codeEl.add(cfg.addInstruction( new Instruction(pc, "bipush", concatByteToInt(b1), 0), false));
                pc++;
                break;
                case (byte)0x12:
                b1[0] = codeBytes[++i];
                codeEl.add(cfg.addInstruction( new Instruction(pc, "ldc", concatByteToInt(b1), 0), false));
                pc++;
                break;
                case (byte)0x18:
                b1[0] = codeBytes[++i];
                codeEl.add(cfg.addInstruction( new Instruction(pc, "dload", concatByteToInt(b1), 0), false));
                pc++;
                break;
                case (byte)0x1A:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "iload_0", 0, 0), false));
                break;
                case (byte)0x1B:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "iload_1", 1, 0), false));
                break;
                case (byte)0x1C:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "iload_2", 2, 0), false));
                break;
                case (byte)0x1D:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "iload_3", 3, 0), false));
                break;
                case (byte)0x27:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "dload_1", 1, 0), false));
                break;
                case (byte)0x2A:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "aload_0", 0, 0), false));
                break;
                case (byte)0x39:
                if (wide) {
                    b2[0] = codeBytes[++i];
                    b2[1] = codeBytes[++i];
                    b1int = concatByteToInt(b2);
                    codeEl.add(cfg.addInstruction( new Instruction(pc, "dstore", b1int, 0), false));
                    pc+=2;
                } else {
                    b1[0] = codeBytes[++i];
                    b1int = concatByteToInt(b1);
                    codeEl.add(cfg.addInstruction( new Instruction(pc, "dstore", b1int, 0), false));
                    pc++;
                }
                break;
                case (byte)0x3C:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "istore_1", 1, 0), false));
                break;
                case (byte)0x3D:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "istore_2", 2, 0), false));
                break;
                case (byte)0x3E:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "istore_3", 3, 0), false));
                break;
                case (byte)0x48:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "dstore_1", 1, 0), false));
                break;
                case (byte)0x49:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "dstore_2", 2, 0), false));
                break;
                case (byte)0x60:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "iadd", 0, 0), false));
                break;
                case (byte)0x63:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "dadd", 0, 0), false));
                break;
                case (byte)0x64:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "isub", 0, 0), false));
                break;
                case (byte)0x68:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "imul", 0, 0), false));
                break;
                case (byte)0x6C:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "idiv", 0, 0), false));
                break;
                case (byte)0x84:
                if (wide) {
                    b2[0] = codeBytes[++i];
                    b2[1] = codeBytes[++i];
                    b1int = concatByteToInt(b2);
                    b2[0] = codeBytes[++i];
                    b2[1] = codeBytes[++i];
                    b2int = concatSignedByteToInt(b2, 0x8000);
                    codeEl.add(cfg.addInstruction( new Instruction(pc, "iinc", b1int, b2int), false));
                    pc+=4;
                } else {
                    b1[0] = codeBytes[++i];
                    b1int = concatByteToInt(b1);
                    b1[0] = codeBytes[++i];
                    b2int = concatSignedByteToInt(b1, 0x80);
                    codeEl.add(cfg.addInstruction( new Instruction(pc, "iinc", b1int, b2int), false));
                    pc++;pc++;
                }
                break;
                case (byte)0x87:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "i2d", 0, 0), false));
                break;
                case (byte)0xA3:
                b2[0] = codeBytes[++i];
                b2[1] = codeBytes[++i];
                int offset2 = ((b2[0]) << 8) | (b2[1]);
                codeEl.add(cfg.addInstruction( new Instruction(pc, "if_icmpgt", offset2 +pc, 0), true));
                pc++;pc++;
                break;
                case (byte)0xA4:
                b2[0] = codeBytes[++i];
                b2[1] = codeBytes[++i];
                int offset3 = ((b2[0]) << 8) | (b2[1]);
                codeEl.add(cfg.addInstruction( new Instruction(pc, "if_icmple", offset3 +pc, 0), true));
                pc++;pc++;
                break;
                case (byte)0xA7:
                b2[0] = codeBytes[++i];
                b2[1] = codeBytes[++i];
                int offset4 = ((b2[0]) << 8) | (b2[1]);
                codeEl.add(cfg.addInstruction( new Instruction(pc, "goto", offset4 + pc, 0), true));
                pc++;pc++;
                break;
                case (byte)0xAC:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "ireturn", 0, 0), true));
                break;
                case (byte)0xAF:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "dreturn", 0, 0), true));
                break;
                case (byte)0xB1:
                codeEl.add(cfg.addInstruction( new Instruction(pc, "return", 0, 0), true));
                break;    
                case (byte)0xB2:
                b2[0] = codeBytes[++i];
                b2[1] = codeBytes[++i];
                codeEl.add(cfg.addInstruction( new Instruction(pc, "getstatic", concatByteToInt(b2), 0), false));
                pc++;pc++;
                break;    
                case (byte)0xB6:
                b2[0] = codeBytes[++i];
                b2[1] = codeBytes[++i];
                codeEl.add(cfg.addInstruction( new Instruction(pc, "invokevirtual", concatByteToInt(b2), 0), true));
                pc++;pc++;
                break;    
                case (byte)0xB7:
                b2[0] = codeBytes[++i];
                b2[1] = codeBytes[++i];
                codeEl.add(cfg.addInstruction( new Instruction(pc, "invokespecial", concatByteToInt(b2), 0), true));
                pc++;pc++;
                break;    
                case (byte)0xB8:
                b2[0] = codeBytes[++i];
                b2[1] = codeBytes[++i];
                codeEl.add(cfg.addInstruction( new Instruction(pc, "invokestatic", concatByteToInt(b2), 0), true));
                pc++;pc++;
                break;    
            default:
                System.out.println("Bytecode type not implemented yet, also make sure to add to cfg");
                String hexString = String.format("%02X", b);
                System.out.println(hexString);
                System.exit(1);
            }

            pc++;
        }

        int exceptionTableLength = getShort();
        
        if (exceptionTableLength != 0) {
            System.out.println("exceptions not implemented in code");
            System.exit(1);
        }

        int attributesCount = getShort();
        List<Dictionary<String, Object>> attr = null;
        if (attributesCount != 0) {
            attr = parseAttr(attributesCount);
        }
        cfg.generateCFG();
        cfg.stringify();
        codeDict.put("max_stack", maxStack);        
        codeDict.put("max_locals", maxLocals);        
        codeDict.put("code", cfg.getInstructions());        
        codeDict.put("attribute_info", attr);
        return codeDict;
    }

    private int concatSignedByteToInt(byte[] bytes, int excessN) {
        int value = 0;
        
        if ((bytes[0] & 0x80) != 0) {
            for (int i = 0; i < bytes.length; i++) {
                value = (value << 8) | (~bytes[i] & 0xFF);
            }

            value = -value - 1;
        } else {
            for (int i = 0; i < bytes.length; i++) {
                value = (value << 8) | (bytes[i] & 0xFF);
            }
        }
        
        return value;
    }


    private int concatByteToInt(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < bytes.length; i++) {
            value = (value << 8) | (bytes[i] & 0xFF);
        }

        return value;
    }

    public String ResolveCPIndex(int i) {
        int nameIndex = 0, classIndex = 0, nameAndTypeIndex = 0;
        String tempString = "";
        Dictionary<String, String> cpEntry = this.constantPool.get(i - 1);
        String type = cpEntry.get("tag");
        switch (type) {
            case "CONSTANT_Integer":
            case "CONSTANT_Long":
            case "CONSTANT_Float":
            case "CONSTANT_Double":
            case "CONSTANT_Utf8":
                tempString = cpEntry.get("bytes");
                break;
            case "CONSTANT_Fieldref":
                classIndex = Integer.parseInt(cpEntry.get("class_index"));
                nameAndTypeIndex = Integer.parseInt(cpEntry.get("name_and_type_index"));
                tempString = ResolveCPIndex(classIndex) + "." + ResolveCPIndex(nameAndTypeIndex);
                break;
            case "CONSTANT_NameAndType":
                nameIndex = Integer.parseInt(cpEntry.get("name_index"));
                tempString = ResolveCPIndex(nameIndex);
                break;
            case "CONSTANT_Class":
                nameIndex = Integer.parseInt(cpEntry.get("name_index"));
                tempString = ResolveCPIndex(nameIndex);
                break;
            case "CONSTANT_String":
                int stringIndex = Integer.parseInt(cpEntry.get("string_index"));
                tempString = "\"" + ResolveCPIndex(stringIndex) + "\"";
                break;
            case "CONSTANT_Methodref":
                nameAndTypeIndex = Integer.parseInt(cpEntry.get("name_and_type_index"));
                tempString = ResolveCPIndex(nameAndTypeIndex);
                break;
            default:
                throw new AssertionError();
        }

        return tempString;
    }

    public enum ParamType {
        INT, BYTE, CHAR, DOUBLE, FLOAT, LONG, SHORT, BOOL, VOID, REF, ARRAY
    }

    public List<String> ResolveMethodParams(int index) {
        Dictionary<String, String> cpEntry = this.constantPool.get(index - 1);
        String type = cpEntry.get("tag");
        int nameAndTypeIndex = Integer.parseInt(cpEntry.get("name_and_type_index"));
        cpEntry = this.constantPool.get(nameAndTypeIndex - 1);
        int typeIndex = Integer.parseInt(cpEntry.get("descriptor_index"));
        String tempString = ResolveCPIndex(typeIndex);
        List<String> params = new ArrayList<>();
        for (int i = 0; i < tempString.length(); i++) {
            char c = tempString.charAt(i);
            if (c == ')') {
                break;
            } else if (c == '(') {
                continue;
            }

            switch (getParam(c)) {
                case ParamType.ARRAY:
                    c = tempString.charAt(++i);
                    if (getParam(c).equals(ParamType.REF)) {
                        String ref = extractRefType(tempString, ++i);
                        params.add(ref+"[]");
                        i += ref.length();
                    } else {
                        params.add(getParam(c).toString().toLowerCase() + "[]");
                    }
                    break;
                case ParamType.REF:
                    String ref = extractRefType(tempString, ++i);
                    params.add(ref);
                    i += ref.length();
                    break;
                default:
                    params.add(getParam(c).toString());
            }
        }
        return params;
    }

    private ParamType getParam(char c) {
        // make this into a lexer
        switch (c) {
            case 'I':
                return ParamType.INT;
            case 'B':
                return ParamType.BYTE;
            case 'C':
                return ParamType.CHAR;
            case 'D':
                return ParamType.DOUBLE;
            case 'F':
                return ParamType.FLOAT;
            case 'J':
                return ParamType.LONG;
            case 'S':
                return ParamType.SHORT;
            case 'Z':
                return ParamType.BOOL;
            case 'V':
                return ParamType.VOID;
            case '[':
                return ParamType.ARRAY;
            case 'L':
                return ParamType.REF;
            default:
                System.out.println("unsupported type " + c);
                throw new AssertionError();
        }
    }

    private String extractRefType(String s, int i) {
        String ref = "";
        while (s.charAt(i) != ';') {
            ref += s.charAt(i++);
        }

        int lastSlashIndex = ref.lastIndexOf('/');
        if (lastSlashIndex != -1) {
            return ref.substring(lastSlashIndex + 1, i - 1);
        }
        return ref;
    }

    private int getByte() throws IOException {
        byte[] b = new byte[1];
        fis.read(b);
        this.offset++;
        return b[0] & 0xFF;
    }

    private int getShort() throws IOException {
        byte[] b = new byte[2];
        this.offset+=2;
        fis.read(b);
        return ByteBuffer.wrap(b).getShort();
    }

    private int getInt() throws IOException {
        byte[] b = new byte[4];
        fis.read(b);
        this.offset+=4;
        return ByteBuffer.wrap(b).getInt();
    }

    public void ReadClass(String path) {
        try {
            System.out.println("Reading class file...");
            readConstantPool(path);
            readInterfaces();
            readFields();
            readMethods();
            readAttributes();
            System.out.println("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
