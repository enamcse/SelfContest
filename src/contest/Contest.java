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
import java.util.Vector;

/**
 *
 * @author Enamul
 */
public class Contest implements Serializable{
    long passedTime;
    int numberOfProblems;
    Problem[] problems;
    Vector<Submission>submissions;
    boolean accepted[];
}
