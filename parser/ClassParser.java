package parser;
import com.sun.source.tree.AssertTree;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Dictionary;
import java.util.List;
import java.util.Stack;
import javax.security.auth.login.CredentialException;

import parser.ast.*;

public class ClassParser {
        boolean forOrWhile, newArray=false, skipFinish = false;
        BitSet varsInUse = new BitSet(0xFF); //FF variables possible
        int level, lowest_num=9999, staticAdjustment=0, lastLine;
        int arrayCounter, arrayElements;
        Object temp;
        String finalMethods="";
        String type="", ClassName, MethodName, MethodParam, MethodProperties="";
        String space="", outstandingType="";
        ClassReader cr;
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

        public parser.ast.ClassDeclaration ParseClass() {
            this.cr = new ClassReader();
            cr.ReadClass("./tests/test.class");
            String flags = String.join(" ", this.cr.accessFlags);
            String name = this.cr.ResolveCPIndex(this.cr.thisClass);
            List<parser.ast.Subroutine> s = parseSubroutines();
            return new ClassDeclaration(flags, name, s);
        }

        private List<Subroutine> parseSubroutines() {
            List<Subroutine> s = new ArrayList<Subroutine>();
            for (int i = 0; i < this.cr.methodsCount; i++) {
                s.add(parseSubroutine(cr, i));
            }
            return s;
        }

        private Subroutine parseSubroutine(ClassReader cr, int i) {
            Dictionary<String, Object> subDict = this.cr.methods.get(i);
            List<String> accessFlagsList = (List<String>)subDict.get("access_flags");
            String flags = String.join(" ", accessFlagsList);
            int nameIndex = (int)subDict.get("name_index");
            String name = this.cr.ResolveCPIndex(nameIndex);
            int descriptorIndex = (int)subDict.get("descriptor_index");
            String paramsAndType = this.cr.ResolveCPIndex(descriptorIndex);
            String types = resolveType(paramsAndType);
            List<Parameter> params = resolveParameters(paramsAndType);
            List<Dictionary<String, Object>> instructions = (List<Dictionary<String, Object>>)subDict.get("attributes");
            Subroutine s = new Subroutine(flags, types, name, params);
            parseInstructions(cr, instructions, s);
            return s;
        }

        private List<Statement> parseInstructions(ClassReader cr, List<Dictionary<String, Object>> instructions, Subroutine sub) {
            List<Statement> statements = new ArrayList<Statement>();
            for (Dictionary<String, Object> instruction : instructions) {
                String attributeName = cr.ResolveCPIndex((int) instruction.get("attribute_name_index"));
                if (attributeName.equals("Code")) {
                    Dictionary<String, Object> codeInfo = (Dictionary<String, Object>) instruction.get("info");
                    List<Instruction> bytecode = (List<Instruction>) codeInfo.get("code");
                    for (Instruction b : bytecode) {
                        parseInstruction(b, sub);
                    }
                }
            }

            return statements;
        }

        private void parseInstruction(Instruction i, Subroutine sub) {
            switch (i.type) {
                case "aload_0":
                    if (!varsInUse.get(i.index)) {
                        varsInUse.set(i.index);
                    }

                    oStack.push("local"+Integer.toString(i.index));
                    break;
                case "ldc":
                    oStack.push(this.cr.ResolveCPIndex(i.index));
                    break;
                case "getstatic":
                    oStack.push(this.cr.ResolveCPIndex(i.index));
                    break;
                case "return":
                    sub.finalStack.push("return;");
                    break;
                case "invokevirtual":
                case "invokespecial":
                    parseInvoke(i, sub);
                    break;
                    
                default:
                    throw new AssertionError();
            }
        }

        private void parseInvoke(Instruction i, Subroutine sub) {
            if (i.type.equals("invokevirtual")) {
                String c = oStack.pop().toString();
                String l = oStack.pop().toString().replace("java/lang/", "");
                String s = this.cr.ResolveCPIndex(i.index);
                sub.finalStack.push(l + "." + s + "(" + c + ")" + ";");
            } else if (i.type.equals("invokespecial")) {
                oStack.pop();
            }
        }

        private String resolveType(String s) {
            int closingParenIndex = s.indexOf(')');
            String ret = s.substring(closingParenIndex + 1);
            if (ret.equals("V")) {
                return "void";
            }
            if (ret.equals("I")) {
                return "int";
            }
            if (ret.equals("B")) {
                return "byte";
            }
            if (ret.equals("C")) {
                return "char";
            }
            if (ret.equals("D")) {
                return "double";
            }
            if (ret.equals("J")) {
                return "long";
            }
            if (ret.equals("S")) {
                return "short";
            }
            if (ret.equals("Z")) {
                return "boolean";
            }

            return ret;
        }

        private List<Parameter> resolveParameters(String s) {
            List<Parameter> parameters = new ArrayList<>();
            int openingParenIndex = s.indexOf('(');
            int closingParenIndex = s.indexOf(')');
            
            if (openingParenIndex != -1 && closingParenIndex != -1 && openingParenIndex < closingParenIndex) {
                String paramPart = s.substring(openingParenIndex + 1, closingParenIndex);

                for (int i = 0; i < paramPart.length(); i++) {
                    char typeChar = paramPart.charAt(i);
                    String name = "param" + i;
                    String javaType = null;

                    if (typeChar == 'L') {
                        int semicolonIndex = paramPart.indexOf(';', i);
                        if (semicolonIndex != -1) {
                            // Convert the class type descriptor to a human-readable format
                            javaType = paramPart.substring(i + 1, semicolonIndex).replace("java/lang/", "");
                            i = semicolonIndex;
                        } else {
                            throw new IllegalArgumentException("Invalid method descriptor");
                        }
                    } else if (typeChar == '[') {
                        StringBuilder arrayType = new StringBuilder();
                        while (paramPart.charAt(i) == '[') {
                            arrayType.append("[]");
                            i++;
                        }
                        char arrayBaseType = paramPart.charAt(i);
                        if (arrayBaseType == 'L') {
                            int semicolonIndex = paramPart.indexOf(';', i);
                            if (semicolonIndex != -1) {
                                javaType = paramPart.substring(i + 1, semicolonIndex).replace("java/lang/", "");
                                arrayType.insert(0, javaType);
                                i = semicolonIndex;
                            } else {
                                throw new IllegalArgumentException("Invalid method descriptor");
                            }
                        } else {
                            javaType = getJavaTypeFromChar(arrayBaseType);
                            arrayType.insert(0, javaType);
                        }
                        javaType = arrayType.toString();
                    } else {
                        javaType = getJavaTypeFromChar(typeChar);
                    }
                    
                    parameters.add(new Parameter(javaType, name));
                }
            } else {
                throw new IllegalArgumentException("Invalid method descriptor");
            }
        
            return parameters;
        }

        private String getJavaTypeFromChar(char typeChar) {
            switch (typeChar) {
                case 'B': return "byte";
                case 'C': return "char";
                case 'D': return "double";
                case 'F': return "float";
                case 'I': return "int";
                case 'J': return "long";
                case 'S': return "short";
                case 'Z': return "boolean";
                case 'V': return "void";
                default: throw new IllegalArgumentException("Unknown type: " + typeChar);
            }
        }
}
