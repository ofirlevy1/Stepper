import DataTypes.DoubleType;
import DataTypes.NumberType;
import DataTypes.DataType;
import DataTypes.StringType;
import Steps.*;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args)
    {
        ArrayList<DataType> arr = new ArrayList<>();

        arr.add(new NumberType(0));

        arr.add(new StringType("Hello there"));
        arr.add(new DoubleType(5.434));

        // testing the presentable strings
        for(DataType step : arr)
            System.out.println(step.getPresentableString());

        Step sst= new SpendSomeTimeStep(arr,null);
        sst.execute();
        System.out.println(sst.getLogsAsString());
        System.out.println(sst.getSummaryLine());
    }
}
