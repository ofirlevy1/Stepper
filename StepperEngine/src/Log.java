import java.util.Date;

public class Log {
    String LogDesctiption;
    Date LogTimeStamp;

    public void Log(String logDesctiption)
    {
        this.LogDesctiption=logDesctiption;
        LogTimeStamp=new Date();
    }
}
