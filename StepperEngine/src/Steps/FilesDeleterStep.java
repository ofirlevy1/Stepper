package Steps;

import DataTypes.FileType;
import DataTypes.ListType;
import DataTypes.StepDataType;
import DataTypes.StringType;

import java.io.File;
import java.util.ArrayList;

public class FilesDeleterStep extends Step{

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

    public FilesDeleterStep(ArrayList<StepDataType> inputs, ArrayList<StepDataType> outputs) {
        super("FILES_DELETER", false, inputs, outputs);
    }
    @Override
    public void execute() {
        try {
            this.runStepFlow();
        }
        catch (EmptyFileListException e){
            this.setSummaryLine(e.getMessage());
            this.setStatus(Status.Success);
            //this.outputs.add()

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
        ArrayList<StepDataType> files=(ArrayList<StepDataType>)inputs.get(0).getData();
        ArrayList<StepDataType> filesNotDeletedList=new ArrayList<>();

        this.addLog("About to start deleting "+files.size()+" files");
        for(StepDataType fileType:files)
        {
            File file=(File)fileType.getData();
            file.delete();
            if(file.exists())
            {
                filesNotDeletedList.add(new StringType(file.getAbsolutePath()));
                this.addLog("Failed to delete file "+file.getName());
            }
        }

        this.outputs.add(new ListType(filesNotDeletedList));
        this.setStatus(Status.Success);
        if(files.isEmpty())throw new EmptyFileListException("File list is empty");
        //this.outputs.add()
        if(filesNotDeletedList.size()==files.size())throw  new EveryFileFailedToDeleteException("Every file in the list has failed to be deleted");


    }
}
