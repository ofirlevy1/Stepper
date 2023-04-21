package DataTypes;

public class NumberType extends DataType<Integer> {

    public NumberType(String name) {
        super(name, name.toLowerCase().replace('_',' '), true, Type.NUMBER);
    }
    public NumberType(Integer num) {
        super("Number", "Number", true, num, Type.NUMBER);
    }

    public NumberType(Integer num, String name) {
        super(name, name.toLowerCase().replace('_',' '), true, num, Type.NUMBER);
    }

    @Override
    public String getPresentableString() {
        return data.toString();
    }
}