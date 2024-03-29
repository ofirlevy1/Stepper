package Steps;

import DataTypes.*;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;


public class HTTPCallerStep extends Step {
    // inputs:
    private StringType resource;
    private StringType address;
    private EnumeratorType protocol;
    private EnumeratorType method;
    private JsonType requestBody;

    // outputs:
    private NumberType responseStatusCode;
    private StringType responseBody;

    private static double stepAvgDuration=0;
    private static int stepStartUpCount=0;

    public enum Protocols { http, https }
    public enum Methods {GET, PUT, POST, DELETE}

    public HTTPCallerStep() {
        super("HTTP Call", false);
        resource = new StringType("RESOURCE", true);
        resource.setMandatory(true);
        address = new StringType("ADDRESS", true);
        address.setMandatory(true);
        protocol = new EnumeratorType("PROTOCOL", true, Protocols.class);
        protocol.setMandatory(true);
        method = new EnumeratorType("METHOD", true, Methods.class);
        requestBody = new JsonType("BODY", true);
        responseStatusCode = new NumberType("CODE", false);
        responseBody = new StringType("RESPONSE_BODY", false);
    }


    @Override
    protected void updateStaticTimers() {
        stepStartUpCount += startUpCounter;
        stepAvgDuration =durationAvgInMs;
    }

    @Override
    protected void outerRunStepFlow() {
        try {
            runStepFlow();
        } catch (Exception e) {
            setStatusAndLog(Status.Failure, e.getMessage(), e.getMessage());
        }
    }

    @Override
    protected void runStepFlow() throws Exception {
        String url = protocol.getPresentableString() + "://" + address.getPresentableString() + resource.getData();

        Request request = new Request.Builder().url(url).method(method.getPresentableString(), requestBody.isDataSet() && !requestBody.getData().isEmpty() ? RequestBody.create(requestBody.getData().getBytes()) : null).build();
        OkHttpClient client = new OkHttpClient();

        addLog("About to invoke http request: " + protocol.getPresentableString() + " | " + method.getPresentableString() + " | " + address.getPresentableString() + " | " + resource.getPresentableString());
        Call call = client.newCall(request);
        Response response = call.execute();
        addLog("Received Response. Status code: " + response.code());

        responseStatusCode.setData(response.code());
        responseBody.setData(response.body().string());
        setStatus(Status.Success);
    }

    @Override
    public void setInputs(DataType... inputs) {
        for(DataType input : inputs) {
            setInputByName(input, input.getEffectiveName());
        }
    }

    @Override
    public void setInputByName(DataType input, String inputName) {
        switch(inputName) {
            case "RESOURCE":
                resource.setData((String) input.getData());
                break;
            case "ADDRESS":
                address.setData((String) input.getData());
                break;
            case "PROTOCOL":
                protocol.setData(input.getPresentableString());
                break;
            case "METHOD":
                method.setData(input.getPresentableString());
                break;
            case "BODY":
                requestBody.setData((String) input.getData());
                break;
            default:
                throw new RuntimeException("DataType '" + inputName + "' does not exist in step '" + getName() + "'");
        }
    }

    @Override
    public ArrayList<DataType> getOutputs(String... outputNames) {
        ArrayList<DataType> outputsArray=new ArrayList<>();
        for(String outputName: outputNames){
            if(this.responseStatusCode.getEffectiveName().equals(outputName))
                outputsArray.add(this.responseStatusCode);
            if(this.responseBody.getEffectiveName().equals(outputName))
                outputsArray.add(this.responseBody);
        }
        return outputsArray;
    }

    @Override
    public ArrayList<DataType> getAllData() {
        ArrayList<DataType> allData = new ArrayList<>();
        allData.add(resource);
        allData.add(address);
        allData.add(protocol);
        allData.add(method);
        allData.add(requestBody);
        allData.add(responseStatusCode);
        allData.add(responseBody);
        return allData;
    }

    @Override
    public void clearDataMembers() {
        for(DataType dataType : getAllData())
            dataType.eraseData();
    }

    public static int getStepStartUpCount() {
        return stepStartUpCount;
    }

    public static double getStepAvgDuration() {
        return stepAvgDuration;
    }
}
