package Steps;

import DataTypes.*;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class CollectFilesInFolderStep extends Step{
    private StringType folderName;

    private StringType filter;

    public CollectFilesInFolderStep(StringType folderName) {
        super("Collect Files In Folder", true);
        this.folderName = folderName;
    }

    public CollectFilesInFolderStep(StringType folderName, StringType filter) {
        this(folderName);
        this.filter = filter;
    }

    @Override
    public void execute() {
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
        addLog("Reading folder " + folderName.toString() + " content with filter " + (filter == null ? "null (no filter)" : filter));
        File folder = new File(folderName.getData());
        if (!folder.exists() || !folder.isDirectory()) {
            SetStatusAndLog(Status.Failure,
                    "The FilesCollector failed because the given path either doesn't exist, or isn't a folder",
                    "The FilesCollector failed because the given path either doesn't exist, or isn't a folder");
            return;
        }
        // If directory is empty - finish with warning:
        if(folder.listFiles().length == 0) {
            SetStatusAndLog(Status.Warning,
                    "The FilesCollector finished with warning because the given folder is empty",
                    "The FilesCollector finished with warning because the given folder is empty");
            outputs.add(new ListType(new ArrayList<DataType>()));
            outputs.add(new NumberType(0));
            return;
        }
        int matchingFiles = addMatchingFilesToOutput(folder.listFiles());
        outputs.add(new NumberType(matchingFiles));
        addLog("Found " + matchingFiles + " files in folder matching the filter");
        setStatus(Status.Success);
    }

    int addMatchingFilesToOutput(File[] files)
    {
        ListType matchingFiles = new ListType(new ArrayList<DataType>());
        for(File file : files)
        {
            if(filter == null || file.getName().endsWith(filter.getData()))
                if(!file.isDirectory())
                    matchingFiles.getData().add(new FileType(file));
        }
        outputs.add(matchingFiles);
        return matchingFiles.getData().size();
    }

}