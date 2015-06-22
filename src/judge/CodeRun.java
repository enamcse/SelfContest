/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package judge;

import java.io.File;
import java.io.IOException;

/**
 * After successfully compiled a file, it is needed to run to get further 
 * verdicts and it is done by this class. It works in another thread extending 
 * the thread class. As like as CodeCompile class, it implements the RunInCmd 
 * class for easy access in cmd.
 * @author      Enamul Hassan    <enamsustcse@gmail.com>
 * @version     1.0
 * @since       2014-03-15
 */
public class CodeRun extends Thread implements RunInCmd {

    private final File fileSTDOUT;
    private final File fileSTDIN;
    private final File fileEXE;

    public int runtimeError;
    public Process pr;

    /**
     * Initiates the thread name and set input, output and executable files as 
     * in parameter as like other constructors. 
     * @param fileEXE       the executable file address for the program
     * @param fileSTDIN     input file address against which the executable file
     *                      should run
     * @param fileSTDOUT    output file address which is created by running the
     *                      executable file against the input file
     */
    public CodeRun(File fileEXE, File fileSTDIN, File fileSTDOUT) {
        super("runnerThread");
        this.fileSTDOUT = fileSTDOUT;
        this.fileSTDIN = fileSTDIN;
        this.fileEXE = fileEXE;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public Process cmdRun(String command) throws IOException{
        Runtime rt=null;
        Process pr=null;
            rt = Runtime.getRuntime();
            pr = rt.exec(command);
        return pr;
    }
    
    @Override
    /**
     * {@inheritDoc}
     * This thread runs a command in the following form to run the executable
     * file in a new cmd.
     * <code>
     * cmd /c <executableFile> < <inputFile> > <outputFile>
     * </code>
     * Then detects is there any runtime error occured or not. If does not occur
     * then runtimeError variable of the class is remained 0 or any integer 
     * except 0 otherwise.
     */
    public void run() {
        runtimeError = 0;
        
        try {
            System.out.println("fileexe = " + fileEXE.toString());
            System.out.println("input = " + fileSTDIN.toString());
            System.out.println("output = " + fileSTDOUT.toString());
            pr = cmdRun(String.format("cmd /c \"\"" + fileEXE.toString() + "\"<\"" + fileSTDIN.toString() + "\">\"" + fileSTDOUT.toString() + "\"\""));
            
        } catch (IOException ex) {
//            Logger.getLogger(CodeCompile.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            runtimeError = pr.waitFor();
        } catch (InterruptedException | NullPointerException ex) {
//            System.out.println("I am here!");
        }
    }
}
