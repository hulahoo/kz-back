package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class JobDataJson implements Serializable {

    public ArrayList<JobJson> getJobs() {
        return jobs;
    }

    public void setJobs(ArrayList<JobJson> jobs) {
        this.jobs = jobs;
    }

    protected ArrayList<JobJson> jobs;
}