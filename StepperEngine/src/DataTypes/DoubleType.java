package DataTypes;

public class DoubleType extends DataType<Double> {
    public DoubleType(String name, boolean isInput) {
        super(name, name.toLowerCase().replace('_',' '), true, Type.DOUBLE, isInput);
    }
    public DoubleType(Double num, boolean isInput) {
        super("Double", "Double", true, num, Type.DOUBLE, isInput);
    }

    public DoubleType(Double num, String name, boolean isInput){super(name, name.toLowerCase().replace('_',' '), true, num, Type.DOUBLE, isInput);}

    @Override
    public String getPresentableString() {
        return data.toString();
    }
}