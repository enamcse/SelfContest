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
package judgecpp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import contest.Problem;

/**
 *
 * @author Enamul
 */
public class FileMatching {

    BufferedReader input1;
    BufferedReader input2;

    /**
     * 
     * @param file1
     * @param file2
     * @return 
     */
    public boolean process(Problem problem,File file1, File file2) {
//        System.out.println("Sometimes it even not comes here!"+problem.problemName);
        String line1, line2;
        Double EPS = 0.0;
        if(problem.dataType.equalsIgnoreCase("DOUBLE")){
            EPS=1.0/Math.pow(10,problem.precision);
        }
        try {
            input1 = new BufferedReader(new FileReader(file1));
            input2 = new BufferedReader(new FileReader(file2));
            line1 = input1.readLine();
            line2 = input2.readLine();
            boolean okay;
            while (line1 != null && line2 != null) {
                okay = check(line2);
//                System.out.println(line1+": when we :"+line2);
                if(problem.dataType.equalsIgnoreCase("INTEGER")&&okay){
                    int x = Integer.valueOf(line1);
                    int y = Integer.valueOf(line2);
//                    System.out.println(x+": Integer :"+y);
                    if(x!=y) {
                        input1.close();
                        input2.close();
                        return false;
                    }

                }
                else if(problem.dataType.equalsIgnoreCase("DOUBLE")&&okay){
                    double x = Double.valueOf(line1);
                    double y = Double.valueOf(line2);
//                    System.out.println(x+": Double :"+y);
                    if(Math.abs(x-y)>EPS) {
                        input1.close();
                        input2.close();
                        return false;
                    }

                }
                else if (!line1.equals(line2)) {
//                    System.out.println(line1+": Line :"+line2);
                    input1.close();
                    input2.close();
                    return false;
                }
                line1 = input1.readLine();
                line2 = input2.readLine();
            }
//            System.out.println(line1+": Sometimes :"+line2);
            if (line1 != null || line2 != null) {
                input1.close();
                input2.close();
                return false;
            }
            input1.close();
            input2.close();
            return true;

        } catch (Exception ex) {
            Logger.getLogger(FileMatching.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    private boolean check(String line) {
        for(int i = 0; i<line.length();i++){
            if(!((line.charAt(i)>='0'&&line.charAt(i)<='9')||line.charAt(i)=='.')) return false;
        }
        return true;
    }
}
