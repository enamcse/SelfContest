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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import static security.Encryption.decrypt;
import static security.Encryption.encrypt;
import static security.Hashing.sha512Hex;

/**
 * This is the main Judge server. It accepts submissions and gives verdicts
 * after running them, notifies Submission Verdicts to the contestants, gives
 * Submission Data on request for updating Ranklist, maintains contest time.
 *
 * @since 1.0 #version 1.0
 * @author Enamul
 */
public class JudgeApplication implements Runnable {
//    public static String dataBaseUrl = "jdbc:mysql://localhost:3306/";
//    public static String dataBaseName = "contest";
//    public static String dataBaseTable = "users";

    StartContest upperClass;
    Contest contest;
    Timer timer, runSubmission;
    int runningThread[] = new int[1];//array of one element for having pass by reference property.
    int limitThread;//till now it is limited to one.
    private String saveFile; /// saving would be implemented later
    Vector<Thread> inQueue;
    HashMap<String, String> userIP; //keeps contestants IP against their handle

    boolean judgeRunning;
    boolean contestRunning;
    ServerSocket serverSocketUpdateNews;
    ServerSocket serverSocketLoginRequest;
    ServerSocket serverSocketContestInfo;
    ServerSocket serverSocketSubmissionReceive;

    private UpdateNews updateNews;
    private SubmissionRecieve submissionRecieve;
    private LoginRequest loginRequest;
    private ContestInfo contestInfo;

    /**
     * Loads the contest settings initiates necessary variables and sets two
     * timers. One timer for contest time and another timer for running
     * submission from submission queue. At last starts the judge thread. when
     * it destroys, all the contest settings would be saved in the filePath
     * file.
     *
     * @param filePath the file path where the contest setting object is
     * located.
     */
    public JudgeApplication(String filePath) {
        judgeRunning = true;
        userIP = new HashMap<>();
        this.inQueue = new Vector<>();
        loadContest(new File(filePath));
        this.saveFile = filePath;
        runningThread[0] = 0;

        /// max number of parallel submission 
        limitThread = 1;

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(contestRunning)contest.passedTime++;
                if(contestRunning&&contest.passedTime>contest.contestDuration){
                    stopContest();
                }
            }
        });
        timer.setRepeats(true);
//        timer.start();

        runSubmission = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
//                System.out.println("running: " + runningThread[0] + ", limit: " + limitThread + ", inQueue: " + inQueue.size() + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

                if (limitThread == runningThread[0]) {
                    return;
                }

                while (limitThread > runningThread[0]) {
                    if (inQueue.size() == 0) {
                        return;
                    }
                    runningThread[0]++;
                    System.out.println("thread name: " + inQueue.elementAt(0).getName());
                    inQueue.elementAt(0).start();
                    inQueue.removeElementAt(0);
                }
                if(contest.passedTime>=contest.contestDuration){
                    runSubmission.stop();
                }
            }
        });
        runSubmission.setRepeats(true);
//        runSubmission.start();
        new Thread(this).start();
    }

    /**
     * To get the submissions with their verdict and contest time, contestant
     * requests to the port 4444 which is run by this class.
     */
    class UpdateNews implements Runnable {

        /**
         * Starts this news thread.
         */
        UpdateNews() {
            new Thread(this).start();
        }

        /**
         * This class serves the contest submission information parallelly.
         */
        class SubmissionInfo implements Runnable {

            Socket connection;

            /**
             * It receives the socket connection and starts the thread.
             */
            public SubmissionInfo(Socket conn) {
                connection = conn;
                new Thread(this).start();
            }

            /**
             * It sends to the user the contest update as an instance of
             * ContestUpdate. No security is added till now.
             */
            @Override
            public void run() {
                ObjectOutputStream output = null;
                ObjectInputStream input = null;
                try {

                    String host = connection.getInetAddress().getHostAddress();;;
                    output = new ObjectOutputStream(connection.getOutputStream());
                    output.flush(); // flush output buffer to send header information
                    input = new ObjectInputStream(connection.getInputStream());

                    String username = (String) input.readObject(); // read new message
                    String hashedPass = (String) input.readObject(); // read new message

                    if (sha512Hex("localhost").equals(hashedPass) || ((contest.contestants.containsKey(username) && sha512Hex(contest.contestants.get(username)).equals(hashedPass)) && contestRunning)) {
                        output.writeObject(sha512Hex(username));
                        if(sha512Hex("1").equals((String) input.readObject())){
                            ContestUpdate upd = new ContestUpdate(contest.passedTime, contest.submissions);
                            output.writeObject(upd);
                        }
                        else{
                            output.writeObject(encrypt(String.format("%d", contest.passedTime),hashedPass));
                        }
                        
                    } else {
                        output.writeObject(sha512Hex("Yes No Very Good"));
                    }

                    output.flush(); // flush output to client

                    userIP.put(username, host);
                } catch (IOException | ClassNotFoundException ex) {
//                    Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
//                    Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        output.close(); // close output stream
                        input.close(); // close input stream
                        connection.close(); // close socket
                    } catch (IOException ex) {
//                        Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        /**
         * It opens port 4444 and gives the contest updates as an object of
         * ContestUpdate.
         */
        @Override
        public void run() {
            try {
                serverSocketUpdateNews = new ServerSocket(4444, 100);

                while (judgeRunning) {
                    try {
                        Socket connection = serverSocketUpdateNews.accept();
                        new SubmissionInfo(connection);
                    } catch (EOFException ex) {
//                        Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                serverSocketUpdateNews.close();
            } catch (IOException ex) {
//                Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * It authorizes all contestants. Contestants have to send request to the
     * port 4949 to get the access to the contest which is run by this class.
     */
    class LoginRequest implements Runnable {

        /**
         * Starts the Authorization thread.
         */
        LoginRequest() {
            new Thread(this).start();
        }

        /**
         * It servers port 4949 to ensure simultaneous authorization process of
         * the contestants.
         */
        class LoginApproval implements Runnable {

            Socket connection;

            /**
             * Starts and initializes the connection to get contestant
             * information.
             *
             * @param conn the socket by which contestant would give data.
             */
            public LoginApproval(Socket conn) {
                connection = conn;
                new Thread(this).start();
            }

            /**
             * Checks and sends the result of contestant authorization.
             */
            @Override
            public void run() {
                ObjectOutputStream output = null;
                ObjectInputStream input = null;
                try {

                    String host = connection.getInetAddress().getHostAddress();;;
                    output = new ObjectOutputStream(connection.getOutputStream());
                    output.flush(); // flush output buffer to send header information

                    input = new ObjectInputStream(connection.getInputStream());

                    String username = (String) input.readObject(); // read new message
                    String password = (String) input.readObject(); // read new message
                    String role = (String) input.readObject(); // read new message
                    System.out.println("user = " + username + " pass = " + password + " role = " + role);

                    if (contest.contestants.containsKey(username) && sha512Hex(contest.contestants.get(username)).equals(password)) {
                        output.writeObject(sha512Hex("Yes"));
                        userIP.put(username, host);
                    } else {
                        output.writeObject(sha512Hex("No"));
                    }
                    output.flush();
                } catch (IOException ex) {
//                    Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
//                    Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        output.close(); // close output stream
                        input.close(); // close input stream
                        connection.close(); // close socket
                    } catch (IOException ex) {
//                        Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }

        /**
         * Opens port 4949 to authorize contestants.
         */
        @Override
        public void run() {
            try {
                serverSocketLoginRequest = new ServerSocket(4949, 100);
                while (judgeRunning) {
                    try {
                        Socket connection = serverSocketLoginRequest.accept();
                        new LoginApproval(connection);
                    } catch (EOFException ex) {
//                        Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
                serverSocketLoginRequest.close();
            } catch (IOException ex) {
//                Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * It opens a server on port 4544 to give total contest information after
     * completing authorization.
     */
    class ContestInfo implements Runnable {

        /**
         * Starts the contest information giving Thread.
         */
        ContestInfo() {
            new Thread(this).start();
        }

        /**
         * It servers contest information parallelly to the contestants.
         */
        class ContestInitials implements Runnable {

            Socket connection;

            /**
             * Initiates individual contestant's socket. By which contestant
             * would get the whole contest information.
             *
             * @param conn a socket by which contestant would get information.
             */
            public ContestInitials(Socket conn) {
                connection = conn;
                new Thread(this).start();
            }

            /**
             * It passes contest information as an instance of Contest.
             */
            @Override
            public void run() {
                ObjectOutputStream output = null;
                ObjectInputStream input = null;
                try {
                    String host = connection.getInetAddress().getHostAddress();;;
                    output = new ObjectOutputStream(connection.getOutputStream());
                    output.flush(); // flush output buffer to send header information
                    input = new ObjectInputStream(connection.getInputStream());
                    
                    String username = (String) input.readObject(); // read new message
                    String pass = (String) input.readObject(); // read new message
                    String role = (String) input.readObject(); // read new message
                    
                    if (contest.contestants.containsKey(username) && sha512Hex(contest.contestants.get(username)).equals(pass)) {
                        if(contestRunning){
                            output.writeObject(sha512Hex("Yes"));
                            output.writeObject(contest);
                            output.flush(); // flush output to client
                        }
                        else{
                            output.writeObject(sha512Hex("Stopped"));
                        }
                        userIP.put(username, host);
                    } else {
                        output.writeObject(sha512Hex("No"));
                    }

                    userIP.put(username, host);
                } catch (IOException | ClassNotFoundException ex) {
//                    Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        output.close(); // close output stream
                        input.close(); // close input stream
                        connection.close(); // close socket
                    } catch (IOException ex) {
//                        Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        /**
         * Opens port 4544 when contest starts to serve contest information.
         */
        @Override
        public void run() {
            try {
//                System.out.println("Sever created!");
                serverSocketContestInfo = new ServerSocket(4544, 100);

                while (judgeRunning) {
                    try {
                        Socket connection = serverSocketContestInfo.accept();
//                        System.out.println("Received it!");
                        new ContestInitials(connection);

                    } catch (EOFException ex) {
//                        Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                serverSocketContestInfo.close();
            } catch (IOException ex) {
//                Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    
    /**
     * This class handles the submission receiving service using port 3626.
     */
    class SubmissionRecieve implements Runnable {

        /**
         * Starts submission Receiving Thread.
         */
        public SubmissionRecieve() {
            new Thread(this).start();
        }

        /**
         * It creates a thread for every submission in separate thread then
         * pushes it in the judge queue. The socket it receives hold port 3626.
         */
        class SubmissionEntry implements Runnable {

            private final Socket connection;

            /**
             * To get the submission information, it receives the socket.
             *
             * @param conn submission socket.
             */
            public SubmissionEntry(Socket conn) {
                this.connection = conn;
                new Thread(this).start();
            }

            /**
             * It gets username, solution file with its problem no and language.
             * Then it creates a judge thread and pushes in submission queue. It
             * also maps the user IP so that the verdict could be notified to
             * the user later. No security checking is implemented till now.
             */
            @Override
            public void run() {
                ObjectOutputStream output = null;
                ObjectInputStream input = null;
                try {

                    String host =connection.getInetAddress().getHostAddress();
                    output = new ObjectOutputStream(connection.getOutputStream());
                    output.flush(); // flush output buffer to send header information
                    input = new ObjectInputStream(connection.getInputStream());

                    String username = (String) input.readObject(); // read new message
                    String pass = (String) input.readObject(); // read new message
                    if(!pass.equals(sha512Hex("localhost"))&&!pass.equals(sha512Hex(contest.contestants.get(username)))){
                        throw new IOException();
                    }
                    String language = (String) input.readObject(); // read new message
                    int problemNo = (int) input.readObject(); // read new message
                    String solution;
                    if(!pass.equals(sha512Hex("localhost")))solution = decrypt((String) input.readObject(),sha512Hex(contest.contestants.get(username))); // read new message
                    else solution = decrypt((String) input.readObject(),sha512Hex("localhost")); // read new message
                    output.writeObject(sha512Hex("Yes")); // read new message
                    SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                    String submissionDirectory = "Submissions";
                    if(!Files.isDirectory(new File(submissionDirectory).toPath()))Files.createDirectory(new File(submissionDirectory).toPath());
                    String fileName = submissionDirectory+"\\"+(contest.submissions.size() + 1) + "_" + username + "_" + problemNo + "_" + format.format(new Date()) + ".cpp";
                    System.out.println("Solution File: " + fileName);
                    writeCode(solution, fileName);

                    if(language.equals(sha512Hex("C++"))){
                        RunProgram rp = new RunProgram(host, "C++", new File(fileName), contest.problems[problemNo], contest, username, runningThread);
                        inQueue.add(rp.getThread());
                    }
                    
                    userIP.put(username, host);
                } catch (IOException | ClassNotFoundException ex) {
//                    Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
//                    Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        output.close(); // close output stream
                        input.close(); // close input stream
                        connection.close(); // close socket
                    } catch (IOException ex) {
//                        Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }

        /**
         * Opens the socket server 3626 to receive submissions from contestants.
         */
        @Override
        public void run() {
            try {
                serverSocketSubmissionReceive = new ServerSocket(3626, 100);

                while (judgeRunning) {
                    try {
                        Socket connection = serverSocketSubmissionReceive.accept();
                        new SubmissionEntry(connection);

                    } catch (EOFException ex) {
                        Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                serverSocketSubmissionReceive.close();
            } catch (IOException ex) {
                Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    
    /**
     * This function saves the contest in the Contest file.
     */
    public void saveContest() {
        try {
            // TODO add your handling code here:
            FileOutputStream fout = new FileOutputStream(saveFile);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(contest);
            oos.flush();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ContestSetting.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ContestSetting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * It closes the server socket for taking submission. port no - 3626.
     */
    private void stopTakingSubmission(){
        try {
            serverSocketSubmissionReceive.close();
        } catch (Exception ex) {
            System.out.println("Submission Taking stopped!");
//            Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * It closes the server socket for authorizing. That means no new log in
     * would be allowed. Port no - 4949.
     */
    private void stopNewLogin(){
        try {
            serverSocketLoginRequest.close();
        } catch (IOException ex) {
            Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Saves the contest settings in the contest file and destroys all threads.
     */
    public void destroyMe() {
        saveContest();
        stopTakingSubmission();
        stopNewLogin();
        try {
            serverSocketContestInfo.close();
        } catch (IOException ex) {
            Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        try {
            serverSocketUpdateNews.close();
        } catch (IOException ex) {
            Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    /**
     * It calls necessary functions and sets necessary variables to start the 
     * contest.
     */
    public void startContest() {
        contestRunning = true;
        submissionRecieve = new SubmissionRecieve();
        timer.start();
        runSubmission.start();
    }
    
    /**
     * It calls necessary functions and sets necessary variables to stop the 
     * contest.
     */
    public void stopContest() {
        saveContest();
        stopTakingSubmission();
        contestRunning = false;
        timer.stop();
        if(contest.passedTime<contest.contestDuration){
            runSubmission.stop();
        }
    }

    /**
     * Loads the contest settings with submission queue and contestants from
     * file.
     *
     * @param inputFile the file path of contest settings.
     */
    void loadContest(File inputFile) {

        FileInputStream fin = null;
        try {
            fin = new FileInputStream(inputFile);
            ObjectInputStream ois = new ObjectInputStream(fin);
            contest = (Contest) ois.readObject();
            ois.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NewContest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(NewContest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fin.close();
            } catch (IOException ex) {
                Logger.getLogger(NewContest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        loadSubmissionQueue();
    }

    /**
     * It pushes the submissions which did not get any verdict yet to the
     * submission queue to judge.
     */
    void loadSubmissionQueue() {
        for (int i = 0; i < contest.submissions.size(); i++) {
            Submission sub = contest.submissions.elementAt(i);
            if (sub.verdict.equalsIgnoreCase("IN QUEUE")) {
                RunProgram rp = new RunProgram(sub, contest, runningThread);
                inQueue.add(rp.getThread());
            } else if (sub.verdict.startsWith("RUNNING...")) {
                sub.verdict = "IN QUEUE";
                RunProgram rp = new RunProgram(sub, contest, runningThread);
                inQueue.add(rp.getThread());
            }
        }
    }

    /**
     * It opens four ports to server four works. <br>
     * Port - Short Description <br>
     * 3626 - Receive Submission and pushes in Judge Queue.<br>
     * 4444 - Receive request to get Submission queue and gives the info.<br>
     * 4949 - Receive authorization request to enter in the contest.<br>
     * 4544 - After authorization request being successful, It gives contest
     * information.
     */
    @Override
    public void run() {
        updateNews = new UpdateNews();
        contestInfo = new ContestInfo();
        loginRequest = new LoginRequest();
    }

    /**
     * While receiving a solution from a contestant, this function writes the
     * code in judge PC.
     *
     * @param solution submitted code of a contestant as string.
     * @param fileName where the solution would be written in judge PC.
     */
    private void writeCode(String solution, String fileName) {
        FileWriter writer = null;
        try {
            File file = new File(fileName);
            Files.deleteIfExists(file.toPath());
            Files.createFile(file.toPath());
            writer = new FileWriter(file);
            writer.write(solution);
            System.out.println("Solution written");
        } catch (IOException ex) {
            Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
