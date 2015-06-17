/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package judge;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * C code compilation is the main work of this class. It works with a new thread
 * implementing runnable class. It also implements RunInCmd class for easy 
 * accessing in cmd.
 * @author      Enamul Hassan    <enamsustcse@gmail.com>
 * @version     1.0
 * @since       2014-03-15
 */
class CodeCompile implements Runnable, RunInCmd {

    Thread compilerThread;
    private final File fileCPP;
    
    public File file;
    public boolean errorHas;

    /**
     * It initiates the class variable fileCPP, which is the source file to be 
     * compiled. It takes an serial number for the file from CodeProcessing 
     * class to avoid overwrite the running program and then makes a file named
     * "compiledFile" extending the serial number. This name is used to create 
     * the executable file of the source code in case of no compilation error.
     * @param fileCPP       the path of the c source code file.
     */
    public CodeCompile(File fileCPP) {
        this.fileCPP = fileCPP;

        file = new File(String.format("compiledFile%d", CodeProcessing.getNewFileNumber()));
        compilerThread = new Thread(this, "compilerThread");
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public Process cmdRun(String command) throws IOException {
        Runtime rt;
        Process pr;
        rt = Runtime.getRuntime();
        pr = rt.exec(command);
        return pr;
    }

    @Override
    /**
     * {@inheritDoc}
     * This thread runs a command in the following form to compile the file in
     * cmd.
     * <code>
     * c++ -o <fileToBeCreated> <c source code file name>
     * </code>
     * Then detects is there any compilation error occured or not. If occurs
     * then errorHas variable of the class is made true or false otherwise.
     */
    public void run() {

        try {

            Process pr = cmdRun(String.format("g++ -std=c++11 -o \"" + file.toString() + "\" \"" + fileCPP.toString() + "\""));
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getErrorStream()));

            errorHas = false;
            while (input.readLine() != null) {
                errorHas = true;
            }
        } catch (IOException ex) {
//            Logger.getLogger(CodeCompile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
