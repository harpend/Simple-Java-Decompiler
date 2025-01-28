# Simple Java Decompiler

## Current status: developing...

This decompiler is written entirely from scratch. For most instructions the process is simply done by keeping track of the stack and more complicated decompilation is done through the use of control flow graphs. The aim of this is to teach myself a basic overview of how decompilation works and before attempting to build decompilers for binary executables.

compile with: 

`
javac main.java parser/*.java parser/ast/*.java parser/cfg/*.java parser/cfg/types/*.java parser/statement/*.java parser/cfg/helpers/*.java
`

run with:

`
java Main
`

### Stage 1 Deserialisation
Purpose: convert class file into abstract expressions operating on constants.
The aim is to get it into a format that can be easily parsed.

### Stage 2 decompile hello world instructions
This is done by tracking the stack.

### Stage 3 decompile simple programs --Current
Currently partially decompiles:
if, do-while, while

Need to complete support for:
for, if-else

### Stage 4 decompile more complicated programs --TODO
This would add support for:
n-way conditionals (switch, (if, else-if, else)), break/continue in loops, and potentially first class functions

### Stage 5 write articles explaining how SJD works --TODO
Hopefully when this is completed I can write some basic tutorials explaining how Java decompilation works and updating as I gain more knowledge.

## References

Identifying Loops In Almost Linear Time - Ganesan Ramalingam

Nesting of Reducible and Irreducible Loops - Paul Havlak

Depth-First Search and Linear Graph Algorithms - Robert Tarjan

Reverse Compilation Techniques - Cristina Cifuentes

Static Single Assignment for Decompilation - Michael James Van Emmerik

Backer Street - Giampiero Caprino

JD-Core - Emmanuel Dupuy

Vineflower