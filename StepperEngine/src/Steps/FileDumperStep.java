package Steps;

import DataTypes.DataType;
import DataTypes.RelationType;
import DataTypes.StringType;

import java.io.*;
import java.util.ArrayList;

public class FileDumperStep extends  Step{

    private StringType content;
    private StringType fileName;
    private StringType result;
    private static double stepAvgDuration=0;
    private static int stepStartUpCount=0;

    public FileDumperStep(){
        super("File Dumper", true);
        this.result=new StringType(new String(), StepOutputNameEnum.RESULT.toString(), false);
        this.content = new StringType(StepInputNameEnum.CONTENT.toString(), true);
        this.fileName = new StringType(StepInputNameEnum.FILE_NAME.toString(), true);
        this.content.setMandatory(true);
        this.fileName.setMandatory(true);
    }

    public FileDumperStep(StringType content, StringType fileName){
        this();
        this.content=content;
        this.fileName=fileName;
        this.content.setMandatory(true);
        this.fileName.setMandatory(true);
    }

    @Override
    protected void outerRunStepFlow() {
        try {
            runStepFlow();
        } catch (Exception e) {
            setStatusAndLog(Status.Failure, e.getMessage(), e.getMessage());
            this.result.setData("Failure");//=new StringType("Failure", StepOutputNameEnum.RESULT.toString(), false);
        }

    }

    @Override
    protected void runStepFlow() throws Exception {
        String content=(String) this.content.getData();
        String fileName=(String) this.fileName.getData();
        addLog("About to create file named "+ fileName);
        File file=new File(fileName);
        if(file.exists()) throw new Exception("File already exists");
        try(BufferedWriter out=new BufferedWriter(new OutputStreamWriter((new FileOutputStream(fileName))))) {
            if(content.length()==0)
                setStatusAndLog(Status.Warning, "Created file with no content", "entered an empty string");
            else
            {
                setStatus(Status.Success);
                setSummaryLine("Created file successfully");
            }
            out.write(content);
        }
        this.result.setData("Success");//;=new StringType("Success", StepOutputNameEnum.RESULT.toString(), false);
    }

    @Override
    public void setInputs(DataType... inputs) {
        for(DataType input: inputs){
            if(input.getEffectiveName().equals(fileName.getEffectiveName())) {
                this.fileName.setData((String) input.getData());
                this.fileName.setMandatory(true);
            }
            if(input.getEffectiveName().equals(content.getEffectiveName())) {
                this.content.setData((String) input.getData());
                this.content.setMandatory(true);
            }
        }
    }

    @Override
    public void setInputByName(DataType input, String inputName) {
        if(inputName.equals(fileName.getEffectiveName()))
            this.fileName.setData((String) input.getData());
        if(inputName.equals(content.getEffectiveName()))
            this.content.setData((String) input.getData());
    }

    @Override
    public ArrayList<DataType> getOutputs(String... outputNames) {
        ArrayList<DataType> outputsArray=new ArrayList<>();
        for(String outputName: outputNames){
            if(this.result.getEffectiveName().equals(outputName))
                outputsArray.add(this.result);
        }
        return outputsArray;
    }

    @Override
    public ArrayList<DataType> getAllData() {
        ArrayList<DataType> allData=new ArrayList<>();
        allData.add(this.result);
        allData.add(this.fileName);
        allData.add(this.content);
        return  allData;
    }

    @Override
    protected void updateStaticTimers() {
        stepStartUpCount += startUpCounter;
        stepAvgDuration=stepAvgDuration+((durationAvgInMs-stepAvgDuration)/ stepStartUpCount);
    }

    public static int getStepStartUpCount() {
        return stepStartUpCount;
    }

    public static double getStepAvgDuration() {
        return stepAvgDuration;
    }

    public void clearDataMembers(){
        this.fileName.eraseData();
        this.content.eraseData();
        this.result.eraseData();
    }

}
