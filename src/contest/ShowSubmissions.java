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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.Timer;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.table.DefaultTableModel;
import security.Hashing;

/**
 * It shows all submissions in the contest. All submissions are included corresponding
 * author, problem name, language, submission time, verdict and consumed time.
 * @since 0.1
 * @version 1.0
 * @author Enamul
 */
public class ShowSubmissions extends javax.swing.JDialog {

    RunContest upperClass;
    Contest contest;
    ContestUpdate upd;
    private final String username;
    private final String judgeIP;
    private final String pass;
    private final Timer timer;
    DefaultTableModel model;
    String role;
    /**
     * Loads the submission queue information as well as time elapsed time of 
     * the contest from Judge.
     */
    void loadUpdate() {
        

        ObjectOutputStream output = null; // output stream to server
        ObjectInputStream input; // input stream from server

        try {
            Socket client = new Socket();
            client.connect(new InetSocketAddress(InetAddress.getByName(judgeIP), 4444),500);

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
                model = (DefaultTableModel) jTableStatus.getModel();
                model.setRowCount(upd.submissions.size());
                int row = upd.submissions.size();
                for (int i = 0; i < row; i++) {
                    String timeFormat = String.format("%02d:%02d:%02d", upd.submissions.get(i).timePassed / 3600, (upd.submissions.get(i).timePassed / 60) % 60, upd.submissions.get(i).timePassed % 60);
                    model.setValueAt(i + 1, row-i-1, 0);
                    model.setValueAt(upd.submissions.get(i).date, row-i-1, 1);
                    model.setValueAt(timeFormat, row-i-1, 2);
                    model.setValueAt(upd.submissions.get(i).user, row-i-1, 3);
                    model.setValueAt(upd.submissions.get(i).problemName, row-i-1, 4);
                    model.setValueAt(upd.submissions.get(i).language, row-i-1, 5);
                    model.setValueAt(upd.submissions.get(i).verdict, row-i-1, 6);
                    model.setValueAt(upd.submissions.get(i).timeElapsed, row-i-1, 7);
                }
            }
            else{
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
        }

    }

    /**
     * Creates new form ShowProblemStatus to show the status of all submissions.
     * @param contest instance of the information of running contest.
     * @param upper instance of parent frame.
     * @param judgeIP the IPv4 address of Judge.
     * @param user username of the contestant.
     * @param pass hashed value of the password assigned for the contestant.
     */
    public ShowSubmissions(Contest contest, RunContest upper, String user, String pass, String judgeIP, String role) {
        super(upper,"Problem Submissions");
        initComponents();
        
        this.role = role;
        this.contest = contest;
        this.upperClass = upper;
        this.username = user;
        this.judgeIP = judgeIP;
        this.pass = pass;

        loadUpdate();

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                loadUpdate();
            }
        });
        timer.setRepeats(true);
        timer.start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                upperClass.setVisible(true);
                setVisible(false);
            }
        });

        //adjust screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        setLocation((int) Math.max((width - getWidth()) / 2, 0), (int) Math.max((height - getHeight()) / 2, 0));

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
        jLabel1 = new javax.swing.JLabel();
        jButtonBack = new javax.swing.JButton();
        jButtonRefresh = new javax.swing.JButton();
        jLabelLastUpdatedTime = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);

        jTableStatus.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Date", "At time", "Username", "Problem Name", "Language", "Verdict", "Time Elapsed(ms)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableStatus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableStatusMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableStatus);
        if (jTableStatus.getColumnModel().getColumnCount() > 0) {
            jTableStatus.getColumnModel().getColumn(0).setPreferredWidth(10);
            jTableStatus.getColumnModel().getColumn(1).setPreferredWidth(120);
            jTableStatus.getColumnModel().getColumn(2).setPreferredWidth(20);
            jTableStatus.getColumnModel().getColumn(3).setPreferredWidth(20);
            jTableStatus.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableStatus.getColumnModel().getColumn(5).setPreferredWidth(10);
            jTableStatus.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTableStatus.getColumnModel().getColumn(7).setPreferredWidth(40);
        }

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Submissions");

        jButtonBack.setText("Back");
        jButtonBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBackActionPerformed(evt);
            }
        });

        jButtonRefresh.setText("Refresh");
        jButtonRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRefreshActionPerformed(evt);
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
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 850, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jButtonRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelLastUpdatedTime, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonBack, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonBack)
                    .addComponent(jButtonRefresh)
                    .addComponent(jLabelLastUpdatedTime))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * Brings to the parent frame of the contest environment.
     * @param evt action event of clicking Back button.
     */
    private void jButtonBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackActionPerformed
        // TODO add your handling code here:
        timer.stop();
        upperClass.setVisible(true);
        dispose();
    }//GEN-LAST:event_jButtonBackActionPerformed
    
    /**
     * Manual refreshing of submission queue.
     * @param evt action event of clicking Refresh button.
     */
    private void jButtonRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRefreshActionPerformed
        // TODO add your handling code here:
        loadUpdate();
    }//GEN-LAST:event_jButtonRefreshActionPerformed

    private void jTableStatusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableStatusMouseClicked
        // TODO add your handling code here:
        if(!role.equalsIgnoreCase("Judge")) return;
        if(evt.getClickCount()==2){
            
            JTable target = (JTable)evt.getSource();
            int row = target.getSelectedRow();
            int subNo = (int)model.getValueAt(row, 0)-1;
            new ShowCode(this,upd.submissions.get(subNo),contest,subNo).setVisible(true);
        }
    }//GEN-LAST:event_jTableStatusMouseClicked

//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(ShowSubmissions.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(ShowSubmissions.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(ShowSubmissions.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(ShowSubmissions.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
////                new ShowProblemStatus().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBack;
    private javax.swing.JButton jButtonRefresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelLastUpdatedTime;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableStatus;
    // End of variables declaration//GEN-END:variables
}
