import java.io.File
import java.util.*



// grammar

// Expr:
//   ApplicationExp - (FiniteExpr '\s'*)+
//   Set            - IDENTIFIER = Expr
//   Exec           - '!' Expr
//   FiniteExpr:
//     UnitExpr     - ()
//     Variable     - IDENTIFIER
//     Closure      - ('?')?{ Expr (';' Expr)* }
//     String       - '"' <any char that is not newline or quotes>* '"'
//     IntLit       - [0-9]+

// special variables:
//   $0   first argument
//   $1   second argument
//   $2   third argument, etc
//   $N   the list of all arguments

fun main(args: Array<String>) {
    if(args.isEmpty()) {
        run(compile(parse(lex(preprocess(File("res/compiler/tests/lexer-tests.selfhost").readText())))))
    }else {
        when(args[0]) {
            "--preprocess" -> {
                println(preprocess(File(args[1]).readText()))
            }
            "--lex" -> {
                println("[")
                lex(preprocess(File(args[1]).readText())).map {
                    println("    [ '" + it.type.name + "', '" + it.content + "' ],")
                }
            }
            "--compile" -> {
                println(compile(parse(lex(preprocess(File(args[1]).readText())))))
            }
            else -> {
                error("no ${args.toList()}")
            }
        }
    }
}

fun repl() {
    val scan = Scanner(System.`in`)
    while(true) {
        print("$ ")
        val line =  try {
            scan.nextLine()
        } catch(e: NoSuchElementException) { break } // <C-d>
        if(line.isEmpty()) continue
        try {
            val parsed = parse(lex(line))
            val js = compile(parsed)
            println(js)
            run(js)
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }
}
/*

# helo, for codeday I wrote a programing language!
# some examples of code written in this language are included as screenshots (for syn. highlighting)
# I'm going to be demonstrating the three programs I wrote in this language

# first, a prime number generator
kotlin-compiler.sh --compile res/primes.selfhost > primes.js
node primes.js

# Now, my actual goal of codedays was to create a language in which the compiler, was written in the language itself
# I didn't make that goal, but I made good progress, both the lexer and the preprocessor were written in this language
# the preprocessser strips comments, and the lexer tokenizes the input, and they are the first two steps of any compiler

# I'll now compile the $lang lexer, which includes the preprocessor
kotlin-compiler.sh --compile res/lexer-runner.selfhost > lexer.js

# now we are going to run this compiled file
node lexer.js res/primes.selfhost | head -n 10
# we can see this is the same as we get with our compiler written in kotlin
kotlin-compiler.sh --lex res/primes.selfhost | head -n 10
# and it's the same for the rest of the file, as well

# In the upcoming days, I'll finish the rest of the compiler. That way, I won't even need a compiler written in kotlin
# anymore, the compiler will have to compile itself. This is a process called "Selfhosting",
# and some major languages will go through it. Rust and C are two good examples.


# Thanks for watching :)

 */