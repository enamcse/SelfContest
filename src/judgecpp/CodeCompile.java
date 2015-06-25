/*
 * Copyright 2015 Enamul.
 *
 * Most of my softwares are open for educational purpose, but some are 
 * confidential. So, before using it openly drop me some lines at
 *
 *      enamsustcse@gmail.com
 *
 * I do not guarantee that the software would work properly. There could
 * remain bugs. If you found any of them, kindly report me.
 * If you need to use this or some part of it, use it at your own risk.
 * This software is not a professionally developed, so commercial use 
 * is not approved by default.
 */
package judgecpp;

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
