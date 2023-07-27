package Steps;

import DataTypes.DataType;
import DataTypes.JsonType;
import DataTypes.Relation;
import DataTypes.StringType;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;


public class ToJsonStep extends Step {

    private StringType content;
    private JsonType json;

    private static double stepAvgDuration=0;
    private static int stepStartUpCount=0;

    public ToJsonStep() {
        super("To Json", true);
        this.content = new StringType("CONTENT", true);
        content.setMandatory(true);
        this.json = new JsonType("JSON", false);
    }

    @Override
    protected void updateStaticTimers() {
        stepStartUpCount += startUpCounter;
        //stepAvgDuration=stepAvgDuration+((durationAvgInMs-stepAvgDuration)/ stepStartUpCount);
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
        JsonElement jsonElement = null;
        try {
            jsonElement = JsonParser.parseString(content.getData());
        }
        catch(Exception e)
        {
            setStatusAndLog(Status.Failure,"Content is not a valid JSON representation","Content is not a valid JSON representation");
            return;
        }
        addLog("Content is JSON string. Converting it to json...");
        json.setData(new Gson().toJson(jsonElement));
        setStatus(Status.Success);
    }

    @Override
    public void setInputs(DataType... inputs) {
        for(DataType input: inputs){
            if(input.getEffectiveName().equals(content.getEffectiveName()))
                this.content.setData((String) input.getData());
            else
                throw new RuntimeException("An attempt was made to set a non-existing input data type '" + input.getEffectiveName() + "'");
        }
    }

    @Override
    public void setInputByName(DataType input, String inputName) {
        if(inputName.equals(content.getEffectiveName()))
            content.setData(input.getPresentableString());
        else
            throw new RuntimeException("An attempt was made to set a non-existing input data type '" + inputName + "'");
    }

    @Override
    public ArrayList<DataType> getOutputs(String... outputNames) {
        ArrayList<DataType> outputsArray=new ArrayList<>();
        for(String outputName: outputNames){
            if(this.json.getEffectiveName().equals(outputName))
                outputsArray.add(this.json);
        }
        return outputsArray;
    }

    @Override
    public ArrayList<DataType> getAllData() {
        ArrayList<DataType> allData = new ArrayList<>();
        allData.add(content);
        allData.add(json);
        return allData;
    }

    @Override
    public void clearDataMembers() {
        content.eraseData();
        json.eraseData();
    }

    public static int getStepStartUpCount() {
        return stepStartUpCount;
    }

    public static double getStepAvgDuration() {
        return stepAvgDuration;
    }
}
