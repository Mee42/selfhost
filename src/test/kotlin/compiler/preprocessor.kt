package compiler

import compile
import org.junit.Test
import parse
import lex
import preprocess
import java.io.File
import runCaptureSTDOUT
import kotlin.test.assertEquals


class CompilerPreProcessorTests {
    @Test
    fun `full suite test`() {
        val removeNewlines = true
        val fileTesting = "res/compiler/preprocessor.selfhost"
        val output = runCaptureSTDOUT(compile(parse(lex(preprocess(File("res/compiler/tests/preprocessor-tests.selfhost").readText())))), fileTesting)
        assertEquals(preprocess(File(fileTesting).readText()).filter{it != '\n'||!removeNewlines}.trim(), output.filter{it != '\n'||!removeNewlines}.trim())
    }
}