/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package contest;

import java.io.File;
import java.util.Date;
import javax.swing.JOptionPane;
import judge.VerdictGiver;

/**
 *
 * @author Enamul
 */
class RunProgram implements Runnable {
    String username;
    String verdict;
    VerdictGiver judge;
    Submission runningSubmission;
    Contest contest;
    Problem problem;
    RunContest upperClass;

    RunProgram(String language, File file, Problem problem, Contest contest, RunContest upper,String username) {
        Thread t = new Thread(this,"judge thread");
        if (language.equals("C") || language.equals("C++")) {
//            System.out.println("Obviously, it should come here!");
            judge = new VerdictGiver(file, problem);
            this.contest = contest;
            this.problem = problem;
            this.username = username;
            
            runningSubmission = new Submission(new Date(), language, problem.problemName, "Running", contest.passedTime,username);
            contest.submissions.add(runningSubmission);
            upperClass = upper;
//            System.out.println("Obviously, it is done!");
        }
        t.start();
    }

    @Override
    public void run() {
        verdict = judge.process();
        runningSubmission.verdict = verdict;
        if (verdict.equalsIgnoreCase("ACCEPTED")) {
            contest.accepted[problem.problemNo - 1] = true;
            upperClass.refreshSolved();
        }
        System.out.println(String.format("The verdict of Your submission of problem %s is:\n%s", runningSubmission.problemName, runningSubmission.verdict));
        JOptionPane.showMessageDialog(null, String.format("The verdict of Your submission of problem %s is:\n%s", runningSubmission.problemName, runningSubmission.verdict), "Verdict", JOptionPane.INFORMATION_MESSAGE);

    }

}
