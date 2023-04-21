package Steps;

import DataTypes.DataType;
import DataTypes.Relation;
import DataTypes.RelationType;
import DataTypes.StringType;

import java.util.ArrayList;

public class CsvExporterStep extends Step {
    private RelationType source;
    private Relation table;
    private String resultString;
    private StringType result;

    public CsvExporterStep(){
        super("CSV Exporter", true);
        this.result=new StringType(new String(), StepOutputNameEnum.RESULT.toString(), false);
        this.source = new RelationType(StepInputNameEnum.SOURCE.toString(), true);
    }

    public CsvExporterStep(RelationType source) {
        this();
        this.source = source;
        this.source.setMandatory(true);
        this.table = source.getData();
    }

    @Override
    public void execute() {
        try {
            runStepFlow();
        } catch (Exception e) {
            setStatusAndLog(Status.Failure, e.getMessage(), e.getMessage());
            this.result=new StringType("Failure", StepOutputNameEnum.RESULT.toString(), false);
        }
    }

    @Override
    protected void runStepFlow() throws Exception {
        addLog("About to process " + table.getRows() + " lines of data");
        addColumnNames();
        if(table.isEmpty())
            setStatusAndLog(Status.Warning, "CSV exporter was called with an empty table...","CSV exporter was called with an empty table...");
        else {
            addDataFromTable();
            setStatus(Status.Success);
        }
        this.result=new StringType(resultString, StepOutputNameEnum.RESULT.toString(), false);
    }

    @Override
    public void setInputs(DataType... inputs) {
        for(DataType input: inputs){
            if(input.getEffectiveName().equals(StepInputNameEnum.SOURCE.toString())) {
                this.source = (RelationType) input;
                this.source.setMandatory(true);
                this.table=(Relation) this.source.getData();
            }
        }
    }

    @Override
    public ArrayList<DataType> getOutputs(String... outputNames) {
        ArrayList<DataType> outputsArray=new ArrayList<>();
        for(String outputName: outputNames){
            if(this.result.getEffectiveName().equals(outputName))
                outputsArray.add(this.result);
        }
        return outputsArray;
    }

    @Override
    public ArrayList<DataType> getAllData() {
        ArrayList<DataType> allData=new ArrayList<>();
        allData.add(this.result);
        allData.add(this.source);
        return  allData;
    }

    private void addColumnNames() {
        for(int i = 0; i < table.getCols() - 1; i++) {
            resultString += table.getColumnNames()[i] + ", ";
        }
        resultString += table.getColumnNames()[table.getCols() - 1];
        resultString += System.lineSeparator();
    }

    private void addDataFromTable() {
        for(int i = 0; i < table.getRows(); i++) {
            addRowToCSVResultString(i);
        }
    }

    public void addRowToCSVResultString(int rowNumber) {
        for(int i = 0; i < table.getCols() - 1; i++) {
            resultString += table.get(rowNumber, i) + ", ";
        }
        resultString += table.get(rowNumber, table.getCols() - 1);
        resultString += System.lineSeparator();
    }

}
