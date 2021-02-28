


fun toDisplayString(expr: Expr, needsParens: Boolean = false): String {
    fun String.paren(): String {
        return (if(needsParens) "(" else "") + this + (if(needsParens) ")" else "")
    }
    return when(expr) {
        is Expr.Application -> expr.children.joinToString(" ") { toDisplayString(it, true) }.paren()
        is Expr.Exec -> ("!" + toDisplayString(expr, false)).paren()
        is Expr.Set -> (expr.identifier + " = " + toDisplayString(expr.value, false)).paren()
        Expr.FiniteExpr.Unit -> "()"
        is Expr.FiniteExpr.Variable -> expr.identifier
        is Expr.FiniteExpr.Closure -> (if(expr.hasArgs) "{" else "?{") + expr.body.joinToString(";") { toDisplayString(it, false) } + "}"
        is Expr.FiniteExpr.StringLiteral -> "\"" + expr.value + "\""
        is Expr.FiniteExpr.IntLiteral -> "${expr.value}"
    }
}

