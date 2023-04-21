package DataTypes;

public class StringType extends DataType<String> {

    public StringType(String name) {
        super(name, "String", true, Type.STRING);
    }

    public StringType(String str, String name) {super(name, name, true, str, Type.STRING);}

    @Override
    public String getPresentableString() {
        return data;
    }
}
