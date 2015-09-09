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
package contest;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import judgecpp.VerdictGiver;
import security.Hashing;

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
    int runningThread[];
    Thread thisThread;
    String host;

    RunProgram(Submission sub, Contest contest, int[] runningThread) {
        thisThread = new Thread(this, "rejudge thread");
        if (sub.language.equals("C") || sub.language.equals("C++")) {
//            System.out.println("Obviously, it should come here!");
            this.contest = contest;
            this.problem = contest.problems[sub.problemId];
            this.username = sub.user;
            this.runningThread = runningThread;
            runningSubmission = sub;
            judge = new VerdictGiver(sub.submittedFile, problem, runningSubmission);

//            System.out.println("Obviously, it is done!");
        }
    }

    Thread getThread() {
        return thisThread;
    }

    RunProgram(String host, String language, File file, Problem problem, Contest contest, String username, int[] runningThread) {
        this.host = host;
        thisThread = new Thread(this, "judge thread");
        if (language.equals("C") || language.equals("C++")) {
//            System.out.println("Obviously, it should come here! "+problem.problemNo+" "+problem.problemName);
            this.contest = contest;
            this.problem = problem;
            this.username = username;
            this.runningThread = runningThread;
            runningSubmission = new Submission(new Date(), language, problem.problemName, "IN QUEUE", contest.passedTime, username, file, problem.problemNo);
            contest.submissions.add(runningSubmission);
            judge = new VerdictGiver(file, problem, runningSubmission);

//            System.out.println("Obviously, it is done!");
        }
    }

    void notifyVerdict() {
        ObjectOutputStream output = null; // output stream to server
        ObjectInputStream input; // input stream from server
        
        try {
            System.out.println("host = "+host);
            Socket client = new Socket();
            client.connect(new InetSocketAddress(host, 4772), 500);
            
            output = new ObjectOutputStream(client.getOutputStream());
//            output.flush(); // flush output buffer to send header information
            input = new ObjectInputStream(client.getInputStream());
            String user = (String) input.readObject();
            String hostIP = client.getInetAddress().getHostAddress();;;
            
            if(!user.equals(Hashing.sha512Hex(username)) || !hostIP.equals(host)) {output.writeObject(Hashing.sha512Hex("No"));new IOException();}
            
            output.writeObject("verdict");
            output.flush();
            output.writeObject(String.format("The verdict of Your submission of problem %s is:\n%s", runningSubmission.problemName, runningSubmission.verdict));
            output.flush();
            
            output.close(); // close output stream
            input.close(); // close input stream
            client.close(); // close socket
            
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(RunProgram.class.getName()).log(Level.SEVERE, null, ex);
        }
         catch (Exception ex) {
            Logger.getLogger(RunProgram.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        verdict = judge.process();
        runningSubmission.verdict = verdict;

        /// notify verdict to the team
        notifyVerdict();
        
        runningThread[0]--;
        System.out.println(String.format("The verdict of Your submission of problem %s is:\n%s", runningSubmission.problemName, runningSubmission.verdict));
        
    }

}
