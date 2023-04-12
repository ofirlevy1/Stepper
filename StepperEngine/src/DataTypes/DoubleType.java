package DataTypes;

public class DoubleType extends DataType<Double> {

    public DoubleType(Double num) {
        super("Double", "Double", true, num);
    }

    @Override
    public String getPresentableString() {
        return data.toString();
    }
}