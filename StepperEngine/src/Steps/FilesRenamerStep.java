package Steps;

import DataTypes.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FilesRenamerStep extends Step {
    private ListType filesToRename;
    private StringType prefix;
    private StringType suffix;
    private RelationType renameResult;
    private ArrayList<String> failedFiles;

    // These are used to fill the Relation result output at the end of the step because
    // Relation is not dynamic - it can be created only at the end of the step
    // when we know what it's dimensions should be.
    private ArrayList<String> renamedFilesOldNames;
    private ArrayList<String> renamedFilesNewNames;

    public FilesRenamerStep(){
        super("Files Renamer", false);
        failedFiles=new ArrayList<>();
        renamedFilesOldNames=new ArrayList<>();
        renamedFilesOldNames=new ArrayList<>();
        this.renameResult=new RelationType(new Relation(1,1,"something to fill"), StepOutputNameEnum.RENAME_RESULT.toString());

        this.filesToRename = new ListType(StepInputNameEnum.FILES_TO_RENAME.toString());
        this.prefix = new StringType(StepInputNameEnum.PREFIX.toString());
        this.suffix = new StringType(StepInputNameEnum.SUFFIX.toString());
    }

    public FilesRenamerStep(ListType filesToRename, StringType prefix, StringType suffix) {
        this();
        this.filesToRename = filesToRename;
        this.prefix = prefix;
        this.suffix = suffix;
        this.filesToRename.setMandatory(true);
        this.prefix.setMandatory(false);
        this.suffix.setMandatory(false);
    }

    @Override
    public void execute() {
        try {
            runStepFlow();
        } catch (Exception e) {
            setStatusAndLog(Status.Failure, e.getMessage(), e.getMessage());
            this.outputs.add(new StringType("Failure"));
        }
    }

    @Override
    protected void runStepFlow() throws Exception {
        addLog("About to start rename " + filesToRename.getData().size() + " files. Adding prefix: " + prefix.getData() + "; adding suffix: " + suffix.getData());
        File currentFile;
        for(DataType dataType : filesToRename.getData()) {
            currentFile = (File) dataType.getData(); // This conversion is not ideal - maybe there's a way to avoid it (redesign to FileType?)
            if(!tryRenamingFile(currentFile)) {
                failedFiles.add(currentFile.getName());
                addLog("Problem renaming file " + currentFile.getName());
            }
        }
        setOutput();
        setStatus(failedFiles.size() != 0 ? Status.Warning : Status.Success);
        writeSummaryLine();
    }

    @Override
    public void setInputs(DataType... inputs) {
        for(DataType input: inputs){
            if(input.getEffectiveName().equals(StepInputNameEnum.FILES_TO_RENAME.toString())) {
                this.filesToRename = (ListType) input;
                this.filesToRename.setMandatory(true);
            }
            if(input.getEffectiveName().equals(StepInputNameEnum.SUFFIX.toString())) {
                this.suffix = (StringType) input;
                this.suffix.setMandatory(false);
            }
            if(input.getEffectiveName().equals(StepInputNameEnum.PREFIX.toString())) {
                this.prefix = (StringType) input;
                this.prefix.setMandatory(false);
            }
        }
    }

    @Override
    public ArrayList<DataType> getOutputs(String... outputNames) {
        ArrayList<DataType> outputsArray=new ArrayList<>();
        for(String outputName: outputNames){
            if(this.renameResult.getEffectiveName().equals(outputName))
                outputsArray.add(this.renameResult);
        }
        return outputsArray;
    }

    @Override
    public ArrayList<DataType> getAllData() {
        ArrayList<DataType> allData=new ArrayList<>();
        allData.add(this.renameResult);
        allData.add(this.filesToRename);
        allData.add(this.prefix);
        allData.add(this.suffix);
        return  allData;
    }

    private void setOutput() {
        Relation output = new Relation(renamedFilesNewNames.size(), 3, "Serial Number", "Original File Name", "New File Name");
        for(int i = 0; i < renamedFilesNewNames.size(); i++) {
            output.set(i, 0, new Integer(i+1).toString());
            output.set(i, 1, renamedFilesOldNames.get(i));
            output.set(i, 2, renamedFilesNewNames.get(i));
        }
        this.renameResult=new RelationType(output, StepOutputNameEnum.RENAME_RESULT.toString());
    }

    private boolean tryRenamingFile(File file) {
        Path path = Paths.get(file.getAbsolutePath());
        String oldName = path.getFileName().toString();

        // building the new full file name, first the root: (C:\ for example)
        String newFileFullPath = path.getRoot().toString();

        // then all the path parts except the last one (which is the actual name)
        for(int i = 0; i < path.getNameCount() - 1; i++) {
            newFileFullPath += path.getName(i) + "\\";
        }
        
        // Then the file name with the added prefix and/or suffix
        String newFileName = prefix.getData() + removeExtension(path.getFileName().toString()) + suffix.getData() + getExtension(path.getFileName().toString());
        newFileFullPath += newFileName;
        if (file.renameTo(new File(newFileFullPath)))
        {
            renamedFilesOldNames.add(oldName);
            renamedFilesNewNames.add(newFileName);
            return true;
        }
            return false;
    }

    private void writeSummaryLine() {
        if(filesToRename.getData().size() == 0)
            setSummaryLine("FilesRenamer got an empty files list, so nothing was accomplished...");
        else if(failedFiles.size() != 0)
        {
            String message = "FilesRenamer failed to rename the following files: ";
            for(String fileName : failedFiles) {
                message += fileName + ",";
            }
            setSummaryLine(message);
        }
        else {
            setSummaryLine("FileRenamer finished successfully (all files were renamed)");
        }
    }

    private String removeExtension(String str) {
        int extensionStartIndex = str.lastIndexOf('.');
        if (extensionStartIndex == -1)
            return str;
        return str.substring(0, extensionStartIndex);
    }

    private String getExtension(String str)
    {
        int extensionStartIndex = str.lastIndexOf('.');
        if (extensionStartIndex == -1)
            return "";
        return str.substring(extensionStartIndex, str.length());
    }
}
