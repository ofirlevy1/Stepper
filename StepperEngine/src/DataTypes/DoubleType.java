package DataTypes;

public class DoubleType extends DataType<Double> {
    public DoubleType(String name) {
        super(name, "Double", true);
    }
    public DoubleType(Double num) {
        super("Double", "Double", true, num);
    }

    public DoubleType(Double num, String name){super(name, name, true, num);}

    @Override
    public String getPresentableString() {
        return data.toString();
    }
}