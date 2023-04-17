package Steps;

import DataTypes.*;
import Steps.Step;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FilesContentExtractorStep extends Step {
    private ListType filesList;
    private NumberType lineNumber;
    public FilesContentExtractorStep(ListType filesList, NumberType lineNumber) {
        super("FILES_CONTENT_EXTRACTOR", true);
        this.filesList = filesList;
        this.lineNumber = lineNumber;
        this.filesList.setMandatory(true);
        this.lineNumber.setMandatory(true);
    }

    public FilesContentExtractorStep(){
        super("FILES_CONTENT_EXTRACTOR", true);
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
            this.outputs.add(new RelationType(new Relation(0,0)));
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

        this.outputs.add(new RelationType(relation));
        this.setSummaryLine("Extracted lines from files");
        this.setStatus(Status.Success);
    }

    @Override
    public void setInputs(ArrayList<DataType> inputs) {
        for(DataType dataType:inputs){
            if(dataType instanceof NumberType){
                this.lineNumber=(NumberType) dataType.getData();
                this.lineNumber.setMandatory(true);
            }
            else {
                this.filesList=(ListType) dataType.getData();
                this.filesList.setMandatory(true);
            }
        }
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
