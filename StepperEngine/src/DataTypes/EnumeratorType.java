package DataTypes;

public class EnumeratorType extends DataType<Enum> implements UserFriendly {
    Class enumClass;
    public EnumeratorType(String name, boolean isInput, Class enumClass) {
        super(name, name.toLowerCase().replace('_',' '), Type.ENUM, isInput);
        this.enumClass=enumClass;
    }

    public EnumeratorType(Enum enumString, String name, boolean isInput, Class enumClass){
        super(name, name.toLowerCase().replace('_',' '), enumString, Type.ENUM, isInput);
        this.enumClass=enumClass;
    }

    @Override
    public String getPresentableString() {
        return getData().toString();
    }

    @Override
    public void setData(String dataStr) {setData(Enum.valueOf(enumClass,dataStr));}
}
