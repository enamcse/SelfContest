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
import java.io.Serializable;

/**
 *
 * @author Enamul
 */
public class Problem implements Serializable {
    public int problemNo;
    public String problemName;
    public boolean oneFile;
    public String inputExtension;
    public String outputExtension;
    public File folderPath;
    public File inputFile;
    public File outputFile;
    public String dataType;
    public int precision;
    public int timeLimit;
    public Problem(){
        dataType = "String";
        problemName = "";
        folderPath = new File("");
        inputExtension = "";
        outputExtension = "";
        precision = 0;
        inputFile = new File("");
        outputFile = new File("");
        oneFile = true;
    }
}
