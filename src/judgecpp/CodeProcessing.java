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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import contest.Problem;
import contest.Submission;
import java.util.Date;

/**
 * All the verdicts of c codes are created by this class. But the process of
 * this class made synchronized to avoid errors. It has a static variable named
 * fileNumber which gives a new number every time for avoiding exceptions for
 * overwriting existing running file.
 *
 * @author Enamul Hassan    <enamsustcse@gmail.com>
 * @version 1.0
 * @since 2014-03-15
 */
public class CodeProcessing {

    private static int fileNumber = 0;
    private final FileMatching matcher = new FileMatching();

    /**
     * If the specified file is running then it kills the program and then
     * deletes the file.
     *
     * @param file the file to be deleted
     */
    public void deleteFile(File file) {
        try {
            Runtime.getRuntime().exec("taskkill /F /IM " + file);
        } catch (IOException ex) {
        }

        while (file.exists()) {
            try {
                Files.deleteIfExists(file.toPath());
            } catch (IOException ex) {
            }
        }
    }

    /**
     * Gives a new file number to avoid creating same name file.
     *
     * @return new number of file
     */
    protected static int getNewFileNumber() {
        return fileNumber++;
    }

    /**
     * To compile a file, it creates a new object of CodeCompile class with the
     * specified source code. Then creates an executable file path deleting if
     * the same name file is already exists. It waits for the thread to complete
     * the work. Then detects any compilation error occured or not.
     *
     * @param fileCPP the c source code file
     * @return the output file in case of no compilation error, otherwise null
     */
    public File compileCode(File fileCPP) {
        CodeCompile codeCompiler = new CodeCompile(fileCPP);
        File fileEXE = new File(codeCompiler.file.toString().concat(".exe"));
        deleteFile(fileEXE);
        codeCompiler.compilerThread.start();
        try {
            codeCompiler.compilerThread.join();

        } catch (InterruptedException ex) {
        }
        if (codeCompiler.errorHas == true) {
            System.out.println("it has errors");
            return null;
        }
        return fileEXE;
    }

    /**
     * To run a file, it creates a new object of codeRun class with the
     * specified source code, input and output files. Then it waits for the
     * thread to complete the work within the time, otherwise time limit
     * exceeded is reported. Then detects any compilation error occured or not.
     *
     * @param fileEXE the executable program file
     * @param fileSTDIN the input file
     * @param fileSTDOUT the output file
     * @return verdict code for the program 1 - Correct answer. 3 - Runtime
     * error. 4 - Time limit exceeded.
     */
    public int RunProgram(File fileEXE, File fileSTDIN, File fileSTDOUT, int timeLimit) {
        boolean timeLimitExceeded;
        CodeRun codeRunner = new CodeRun(fileEXE, fileSTDIN, fileSTDOUT);
        codeRunner.start();
        timeLimitExceeded = false;
        if (codeRunner.runtimeError != 0) {
            return 3;
        }
        try {
            codeRunner.join(timeLimit);
            sleep(100);
            if (codeRunner.isAlive() == true) {
                timeLimitExceeded = true;
                sleep(100);
            }
        } catch (InterruptedException ex) {
        }
        try{
        codeRunner.pr.destroy();
        }catch(Exception ex){
            System.out.println("Exception: "+ex);
        }
//        deleteFile(fileEXE);

        if (timeLimitExceeded == true) {
            return 4;
        }

        if (codeRunner.runtimeError != 0) {
            return 3;
        }

        return 1;
    }

    /**
     * This method manages the compilation and running of the program. It
     * returns an array of string size two containing the verdict short form and
     * the verdict code if the output is not correct otherwise array of string
     * size one containing the output file name.
     *
     * @param fileCPP the c source code file
     * @param fileSTDIN the input file
     * @return array of string size two containing the verdict short form and
     * the verdict code if the output is not correct otherwise array of string
     * size one containing the output file name.
     */
    public synchronized String process(File fileCPP, Problem problem, Submission submission) {
        submission.verdict = "COMPILING";
        File fileEXE = compileCode(fileCPP);
        if (fileEXE == null) {
//            System.out.println("CodeProcessing, 136 null");
            return "COMPILE ERROR";
        }
        
//        System.out.println("CodeProcessing 141\n");
//        problem.timeLimit = 5000;
        if (problem.oneFile) {
            File fileSTDOUT = new File(String.format("output%d.txt", getNewFileNumber()));
            deleteFile(fileSTDOUT);
            submission.verdict = "RUNNING...";
            
            long now,end;
            now = new Date().getTime();
            int verdictCode = RunProgram(fileEXE, problem.inputFile, fileSTDOUT, problem.timeLimit);
            end = new Date().getTime();
            deleteFile(fileEXE);

            if (verdictCode == 4) {
                submission.timeElapsed = end - now;
                return "TIME LIMIT EXCEEDED";

            }
            if (verdictCode == 3) {
                return "RUNTIME ERROR";
            }
            if (matcher.process(problem,fileSTDOUT, problem.outputFile)) {
                deleteFiles(fileSTDOUT);
                submission.timeElapsed = end - now;
                return "ACCEPTED";
            } else {
                deleteFiles(fileSTDOUT);
                System.out.println("returned from 166th line");
                return "WRONG ANSWER";
            }
            
        } else {
            submission.verdict = String.format("PREPARING TESTS...");
            File inputFiles[] = problem.folderPath.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(problem.inputExtension);
//                                System.out.println("Name: \'" + name+"\' out: \'"+name.toLowerCase().endsWith(extensions[it]) +"\' returned == "+shouldBeReturned+ "for  ext: "+extensions[it]);
                }
            });
            
            int extensionLength = problem.inputExtension.length();

            long now, end, totalTime=0;
            for (int i = 0; i < inputFiles.length; i++) {
                submission.verdict = String.format("RUNNING...(%d)",i+1);
                File fileSTDOUT = new File(String.format("output%d.txt", getNewFileNumber()));
                deleteFile(fileSTDOUT);
                now = new Date().getTime();
                int verdictCode = RunProgram(fileEXE, inputFiles[i], fileSTDOUT, problem.timeLimit);
                end = new Date().getTime();
                totalTime += end-now; 
                if (verdictCode == 4) {
                    deleteFile(fileEXE);
                    deleteFiles(fileSTDOUT);
                    submission.timeElapsed = problem.timeLimit;
                    return "TIME LIMIT EXCEEDED";

                }
                if (verdictCode == 3) {
                    deleteFile(fileEXE);
                    deleteFiles(fileSTDOUT);
                    return "RUNTIME ERROR";
                }

                if (!matcher.process(problem,fileSTDOUT, new File(inputFiles[i].toString().substring(0, inputFiles[i].toString().length()-extensionLength)+problem.outputExtension))) {
                    deleteFile(fileEXE);
                    deleteFiles(fileSTDOUT);
//                    System.out.println("returned from 203th line");
                    return String.format("WRONG ANSWER ON TEST %d",(i+1));
                }
                deleteFiles(fileSTDOUT);
            }
//            submission.timeElapsed = new Date().getTime() - now.getTime();
            submission.timeElapsed= totalTime/inputFiles.length;
            deleteFile(fileEXE);
            return "ACCEPTED";
        }
    }

    /**
     * If the specified file is running then it kills the program and then
     * deletes the file.
     *
     * @param file the file to be deleted
     */
    private void deleteFiles(File file) {
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException ex) {
//                Logger.getLogger(AssignmentSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
