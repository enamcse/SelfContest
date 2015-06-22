/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package judge;

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
