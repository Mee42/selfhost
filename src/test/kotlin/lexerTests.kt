import TokenType.*
import org.junit.Test
import kotlin.test.assertEquals

class LexerTests {
    @Test
    fun identifiers() {
        val tokens = lex("abc ABC a1 B1 a753+- - -7 ===")
        tokens.forEach { token ->
            assertEquals(IDENTIFIER, token.type, "Token(${token.content})")
        }
    }
    @Test
    fun strings() {
        lex("""   "hello" "world""yes" """).forEachIndexed { i, token ->
            assertEquals(STRING_LITERAL, token.type)
            assertEquals(listOf("hello","world","yes")[i], token.content)
        }
    }
    @Test
    fun integers() {
        lex("1 27 567 38").forEach { token ->
            assertEquals(INTEGER_LITERAL, token.type)
        }
    }
    @Test
    fun specialChars() {
        assertEquals(listOf(LPAREN, RPAREN, LBRACE, RBRACE, QUESTION_MARK, EXCLAMATION_MARK),
            lex("(){}?!").map { it.type })
    }
}