import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream

sealed class JS {
    data class IntLiteral(val value: Int): JS()
    data class StringLiteral(val value: String): JS()
    data class Variable(val identifier: Identifier): JS()
    object Null: JS()
    data class Set(val variable: Identifier, val value: JS): JS()
    data class Closure(val body: List<JS>): JS()
    data class Call(val function: JS, val arguments: List<JS>): JS()
}

private fun Expr.toJS(): JS = when(this){
    is Expr.Application -> JS.Call(children.first().toJS(), children.subList(1, children.size).map(Expr::toJS))
    is Expr.Exec -> JS.Call(expr.toJS(), emptyList())
    is Expr.Set -> JS.Set(identifier, value.toJS())
    Expr.FiniteExpr.Unit -> JS.Null
    is Expr.FiniteExpr.Variable -> {
        val newIdent = identifier.replace("arr", "_arr_")
        JS.Variable(newIdent)
    }
    is Expr.FiniteExpr.Closure -> JS.Closure(body.map(Expr::toJS))
    is Expr.FiniteExpr.StringLiteral -> JS.StringLiteral(value) 
    is Expr.FiniteExpr.IntLiteral -> JS.IntLiteral(value)
}


private fun String.paren(boolean: Boolean): String = 
    (if(boolean) "(" else "") + this + (if(boolean) ")" else "")


val prelude = File("src/main/resources/prelude.js").readText().replace("\n","") + ";\n;"

fun compile(expr: Expr): String {
    return ";" + prelude + ";" + "(" + compile(expr.toJS(), true) +")" + ";"
}

fun mangleVariableName(str: Identifier, `allow$`: Boolean = true): String {
    if(str.startsWith("$")) {
        if(!`allow$`) error("use of $ variables not allowed in that context")
        if(str[1] == 'N') {
            return "arr"
        }
        return "(arr[" + str.drop(1).toInt() + "])"
    }
    return when(str) {
        "+" -> "std_plus"
        "-" -> "std_minus"
        "*" -> "std_times"
        "/" -> "std_div"
        "%" -> "std_mod"
        "if" -> "std_if"
        "++" -> "std_concat"
        "true" -> "std_true"
        "false" -> "std_false"
        else -> str.replace("arr", "_arr_")
    }
}

private fun compile(js: JS, needsParens: Boolean): String = when(js) {
    is JS.IntLiteral -> "" + js.value
    is JS.StringLiteral -> '"' + js.value + '"'
    is JS.Variable -> mangleVariableName(js.identifier)
    JS.Null -> "null"
    is JS.Set -> "const " + mangleVariableName(js.variable, `allow$` = false)+ " = " + compile(js.value, false)
    is JS.Closure -> {
        var s = ((if (true) "(...arr) => { " else "() => {"))
        for((i, child) in js.body.withIndex()) {
            if(i == js.body.lastIndex && child !is JS.Set) {
                s += "return " + compile(child, true)
            } else {
                s += compile(child, true) + ";"
            }
        }
            s += "}"
        s.paren(needsParens)
    }
    is JS.Call -> {
        compile(js.function, true) + js.arguments.joinToString(",","(",")") { compile(it, false) }
    }
}


fun run(str: String) {
    val file = File("/tmp/selfhost/" + { ('a'..'z').random()}.repeat(25).joinToString("") { "" + it() } + ".js")
    file.parentFile.mkdir()
    PrintStream(FileOutputStream(file)).use { it.print(str) }
    ProcessBuilder("node", file.absolutePath).inheritIO().start().waitFor()
}

fun runCaptureSTDOUT(str: String, firstArgument: String) : String {
    val file = File("/tmp/selfhost/" + { ('a'..'z').random()}.repeat(25).joinToString("") { "" + it() } + ".js")
    val logFile = File("/tmp/selfhost/" + { ('a'..'z').random()}.repeat(25).joinToString("") { "" + it() } + ".output")
    file.parentFile.mkdir()
    PrintStream(FileOutputStream(file)).use { it.print(str) }
    ProcessBuilder("node", "--stack-size=1280000", file.absolutePath, firstArgument).redirectErrorStream(true).redirectOutput(logFile).start().waitFor()
    return logFile.readText()
}

fun <T> T.repeat(n: Int): List<T> {
    val x = mutableListOf<T>()
    for(i in 0 until n) x.add(this)
    return x
}
