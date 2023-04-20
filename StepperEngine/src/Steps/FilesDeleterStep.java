package Steps;

import DataTypes.*;

import java.io.File;
import java.util.ArrayList;

public class FilesDeleterStep extends Step{
    private ListType filesList;
    private ListType deletedList;
    private MappingType deletionStats;

    public FilesDeleterStep(ListType filesList) {
        this();
        this.filesList = filesList;
        this.filesList.setMandatory(true);
    }

    public FilesDeleterStep(){
        super("Files Deleter", false);
        this.deletedList=new ListType(new ArrayList<>(), StepOutputNameEnum.DELETED_LIST.toString());
        this.deletionStats=new MappingType(new Mapping(), StepOutputNameEnum.DELETION_STATS.toString());
    }
    @Override
    public void execute() {
        try {
            this.runStepFlow();
        }
        catch (EmptyFileListException e){
            this.setSummaryLine(e.getMessage());
            this.setStatus(Status.Success);
            this.deletionStats=new MappingType(new Mapping(new NumberType(0),new NumberType(0)), StepOutputNameEnum.DELETION_STATS.toString());

        } catch (EveryFileFailedToDeleteException e) {
            this.setSummaryLine(e.getMessage());
            this.setStatus(Status.Warning);
            this.addLog(e.getMessage());

        } catch (Exception e) {
            this.setSummaryLine(e.getMessage());
            this.setStatus(Status.Failure);
        }
    }

    @Override
    protected void runStepFlow() throws Exception {
        ArrayList<DataType> files=filesList.getData();
        ArrayList<DataType> filesNotDeletedList=new ArrayList<>();
        int existingFilesCount=0;

        this.addLog("About to start deleting "+files.size()+" files");
        for(DataType fileType:files)
        {
            File file=(File)fileType.getData();
            if(file.exists())
                existingFilesCount++;
            file.delete();
            if(file.exists())
            {
                filesNotDeletedList.add(new StringType(file.getAbsolutePath()));
                this.addLog("Failed to delete file "+file.getName());
            }
        }
        this.deletedList=new ListType(filesNotDeletedList, StepOutputNameEnum.DELETED_LIST.toString());
        this.setStatus(Status.Success);
        this.setSummaryLine("Successfully deleted "+(existingFilesCount-filesNotDeletedList.size())+" out of "+files.size()+" files");
        if(files.isEmpty())throw new EmptyFileListException("File list is empty");
        this.deletionStats=new MappingType(new Mapping(new NumberType(files.size()-filesNotDeletedList.size()),new NumberType(filesNotDeletedList.size())), StepOutputNameEnum.DELETION_STATS.toString());
        if(filesNotDeletedList.size()==files.size())throw  new EveryFileFailedToDeleteException("Every file in the list has failed to be deleted");


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
            if(this.deletedList.getEffectiveName().equals(outputName))
                outputsArray.add(this.deletedList);
            if(this.deletionStats.getEffectiveName().equals(outputName))
                outputsArray.add(deletionStats);
        }
        return outputsArray;
    }

    public class EmptyFileListException extends Exception{
        public EmptyFileListException(String str){
            super(str);
        }
    }

    public class EveryFileFailedToDeleteException extends Exception{
        public EveryFileFailedToDeleteException(String str){
            super(str);
        }
    }
}
