<div align="center">
    <h1>TaylorScript</h1>
</div>

## Contents
- [Building the project](#building-the-project)
  - [Prerequisites](#prerequisites)
  - [Build commands](#build-commands)
- [Run the program](#run-the-program)

## Building the project

### Prerequisites
The following prerequisites are:
- Java 17 SDK
- [Maven](https://maven.apache.org/download.cgi)

To check if they're properly installed, run the
following in your terminal (the output should tell their version info):
```shell
# For checking if Java is installed:
java --version
javac --version

# For Maven:
mvn -v
```

### Build commands
Install all the necessaary plugins with:
```shell
mvn clean install
```

Then we initialize first the project to include all the external `.jar`
files or plugins:
```shell
mvn initialize
```

Then we compile the project that will output all the `.class`
files in `target/` directory:
```shell
mvn compile
```

## Run the program
- The program optionally accepts one parameter: a TaylorScript
  file (ends with `.tay` file).
- Without parameters, the program will provide you an **interactive
  shell** (also called **REPL** - read-eval-print loop), where you can quickly
  run simple expressions and get immediate results
- As of the moment, there are two branches in this repository:
  `part1-lexer` and `feature-parser`.
- In `part1-lexer`, the program only outputs all the scanned tokens in
  an expression or a source file
- `feature-parser` currently only recognizes arithmetic operations; it outputs
the expression in [Polish Notation](https://en.wikipedia.org/wiki/Polish_notation) to easily recognize the order
of precedence.

To run the REPL with `java`:
```shell
# In Windows CMD/Powershell:
java -cp "target\classes;lib\*" com.taylorscript.main.TaylorScript

# In Linux (uses colon instead semicolon):
java -cp "target/classes:lib/*" com.taylorscript.main.TaylorScript
```

> To simplify this, there is a provided script for Linux: `taylorscript`;
> make it executable by running `chmod +x taylorscript`

## Resources
* Robert Nystrom - [Crafting Interpreters](https://craftinginterpreters.com/)
* Thorsten Ball - [Writing an Interpreter in Go](https://interpreterbook.com/)
* Robert Sebesta - [Concepts of Programming Languages](https://books.google.com.ph/books/about/Concepts_of_Programming_Languages.html?id=Z1Y_AQAAIAAJ&redir_esc=y)

## License
[Apache License 2.0](https://github.com/datsudo/taylorscript/blob/main/LICENSE)
