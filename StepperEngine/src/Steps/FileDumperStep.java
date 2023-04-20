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

    public FileDumperStep(){
        super("File dumper", true);
        this.result=new StringType(new String(), StepOutputNameEnum.RESULT.toString());
        this.content = new StringType(StepInputNameEnum.CONTENT.toString());
        this.fileName = new StringType(StepInputNameEnum.FILE_NAME.toString());
    }

    public FileDumperStep(StringType content, StringType fileName){
        this();
        this.content=content;
        this.fileName=fileName;
        this.content.setMandatory(true);
        this.fileName.setMandatory(true);
    }

    @Override
    public void execute() {
        try {
            runStepFlow();
        } catch (Exception e) {
            setStatusAndLog(Status.Failure, e.getMessage(), e.getMessage());
            this.result=new StringType("Failure", StepOutputNameEnum.RESULT.toString());
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
        this.result=new StringType("Success", StepOutputNameEnum.RESULT.toString());
    }

    @Override
    public void setInputs(DataType... inputs) {
        for(DataType input: inputs){
            if(input.getEffectiveName().equals(StepInputNameEnum.FILE_NAME.toString())) {
                this.fileName = (StringType) input;
                this.fileName.setMandatory(true);
            }
            if(input.getEffectiveName().equals(StepInputNameEnum.CONTENT.toString())) {
                this.content = (StringType) input;
                this.content.setMandatory(true);
            }
        }
    }

    public ArrayList<DataType> getOutputs(String... outputNames) {
        ArrayList<DataType> outputsArray=new ArrayList<>();
        for(String outputName: outputNames){
            if(this.result.getEffectiveName().equals(outputName))
                outputsArray.add(this.result);
        }
        return outputsArray;
    }

}
