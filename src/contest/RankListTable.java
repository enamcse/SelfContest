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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import security.Hashing;

/**
 *
 * @author Enamul
 */
public class RankListTable extends javax.swing.JDialog {

    RunContest upperClass;
    Contest contest;
    String username;
    String pass;
    String judgeIP;
    private int penaltyPerSubmission=20;
    private final Timer timer;

    /**
     * Creates new form ShowProblemStatus
     */
    public RankListTable(Contest contest, RunContest upper, String user, String pass, String judgeIP) {
        super(upper, "Rank List");
        initComponents();

        this.contest = contest;
        this.upperClass = upper;
        this.username = user;
        this.judgeIP = judgeIP;
        this.pass = pass;

        setRankList();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                upperClass.setVisible(true);
                setVisible(false);
            }
        });
        
        timer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                loadUpdate();
            }
        });
        timer.setRepeats(true);
        timer.start();
        
        //adjust screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        setSize((int)(9*width/10), (int)(9*height/10));
        setLocation((int) Math.max((width - getWidth()) / 2, 0), (int) Math.max((height - getHeight()) / 2, 0));

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    class Info {

        String contestant;
        int solved;
        int penalty;
        

        private Info(String name, int size, int pen) {
            contestant = name;
            solved = size;
            penalty = pen;
        }

        private Info() {
        }
        /**
         * The following class is for sorting of the rank list. hence it implements
         * Comparator.
        */
        class comp implements Comparator<Info> {

            @Override
            public int compare(Info p1, Info p2) {
                int age1 = p1.solved;
                int age2 = p2.solved;

                if (age1 == age2) {
                    int aged1 = p1.penalty;
                    int aged2 = p2.penalty;
//                    if (aged1 == aged2) {
//                        if(p1.contestant > p2.contestant)
//                            return 1;
//                        return -1;
//                    }
                    if (aged1 > aged2) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else if (age1 < age2) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }
    }
    
    public void setRankList() {
        loadUpdate();
    }
    
    /**
     * Loads the submission queue information as well as time elapsed time of
     * the contest from Judge.
     */
    void loadUpdate() {
        ContestUpdate upd;

        ObjectOutputStream output = null; // output stream to server
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
                output.writeObject(Hashing.sha512Hex("1"));
                upd = (ContestUpdate) input.readObject();
                contest.passedTime = upd.passedTime;
                Object[] columnNames = new Object[contest.numberOfProblems+3];
                columnNames[0] = "No";
                columnNames[1] = "Contestant";
                columnNames[2] = "Solved(Penalty)";
                for(int i = 3; i<columnNames.length; i++){
                    columnNames[i] = contest.problems[i-3].problemName;
                }
                
                int contestantCount = 0;
                
                HashMap<String,Integer> cont = new HashMap<>();
                for (Submission current : upd.submissions) {
                        if(!cont.containsKey(current.user))cont.put(current.user, contestantCount++);
                    }
                
                int solved[ ] = new int[contestantCount];
                int penalty[ ] = new int[contestantCount];
                
                int accepted[ ][ ] =  new int[contestantCount][contest.numberOfProblems];
                int attempt[ ][ ] =  new int[contestantCount][contest.numberOfProblems];
                
                
                for (Submission submission: upd.submissions) {
                    
                    if(accepted[cont.get(submission.user)][ submission.problemId-1 ]==0){
                        String verdict = submission.verdict;
                        if(verdict.startsWith("IN QUEUE") ||verdict.startsWith("RUNNING")||verdict.startsWith("COMPILING")) continue;
                        if(verdict.equalsIgnoreCase("ACCEPTED")){
                            accepted[cont.get(submission.user)][submission.problemId-1 ] = (int)submission.timePassed/60;
                            solved[ cont.get(submission.user) ]++;
                            penalty[ cont.get(submission.user) ]+=(accepted[cont.get(submission.user)][submission.problemId-1 ]+attempt[cont.get(submission.user)][submission.problemId-1]*penaltyPerSubmission);
                            attempt[cont.get(submission.user)][submission.problemId-1]++;
                        }
                        else{
                            attempt[cont.get(submission.user)][submission.problemId-1]++;
                        }
                    }                   
                }
                List<Info> data = new ArrayList<Info>();
                for (Map.Entry<String, String> entrySet : contest.contestants.entrySet()) {
                    String key = entrySet.getKey();
                    if(cont.containsKey(key)){
                        data.add(new Info(key, solved[cont.get(key)], penalty[cont.get(key)]));
                    }
                    else data.add(new Info(key,0, 0));
                }
                Collections.sort(data, new Info().new comp()); // data for the rank list is now sorted.
                
                Object[][] tableData = new Object[data.size()][contest.numberOfProblems+3];
                int cnt=0, rank=0,prevPen=-1, prevSol=-1;
                for (Info info : data) {
                    String key = info.contestant;
                    if(prevPen!=info.penalty || prevSol!=info.solved)rank++;
                    tableData[cnt][1] = key;
                    tableData[cnt][0] = rank;
                    prevPen = info.penalty;
                    prevSol = info.solved;
                    if(cont.containsKey(key)){
                        tableData[cnt][2] = String.format("%d(%d)", info.solved,info.penalty);
                        int serial = cont.get(key);
                        for(int i = 0; i<contest.numberOfProblems;i++){
                            if(accepted[serial][i]!=0){
                                tableData[cnt][i+3] = String.format("%d/%d", attempt[serial][i],accepted[serial][i]);
                            }else{
                                tableData[cnt][i+3] = String.format("%d/-", attempt[serial][i]);
                            }
                        }
                    }
                    else {
                        tableData[cnt][2] = "0(0)";
                        for(int i = 0; i<contest.numberOfProblems;i++){
                            tableData[cnt][i+3] = "0/-";
                        }
                    }
                    
                    cnt++;
                }
                prepareTable(columnNames, tableData);
            } else {
                JOptionPane.showMessageDialog(this, "Either contest is not running or You are not authorized to view this now!", "Unauthorized!", JOptionPane.INFORMATION_MESSAGE);
                jButtonBackActionPerformed(null);
            }

            output.flush();

            output.close(); // close output stream
            input.close(); // close input stream
            client.close(); // close socket
            jLabelLastUpdatedTime.setText(String.format("Last Updated on: %s",new Date()));
        } catch (IOException | ClassNotFoundException ex) {
//            Logger.getLogger(RunProgram.class.getName()).log(Level.SEVERE, null, ex);
            
//            jButtonBackActionPerformed(null);
        }

    }
    
    /**
     * It would update the table as the column name and data table provided.
     * @param columnNames heading of the columns
     * @param data 2D data grid.
     */
    private void prepareTable(Object[] columnNames, Object[][] data) {
        jTableStatus.setModel(new javax.swing.table.DefaultTableModel(
                data,
                columnNames
        ) {
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTableStatus = new javax.swing.JTable();
        jLabelRankListOfTheContest = new javax.swing.JLabel();
        jButtonBack = new javax.swing.JButton();
        jLabelLastUpdatedTime = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jTableStatus.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Problem Name", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTableStatus);
        if (jTableStatus.getColumnModel().getColumnCount() > 0) {
            jTableStatus.getColumnModel().getColumn(0).setPreferredWidth(20);
        }

        jLabelRankListOfTheContest.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabelRankListOfTheContest.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelRankListOfTheContest.setText("Rank List of the Contest");

        jButtonBack.setText("Back");
        jButtonBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBackActionPerformed(evt);
            }
        });

        jLabelLastUpdatedTime.setText("None");

        jMenu1.setText("File");

        jMenuItem1.setText("Back");
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 856, Short.MAX_VALUE)
            .addComponent(jLabelRankListOfTheContest, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelLastUpdatedTime, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonBack, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(374, 374, 374))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabelRankListOfTheContest, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonBack)
                    .addComponent(jLabelLastUpdatedTime))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackActionPerformed
        // TODO add your handling code here:
        timer.stop();
        upperClass.setVisible(true);
        dispose();
    }//GEN-LAST:event_jButtonBackActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBack;
    private javax.swing.JLabel jLabelLastUpdatedTime;
    private javax.swing.JLabel jLabelRankListOfTheContest;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableStatus;
    // End of variables declaration//GEN-END:variables
}
