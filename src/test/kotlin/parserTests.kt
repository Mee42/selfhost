import org.junit.Test
import kotlin.test.*

class ParserTests {
    @Test
    fun `empty input`() {
        assertFails { parse(lex("")) }
    }
    @Test
    fun `unit type`() {
        assertEquals(Expr.FiniteExpr.Unit, parse(lex("()")))
    }
    @Test
    fun `variable uses, with parentheses`() {
        assertEquals(Expr.FiniteExpr.Variable("a"), parse(lex("a")))
        assertEquals(Expr.FiniteExpr.Variable("a"), parse(lex("(((((a)))))")))
    }
    @Test
    fun application() {
        val expression =
            Expr.Application(listOf(
                Expr.FiniteExpr.Variable("hello"),
                Expr.FiniteExpr.Unit
            ))

        assertEquals(expression, parse(lex("hello ()")))
        assertEquals(expression, parse(lex("((hello) ())")))
    }
    @Test
    fun closures() {
        assertEquals(Expr.FiniteExpr.Closure(true, listOf()), parse(lex("{}")))
        assertEquals(Expr.FiniteExpr.Closure(false, listOf()), parse(lex("?{}")))
        assertEquals(Expr.FiniteExpr.Closure(true, listOf(Expr.FiniteExpr.Unit)), parse(lex("{()}")))
        assertEquals(
            Expr.FiniteExpr.Closure(true, 
                listOf(Expr.Application(
                    listOf(Expr.FiniteExpr.Variable("print"), 
                        Expr.FiniteExpr.StringLiteral("Hello, World!"))))),
            parse(lex("{print \"Hello, World!\"}"))
            )
    }
    @Test
    fun `set`() {
        
        assertEquals(Expr.Set("a", Expr.FiniteExpr.IntLiteral(7)), parse(lex("a = 7")))
        
        
        val expr = Expr.FiniteExpr.Closure(true, listOf(
            Expr.Set("a", Expr.FiniteExpr.IntLiteral(7)),
            Expr.Set("b", Expr.FiniteExpr.IntLiteral(8)),
            Expr.Set("c",Expr.Application(listOf(
                Expr.FiniteExpr.Variable("+"),
                Expr.FiniteExpr.Variable("a"),
                Expr.FiniteExpr.Variable("b")
            )))
        ))
        assertEquals(expr, parse(lex("{a = 7; b = 8; c = + a b}")))
    }
}