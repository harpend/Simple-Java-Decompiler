# Simple Java Decompiler

## Current status: developing...

This decompiler is written entirely from scratch. For most instructions the process is simply done by keeping track of the stack and more complicated decompilation is done through the use of control flow graphs. The aim of this is to teach myself a basic overview of how decompilation works and before attempting to build decompilers for binary executables.

compile with: 

`
javac main.java parser/*.java parser/ast/*.java parser/cfg/*.java parser/cfg/types/*.java
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

## References

Identifying Loops In Almost Linear Time - Ganesan Ramalingam

Nesting of Reducible and Irreducible Loops - Paul Havlak

Depth-First Search and Linear Graph Algorithms - Robert Tarjan

Reverse Compilation Techniques - Cristina Cifuentes

Backer Street - Giampiero Caprino

