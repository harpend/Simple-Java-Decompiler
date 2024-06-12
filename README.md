# SimpleJavaDecompiler

## Stage 1 Deserialisation
purpose: convert class file into abstract expressions operating on constants. 
This means it wont rely on stack operations anymore.

push and pop abstract expressions on to a stack for each bytecode instructions.
This generates an abstract syntax tree.

You can then use single static assignment to generate local variables from stack values.
