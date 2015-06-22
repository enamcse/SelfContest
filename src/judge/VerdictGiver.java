/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package judge;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import contest.Problem;
import contest.Submission;

/**
 *
 * @author Enamul
 */
public class VerdictGiver  {

        private final CodeProcessing codeProcessor = new CodeProcessing();
//        private final FileMatching matcher = new FileMatching();
//        private String[] userOUT;
        Problem problem;
        File fileCPP;
        Submission runningSubmission;
//        /**
//         * If the specified file is running then it kills the program and then
//         * deletes the file.
//         *
//         * @param file the file to be deleted
//         */
//        private void deleteFiles(File file) {
//            try {
//                Files.deleteIfExists(file.toPath());
//            } catch (IOException ex) {
////                Logger.getLogger(AssignmentSimulation.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }

        /**
         * 
         * @param fileCPP       source file path
         * @param fileSTDIN     input file path
         * @param fileSTDOUT    output file path
         */
        public VerdictGiver(File fileCPP, Problem problem, Submission sub) {
            this.fileCPP = fileCPP;
            this.problem = problem;
            this.runningSubmission = sub;
        }
        /**
         * process data and gives verdicts.
         */
        public String process() {

            return codeProcessor.process(fileCPP, problem, runningSubmission);

//            if (userOUT.length <= 1) {
//                if (matcher.process(new File(userOUT[0]), fileSTDOUT)) {
//                    deleteFiles(new File(userOUT[0]));
//                    return "ACCEPTED";
//                } else {
//                    deleteFiles(new File(userOUT[0]));
//                    System.out.println("returned from 67th line");
//                    return "WRONG ANSWER";
//                }
//            } else {
//                deleteFiles(new File(userOUT[0]));
//                int verdictCode = Integer.parseInt(userOUT[1]);
//                System.out.println("verdict code = " + verdictCode);
//                if(verdictCode==3)
//                    return "RUNTIME ERROR";
//                else if(verdictCode==2)
//                    return "WRONG ANSWER";
//                else if(verdictCode==4)
//                    return "TIME LIMIT EXCEEDED";
//                else if(verdictCode==5)
//                    return "COMPILE ERROR";
//                else{
//                    JOptionPane.showMessageDialog(null,"Something happened wrong\nTell your service provider to fix the JUDGEMENT SECTION!" ,"VERDICT ERROR!", ERROR_MESSAGE);
//                    return "JUDGEMENT FAILED";
//                }
//                System.out.println("here@here|"+Integer.parseInt(userOUT[1])+"|");
//            }
        }
    }