## The FOOL Compiler

A simple Object Oriented language compiler made with ANTLR4.


### Build
`$ make`

### Usage
Start the compiler with `$ ./fjc -f prog.fool`, where prog.fool is a fool program.

It is possibile to specify the following parameters:

- `-c`, `--check` : only perform semantic and type check

- `-d`, `--debug` : verbose output (parse tree)

- `-f`, `--input-file <arg>` : input file to be compilated

- `-h`, `--help` : show this help menu

- `-v`, `--version` : compiler version

Once compiled, a `prog.fool.asm` file will be produced. Such file can be executed by running the assembly code interpreter with `$ ./fool -f prog.fool.asm`. The interpreter has similar flags.

The `test/` folder contains some examples of Fool programs. It is possible to compile and execute all of them by running `$ make test`.

### Cleaning the project
`$ make clean`

### Authors
Mattia Maldini, Carlo Stomeo, Alessandro Zini
