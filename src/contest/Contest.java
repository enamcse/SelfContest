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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

/**
 * All information of a contest would be stored as an object of this class.
 * @since 0.1
 * @version 1.0
 * @author Enamul
 */
public class Contest implements Serializable{
    long contestDuration;
    long passedTime;
    int numberOfProblems;
    HashMap<String,String>contestants;
    Problem[] problems;
    Vector<Submission>submissions;
    
    /**
     * Initializes all components.
     */
    public Contest(){
        contestDuration = 0;
        passedTime = 0;
        numberOfProblems = 0;
        contestants = new HashMap();
        problems = null;
        submissions = new Vector<>();
    }
}
