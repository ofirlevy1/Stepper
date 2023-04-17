package Steps;

import DataTypes.*;

import java.io.File;
import java.util.ArrayList;

public class FilesDeleterStep extends Step{
    private ListType filesList;
    public FilesDeleterStep(ListType filesList) {
        super("FILES_DELETER", false);
        this.filesList = filesList;
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
            this.outputs.add(new MappingType(new Mapping(new NumberType(0),new NumberType(0))));

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

        this.outputs.add(new ListType(filesNotDeletedList));
        this.setStatus(Status.Success);
        this.setSummaryLine("Successfully deleted "+(existingFilesCount-filesNotDeletedList.size())+" out of "+files.size()+" files");
        if(files.isEmpty())throw new EmptyFileListException("File list is empty");
        this.outputs.add(new MappingType(new Mapping(new NumberType(files.size()-filesNotDeletedList.size()),new NumberType(filesNotDeletedList.size()))));
        if(filesNotDeletedList.size()==files.size())throw  new EveryFileFailedToDeleteException("Every file in the list has failed to be deleted");


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
