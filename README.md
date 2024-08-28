# SimpleJavaDecompiler

(Somewhat inspired by Decompiling Java - Godfrey Nolan)

##

Building myself a java decompiler to understand how it works at a basic level. I plan to continue with decompilation further after successfully decompiling the example test cases from Godfrey's book. I borrow some inspiration from the book's techniques although in many ways I deviate such as not using a lexer/parser generator. Other items that have inspired me is Christina Cifuentes' phd paper on decompiling C, although I plan to take more direct inspiration from this when I have a go a decompiling C or another compiled language. 

compile with: 

`
javac main.java parser/*.java parser/ast/*.java
`

### Stage 1 Deserialisation
purpose: convert class file into abstract expressions operating on constants.
The aim is to get it into a format that can be easily parsed.

### Stage 2 decompile hello world instructions
this is done very simply by managing stack

### Stage 3 decompile simple programs from book --Current
Currently on example 4/9

### Stage 4 decompile more complicated programs --TODO

### Stage 5 decompile small applications/games --TODO