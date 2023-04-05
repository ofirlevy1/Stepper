import java.text.DateFormat;
import java.util.Date;

public class Log {
    private String logDescription;
    private Date logTimeStamp;

    public Log(String logDescription)
    {
        this.logDescription = logDescription;
        logTimeStamp =new Date();
    }

    @Override
    public String toString() {
        return logTimeStamp+" "+logDescription;
    }
}
