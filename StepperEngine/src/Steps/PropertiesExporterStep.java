package Steps;

import DataTypes.*;

import java.util.ArrayList;

public class PropertiesExporterStep extends Step{
    private RelationType source;
    private StringType result;
    private static double stepAvgDuration=0;
    private static int stepStartUpCount=0;

    public PropertiesExporterStep(){
        super("Properties Exporter", true);
        this.result=new StringType(new String(), StepOutputNameEnum.RESULT.toString(), false);

        this.source = new RelationType(StepInputNameEnum.SOURCE.toString(), true);
        this.source.setMandatory(true);
    }

    public PropertiesExporterStep(RelationType source) {
        this();
        this.source = source;
        this.source.setMandatory(true);
    }

    @Override
    protected void outerRunStepFlow(){
        try{
            this.runStepFlow();
        } catch (EmptyPropertiesRelationException e) {
            this.result.setData("");//=new StringType("", StepOutputNameEnum.RESULT.toString(), false);
            this.setStatus(Status.Warning);
            this.setSummaryLine(e.getMessage());
            this.addLog(e.getMessage());
        } catch (Exception e) {
            this.setSummaryLine(e.getMessage());
            this.addLog(e.getMessage());
            this.setStatus(Status.Failure);
        }
    }

    @Override
    protected void runStepFlow() throws Exception {
        Relation relation=(Relation) source.getData();
        String properties="";
        this.addLog("About to process "+relation.getRows()+" lines of data");
        if(relation.getRows()==0&&relation.getCols()==0||source.getData()==null) throw new EmptyPropertiesRelationException("Properties Relation is empty");
        for(int i=0;i<relation.getRows();i++){
            for(int j=0;j< relation.getCols();j++){
                properties+="row-"+(i+1)+"."+relation.getColumnNames()[j]+"="+relation.get(i,j)+" ";
            }
            properties+="\n";
        }

        this.setStatus(Status.Success);
        this.addLog("Extracted total of "+(relation.getRows()* relation.getCols())+" properties");
        this.setSummaryLine("Extracted total of "+(relation.getRows()* relation.getCols())+" properties");
        this.result.setData(properties.substring(0,properties.length()-1));//=new StringType(properties.substring(0,properties.length()-1), StepOutputNameEnum.RESULT.toString(), false);//getting rid of unnecessary new line at end of string
    }

    @Override
    public void setInputs(DataType... inputs) {
        for(DataType input: inputs){
            if(input.getEffectiveName().equals(source.getEffectiveName())) {
                this.source.setData((Relation) input.getData());
                this.source.setMandatory(true);
            }
        }
    }

    @Override
    public void setInputByName(DataType input, String inputName) {
        if(inputName.equals(source.getEffectiveName()))
            this.source.setData((Relation) input.getData());
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

    public class EmptyPropertiesRelationException extends Exception{
        public EmptyPropertiesRelationException(String str){
            super(str);
        }
    }

    @Override
    protected void updateStaticTimers() {
        stepStartUpCount= startUpCounter;
        stepAvgDuration=durationAvgInMs;
    }

    public static int getStepStartUpCount() {
        return stepStartUpCount;
    }

    public static double getStepAvgDuration() {
        return stepAvgDuration;
    }

    public void clearDataMembers(){
        this.result.setData(null);
        this.source.setData(null);
    }
}
