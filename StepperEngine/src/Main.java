
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
        arr.add(new FileType(new File("D:\\tasks\\java\\Stepper\\StepperEngine\\src\\text3.txt")));
        
        ListType arr2 = new ListType(arr);//ListType receives a list of StepDataTypes
        ArrayList<DataType> arr3=new ArrayList<>();
        arr3.add(arr2);
        arr3.add(new NumberType(9));

        Relation relation=new Relation(0,0/*,"name","age","eye color"*/);
        //relation.set(0,0,"asher");
        //relation.set(0,1,"25");
        //relation.set(0,2,"green");
        //relation.set(1,0,"neta");
        //relation.set(1,1,"39");
        //relation.set(1,2,"brown");
        ArrayList<DataType> arr4=new ArrayList<>();
        arr4.add(new RelationType(relation));

        // testing the presentable strings
        for(DataType step : arr4)
            System.out.println(step.getPresentableString());
            
        Step fds=new PropertiesExporterStep(arr4,null);
        fds.execute();
        
        System.out.println(fds.getLogsAsString());
        System.out.println(fds.getSummaryLine());
        System.out.println(fds.getOutputs().get(0).getPresentableString());

    }
}
