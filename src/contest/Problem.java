/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
}
