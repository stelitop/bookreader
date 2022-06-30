package bookreader.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.python.core.PyException;
import org.python.core.PySyntaxError;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PyScriptRunnerTest {

    private PyScriptRunner psr;

    @BeforeEach
    public void setUp() {
        psr = new PyScriptRunner();
    }

    @Test
    public void simpleTest() throws IOException {
        psr.useScript("x = 10+10");
        psr.setOutputVariableNames("x");
        var output = psr.run();
        assertThat(output).hasSize(1);
        assertThat(output.get(0).asInt()).isEqualTo(20);
    }

    @Test
    public void testNotRunnable() {
        assertThat(psr.canRun()).isFalse();
        assertThrows(IllegalStateException.class, () -> psr.run());
    }

    @Test
    public void testFromFile() throws IOException {
        psr.useFile("testfiles/python/squareroot.py");
        psr.setOutputVariableNames("num_sqrt");
        var output = psr.run();
        assertThat(output).hasSize(1);
        assertThat(output.get(0).asInt()).isEqualTo(4);
    }

    @Test
    public void requestMultipleVariables() throws IOException {
        psr.useScript("a = 10\nb= 25\nc = a*b");
        psr.setOutputVariableNames("a", "b", "c");
        var output = psr.run();
        assertThat(output).hasSize(3);
        assertThat(output.get(0).asInt()).isEqualTo(10);
        assertThat(output.get(1).asInt()).isEqualTo(25);
        assertThat(output.get(2).asInt()).isEqualTo(250);
    }

    @Test
    public void requestNonexistentVariables() throws IOException {
        psr.useScript("x = 123");
        psr.setOutputVariableNames("y");
        var output = psr.run();
        assertThat(output).hasSize(1);
        assertThat(output.get(0)).isNull();
    }

    @Test
    public void outputStreamTest() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        psr.useScript("print(\"Output Stream Test\")");
        psr.setOutputStream(stream);
        var output = psr.run();
        assertThat(output).hasSize(0);
        assertThat(stream.toString().trim()).isEqualTo("Output Stream Test");
    }

    @Test
    public void incorrectFileSyntax() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        psr.useScript("a = 15 ( 7");
        psr.setOutputVariableNames("a");
        psr.setOutputStream(stream);
        assertThrows(PyException.class, () -> psr.run());
    }

    @Test
    public void testCanRun() {
        assertThat(psr.canRun()).isFalse();
        psr.useScript("x = \"Giving this a script should make it runnable.\"");
        assertThat(psr.canRun()).isTrue();
    }

    @Test
    public void testReset() throws IOException {
        psr.useScript("abc = 123");
        psr.setOutputVariableNames("abc");
        psr.reset();
        assertThat(psr.canRun()).isFalse();
        psr.useScript("abc = 123");
        var output = psr.run();
        assertThat(output.size()).isZero();
    }
}