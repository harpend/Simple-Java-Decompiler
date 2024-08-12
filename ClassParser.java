import java.util.ArrayList;
import java.util.BitSet;
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
}
