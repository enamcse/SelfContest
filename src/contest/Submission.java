/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package contest;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Enamul
 */
public class Submission implements Serializable{
    String user;
    Date date;
    long timePassed;
    public int problemId;
    String problemName;
    String language;
    public String verdict;
    public long timeElapsed;
    public File submittedFile;
    public Submission(Date date, String language,String problemName, String verdict, long timePassed, String user, File submittedFile, int problemId){
        this.date = date;
        this.language = language;
        this.problemName = problemName;
        this.verdict = verdict;
        this.timePassed = timePassed;
        this.user = user;
        this.submittedFile = submittedFile;
    }
}
