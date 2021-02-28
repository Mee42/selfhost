

enum class TokenType {
    IDENTIFIER,
    LPAREN,
    RPAREN,
    RBRACE,
    LBRACE,
    EXCLAMATION_MARK,
    SEMICOLON,
    INTEGER_LITERAL,
    STRING_LITERAL,
}
class Token(val type: TokenType, val content: String)

const val letters = "abcdefghijklmnopqrstuvwxyz"
val identifierStarterCharset = letters + letters.toUpperCase() + "+-/\\><.,%'=_$"
val identifierBodyCharset = "$identifierStarterCharset!?0123456789"

fun lex(str: String): List<Token> {
    if(str.isEmpty()) return emptyList()
    val first = str[0]
    val rest = str.substring(1)
    return when(first) {
        ' ', '\n', '\t' -> lex(rest)
        '(' -> listOf(Token(TokenType.LPAREN, "(")) + lex(rest)
        ')' -> listOf(Token(TokenType.RPAREN, ")")) + lex(rest)
        '{' -> listOf(Token(TokenType.LBRACE, "{")) + lex(rest)
        '}' -> listOf(Token(TokenType.RBRACE, "}")) + lex(rest)
        ';' -> listOf(Token(TokenType.SEMICOLON, ";")) + lex(rest)
        '!' -> listOf(Token(TokenType.EXCLAMATION_MARK, "!")) + lex(rest)
        in '0'..'9' -> {
            val x = rest.indexOfFirstOrNull { it !in '0'..'9' } ?: rest.length
            val int = first + rest.substring(0, x)
            listOf(Token(TokenType.INTEGER_LITERAL, int)) + lex(rest.substring(x))
        }
        in identifierStarterCharset -> {
            val x = rest.indexOfFirstOrNull { it !in identifierBodyCharset } ?: rest.length
            val ident = first + rest.substring(0, x)
            listOf(Token(TokenType.IDENTIFIER, ident)) + lex(rest.substring(x))
        }
        '"' -> {
            val x = rest.indexOfFirstOrNull { it == '"' } ?: error("no ending quote while lexing string '$str'")
            val stringValue = rest.substring(0, x)
            listOf(Token(TokenType.STRING_LITERAL, stringValue)) + lex(rest.substring(x + 1))
        }
        else -> error("i dunno how to lex \"$str\"")
    }
}



fun CharSequence.indexOfFirstOrNull(conditional: (Char) -> Boolean): Int? {
    val x = this.indexOfFirst(conditional)
    return if(x == -1) null else x
}