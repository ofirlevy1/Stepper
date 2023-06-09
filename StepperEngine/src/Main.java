
import DataTypes.*;

import Flow.*;
import Generated.STStepper;
import Stepper.Stepper;
import Stepper.StepperUIManager;
import Steps.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class Main {
    public static void main(String[] args) throws FileNotFoundException, JAXBException {
        testContinuation();
    }

    public static void testContinuation() {
        Stepper stepper = null;
        try {
            stepper = new Stepper("C:\\users\\ofir\\ex2.xml");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }
        ArrayList<FreeInputDescriptor> arr = stepper.getFreeInputDescriptorsByFlow("Rename Files Zip Results");

        stepper.setFreeInput("Rename Files Zip Results", "FOLDER_NAME", "C:\\temp");
        stepper.setFreeInput("Rename Files Zip Results", "PREFIX", "123");
        stepper.setFreeInput("Rename Files Zip Results", "CSV_FILE_NAME", "C:\\temp\\dump.csv");
        stepper.setFreeInput("Rename Files Zip Results", "PROP_FILE_NAME", "C:\\temp\\propdump.csv");
        //stepper.setFreeInput("Rename Files Zip Results", "OPERATION", "ZIP");

        System.out.println(("are all mandatory inputs set: " + stepper.areAllMandatoryFreeInputsSet("Rename Files Zip Results")));

        stepper.runFlow("Rename Files Zip Results");

        ArrayList<String> contOptions = stepper.getFlowContinuationOptions("Rename Files Zip Results");

        stepper.activateContinuation("Rename Files Zip Results", "Delete Matched Files");

        stepper.getFreeInputDescriptorsByFlow("Delete Matched Files");

        stepper.setFreeInput("Delete Matched Files", "FOLDER_NAME", "C:\\temp");
        ArrayList<FreeInputDescriptor> arr2 = stepper.getFreeInputDescriptorsByFlow("Delete Matched Files");

        HashMap<String, String> currentFreeInputVlauesInNewFlow = stepper.getFreeInputsCurrentValues("Delete Matched Files");

        System.out.println("If this is true, that means that continuation worked - the field TIME_TO_SPEND was copied from the first flow : " + stepper.areAllMandatoryFreeInputsSet("Delete Matched Files"));
    }

    public static void testZipperStep(){
        Step st=new ZipperStep();
        ArrayList<DataType> arr= st.getAllData();
        ((UserFriendly)arr.get(0)).setData("C:\\Users\\igal6\\Downloads\\folder test.zip");
        //validation check should alert
        try {
            ((UserFriendly) arr.get(1)).setData("kkZIP");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        ((UserFriendly) arr.get(1)).setData("UNZIP");

        st.execute();
    }

    public static void testCommandLineStep(){
        Step st=new CommandLineStep(new StringType("ping","",true), new StringType("-n 3 google.com","",true));
        st.execute();
        System.out.println(st.getLogsAsString());
        System.out.println(st.getOutputs("RESULT").get(0).getPresentableString());
    }

    public static void testDeleteMatchedFilesFlowExecution() throws FileNotFoundException, JAXBException
    {
        StepperUIManager uiManager = new StepperUIManager();
        uiManager.LoadStepperFromXmlFile("C:\\Users\\Ofir\\Downloads\\ex1(8).xml");
        System.out.println(uiManager.getFlowNames());
        FlowDescriptor flowDescriptor = uiManager.getFlowDescriptor("Delete Matched Files");

        ArrayList<FreeInputDescriptor> freeInputDescriptors = uiManager.getFreeInputDescriptorsByFlow("Delete Matched Files");

        uiManager.setFreeInput("Delete Matched Files", "FOLDER_NAME", "E:\\folderToDelete");
        uiManager.setFreeInput("Delete Matched Files", "TIME_TO_SPEND", "3");


        uiManager.runFlow("Delete Matched Files");


    }

    public static void testRenameFilesFlowExecution() throws FileNotFoundException, JAXBException
    {
        StepperUIManager uiManager = new StepperUIManager();
        uiManager.LoadStepperFromXmlFile("C:\\Users\\Ofir\\Downloads\\ex1(8).xml");
        System.out.println(uiManager.getFlowNames());
        FlowDescriptor flowDescriptor = uiManager.getFlowDescriptor("Rename Files");

        ArrayList<FreeInputDescriptor> freeInputDescriptors = uiManager.getFreeInputDescriptorsByFlow("Rename Files");


        uiManager.setFreeInput("Rename Files", "FOLDER_NAME", "C:\\temp");
        uiManager.setFreeInput("Rename Files", "PREFIX", "123");
        uiManager.setFreeInput("Rename Files", "CSV_FILE_NAME", "C:\\temp\\dump.csv");
        uiManager.setFreeInput("Rename Files", "PROP_FILE_NAME", "C:\\temp\\propdump.csv");


        uiManager.runFlow("Rename Files");


    }


    private static STStepper deserializeFrom(FileInputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance("Generated");
        Unmarshaller u = jc.createUnmarshaller();
        return (STStepper) u.unmarshal(in);
    }

//    public static void flowTest() throws FileNotFoundException, JAXBException {
//        String xmlPath = "C:\\Users\\igal6\\Downloads\\ex1.xml";
//        STStepper stepper = deserializeFrom(new FileInputStream(new File(xmlPath)));
//        Flow flow = new Flow(stepper.getSTFlows().getSTFlow().get(1));
//        ArrayList<DataType> arr=new ArrayList<>();
//        DataType<Integer> number=new NumberType(3,"TIME_TO_SPEND",true);
//        DataType<String> filter=new StringType(".txt","FILTER",true);
//        DataType<String> folder=new StringType("C:\\Users\\igal6\\Downloads\\folder test","FOLDER_NAME",true);
//        arr.add(number);
//        arr.add(filter);
//        arr.add(folder);
//        flow.execution(arr);
//    }

    public static void csvExporterTest() {
//        Relation table = new Relation(3,3, "First Name", "Family Name", "Age");
//        table.set(0,0, "Ofir");
//        table.set(0,1, "Levy");
//        table.set(0,2, "28");
//
//        table.set(1,0, "Avi");
//        table.set(1,1, "Cohen");
//        table.set(1,2, "59");
//
//        table.set(2,0, "Michelle");
//        table.set(2,1, "Bar");
//        table.set(2,2, "2");
//
//        Step step = new CsvExporterStep();
//        step.setInputs(new RelationType(table,StepInputNameEnum.SOURCE.toString()));
//        step.execute();
//        System.out.println("returned from CSV Exporter, logs:");
//        System.out.println(step.getLogsAsString());
//        System.out.println("result CSV string:");
//        System.out.println(step.getOutputs().get(0).getPresentableString());
    }

    public static void filesRenamerTest()
    {
//        ArrayList<DataType> filesList = new ArrayList<DataType>();
//
////       filesList.add(new FileType(new File("C:\\Users\\Ofir\\Desktop\\temp\\testJava\\filesToRename\\renameMe.txt")));
////       filesList.add(new FileType(new File("C:\\Users\\Ofir\\Desktop\\temp\\testJava\\filesToRename\\twoSuffixes.hello.txt")));
////       filesList.add(new FileType(new File("C:\\Users\\Ofir\\Desktop\\temp\\testJava\\filesToRename\\dir.dir")));
//        ListType filesListType = new ListType(filesList,StepInputNameEnum.FILES_TO_RENAME.toString());
//        Step step = new FilesRenamerStep();
//        step.setInputs(filesListType, new StringType("1",StepInputNameEnum.SUFFIX.toString()), new StringType("2",StepInputNameEnum.PREFIX.toString()));
//        step.execute();
//
//        System.out.println("FilesRenamer done. logs:");
//        System.out.println(step.getLogsAsString());
//        System.out.println("summaryLine: " + step.getSummaryLine());
//        System.out.println("Status: " + step.getStatus());
    }

    public static void filesDumperTest(){
//        Step filesDumper=new FileDumperStep();
//        filesDumper.setInputs(new StringType("some line and new line",StepInputNameEnum.CONTENT.toString()), new StringType("D:\\tasks\\text1.txt",StepInputNameEnum.FILE_NAME.toString()));
//        filesDumper.execute();
//        System.out.println(filesDumper.getSummaryLine());
//        System.out.println(filesDumper.getLogsAsString());
    }

    public static void filesCollectorTest()
    {
//        Step filesCollector = new CollectFilesInFolderStep(new StringType("C:\\Users\\Ofir\\Desktop\\temp\\testJava"));
//        filesCollector.execute();
//        System.out.println("step finished. logs:");
//        System.out.println(filesCollector.getLogsAsString());
//        System.out.println("matching files list:");
//        System.out.println(filesCollector.getOutputs().get(0).getPresentableString());
//        System.out.println("number of matching files:");
//        System.out.println(filesCollector.getOutputs().get(1).getPresentableString());
    }



    public static void filesDeleterTest()
    {
//        ArrayList<DataType> filesList = new ArrayList<DataType>();
//
//        filesList.add(new FileType(new File("C:\\Users\\Ofir\\Desktop\\temp\\testJava\\file1.txt")));
//        filesList.add(new FileType(new File("C:\\Users\\Ofir\\Desktop\\temp\\testJava\\files2.txt")));
//        filesList.add(new FileType(new File("C:\\Users\\Ofir\\Desktop\\temp\\testJava\\file3.txt")));
//
//
//        ListType filesListType = new ListType(filesList);
//
//
//        Step step = new FilesDeleterStep(filesListType);
//        step.execute();
//
//        System.out.println("returned from extractor, logs:");
//        System.out.println(step.getLogsAsString());
//        System.out.println("The extractor's results:");
//        System.out.println(step.getOutputs().get(0).getData());
    }

    public static void dataExtractor1Test()
    {
//        ArrayList<DataType> filesList = new ArrayList<DataType>();
//
//        filesList.add(new FileType(new File("C:\\Users\\Ofir\\Desktop\\temp\\testJava\\file1.txt")));
//        filesList.add(new FileType(new File("C:\\Users\\Ofir\\Desktop\\temp\\testJava\\files2.txt")));
//        filesList.add(new FileType(new File("C:\\Users\\Ofir\\Desktop\\temp\\testJava\\file3.txt")));
//
//
//        ListType filesListType = new ListType(filesList);
//
//
//        Step step = new FilesContentExtractorStep(filesListType, new NumberType(4));
//        step.execute();
//
//        System.out.println("returned from extractor, logs:");
//        System.out.println(step.getLogsAsString());
//        System.out.println("The extractor's results:");
//        System.out.println(step.getOutputs().get(0).getData());
    }


    public static void timeSpenderTest()
    {
//        Step step = new SpendSomeTimeStep(new NumberType(5));
//        System.out.println("calling spend time step...");
//        step.execute();
//        System.out.println("returned from step.... logs:");
//        System.out.println(step.getLogsAsString());
    }

    public static void olsTests() {
//        ArrayList<DataType> arr = new ArrayList<>();
//
//        //arr.add(new NumberType(0));
//        //arr.add(new StringType("Hello there"));
//        //arr.add(new DoubleType(5.434));
//
//        arr.add(new FileType(new File("D:\\tasks\\java\\Stepper\\StepperEngine\\src\\text1.txt")));
//        arr.add(new FileType(new File("D:\\tasks\\java\\Stepper\\StepperEngine\\src\\text2.txt")));
//        arr.add(new FileType(new File("D:\\tasks\\java\\Stepper\\StepperEngine\\src\\text3.txt")));
//
//        ListType arr2 = new ListType(arr);//ListType receives a list of StepDataTypes
//        ArrayList<DataType> arr3=new ArrayList<>();
//        arr3.add(arr2);
//        arr3.add(new NumberType(9));
//
//        Relation relation=new Relation(0,0/*,"name","age","eye color"*/);
//        //relation.set(0,0,"asher");
//        //relation.set(0,1,"25");
//        //relation.set(0,2,"green");
//        //relation.set(1,0,"neta");
//        //relation.set(1,1,"39");
//        //relation.set(1,2,"brown");
//        ArrayList<DataType> arr4=new ArrayList<>();
//        arr4.add(new RelationType(relation));
//
//        // testing the presentable strings
//        for(DataType step : arr4)
//            System.out.println(step.getPresentableString());
//
//        Step fds=new PropertiesExporterStep(arr4,null);
//        fds.execute();
//
//        System.out.println(fds.getLogsAsString());
//        System.out.println(fds.getSummaryLine());
//        System.out.println(fds.getOutputs().get(0).getPresentableString());
    }
}
