/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
