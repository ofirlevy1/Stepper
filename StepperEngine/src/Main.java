
import DataTypes.*;

import Steps.*;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args)
    {
        ArrayList<DataType> arr = new ArrayList<>();

        //arr.add(new NumberType(0));
        //arr.add(new StringType("Hello there"));
        //arr.add(new DoubleType(5.434));
        
        arr.add(new FileType(new File("D:\\tasks\\java\\Stepper\\StepperEngine\\src\\text1.txt")));
        arr.add(new FileType(new File("D:\\tasks\\java\\Stepper\\StepperEngine\\src\\text2.txt")));
        arr.add(new FileType(new File("D:\\tasks\\java\\Stepper\\StepperEngine\\src\\text4.txt")));
        
        ListType arr2 = new ListType(arr);//ListType recieves a list of StepDataTypes
        ArrayList<StepDataType> arr3=new ArrayList<>();
        arr3.add(arr2);
        arr3.add(new NumberType(3));


        System.out.println(fds.getSummaryLine());


        // testing the presentable strings
        for(DataType step : arr3)
            System.out.println(step.getPresentableString());
            
        Step fds=new FilesDeleterStep(arr3,null);
        fds.execute();
        
        System.out.println(fds.getLogsAsString());
        System.out.println(fds.getSummaryLine());

    }
}
