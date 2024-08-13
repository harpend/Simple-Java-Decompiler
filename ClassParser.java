import java.util.ArrayList;
import java.util.BitSet;
import java.util.Dictionary;
import java.util.List;
import java.util.Stack;

public class ClassParser {
        boolean forOrWhile, newArray=false, skipFinish = false;
        BitSet varsInUse = new BitSet(0xFF); //FF variables possible
        int level, lowest_num=9999, staticAdjustment=0, lastLine;
        int arrayCounter, arrayElements;
        Object temp;
        String finalMethods="";
        String type="", ClassName, MethodName, MethodParam, MethodProperties="";
        String space="", outstandingType="";
        
        ArrayList ConstantType=new ArrayList();
        ArrayList ConstantVal=new ArrayList();
        ArrayList FieldType=new ArrayList();
        ArrayList FieldName=new ArrayList();
        
        int lineNum=0;
        
        Stack oStack = new Stack(); //analogous to the operand stack
        Stack ifStack = new Stack(); //keeps track of where an if statement ends
        Stack gotoStack = new Stack(); //keeps track of where goto statements branch to
        Stack fieldStack = new Stack(); //stores field data
        Stack finalStack = new Stack(); //stores final java code

        public ClassDeclaration ParseClass() {
            ClassReader cr = new ClassReader();
            cr.ReadClass("./tests/test.class");
            String flags = String.join(" ", cr.accessFlags);
            String name = cr.ResolveCPIndex(cr.thisClass);
            List<Subroutine> s = parseSubroutines(cr);
            return new ClassDeclaration(flags, name, s);
        }

        private List<Subroutine> parseSubroutines(ClassReader cr) {
            List<Subroutine> s = new ArrayList<Subroutine>();
            for (int i = 0; i < cr.methodsCount; i++) {
                s.add(parseSubroutine(cr, i));
            }
            return s;
        }

        private Subroutine parseSubroutine(ClassReader cr, int i) {
            Dictionary<String, Object> subDict = cr.methods.get(i);
            List<String> accessFlagsList = (List<String>)subDict.get("access_flags");
            String flags = String.join(" ", accessFlagsList);
            int nameIndex = (int)subDict.get("name_index");
            String name = cr.ResolveCPIndex(nameIndex);
            int descriptorIndex = (int)subDict.get("descriptor_index");
            String paramsAndType = cr.ResolveCPIndex(descriptorIndex);
            String type = resolveType(paramsAndType);
            Subroutine s = new Subroutine(flags, type, name);
        }

        private String resolveType(String s) {
            int closingParenIndex = s.indexOf(')');
            String ret = s.substring(closingParenIndex + 1);
            if (ret.equals("V")) {
                return "void";
            }
            return ret;

        }
}
