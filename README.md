# Simple Java Decompiler

Current status: in development.

(Somewhat inspired by Decompiling Java - Godfrey Nolan, JD-Core - Emmanuel Dupuy and Backer Street - Giampiero Caprino)

##

This decompiler is written entirely from scratch. For most instructions the process is simply done by keeping track of the stack and more complicated decompilation is done through the use of control flow graphs. The aim of this is to teach myself a basic overview of how decompilation works and prepare myself for more complicated decomplation such as a C decompiler.

compile with: 

`
javac main.java parser/*.java parser/ast/*.java parser/cfg/*.java
`

run with:

`
java Main
`

### Stage 1 Deserialisation
purpose: convert class file into abstract expressions operating on constants.
The aim is to get it into a format that can be easily parsed.

### Stage 2 decompile hello world instructions
this is done by tracking the stack.

### Stage 3 decompile simple programs from book --Current
Currently on example 5/10

### Stage 4 decompile more complicated programs --TODO

### Stage 5 decompile small applications/games --TODO