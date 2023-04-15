package Steps;

import DataTypes.StringType;

public class FileDumperStep extends  Step{

    private StringType content;
    private StringType fileName;

    public FileDumperStep(StringType content, StringType fileName){super("FILE_DUMPER", true);}
    @Override
    public void execute() {

    }

    @Override
    protected void runStepFlow() throws Exception {

    }
}
