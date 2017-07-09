A simple Object Oriented language compiler made with ANTLR4.

------

### Build
`$ make`

### Usage
Start the compiler with 
`$ ./fjc -f prog.fool`
, where prog.fool is a fool program.

It is possibile to specify the following parameters:

- `-c`, `--check` : only perform semantic and type check

- `-d`, `--debug` : verbose output (parse tree)

- `-f`, `--input-file <arg>` : input file to be compilated

- `-h`, `--help` : show this help menu

- `-v`, `--version` : compiler version

Once compiled a ".fool.asm" file will be produced. You can execute the result by running the assembly code interpreter
`$ ./fool -f prog.fool.asm`
The interpreter has similar flags.

The test/ folder contains some fool program examples. run
`$ make test`
To compile and execute all of them.

### Cleaning the project
`$ make clean`

### Authors
Mattia Maldini, Carlo Stomeo, Alessandro Zini
