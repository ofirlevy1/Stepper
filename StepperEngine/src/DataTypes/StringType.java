package DataTypes;

public class StringType extends DataType<String> {

    public StringType(String str) {super("String", "String", true, str);}

    public StringType(String str, String name) {super(name, name, true, str);}

    @Override
    public String getPresentableString() {
        return data;
    }
}
