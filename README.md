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
**[Only for `part1-lexer` branch]** We initialize first the project to include all the external `.jar`
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
- Without parameter, the program will provide you an **interactive
  shell** (also called **REPL** - read-eval-print loop), where you can quickly
  run simple expressions and get immediate results
- The REPL only accepts single one-line statements. To run simple control flow and functions, put it in `.tay` file and run it instead

To run the REPL with `java`:
```shell
# In Windows CMD/Powershell:
java -cp "target\classes;lib\*" com.taylorscript.main.TaylorScript

# In Linux (uses colon instead semicolon):
java -cp "target/classes:lib/*" com.taylorscript.main.TaylorScript
```

To simplify this:
- There is a provided script for Linux: `taylorscript`;
make it executable by running `chmod +x taylorscript`
- For Windows: run the `tayloscript.bat` file that runs both REPL and accepts source file input

## Resources
* Robert Nystrom - [Crafting Interpreters](https://craftinginterpreters.com/)
* Thorsten Ball - [Writing an Interpreter in Go](https://interpreterbook.com/)
* Robert Sebesta - [Concepts of Programming Languages](https://books.google.com.ph/books/about/Concepts_of_Programming_Languages.html?id=Z1Y_AQAAIAAJ&redir_esc=y)

## License
[Apache License 2.0](https://github.com/datsudo/taylorscript/blob/main/LICENSE)
