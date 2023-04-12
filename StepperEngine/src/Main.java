import DataTypes.*;
import Steps.*;

import java.util.ArrayList;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args)
    {
        ArrayList<StepDataType> arr = new ArrayList<>();

        arr.add(new NumberType(0));
        arr.add(new StringType("Hello there"));
        arr.add(new DoubleType(5.434));
        ListType arr2 = new ListType(arr);
        ArrayList<StepDataType> arr3=new ArrayList<>();
        arr3.add(arr2);
        // testing the presentable strings
        for(StepDataType step : arr)
            System.out.println(step.getPresentableString());

        System.out.println(arr2.getPresentableString());

        Step fds=new FilesDeleterStep(arr3,null);
        fds.execute();

    }
}
