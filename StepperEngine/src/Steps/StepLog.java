package Steps;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StepLog {
    private String logDescription;
    private Date logTimeStamp;

    public StepLog(String logDescription)
    {
        this.logDescription = logDescription;
        logTimeStamp =new Date();
    }

    @Override
    public String toString() {
        return new SimpleDateFormat("HH.mm.ss:sss").format(logTimeStamp)+" "+logDescription;
    }
}
