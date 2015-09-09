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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import javax.swing.Timer;
import static security.Encryption.decrypt;
import static security.Encryption.encrypt;
import security.Hashing;
import static security.Hashing.sha512Hex;

/**
 * It opens the the contest environment. Judge would also get the same
 * environment but he would get Extra Menu Judge option to maintain Judge
 * Options. Judge can start and stop contest and change the contest duration.
 * Other features like watching rank list, submit solution, watching submissions
 * verdicts etc are open to all. Submission port - 3626 Verdict port - 4772
 * Update - 4444 Contest port - 4544 login port - 4949
 *
 * @since 0.1
 * @version 1.0
 * @author Enamul
 */
public class RunContest extends javax.swing.JFrame {
    
    String username;
    String role;
    UserInterface upperClass;
    JudgeApplication judgeClass;
    Contest contest;
    Timer timer;
    String judgeIP;
    String days[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
//    boolean loaded;///contest loaded or not
    ServerSocket serverSocketNotification;
    private String pass;
    private boolean contestStop;

    /**
     * When Judge has to notify something to any of the contestant like verdict
     * of a submission, Judge notifies it through port 4772. Every contestant
     * runs this server and get Judge notification through it.
     */
    class Notification implements Runnable {

        /**
         * It starts Notification thread.
         */
        Notification() {
            new Thread(this).start();
        }

        /**
         * It handles several notification at the same time.
         */
        class NotifySocket implements Runnable {
            
            Socket connection;

            /**
             * It initializes socket connected with Judge.
             *
             * @param conn socket by which Judge would send notification.
             */
            public NotifySocket(Socket conn) {
                this.connection = conn;
                new Thread(this).start();
            }

            /**
             * It takes notification from judge and Pop up a new Window with it.
             */
            @Override
            public void run() {
                ObjectOutputStream output = null;
                ObjectInputStream input = null;
                try {
//                    System.out.println("Run Contest -1");
                    output = new ObjectOutputStream(connection.getOutputStream());
                    output.flush(); // flush output buffer to send header information
                    input = new ObjectInputStream(connection.getInputStream());
//                    System.out.println("Run Contest 1");
                    output.writeObject(sha512Hex(username));
                    output.flush(); // flush output to client
                    String title = (String) input.readObject(); // read new message
                    if (title.equals(sha512Hex("No"))) {
                        throw new IOException();
                    }
//                    System.out.println("Run Contest 1");
                    String message = (String) input.readObject(); // read new message
//                    System.out.println("Run Contest 2");
                    JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    Logger.getLogger(RunContest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(RunContest.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        output.close(); // close output stream
                        input.close(); // close input stream
                        connection.close(); // close socket
                    } catch (IOException ex) {
                        Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
        }

        /**
         * It opens a server socket in every contestant program for getting
         * notification from server. It uses port 4772.
         */
        @Override
        public void run() {
            try {
                serverSocketNotification = new ServerSocket(4772, 100);
                
                while (true) {
                    try {
                        Socket connection = serverSocketNotification.accept();
                        new NotifySocket(connection);
                    } catch (EOFException ex) {
                        Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
            } catch (IOException ex) {
                Logger.getLogger(JudgeApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    void updateContestComponents(){
        //contest.passedTime++;/// here taken update from port should be implemented
                if (!role.equals("Judge") || !jMenuItemContestStartStop.getText().equals("Start Contest")) {
                    if (!loadUpdate()) {
                        contestStop = true;
                    }
                    if (contestStop) {
                        loadContest();
                    }
                }

//                if (!loaded) {
//                    loadContest();
//                }
                Date now = new Date();
                if (contestStop) {
                    jLabelTimePassed.setText("Stopped");
                } else {
                    jLabelTimePassed.setText(String.format("%02d:%02d:%02d", contest.passedTime / 3600, (contest.passedTime / 60) % 60, contest.passedTime % 60));
                }
                jLabelDate.setText(String.format("%02d/%02d/%04d", now.getDate(), now.getMonth(), 1900 + now.getYear()));
                jLabelWeekDay.setText(String.format("%10s,", days[now.getDay()]));
                int hh = now.getHours();
                String AMPM;
                if (hh < 12) {
                    AMPM = "AM";
                } else {
                    AMPM = "PM";
                }
                if (hh == 0) {
                    hh = 12;
                } else if (hh > 12) {
                    hh -= 12;
                }
                jLabelTime.setText(String.format("%02d:%02d:%02d %2s", hh, now.getMinutes(), now.getSeconds(), AMPM));

    }
    
    /**
     * Creates new form RunContest to submit solution, see rank list, submission
     * queue. It also shows judge options for judge.
     *
     * @param upper instance of parent class.
     * @param username contestant handle.
     * @param role Contestant or Judge.
     * @param pass hashed password of user.
     * @param judgeClass instance of Judge class. null for contestant.
     * @param judgeIP IP address of Judge.
     */
    public RunContest(UserInterface upper, String username, String pass, String role, JudgeApplication judgeClass, String judgeIP) {
        super("Contest Running");
        initComponents();
        this.upperClass = upper;
        this.judgeIP = judgeIP;
        this.pass = pass;
        this.username = username;
        this.role = role;
        this.judgeClass = judgeClass;
        jButtonSubmit.setEnabled(false);
        if (!role.equalsIgnoreCase("Judge")) {
            jMenuBarMain.remove(jMenuJudgeOptions);
            loadContest();
            jButtonSaveAndExit.setText("Back");
        } else {
            this.contest = judgeClass.contest;
            loadContestElements();
        }

        /// max number of parallel submission 
        jLabelUser.setText(String.format("Hi, %s", username));
        try {
            jLabelIP.setText(String.format("IPv4 address: %s", InetAddress.getLocalHost().getHostAddress()));
        } catch (UnknownHostException ex) {
            Logger.getLogger(RunContest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                upperClass.setVisible(true);
                setVisible(false);
                if (role.equalsIgnoreCase("Judge")) {
                    System.out.println("Entry 1.2");
                    judgeClass.destroyMe();
                }
            }
        });
        
        timer = new Timer(1000, new ActionListener() {
            /**
             * It updates date and times and contest time. It also loads contest
             * settings if not loaded.
             *
             * @param ae action event of one second timer.
             */
            @Override
            public void actionPerformed(ActionEvent ae) {
                updateContestComponents();
            }
        });
        timer.setRepeats(true);
        timer.start();
        updateContestComponents();
        //adjust screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        setLocation((int) Math.max((width - getWidth()) / 2, 0), (int) Math.max((height - getHeight()) / 2, 0));
        new Notification();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }
    
    /**
     * It loads problem names and other contest information if available.
     */
    void loadContest() {
        
        ObjectOutputStream output = null; // output stream to server
        ObjectInputStream input; // input stream from server

        try {
            Socket client = new Socket();
            client.connect(new InetSocketAddress(InetAddress.getByName(judgeIP), 4544), 500);
//            System.out.println("Client Requested.");
            output = new ObjectOutputStream(client.getOutputStream());
            output.flush(); // flush output buffer to send header information

            input = new ObjectInputStream(client.getInputStream());
            
            output.writeObject(username);
            output.writeObject(pass);
            output.writeObject(sha512Hex(role));
            String response = (String) input.readObject();
            if (response.equals(sha512Hex("Yes"))) {
                contest = (Contest) input.readObject();
                
            } else if (response.equals(sha512Hex("Stopped"))) {
                jComboBoxProblemName.removeAllItems();
                jComboBoxLanguage.removeAllItems();
                contest = null;
                contestStop = true;
                jButtonSubmit.setEnabled(false);
                return;
            }
//            output.flush();System.out.println("Got something");

            output.close(); // close output stream
            input.close(); // close input stream
            client.close(); // close socket

        } catch (IOException | ClassNotFoundException ex) {
            //JOptionPane.showMessageDialog(this, "Opps!\nIt seems something went wrong.\nContest could not be loaded!\nTry to relogin or contact to the Administrator.", "Contest Loading Failed!", JOptionPane.ERROR_MESSAGE);
            loadContestElements();
            contest = null;
            contestStop = true;
            jButtonSubmit.setEnabled(false);
            //Logger.getLogger(RunProgram.class.getName()).log(Level.SEVERE, null, ex);
        }
        loadContestElements();

        //loadUpdate();
    }
    
    void loadContestElements() {
        jComboBoxLanguage.removeAllItems();
        jComboBoxProblemName.removeAllItems();
        if (contest == null) {
            jComboBoxProblemName.setEnabled(false);
            jComboBoxProblemName.setEnabled(false);
            return;
        }
        jComboBoxProblemName.setEnabled(true);
            jComboBoxProblemName.setEnabled(true);
        for (int i = 0; i < contest.numberOfProblems; i++) {
            jComboBoxProblemName.addItem(contest.problems[i].problemName);
        }

        //        jComboBoxLanguage.addItem("C");
        jComboBoxLanguage.addItem("C++");
//        jComboBoxLanguage.addItem("JAVA");
        contestStop = false;
        jButtonSubmit.setEnabled(true);
    }

    /**
     * It updates contest passed time.
     */
    boolean loadUpdate() {
        
        ObjectOutputStream output; // output stream to server
        ObjectInputStream input; // input stream from server

        try {
            Socket client = new Socket();
            client.connect(new InetSocketAddress(InetAddress.getByName(judgeIP), 4444), 500);
            
            output = new ObjectOutputStream(client.getOutputStream());
            output.flush(); // flush output buffer to send header information
            input = new ObjectInputStream(client.getInputStream());
            
            output.writeObject(username);
            output.writeObject(pass);
            
            String response = (String) input.readObject();
            if (response.equals(Hashing.sha512Hex(username))) {
                output.writeObject(Hashing.sha512Hex("Anything"));
                String timePassed = decrypt((String) input.readObject(), pass);
                contest.passedTime = Long.parseLong(timePassed);
                if (contestStop) {
                    loadContest();
                }
                contestStop = false;
            } else {
                if (!contestStop) {
                    loadContest();
                }
                contestStop = true;
                
            }
            output.flush();
            
            output.close(); // close output stream
            input.close(); // close input stream
            client.close(); // close socket

        } catch (IOException | ClassNotFoundException ex) {
            return false;
//            Logger.getLogger(RunProgram.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            return false;
//            Logger.getLogger(RunContest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        jComboBoxProblemName = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBoxLanguage = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldFilePath = new javax.swing.JTextField();
        jButtonBrowse = new javax.swing.JButton();
        jButtonSubmit = new javax.swing.JButton();
        jLabelIP = new javax.swing.JLabel();
        jButtonSubmissions = new javax.swing.JButton();
        jButtonRanklist = new javax.swing.JButton();
        jButtonSaveAndExit = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabelDate = new javax.swing.JLabel();
        jLabelTime = new javax.swing.JLabel();
        jLabelWeekDay = new javax.swing.JLabel();
        jLabelTimePassed = new javax.swing.JLabel();
        jLabelUser = new javax.swing.JLabel();
        jButtonExit = new javax.swing.JButton();
        jMenuBarMain = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuJudgeOptions = new javax.swing.JMenu();
        jMenuItemContestStartStop = new javax.swing.JMenuItem();
        jMenuItemSaveContest = new javax.swing.JMenuItem();
        jMenuItemChangeTime = new javax.swing.JMenuItem();
        jMenuItemContestantSetting = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jLabel1.setText("Problem Name:");

        jLabel2.setText("Language:");

        jLabel3.setText("Source File:");

        jTextFieldFilePath.setMaximumSize(new java.awt.Dimension(6, 20));
        jTextFieldFilePath.setName(""); // NOI18N

        jButtonBrowse.setText("Browse");
        jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed(evt);
            }
        });

        jButtonSubmit.setText("Submit");
        jButtonSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSubmitActionPerformed(evt);
            }
        });

        jLabelIP.setText("IPv4 address: localhost");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jTextFieldFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonBrowse))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jComboBoxLanguage, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabelIP))
                            .addComponent(jComboBoxProblemName, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(203, 203, 203)
                        .addComponent(jButtonSubmit)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxProblemName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBoxLanguage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelIP))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonSubmit)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButtonSubmissions.setText("Submissions");
        jButtonSubmissions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSubmissionsActionPerformed(evt);
            }
        });

        jButtonRanklist.setText("Ranklist");
        jButtonRanklist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRanklistActionPerformed(evt);
            }
        });

        jButtonSaveAndExit.setText("Save and Back");
        jButtonSaveAndExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveAndExitActionPerformed(evt);
            }
        });

        jLabelDate.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabelDate.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelDate.setText("--/--/----");

        jLabelTime.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabelTime.setText("--:--:-- --");

        jLabelWeekDay.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabelWeekDay.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelWeekDay.setText("---,");

        jLabelTimePassed.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabelTimePassed.setForeground(new java.awt.Color(0, 0, 204));
        jLabelTimePassed.setText("Stopped");

        jLabelUser.setText("Username");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTimePassed, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelDate, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelWeekDay, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelTime, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelUser)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabelUser)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelDate)
                    .addComponent(jLabelTime, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                    .addComponent(jLabelWeekDay, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                    .addComponent(jLabelTimePassed))
                .addContainerGap())
        );

        jButtonExit.setText("Exit");
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });

        jMenuFile.setText("File");

        jMenuItemExit.setText("Exit");
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExit);

        jMenuBarMain.add(jMenuFile);

        jMenuJudgeOptions.setText("Judge Options");

        jMenuItemContestStartStop.setText("Start Contest");
        jMenuItemContestStartStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemContestStartStopActionPerformed(evt);
            }
        });
        jMenuJudgeOptions.add(jMenuItemContestStartStop);

        jMenuItemSaveContest.setText("Save Contest");
        jMenuItemSaveContest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveContestActionPerformed(evt);
            }
        });
        jMenuJudgeOptions.add(jMenuItemSaveContest);

        jMenuItemChangeTime.setText("Change Contest Time");
        jMenuItemChangeTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemChangeTimeActionPerformed(evt);
            }
        });
        jMenuJudgeOptions.add(jMenuItemChangeTime);

        jMenuItemContestantSetting.setText("Contestants");
        jMenuItemContestantSetting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemContestantSettingActionPerformed(evt);
            }
        });
        jMenuJudgeOptions.add(jMenuItemContestantSetting);

        jMenuBarMain.add(jMenuJudgeOptions);

        setJMenuBar(jMenuBarMain);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonRanklist, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSubmissions, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSaveAndExit, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSubmissions)
                    .addComponent(jButtonRanklist)
                    .addComponent(jButtonSaveAndExit)
                    .addComponent(jButtonExit))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * It brings to the submission page.
     *
     * @param evt action event of clicking Submission button.
     */
    private void jButtonSubmissionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSubmissionsActionPerformed
        // TODO add your handling code here:
        new ShowSubmissions(contest, this, username, pass, judgeIP, role).setVisible(true);
    }//GEN-LAST:event_jButtonSubmissionsActionPerformed

    /**
     * It opens window to browse and choose the solution file to be submitted.
     *
     * @param evt action event of clicking Browse button.
     */
    private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseActionPerformed
        // TODO add your handling code here:
        int retrival;
        jFileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        retrival = jFileChooser.showOpenDialog(this);
        
        if (retrival == JFileChooser.APPROVE_OPTION) {
            try {
                File fileName = jFileChooser.getSelectedFile();
                jTextFieldFilePath.setText(fileName.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_jButtonBrowseActionPerformed

    /**
     * It reads the solution file and convert it to string. It is done for
     * sending file to the Judge.
     *
     * @param input file path of the solution.
     * @return string representation of the solution file.
     */
    private String readFile(File input) {
        String ret = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(input));
            String line = reader.readLine();
            while (line != null) {
                ret += line + "\n";
                line = reader.readLine();
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RunContest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RunContest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    /**
     * It sends the solution to the judge and opens the Submission Queue. It
     * uses port 3626 to send the solution.
     *
     * @param evt action event of clicking submit button.
     */
    private void jButtonSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSubmitActionPerformed
        File fileToSubmit = new File(jTextFieldFilePath.getText());
        long fourMB = 4194304;
        if (fileToSubmit.length() > fourMB) {
            JOptionPane.showMessageDialog(this, "Submission Failed!\nFile Size Too Large(>4MB)!", "File Size Too Large!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String language = (String) jComboBoxLanguage.getSelectedItem();
        int problemId = jComboBoxProblemName.getSelectedIndex();
//        Submission runningSubmission = new Submission(new Date(), language, contest.problems[problemId].problemName, "Running", contest.passedTime);
//        contest.submissions.add(runningSubmission);
        
        ObjectOutputStream output; // output stream to server
        ObjectInputStream input; // input stream from server

        try {
            Socket client = new Socket();
            client.connect(new InetSocketAddress(InetAddress.getByName(judgeIP), 3626), 500);
            
            output = new ObjectOutputStream(client.getOutputStream());
            output.flush(); // flush output buffer to send header information

            input = new ObjectInputStream(client.getInputStream());
            
            output.writeObject(username); // read new message
            output.writeObject(pass); // read new message
            output.writeObject(sha512Hex(language)); // read new message
            output.writeObject(problemId); // read new message
            output.writeObject(encrypt(readFile(fileToSubmit), pass)); // read new message
            output.flush();
            String response = (String) input.readObject(); // read new message

            if (!response.equals(sha512Hex("Yes"))) {
                throw new IOException();
            }
            
            output.close(); // close output stream
            input.close(); // close input stream
            client.close(); // close socket
            /// go to submission page
            new ShowSubmissions(contest, this, username, pass, judgeIP, role).setVisible(true);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Submission Failed!\nContest is stopped by the Judge.", "Submission Failed!", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(RunProgram.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(RunContest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonSubmitActionPerformed

    /**
     * It stops the contest and saves everything before it if user is the Judge.
     *
     * @param evt action event of clicking Save and Back or Back button.
     */
    private void jButtonSaveAndExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveAndExitActionPerformed
        // TODO add your handling code here:
        timer.stop();
        
        setVisible(false);
        upperClass.upperClass.setVisible(true);
        if (role.equalsIgnoreCase("Judge")) {
//            System.out.println("Entry 1.1");
            judgeClass.destroyMe();
        }
        upperClass.dispose();
        dispose();
    }//GEN-LAST:event_jButtonSaveAndExitActionPerformed

    /**
     * It opens the current ranklist of the contest in a window.
     *
     * @param evt action event of clicking Ranklist button.
     */
    private void jButtonRanklistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRanklistActionPerformed
        // TODO add your handling code here:
        new RankListTable(contest, this, username, pass, judgeIP).setVisible(true);
//        setVisible(false);
    }//GEN-LAST:event_jButtonRanklistActionPerformed

    /**
     * It exits the program, discard all changes if user is judge.
     *
     * @param evt action event of clicking Exit menu from file.
     */
    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed
        // TODO add your handling code here:
        jButtonExitActionPerformed(evt);
    }//GEN-LAST:event_jMenuItemExitActionPerformed

    /**
     * It exits the program, discard all changes if user is judge.
     *
     * @param evt action event of clicking Exit button.
     */
    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        // TODO add your handling code here:
        int response = JOptionPane.showConfirmDialog(this, "Are You Sure To Exit?\nAll unsaved data would be lost!", "Confirmation!", JOptionPane.INFORMATION_MESSAGE);
        if (response != YES_OPTION) {
            return;
        }
        System.exit(0);
    }//GEN-LAST:event_jButtonExitActionPerformed
    
    /**
     * Sets text "Stop Contest" to the menu item in Judge Option.
     */
    public void setStopContest(){
        jMenuItemContestStartStop.setText("Stop Contest");
    }
    
    /**
     * Sets text "Start Contest" to the menu item in Judge Option.
     */
    public void setStartContest(){
        jMenuItemContestStartStop.setText("Start Contest");
    }
    
    /**
     * It takes the necessary steps to start or stop the contest. It is visible
     * to the judge only.
     *
     * @param evt action event of choosing Start Contest/Stop Contest menu.
     */
    private void jMenuItemContestStartStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemContestStartStopActionPerformed
        // TODO add your handling code here:
        if (jMenuItemContestStartStop.getText().equalsIgnoreCase("Start Contest")) {
            if(contest.passedTime>contest.contestDuration){
                JOptionPane.showMessageDialog(this, "Opps!\nContest Time Is Already Exceeded The Contest Duration!\nIf You Want To Continue, Please Extend Contest Time First!", "Contest Time Exceeded!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            judgeClass.startContest();
            jButtonSubmit.setEnabled(true);

        } else {
            judgeClass.stopContest();
            jButtonSubmit.setEnabled(false);
        }
    }//GEN-LAST:event_jMenuItemContestStartStopActionPerformed

    /**
     * It opens a window to change the duration of the contest. It is visible to
     * the judge only.
     *
     * @param evt action event of clicking Change Contest Duration.
     */
    private void jMenuItemChangeTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemChangeTimeActionPerformed
        // TODO add your handling code here:
        new ContestDurationEdit(this, judgeClass.contest).setVisible(true);
    }//GEN-LAST:event_jMenuItemChangeTimeActionPerformed

    private void jMenuItemSaveContestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveContestActionPerformed
        // TODO add your handling code here:
        judgeClass.saveContest();
    }//GEN-LAST:event_jMenuItemSaveContestActionPerformed

    private void jMenuItemContestantSettingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemContestantSettingActionPerformed
        // TODO add your handling code here:
        new ConfigureContestants(this, contest.contestants).setVisible(true);
    }//GEN-LAST:event_jMenuItemContestantSettingActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBrowse;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonRanklist;
    private javax.swing.JButton jButtonSaveAndExit;
    private javax.swing.JButton jButtonSubmissions;
    private javax.swing.JButton jButtonSubmit;
    private javax.swing.JComboBox jComboBoxLanguage;
    private javax.swing.JComboBox jComboBoxProblemName;
    private javax.swing.JFileChooser jFileChooser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelDate;
    javax.swing.JLabel jLabelIP;
    private javax.swing.JLabel jLabelTime;
    private javax.swing.JLabel jLabelTimePassed;
    private javax.swing.JLabel jLabelUser;
    private javax.swing.JLabel jLabelWeekDay;
    private javax.swing.JMenuBar jMenuBarMain;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenuItem jMenuItemChangeTime;
    private javax.swing.JMenuItem jMenuItemContestStartStop;
    private javax.swing.JMenuItem jMenuItemContestantSetting;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemSaveContest;
    private javax.swing.JMenu jMenuJudgeOptions;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextFieldFilePath;
    // End of variables declaration//GEN-END:variables
}
