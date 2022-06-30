package bookreader.components;

import org.python.core.PyException;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

@Component
public class PyScriptRunner {

    /**
     * The script that will be executed. Can be either the script itself or a
     * filepath to it.
     */
    private String input;
    /**
     * The type of input that has been passed.
     */
    private InputType inputType;
    /**
     * List of output variables. At the end of execution of a script those
     * variable will be retrieved from the python environment.
     */
    private String[] outputVariableNames;
    /**
     * The output stream used by the runner. Default is System.out.
     */
    private OutputStream outputStream;
    /**
     * Default constructor.
     */
    public PyScriptRunner() {
        this.reset();
    }

    /**
     * Resets the state of the runner by clearing up any loaded input.
     */
    public void reset() {
        this.input = null;
        this.inputType = InputType.None;
        this.outputVariableNames = new String[0];
        this.outputStream = System.out;
    }

    /**
     * Uses a script located in a file. The file is not opened when this
     * method is called.
     * @param filepath The filepath to the file.
     */
    public void useFile(final String filepath) {
        this.input = filepath;
        this.inputType = InputType.Filepath;
    }
    /**
     * Uses a script that's been stored in memory.
     * @param script The script.
     */
    public void useScript(final String script) {
        this.input = script;
        this.inputType = InputType.SourceCode;
    }

    /**
     * Sets the python variable names which are to be retrieved after a
     * script is executed.
     * @param outputVariables The names of the python variables.
     */
    public void setOutputVariableNames(final String... outputVariables) {
        this.outputVariableNames = outputVariables;
    }

    /**
     * Sets an output stream for the python runner. Default is System.out
     * @param stream Output stream.
     */
    public void setOutputStream(OutputStream stream) {
        this.outputStream = stream;
    }

    /**
     * Runs the python script with the given parameters.
     * @return A list of all request variables to be outputted. Use
     * {@link PyScriptRunner#setOutputVariableNames(String...)} to assign them
     * @throws IOException When a problem occurs when reading from a file.
     * @throws IllegalStateException When no input has been given to the runner.
     * @throws PyException When an exception arises from the python code.
     * interpreter
     */
    public List<PyObject> run() throws IOException, IllegalStateException, PyException {
        if (!this.canRun()) throw new IllegalStateException();

        List<PyObject> ret;
        try (PythonInterpreter pyInterp = new PythonInterpreter()) {
            pyInterp.setOut(this.outputStream);
            pyInterp.setErr(this.outputStream);

            if (this.inputType == InputType.SourceCode) pyInterp.exec(this.input);
            else if (this.inputType == InputType.Filepath) pyInterp.execfile(this.input);

            // retrieve all the requested variables
            ret = Arrays.stream(outputVariableNames)
                    .map(pyInterp::get)
                    .toList();
        }
        return ret;
    }

    /**
     * Checks whether the runner is in a state where it's ready to run.
     * The minimum requirements for it to be able to run is to be passed
     * any type of input.
     * @return True if it can be run, false otherwise.
     */
    public boolean canRun() {
        if (this.input == null || this.inputType == InputType.None) return false;

        return true;
    }

    private enum InputType {
        /**
         * Default state when no input has been provided.
         */
        None,
        /**
         * When the provided input is a path to a file.
         */
        Filepath,
        /**
         * When the provided input is a python script string.
         */
        SourceCode
    }
}

