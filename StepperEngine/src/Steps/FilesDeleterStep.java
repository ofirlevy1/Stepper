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
        this.deletedList=new ListType(new ArrayList<>(), StepOutputNameEnum.DELETED_LIST.toString(), false);
        this.deletionStats=new MappingType(new Mapping(), StepOutputNameEnum.DELETION_STATS.toString(), false);

        this.filesList = new ListType(StepInputNameEnum.FILES_LIST.toString(), true);
        this.filesList.setMandatory(true);
    }
    @Override
    public void execute() {
        try {
            this.runStepFlow();
        }
        catch (EmptyFileListException e){
            this.setSummaryLine(e.getMessage());
            this.setStatus(Status.Success);
            this.deletionStats.setData(new Mapping(new NumberType(0, false),new NumberType(0, false)));//=new MappingType(new Mapping(new NumberType(0, false),new NumberType(0, false)), StepOutputNameEnum.DELETION_STATS.toString(), false);

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
                filesNotDeletedList.add(new StringType(file.getAbsolutePath(), false));
                this.addLog("Failed to delete file "+file.getName());
            }
        }
        this.deletedList.setData(filesNotDeletedList);//=new ListType(filesNotDeletedList, StepOutputNameEnum.DELETED_LIST.toString(), false);
        this.setStatus(Status.Success);
        this.setSummaryLine("Successfully deleted "+(existingFilesCount-filesNotDeletedList.size())+" out of "+files.size()+" files");
        if(files.isEmpty())throw new EmptyFileListException("File list is empty");
        this.deletionStats.setData(new Mapping(new NumberType(files.size()-filesNotDeletedList.size(), false),new NumberType(filesNotDeletedList.size(), false)));//=new MappingType(new Mapping(new NumberType(files.size()-filesNotDeletedList.size(), false),new NumberType(filesNotDeletedList.size(), false)), StepOutputNameEnum.DELETION_STATS.toString(), false);
        if(filesNotDeletedList.size()==files.size())throw  new EveryFileFailedToDeleteException("Every file in the list has failed to be deleted");


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
        if(inputName.equals(filesList.getEffectiveName())) {
            this.filesList.setData((ArrayList<DataType>) input.getData());
            this.filesList.setMandatory(true);
        }
    }

    @Override
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

    @Override
    public ArrayList<DataType> getAllData() {
        ArrayList<DataType> allData=new ArrayList<>();
        allData.add(this.filesList);
        allData.add(this.deletedList);
        allData.add(this.deletionStats);
        return  allData;
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
