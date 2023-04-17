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

    public CsvExporterStep(RelationType source) {
        super("CSV Exporter", true);
        this.source = source;
        this.source.setMandatory(true);
        this.table = source.getData();
        this.resultString = "";
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
        addLog("About to process " + table.getRows() + " lines of data");
        addColumnNames();
        if(table.isEmpty())
            setStatusAndLog(Status.Warning, "CSV exporter was called with an empty table...","CSV exporter was called with an empty table...");
        else {
            addDataFromTable();
            setStatus(Status.Success);
        }
        outputs.add(new StringType(resultString));
    }

    @Override
    public void setInputs(ArrayList<DataType> inputs) {
        this.source=(RelationType) inputs.get(0);
        this.table=source.getData();
        this.source.setMandatory(true);
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