package Steps;

import DataTypes.*;

import java.io.*;
import java.util.ArrayList;

public class FilesContentExtractorStep extends Step {
    private ListType filesList;
    private NumberType lineNumber;
    private RelationType data;
    private static double stepAvgDuration=0;
    private static int stepStartUpCount=0;

    public FilesContentExtractorStep() {
        super("Files Content Extractor", true);
        this.data = new RelationType(new Relation(1, 1, "something to fill"), StepOutputNameEnum.DATA.toString(), false);

        this.filesList = new ListType(StepInputNameEnum.FILES_LIST.toString(), true);
        this.lineNumber = new NumberType(StepInputNameEnum.LINE.toString(), true);
        this.filesList.setMandatory(true);
        this.lineNumber.setMandatory(true);
    }

    public FilesContentExtractorStep(ListType filesList, NumberType lineNumber) {
        this();
        this.filesList = filesList;
        this.lineNumber = lineNumber;
        this.filesList.setMandatory(true);
        this.lineNumber.setMandatory(true);
    }



    @Override
    protected void outerRunStepFlow(){
        try{
            this.runStepFlow();
        }
        catch (EmptyFileListException e){
            this.setSummaryLine(e.getMessage());
            this.setStatus(Status.Success);
            this.addLog(e.getMessage());
            this.data.setData(new Relation(0, 0));//=new RelationType(new Relation(0,0), StepOutputNameEnum.DATA.toString(), false);
        }
        catch (Exception e){
            this.setSummaryLine("Exception: " + e.getMessage());
            this.setStatus(Status.Failure);
        }
    }

    @Override
    protected void runStepFlow() throws Exception {
        String extractedLine="";
        ArrayList<DataType> files=(ArrayList<DataType>)filesList.getData();
        Integer fileLine= (Integer) lineNumber.getData();
        Relation relation=new Relation( files.size(), 3,"serial number", "file name", "text data");

        if(files.isEmpty())throw new EmptyFileListException("File list is empty");
        for(int i=0;i<files.size();i++)
        {
            File file=(File) files.get(i).getData();

            try(BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream(file.getPath())));){
                for(int j=0;j<=fileLine;j++) {
                    extractedLine = in.readLine();
                    if(extractedLine==null)throw new NoSuchLineException("No Such Line");
                }

                relation.set(i,0,String.valueOf(i+1));
                relation.set(i,1,file.getName());
                relation.set(i,2,extractedLine);
            }
            catch (FileNotFoundException e){
                relation.set(i,0,String.valueOf(i+1));
                relation.set(i,1,file.getName());
                relation.set(i,2,"File not Found");
                this.addLog("Problem extracting line number "+fileLine+" from file "+file.getName()+" :file does not exist");
            }
            catch (NoSuchLineException e){
                relation.set(i,0,String.valueOf(i+1));
                relation.set(i,1,file.getName());
                relation.set(i,2,"No such line");
                this.addLog("Problem extracting line number "+fileLine+" from file "+file.getName()+" :no such line");
            }

        }

        this.data.setData(relation);//=new RelationType(relation, StepOutputNameEnum.DATA.toString(), false);
        this.setSummaryLine("Extracted lines from files");
        this.setStatus(Status.Success);
    }

    @Override
    public void setInputs(DataType... inputs) {
        for(DataType input: inputs){
            if(input.getEffectiveName().equals(filesList.getEffectiveName())) {
                this.filesList.setData((ArrayList<DataType>) input.getData());
                this.filesList.setMandatory(true);
            }
        }
    }

    @Override
    public void setInputByName(DataType input, String inputName) {
        if(inputName.equals(filesList.getEffectiveName()))
            this.filesList.setData((ArrayList<DataType>) input.getData());
    }

    @Override
    public ArrayList<DataType> getOutputs(String... outputNames) {
        ArrayList<DataType> outputsArray=new ArrayList<>();
        for(String outputName: outputNames){
            if(this.data.getEffectiveName().equals(outputName))
                outputsArray.add(this.data);
            if(this.filesList.getEffectiveName().equals(outputName))
                outputsArray.add(filesList);
        }
        return outputsArray;
    }

    @Override
    public ArrayList<DataType> getAllData() {
        ArrayList<DataType> allData=new ArrayList<>();
        allData.add(this.filesList);
        allData.add(this.data);
        allData.add(this.lineNumber);
        return  allData;
    }

    public class EmptyFileListException extends Exception{
        public EmptyFileListException(String str){
            super(str);
        }
    }

    public class NoSuchLineException extends Exception{
        public NoSuchLineException(String str){
            super(str);
        }
    }

    @Override
    protected void updateStaticTimers() {
        stepStartUpCount= startUpCounter;
        stepAvgDuration=durationAvgInMs;
    }

    public static int getStepStartUpCount() {
        return stepStartUpCount;
    }

    public static double getStepAvgDuration() {
        return stepAvgDuration;
    }
}
