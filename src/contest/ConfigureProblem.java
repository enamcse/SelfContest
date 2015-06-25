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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

/**
 *
 * @author Enamul
 */
public class ConfigureProblem extends javax.swing.JFrame {

    ContestSetting upperClass;
    Problem currentProblem;
    File currentDirectory = new File("");

    /**
     * Creates new form ConfigureProblem
     */
    public ConfigureProblem(ContestSetting upper, Problem currentProblem) {
        super("Problem Setting");
        initComponents();
        this.upperClass = upper;
        this.currentProblem = currentProblem;
        buttonGroup1.add(jRadioButtonOneFile);
        buttonGroup1.add(jRadioButtonMultiFile);
        buttonGroup2.add(jRadioButtonInput);
        buttonGroup2.add(jRadioButtonOutput);
        if (currentProblem.dataType == null) {
            initializeProblem();
        }
        initializeFrame();
        
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

    void initializeFrame() {
        jTextFieldProblemNo.setText(String.format("%d", this.currentProblem.problemNo));
        jTextFieldProblemName.setText(String.format("%s", this.currentProblem.problemName));
        if (this.currentProblem.oneFile) {
            jRadioButtonOneFile.setSelected(true);
            jRadioButtonMultiFile.setSelected(false);
            
            jRadioButtonInput.setEnabled(true);
            jRadioButtonOutput.setEnabled(true);
            jRadioButtonInput.setSelected(true);
            jRadioButtonOutput.setSelected(false);

            jTextFieldFilePath.setText(String.format("%s", this.currentProblem.inputFile.toString()));
            jCheckBoxInput.setSelected(currentProblem.inputFile.exists());
            jCheckBoxOutput.setSelected(currentProblem.outputFile.exists());

            jTextFieldInputExtension.setText("");
            jTextFieldOutputExtension.setText("");
            currentProblem.inputExtension = "";
            currentProblem.outputExtension = "";
            jTextFieldInputExtension.setEnabled(false);
            jTextFieldOutputExtension.setEnabled(false);

            

        } else {
            
            jRadioButtonOneFile.setSelected(false);
            jRadioButtonMultiFile.setSelected(true);
            jRadioButtonInput.setEnabled(false);
            jRadioButtonOutput.setEnabled(false);
            jRadioButtonInput.setSelected(false);
            jRadioButtonOutput.setSelected(false);

            jTextFieldFilePath.setText(String.format("%s", this.currentProblem.folderPath.toString()));
            jCheckBoxInput.setSelected(false);
            jCheckBoxOutput.setSelected(false);

            jTextFieldInputExtension.setText(this.currentProblem.inputExtension);
            jTextFieldOutputExtension.setText(this.currentProblem.outputExtension);

            jTextFieldInputExtension.setEnabled(true);
            jTextFieldOutputExtension.setEnabled(true);

        }
        
        jComboBoxDataType.setSelectedItem(currentProblem.dataType);
        jTextFieldPrecision.setEnabled(currentProblem.dataType.equalsIgnoreCase("Double"));

            jTextFieldTimeLimit.setText(String.format("%s", currentProblem.timeLimit));
            
            if (currentProblem.dataType.equalsIgnoreCase("Double")) {
                jTextFieldPrecision.setText(String.format("%s", currentProblem.precision));
            } else {
                jTextFieldPrecision.setText("");
            }

    }

    void initializeProblem() {
        this.currentProblem.dataType = "String";
        this.currentProblem.problemName = "";
        this.currentProblem.folderPath = new File("");
        this.currentProblem.inputExtension = "";
        this.currentProblem.outputExtension = "";
        this.currentProblem.precision = 0;
        this.currentProblem.inputFile = new File("");
        this.currentProblem.outputFile = new File("");
        this.currentProblem.oneFile = true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldProblemNo = new javax.swing.JTextField();
        jTextFieldProblemName = new javax.swing.JTextField();
        jRadioButtonOneFile = new javax.swing.JRadioButton();
        jRadioButtonMultiFile = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldFilePath = new javax.swing.JTextField();
        jButtonBrowse = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldInputExtension = new javax.swing.JTextField();
        jTextFieldOutputExtension = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jComboBoxDataType = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldPrecision = new javax.swing.JTextField();
        jButtonSubmit = new javax.swing.JButton();
        jRadioButtonInput = new javax.swing.JRadioButton();
        jRadioButtonOutput = new javax.swing.JRadioButton();
        jCheckBoxInput = new javax.swing.JCheckBox();
        jCheckBoxOutput = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldTimeLimit = new javax.swing.JTextField();
        jButtonExit = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemSaveAndBack = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Problem No:");

        jLabel2.setText("Problem Name:");

        jTextFieldProblemNo.setEditable(false);

        jRadioButtonOneFile.setSelected(true);
        jRadioButtonOneFile.setText("One File");
        jRadioButtonOneFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonOneFileActionPerformed(evt);
            }
        });

        jRadioButtonMultiFile.setText("Multiple File");
        jRadioButtonMultiFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMultiFileActionPerformed(evt);
            }
        });

        jLabel3.setText("Path:");

        jButtonBrowse.setText("Browse");
        jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed(evt);
            }
        });

        jLabel4.setText("Input File Extension:");

        jLabel5.setText("Output File Extension:");

        jLabel6.setText("Data Type:");

        jComboBoxDataType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "String", "Integer", "Double" }));
        jComboBoxDataType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxDataTypeActionPerformed(evt);
            }
        });

        jLabel7.setText("Precision:");

        jButtonSubmit.setText("Save & Back");
        jButtonSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSubmitActionPerformed(evt);
            }
        });

        jRadioButtonInput.setText("Input");

        jRadioButtonOutput.setText("Output");

        jCheckBoxInput.setText("Input");
        jCheckBoxInput.setEnabled(false);

        jCheckBoxOutput.setText("Output");
        jCheckBoxOutput.setEnabled(false);

        jLabel8.setText("Time Limit:");

        jButtonExit.setText("Exit");
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });

        jMenuFile.setText("File");

        jMenuItemSaveAndBack.setText("Save & Back");
        jMenuItemSaveAndBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveAndBackActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSaveAndBack);

        jMenuBar1.add(jMenuFile);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jTextFieldProblemNo, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(jTextFieldProblemName)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3)
                                    .addComponent(jRadioButtonOneFile)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jRadioButtonMultiFile)
                                        .addGap(29, 29, 29)
                                        .addComponent(jRadioButtonInput)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRadioButtonOutput)
                                        .addGap(18, 18, 18)
                                        .addComponent(jCheckBoxInput)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jCheckBoxOutput))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jTextFieldFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButtonBrowse))
                                    .addComponent(jTextFieldInputExtension, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextFieldOutputExtension, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel7)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldPrecision, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jComboBoxDataType, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(73, 73, 73))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(71, 71, 71)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldTimeLimit, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(179, 179, 179)
                        .addComponent(jButtonSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldProblemNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldProblemName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jRadioButtonInput)
                        .addComponent(jRadioButtonOutput)
                        .addComponent(jCheckBoxInput)
                        .addComponent(jCheckBoxOutput))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jRadioButtonOneFile)
                        .addComponent(jRadioButtonMultiFile)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextFieldInputExtension, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jTextFieldOutputExtension, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jComboBoxDataType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextFieldPrecision, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextFieldTimeLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSubmit)
                    .addComponent(jButtonExit))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void jButtonSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSubmitActionPerformed
        // TODO add your handling code here:

        if (!jRadioButtonOneFile.isSelected()) {
            this.currentProblem.inputExtension = jTextFieldInputExtension.getText();
            this.currentProblem.outputExtension = jTextFieldOutputExtension.getText();
        }

        this.currentProblem.problemName = (String)jTextFieldProblemName.getText();
        this.currentProblem.oneFile = jRadioButtonOneFile.isSelected();
        this.currentProblem.dataType = (String) jComboBoxDataType.getSelectedItem();
        this.currentProblem.timeLimit = Integer.parseInt(jTextFieldTimeLimit.getText());
        if (currentProblem.dataType.equalsIgnoreCase("Double"))this.currentProblem.precision = Integer.parseInt(jTextFieldPrecision.getText());

        upperClass.setProblemName(this.currentProblem.problemNo, this.currentProblem.problemName);

        setVisible(false);
        upperClass.setVisible(true);
    }//GEN-LAST:event_jButtonSubmitActionPerformed

    private void browseFile(JTextField current) {
        if (currentDirectory != null) {
            if (currentDirectory.isDirectory()) {
                fileChooser.setCurrentDirectory(currentDirectory);
            } else {
                fileChooser.setCurrentDirectory(currentDirectory.getParentFile());
            }
        }

        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            currentDirectory = fileChooser.getSelectedFile();
            current.setText(currentDirectory.toString());
        } else {
            System.out.println("File access cancelled by user.");
        }
    }

    private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseActionPerformed
        // TODO add your handling code here:
        if (jRadioButtonOneFile.isSelected()) {
            fileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
            browseFile(jTextFieldFilePath);
            if (jRadioButtonInput.isSelected()) {
                this.currentProblem.inputFile = new File(jTextFieldFilePath.getText());
                jCheckBoxInput.setSelected(true);
            } else {
                this.currentProblem.outputFile = new File(jTextFieldFilePath.getText());
                jCheckBoxOutput.setSelected(true);
            }
        } else {
            fileChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
            browseFile(jTextFieldFilePath);
            this.currentProblem.folderPath = new File(jTextFieldFilePath.getText());
        }
    }//GEN-LAST:event_jButtonBrowseActionPerformed

    private void jMenuItemSaveAndBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAndBackActionPerformed
        // TODO add your handling code here:
        jButtonSubmitActionPerformed(evt);
    }//GEN-LAST:event_jMenuItemSaveAndBackActionPerformed

    private void jRadioButtonOneFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonOneFileActionPerformed
        // TODO add your handling code here:
        jRadioButtonInput.setEnabled(true);
        jRadioButtonOutput.setEnabled(true);
        jRadioButtonInput.setSelected(true);
        jRadioButtonOutput.setSelected(false);

        jTextFieldFilePath.setText(String.format("%s", this.currentProblem.inputFile.toString()));
        jCheckBoxInput.setSelected(currentProblem.inputFile.exists());
        jCheckBoxOutput.setSelected(currentProblem.outputFile.exists());

        jTextFieldInputExtension.setText("");
        jTextFieldOutputExtension.setText("");
        currentProblem.inputExtension = "";
        currentProblem.outputExtension = "";
        jTextFieldInputExtension.setEnabled(false);
        jTextFieldOutputExtension.setEnabled(false);

        jComboBoxDataType.setSelectedItem(currentProblem.dataType);

        jTextFieldPrecision.setEnabled(currentProblem.dataType.equalsIgnoreCase("Double"));

        if (currentProblem.dataType.equalsIgnoreCase("Double")) {
            jTextFieldPrecision.setText(String.format("%s", currentProblem.precision));
        } else {
            jTextFieldPrecision.setText("");
        }

    }//GEN-LAST:event_jRadioButtonOneFileActionPerformed

    private void jRadioButtonMultiFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMultiFileActionPerformed
        // TODO add your handling code here:
        jRadioButtonInput.setEnabled(false);
        jRadioButtonOutput.setEnabled(false);
        jRadioButtonInput.setSelected(false);
        jRadioButtonOutput.setSelected(false);

        jTextFieldFilePath.setText(String.format("%s", this.currentProblem.folderPath.toString()));
        jCheckBoxInput.setSelected(false);
        jCheckBoxOutput.setSelected(false);

        jTextFieldInputExtension.setText(this.currentProblem.inputExtension);
        jTextFieldOutputExtension.setText(this.currentProblem.outputExtension);

        jTextFieldInputExtension.setEnabled(true);
        jTextFieldOutputExtension.setEnabled(true);

        jComboBoxDataType.setSelectedItem(currentProblem.dataType);

        jTextFieldPrecision.setEnabled(currentProblem.dataType.equalsIgnoreCase("Double"));

        if (currentProblem.dataType.equalsIgnoreCase("Double")) {
            jTextFieldPrecision.setText(String.format("%s", currentProblem.precision));
        } else {
            jTextFieldPrecision.setText("");
        }
    }//GEN-LAST:event_jRadioButtonMultiFileActionPerformed

    private void jComboBoxDataTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxDataTypeActionPerformed
        // TODO add your handling code here:
        if(currentProblem.dataType.equalsIgnoreCase("Double")){
            jTextFieldPrecision.setEnabled(true);
        }
        else{
            jTextFieldPrecision.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBoxDataTypeActionPerformed

    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jButtonExitActionPerformed

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
            java.util.logging.Logger.getLogger(ConfigureProblem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConfigureProblem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConfigureProblem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConfigureProblem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                new ConfigureProblem().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JButton jButtonBrowse;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonSubmit;
    private javax.swing.JCheckBox jCheckBoxInput;
    private javax.swing.JCheckBox jCheckBoxOutput;
    private javax.swing.JComboBox jComboBoxDataType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenuItem jMenuItemSaveAndBack;
    private javax.swing.JRadioButton jRadioButtonInput;
    private javax.swing.JRadioButton jRadioButtonMultiFile;
    private javax.swing.JRadioButton jRadioButtonOneFile;
    private javax.swing.JRadioButton jRadioButtonOutput;
    private javax.swing.JTextField jTextFieldFilePath;
    private javax.swing.JTextField jTextFieldInputExtension;
    private javax.swing.JTextField jTextFieldOutputExtension;
    private javax.swing.JTextField jTextFieldPrecision;
    private javax.swing.JTextField jTextFieldProblemName;
    private javax.swing.JTextField jTextFieldProblemNo;
    private javax.swing.JTextField jTextFieldTimeLimit;
    // End of variables declaration//GEN-END:variables
}
