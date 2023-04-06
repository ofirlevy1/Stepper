package DataTypes;

public class DoubleType extends StepDataType<Double> {

    public DoubleType(Double num) {
        super("Double", "Double", true, num);
    }

    @Override
    public String getPresentableString() {
        return data.toString();
    }
}