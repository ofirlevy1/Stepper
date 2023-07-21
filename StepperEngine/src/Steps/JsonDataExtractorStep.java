package Steps;

import DataTypes.DataType;
import DataTypes.JsonType;
import DataTypes.Relation;
import DataTypes.StringType;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.JsonPath;

import java.util.ArrayList;


public class JsonDataExtractorStep extends Step {

    private JsonType json;
    private StringType jsonPath;
    private StringType value;

    private static double stepAvgDuration=0;
    private static int stepStartUpCount=0;

    public JsonDataExtractorStep() {
        super("Json Data Extractor", true);
        json = new JsonType("JSON", true);
        json.setMandatory(true);
        jsonPath = new StringType("JSON_PATH", true);
        jsonPath.setMandatory(true);
        value = new StringType("VALUE", false);
    }

    @Override
    protected void updateStaticTimers() {
        stepStartUpCount= startUpCounter;
        stepAvgDuration=durationAvgInMs;
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
        try {
            if(!JsonPath.isPathDefinite(jsonPath.getData())) {
                ArrayList<String> pathValues = JsonPath.read(json.getData(), jsonPath.getData());
                for(String currentValue : pathValues) {
                    if(!value.isDataSet())
                        value.setData(currentValue);
                    else
                        value.setData(value.getData() + ", " + currentValue);
                }
            }
            else {
                String pathValue = JsonPath.read(json.getData(), jsonPath.getData()).toString();
                value.setData(pathValue);
            }
            setStatusAndLog(Status.Success, "Extracting data " + jsonPath.getData() + ". Value: " + value.getData(), "Extracting data " + jsonPath.getData() + ". Value: " + value.getData());
        }
        catch(Exception e) {
            setStatusAndLog(Status.Failure, "Error: " + e.getMessage(), e.getMessage());
        }
    }

    @Override
    public void setInputs(DataType... inputs) {
        for(DataType input: inputs){
            if(input.getEffectiveName().equals(json.getEffectiveName()))
                this.json.setData((String) input.getData());
            else if (input.getEffectiveName().equals(jsonPath.getEffectiveName()))
                this.jsonPath.setData((String) input.getData());
            else
                throw new RuntimeException("An attempt was made to set a non-existing input data type '" + input.getEffectiveName() + "'");
        }
    }

    @Override
    public void setInputByName(DataType input, String inputName) {
        if(inputName.equals(json.getEffectiveName()))
            this.json.setData((String) input.getData());
        else if (inputName.equals(jsonPath.getEffectiveName()))
            this.jsonPath.setData((String) input.getData());
        else
            throw new RuntimeException("An attempt was made to set a non-existing input data type '" + inputName + "'");
    }

    @Override
    public ArrayList<DataType> getOutputs(String... outputNames) {
        ArrayList<DataType> outputs = new ArrayList<>();
        outputs.add(json);
        outputs.add(jsonPath);
        return outputs;
    }

    @Override
    public ArrayList<DataType> getAllData() {
        ArrayList<DataType> allData = new ArrayList<>();
        allData.add(json);
        allData.add(jsonPath);
        allData.add(value);
        return allData;
    }

    @Override
    public void clearDataMembers() {
        jsonPath.eraseData();
        json.eraseData();
        value.eraseData();
    }
}
