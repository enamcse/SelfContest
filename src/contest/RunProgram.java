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
    int runningThread[];
    Thread thisThread;

    RunProgram(Submission sub, Contest contest,int[] runningThread, RunContest upper) {
        thisThread = new Thread(this,"rejudge thread");
        if (sub.language.equals("C") || sub.language.equals("C++")) {
//            System.out.println("Obviously, it should come here!");
            this.contest = contest;
            this.problem = contest.problems[sub.problemId];
            this.username = sub.user;
            this.runningThread = runningThread;
            runningSubmission = sub;
            upperClass = upper;
            judge = new VerdictGiver(sub.submittedFile, problem, runningSubmission);
            
//            System.out.println("Obviously, it is done!");
        }
    }
    Thread getThread(){
        return thisThread;
    }
    
    RunProgram(String language, File file, Problem problem, Contest contest, RunContest upper,String username, int[] runningThread) {
        thisThread = new Thread(this,"judge thread");
        if (language.equals("C") || language.equals("C++")) {
//            System.out.println("Obviously, it should come here!");
            this.contest = contest;
            this.problem = problem;
            this.username = username;
            this.runningThread = runningThread;
            runningSubmission = new Submission(new Date(), language, problem.problemName, "IN QUEUE", contest.passedTime,username,file,problem.problemNo);
            contest.submissions.add(runningSubmission);
            upperClass = upper;
            judge = new VerdictGiver(file, problem, runningSubmission);
            
//            System.out.println("Obviously, it is done!");
        }
    }

    @Override
    public void run() {
        verdict = judge.process();
        runningSubmission.verdict = verdict;
        if (verdict.equalsIgnoreCase("ACCEPTED")) {
            contest.accepted[problem.problemNo - 1] = true;
            upperClass.refreshSolved();
        }
        runningThread[0]--;
        System.out.println(String.format("The verdict of Your submission of problem %s is:\n%s", runningSubmission.problemName, runningSubmission.verdict));
        JOptionPane.showMessageDialog(null, String.format("The verdict of Your submission of problem %s is:\n%s", runningSubmission.problemName, runningSubmission.verdict), "Verdict", JOptionPane.INFORMATION_MESSAGE);

    }

}
