import DataTypes.DoubleType;
import DataTypes.NumberType;
import DataTypes.StepDataType;
import DataTypes.StringType;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args)
    {
        ArrayList<StepDataType> arr = new ArrayList<>();

        arr.add(new NumberType(5));

        arr.add(new StringType("Hello there"));
        arr.add(new DoubleType(5.434));

        // testing the presentable strings
        for(StepDataType step : arr)
            System.out.println(step.getPresentableString());

    }
}
