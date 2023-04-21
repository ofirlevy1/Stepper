package DataTypes;

public class DoubleType extends DataType<Double> {
    public DoubleType(String name) {
        super(name, "Double", true, Type.DOUBLE);
    }
    public DoubleType(Double num) {
        super("Double", "Double", true, num, Type.DOUBLE);
    }

    public DoubleType(Double num, String name){super(name, name, true, num, Type.DOUBLE);}

    @Override
    public String getPresentableString() {
        return data.toString();
    }
}