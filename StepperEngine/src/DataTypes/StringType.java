package DataTypes;

public class StringType extends DataType<String> {

    public StringType(String name, boolean isInput) {
        super(name, name.toLowerCase().replace('_',' '), true, Type.STRING, isInput);
    }

    public StringType(String str, String name, boolean isInput) {super(name, name.toLowerCase().replace('_',' '), true, str, Type.STRING, isInput);}

    @Override
    public String getPresentableString() {
        return data;
    }
}
