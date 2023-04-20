package Steps;

import DataTypes.*;

import java.io.*;
import java.util.ArrayList;

public class FilesContentExtractorStep extends Step {
    private ListType filesList;
    private NumberType lineNumber;
    private RelationType data;

    public FilesContentExtractorStep() {
        super("Files Content Extractor", true);
        this.data = new RelationType(new Relation(1, 1, "something to fill"), StepOutputNameEnum.DATA.toString());

        this.filesList = new ListType(StepInputNameEnum.FILES_LIST.toString());
        this.lineNumber = new NumberType(StepInputNameEnum.LINE.toString());
    }

    public FilesContentExtractorStep(ListType filesList, NumberType lineNumber) {
        this();
        this.filesList = filesList;
        this.lineNumber = lineNumber;
        this.filesList.setMandatory(true);
        this.lineNumber.setMandatory(true);
    }



    @Override
    public void execute() {
        try{
            this.runStepFlow();
        }
        catch (EmptyFileListException e){
            this.setSummaryLine(e.getMessage());
            this.setStatus(Status.Success);
            this.addLog(e.getMessage());
            this.data=new RelationType(new Relation(0,0), StepOutputNameEnum.DATA.toString());
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

        this.data=new RelationType(relation, StepOutputNameEnum.DATA.toString());
        this.setSummaryLine("Extracted lines from files");
        this.setStatus(Status.Success);
    }

    @Override
    public void setInputs(DataType... inputs) {
        for(DataType input: inputs){
            if(input.getEffectiveName().equals(StepInputNameEnum.FILES_LIST.toString())) {
                this.filesList = (ListType) input;
                this.filesList.setMandatory(true);
            }
        }
    }

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
}
