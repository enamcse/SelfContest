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

import java.io.IOException;

/**
 * RunInCmd interface has the only method to run the a command in cmd. command
 * in cmd is one of the biggest part of this program. To compile and to run the
 * program, command in cmd is needed. So, this interface is needed in two 
 * places,one is the place of compiling and other is the place of running the
 * c code.
 * @author      Enamul Hassan    <enamsustcse@gmail.com>
 * @version     1.0
 * @since       2014-03-19
 */
public interface RunInCmd {

    /**
     * cmdRun method takes an string and runs it in the command line prompt
     * then returns the process.
     * @param   command     this is string should be processed in cmd.
     * @return  process     The running process is returned.
     * @throws  IOException to work in Runtime class, this method throws this
     *                      exception. 
     */
    public Process cmdRun(String command) throws IOException;
}
