package kz.uco.tsadv.global.common;


import kz.uco.tsadv.global.enums.CompletionStatus;

import java.lang.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Process which can perform some action
 * Or/And which can perform the list of subProcesses
 * And which could be a part of another process
 * @author Felix Kamalov
 * @version 1.0
 */
public class AbstractProcess implements Process {

    /**
     * Object which is used in process
     */
    protected Object item;

    /**
     * List of sub-processes
     */
    protected List<Process> subProcesses = new ArrayList<>();

    /**
     * Constructor
     * @param item Object which is used in process
     */
    public AbstractProcess(Object item) {
        this.item = item;
    }

    /**
     * Action and all sub-process-actions
     */
    public ProcessResult performAction() {
        ProcessResult result = new ProcessResult();

        for (Process step : subProcesses) {
            result = step.performAction();
            if (!result.isSuccess()) {
                return result;
            }
        }

        return result;
    }

    /**
     * Add subProcess to sub-processes list
     * @param subProcess Sub-process
     */
    public void addSubProcess(Process subProcess) {
        if (subProcess != null) {
            subProcesses.add(subProcess);
        }
    }


}
