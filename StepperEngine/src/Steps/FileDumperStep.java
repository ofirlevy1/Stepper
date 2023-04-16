package Steps;

import DataTypes.StringType;

import java.io.*;

public class FileDumperStep extends  Step{

    private StringType content;
    private StringType fileName;

    public FileDumperStep(StringType content, StringType fileName){
        super("FILE_DUMPER", true);
        this.content=content;
        this.fileName=fileName;
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
        String content=(String) this.content.getData();
        String fileName=(String) this.fileName.getData();
        addLog("About to create file named "+ fileName);
        File file=new File(fileName);
        if(file.exists()) throw new Exception("File already exists");
        try(BufferedWriter out=new BufferedWriter(new OutputStreamWriter((new FileOutputStream(fileName))))) {
            if(content.length()==0)
                setStatusAndLog(Status.Warning, "Created file with no content", "entered an empty string");
            else
            {
                setStatus(Status.Success);
                setSummaryLine("Created file successfully");
            }
            out.write(content);
        }

        this.outputs.add(new StringType("Success"));
    }
}
