package kz.uco.tsadv.global.common;


/**
 * Process which can perform some action
 * Or/And which can perform the list of subProcesses
 * And which could be a part of another process
 * @author Felix Kamalov
 * @version 1.0
 */
public interface Process {

    //List<Process> subProcessList = null;

    /** Action and all sub-process-actions */
    ProcessResult performAction();

    /** Add subProcess to sub-processes list */
    void addSubProcess(Process subProcess);
}
