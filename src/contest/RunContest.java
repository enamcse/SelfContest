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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 *
 * @author Enamul
 */
public class RunContest extends javax.swing.JFrame {

    String username;
    StartContest upperClass;
    Contest contest;
    Timer timer, runSubmission;
    int runningThread[] = new int[1];
    int limitThread;
    String days[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private String saveFile;
    Vector<Thread> inQueue;

    /**
     * Creates new form RunContest
     */
    public RunContest(StartContest upper, String filePath, String username) {
        super("Contest Running");
        initComponents();
        this.inQueue = new Vector<>();
        this.upperClass = upper;
        loadContest(new File(filePath));
        this.saveFile = filePath;
        this.username = username;
        runningThread[0] = 0;

        /// max number of parallel submission 
        limitThread = 1;
        jLabelUser.setText(String.format("Hi, %s", username));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                upperClass.setVisible(true);
                setVisible(false);
            }
        });

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                contest.passedTime++;
                Date now = new Date();
                jLabelTimePassed.setText(String.format("%02d:%02d:%02d", contest.passedTime / 3600, (contest.passedTime / 60) % 60, contest.passedTime % 60));
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
        });
        timer.setRepeats(true);
        timer.start();

        runSubmission = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                System.out.println("running: " + runningThread[0] + ", limit: " + limitThread + ", inQueue: " + inQueue.size() + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                
                if (limitThread == runningThread[0]) {
                    return;
                }
                
                while (limitThread > runningThread[0]) {
                    if (inQueue.size() == 0) {
                        return;
                    }
                    runningThread[0]++;
                    System.out.println("thread name: "+inQueue.elementAt(0).getName());
                    inQueue.elementAt(0).start();
                    inQueue.removeElementAt(0);
                }
            }
        });
        runSubmission.setRepeats(true);
        runSubmission.start();

        //adjust screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        setLocation((int) Math.max((width - getWidth()) / 2, 0), (int) Math.max((height - getHeight()) / 2, 0));

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    void loadContest(File inputFile) {

        FileInputStream fin = null;
        try {
            fin = new FileInputStream(inputFile);
            ObjectInputStream ois = new ObjectInputStream(fin);
            contest = (Contest) ois.readObject();
            ois.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NewContest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NewContest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(NewContest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fin.close();
            } catch (IOException ex) {
                Logger.getLogger(NewContest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        jComboBoxProblemName.removeAllItems();
        for (int i = 0; i < contest.numberOfProblems; i++) {
            jComboBoxProblemName.addItem(contest.problems[i].problemName);
        }

        refreshSolved();
        loadSubmissionQueue();
//        jComboBoxLanguage.addItem("C");
        jComboBoxLanguage.addItem("C++");
//        jComboBoxLanguage.addItem("JAVA");
    }
    
    void loadSubmissionQueue(){
        for (int i = 0; i < contest.submissions.size(); i++) {
            Submission sub = contest.submissions.elementAt(i);
            if(sub.verdict.equalsIgnoreCase("IN QUEUE")){
                RunProgram rp = new RunProgram(sub, contest,runningThread,this);
                inQueue.add(rp.getThread());
            }
            else if(sub.verdict.startsWith("RUNNING...")){
                sub.verdict = "IN QUEUE";
                RunProgram rp = new RunProgram(sub, contest,runningThread,this);
                inQueue.add(rp.getThread());
            }
        }
    }

    void refreshSolved() {
        int solved = 0;
        for (int i = 0; i < contest.numberOfProblems; i++) {
            solved += contest.accepted[i] ? 1 : 0;
        }

        jLabelSolvedCount.setText(String.format("Solved %d out of %d problems", solved, contest.numberOfProblems));

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
        jLabelSolvedCount = new javax.swing.JLabel();
        jButtonSubmissions = new javax.swing.JButton();
        jButtonStatusOfProblems = new javax.swing.JButton();
        jButtonSaveAndExit = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabelDate = new javax.swing.JLabel();
        jLabelTime = new javax.swing.JLabel();
        jLabelWeekDay = new javax.swing.JLabel();
        jLabelTimePassed = new javax.swing.JLabel();
        jLabelUser = new javax.swing.JLabel();
        jButtonSaveAndExit1 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Problem Name:");

        jLabel2.setText("Language:");

        jLabel3.setText("Source File:");

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

        jLabelSolvedCount.setText("Solved 0 out of 0 problems");

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
                                .addComponent(jTextFieldFilePath)
                                .addGap(4, 4, 4)
                                .addComponent(jButtonBrowse))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jComboBoxLanguage, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 91, Short.MAX_VALUE)
                                .addComponent(jLabelSolvedCount))
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
                    .addComponent(jLabelSolvedCount))
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

        jButtonStatusOfProblems.setText("Status of Problems");
        jButtonStatusOfProblems.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStatusOfProblemsActionPerformed(evt);
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
        jLabelDate.setText("Date");

        jLabelTime.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabelTime.setText("Date");

        jLabelWeekDay.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabelWeekDay.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelWeekDay.setText("Date");

        jLabelTimePassed.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabelTimePassed.setForeground(new java.awt.Color(0, 0, 204));
        jLabelTimePassed.setText("Date");

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

        jButtonSaveAndExit1.setText("Exit");
        jButtonSaveAndExit1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveAndExit1ActionPerformed(evt);
            }
        });

        jMenu1.setText("File");

        jMenuItem1.setText("Exit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

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
                        .addComponent(jButtonStatusOfProblems)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSubmissions)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSaveAndExit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonSaveAndExit1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                    .addComponent(jButtonStatusOfProblems)
                    .addComponent(jButtonSaveAndExit)
                    .addComponent(jButtonSaveAndExit1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSubmissionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSubmissionsActionPerformed
        // TODO add your handling code here:
        new ShowSubmissions(contest, this).setVisible(true);
        setVisible(false);
    }//GEN-LAST:event_jButtonSubmissionsActionPerformed

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

    private void jButtonSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSubmitActionPerformed

        String language = (String) jComboBoxLanguage.getSelectedItem();
        int problemId = jComboBoxProblemName.getSelectedIndex();
//        Submission runningSubmission = new Submission(new Date(), language, contest.problems[problemId].problemName, "Running", contest.passedTime);
//        contest.submissions.add(runningSubmission);

        RunProgram rp = new RunProgram(language, new File(jTextFieldFilePath.getText()), contest.problems[jComboBoxProblemName.getSelectedIndex()], contest, this, username,  runningThread);

        inQueue.add(rp.getThread());
        /// go to submission page
        new ShowSubmissions(contest, this).setVisible(true);
        setVisible(false);
    }//GEN-LAST:event_jButtonSubmitActionPerformed

    private void jButtonSaveAndExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveAndExitActionPerformed
        // TODO add your handling code here:
        timer.stop();
        runSubmission.stop();

        try {
            // TODO add your handling code here:
            FileOutputStream fout = new FileOutputStream(saveFile);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(contest);
            oos.flush();
            setVisible(false);
            upperClass.setVisible(true);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ContestSetting.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ContestSetting.class.getName()).log(Level.SEVERE, null, ex);
        }

        dispose();
    }//GEN-LAST:event_jButtonSaveAndExitActionPerformed

    private void jButtonStatusOfProblemsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStatusOfProblemsActionPerformed
        // TODO add your handling code here:
        new ShowProblemStatus(contest, this).setVisible(true);
        setVisible(false);
    }//GEN-LAST:event_jButtonStatusOfProblemsActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jButtonSaveAndExit1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveAndExit1ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jButtonSaveAndExit1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(RunContest.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RunContest.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RunContest.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RunContest.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                new RunContest().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBrowse;
    private javax.swing.JButton jButtonSaveAndExit;
    private javax.swing.JButton jButtonSaveAndExit1;
    private javax.swing.JButton jButtonStatusOfProblems;
    private javax.swing.JButton jButtonSubmissions;
    private javax.swing.JButton jButtonSubmit;
    private javax.swing.JComboBox jComboBoxLanguage;
    private javax.swing.JComboBox jComboBoxProblemName;
    private javax.swing.JFileChooser jFileChooser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelDate;
    javax.swing.JLabel jLabelSolvedCount;
    private javax.swing.JLabel jLabelTime;
    private javax.swing.JLabel jLabelTimePassed;
    private javax.swing.JLabel jLabelUser;
    private javax.swing.JLabel jLabelWeekDay;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextFieldFilePath;
    // End of variables declaration//GEN-END:variables
}
