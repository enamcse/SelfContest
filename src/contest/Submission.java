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
        this.problemId = problemId;
    }
}
