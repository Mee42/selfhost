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

fun main() {
    run(compile(parse(lex(preprocess(File("res/compiler/lexer.selfhost").readText())))))
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




 */