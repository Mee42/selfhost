import Expr.FiniteExpr
import java.text.ParseException

typealias Identifier = String

sealed class Expr {
    data class Application(val children: List<Expr>): Expr() {
        init { assert(children.size > 1) { "Application created with ${children.size} children"} }
    }

    data class Exec(val expr: Expr): Expr()
    data class Set(val identifier: Identifier, val value: Expr): Expr()
    sealed class FiniteExpr: Expr() {
        object Unit: FiniteExpr()
        data class Variable(val identifier: Identifier): FiniteExpr()
        data class Closure(val body: List<Expr>): FiniteExpr()
        data class StringLiteral(val value: String): FiniteExpr()
        data class IntLiteral(val value: Int): FiniteExpr()
    }
}



fun parse(str: List<Token>): Expr {
    return parseExpr(ArrayDeque(str))
}


private fun parseExpr(tokens: ArrayDeque<Token>): Expr {
    if(tokens.isEmpty()) error("reached end of file while parsing")
    // TODO
    if(tokens.first().type == TokenType.EXCLAMATION_MARK) {
        // exec time
        tokens.removeFirst()
        return Expr.Exec(parseExpr(tokens))
    }
    if(tokens.first().type == TokenType.IDENTIFIER && tokens.getOrNull(1)?.content == "=") {
        // it's a Set
        val identifier = tokens.removeFirst().content
        tokens.removeFirst() // the equals
        val value = parseExpr(tokens)
        return Expr.Set(identifier, value)
    }
    val exprs = mutableListOf<Expr>()
    while(tokens.firstOrNull()?.type !in listOf(TokenType.SEMICOLON, TokenType.RPAREN, TokenType.RBRACE, null)) {
        exprs += parseFiniteExpr(tokens)
    }
    if(exprs.size == 1) {
        return exprs.first()
    }
    return Expr.Application(exprs)

}
private fun parseFiniteExpr(tokens: ArrayDeque<Token>): Expr {
    if(tokens.isEmpty()) error("reached end of file while parsing")
    // simple parse
    val first = tokens.removeFirst()
    return when(first.type) {
        TokenType.INTEGER_LITERAL -> FiniteExpr.IntLiteral(first.content.toInt())
        TokenType.IDENTIFIER -> FiniteExpr.Variable(first.content)
        TokenType.STRING_LITERAL -> FiniteExpr.StringLiteral(first.content)
        TokenType.LPAREN -> {
            if(tokens.first().type == TokenType.RPAREN) {
                tokens.removeFirst()
                FiniteExpr.Unit
            } else {
                val expr = parseExpr(tokens)
                tokens.removeFirst().assertType(TokenType.RPAREN)
                expr
            }
        }
        TokenType.RPAREN -> error("not expecting rparen here")
        TokenType.LBRACE -> {
            val lines = mutableListOf<Expr>()
            while(tokens.first().type != TokenType.RBRACE) {
                while(tokens.first().type == TokenType.SEMICOLON) tokens.removeFirst()
                lines += parseExpr(tokens)
                while(tokens.first().type == TokenType.SEMICOLON) tokens.removeFirst()
            }
            tokens.removeFirst().assertType(TokenType.RBRACE)
            FiniteExpr.Closure(lines)
        }
        TokenType.RBRACE -> error("Not expecting lparen here")

        TokenType.EXCLAMATION_MARK -> error("can't use ! in finite expressions")
        TokenType.SEMICOLON -> error("can't use ; in finite expressions")
    }
}


fun Token.assertType(type: TokenType) {

    assert(this.type == type) { "expecting a $type, but found ${this.type}" }
}