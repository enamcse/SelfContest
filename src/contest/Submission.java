/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package contest;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Enamul
 */
class Submission implements Serializable{
    String user;
    Date date;
    long timePassed;
    String problemName;
    String language;
    String verdict;
    public Submission(Date date, String language,String problemName, String verdict, long timePassed, String user){
        this.date = date;
        this.language = language;
        this.problemName = problemName;
        this.verdict = verdict;
        this.timePassed = timePassed;
        this.user = user;
    }
}
