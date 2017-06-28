A simple Object Oriented language compiler made with ANTLR4.

------

### Build
`$ make`

### Usage
Start the compiler with 
`$ ./fcc -f prog.fool`
, where prog.fool is a fool program. Some examples can be found under *test/* folder.

It is possibile to specify the following parameters:

- `-c`, `--check` : only perform semantic and type check

- `-d`, `--debug` : verbose output (parse tree)

- `-f`, `--input-file <arg>` : input file to be compilated

- `-h`, `--help` : show this help menu

- `-v`, `--version` : compiler version

### Cleaning the project
`$ make clean`

### Authors
Mattia Maldini, Carlo Stomeo, Alessandro Zini
