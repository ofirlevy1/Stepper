package Steps;

import DataTypes.*;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class CollectFilesInFolderStep extends Step{
    private StringType folderName;
    private StringType filter;
    private ListType filesList;
    private NumberType totalFound;

    public CollectFilesInFolderStep(StringType folderName) {
        super("Collect Files In Folder", true);
        this.folderName = folderName;
        this.folderName.setMandatory(true);
        this.filesList=new ListType(new ArrayList<>(),StepOutputNameEnum.FILES_LIST.toString(), false);
        this.totalFound=new NumberType(new Integer(0),StepOutputNameEnum.TOTAL_FOUND.toString(), false);
    }

    public CollectFilesInFolderStep(StringType folderName, StringType filter) {
        this(folderName);
        this.filter = filter;
        this.filter.setMandatory(false);
        this.folderName.setMandatory(true);
    }

    public CollectFilesInFolderStep(){
        super("Collect Files In Folder", true);
        this.filesList=new ListType(new ArrayList<>(),StepOutputNameEnum.FILES_LIST.toString(), false);
        this.totalFound=new NumberType(new Integer(0),StepOutputNameEnum.TOTAL_FOUND.toString(), false);
        this.folderName = new StringType(StepInputNameEnum.FOLDER_NAME.toString(), true);
        this.filter = new StringType(StepInputNameEnum.FILTER.toString(), true);
        this.folderName.setMandatory(true);
    }

    @Override
    protected void outerRunStepFlow() {
        try{
            this.runStepFlow();
        }
        catch (Exception e){
            this.setSummaryLine("Exception: " + e.getMessage());
            this.setStatus(Status.Failure);
        }
    }

    @Override
    protected void runStepFlow() throws Exception {
        addLog("Reading folder " + folderName.getPresentableString() + " content with filter " + (filter.isDataSet() ? filter.getPresentableString(): "null (no filter)" ));
        File folder = new File(folderName.getData());
        if (!folder.exists() || !folder.isDirectory()) {
            setStatusAndLog(Status.Failure,
                    "The FilesCollector failed because the given path either doesn't exist, or isn't a folder",
                    "The FilesCollector failed because the given path either doesn't exist, or isn't a folder");
            return;
        }
        // If directory is empty - finish with warning:
        if(folder.listFiles().length == 0) {
            setStatusAndLog(Status.Warning,
                    "The FilesCollector finished with warning because the given folder is empty",
                    "The FilesCollector finished with warning because the given folder is empty");
            return;
        }
        int matchingFiles = addMatchingFilesToOutput(folder.listFiles());
        this.totalFound.setData(matchingFiles);//;=new NumberType(matchingFiles, StepOutputNameEnum.TOTAL_FOUND.toString(), false);
        addLog("Found " + matchingFiles + " files in folder matching the filter");
        setStatus(Status.Success);
    }

    @Override
    public void setInputs(DataType... inputs) {
        for(DataType input :inputs){
            if(input.getEffectiveName().equals(filter.getEffectiveName())) {
                this.filter.setData((String) input.getData());
                this.filter.setMandatory(false);
            }
            else if(input.getEffectiveName().equals(folderName.getEffectiveName())) {
                this.folderName.setData((String) input.getData());
                this.folderName.setMandatory(true);
            }
        }
    }

    @Override
    public void setInputByName(DataType input, String inputName) {
        if(inputName.equals(filter.getEffectiveName()))
            this.filter.setData((String) input.getData());
        else if(inputName.equals(folderName.getEffectiveName()))
            this.folderName.setData((String) input.getData());

    }

    @Override
    public ArrayList<DataType> getOutputs(String... outputNames) {
        ArrayList<DataType> outputsArray=new ArrayList<>();
        for(String outputName: outputNames){
            if(this.totalFound.getEffectiveName().equals(outputName))
                outputsArray.add(this.totalFound);
            if(this.filesList.getEffectiveName().equals(outputName))
                outputsArray.add(filesList);
        }
        return outputsArray;
    }

    @Override
    public ArrayList<DataType> getAllData() {
        ArrayList<DataType> allData=new ArrayList<>();
        allData.add(this.filesList);
        allData.add(this.totalFound);
        allData.add(this.folderName);
        allData.add(this.filter);
        return  allData;
    }

    int addMatchingFilesToOutput(File[] files)
    {
        ListType matchingFiles = new ListType(new ArrayList<DataType>(), StepOutputNameEnum.FILES_LIST.toString(), false);
        for(File file : files)
        {
            if(!filter.isDataSet() || file.getName().endsWith(filter.getData()))
                if(!file.isDirectory())
                    matchingFiles.getData().add(new FileType(file, false));
        }
        this.filesList.setData(matchingFiles.getData());//=matchingFiles;
        return matchingFiles.getData().size();
    }

}
