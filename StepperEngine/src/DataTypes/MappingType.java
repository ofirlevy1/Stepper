package DataTypes;

public class MappingType extends DataType<Mapping>{

    public MappingType(String name, boolean isInput) {
        super(name, name.toLowerCase().replace('_',' '), Type.MAPPING, isInput);
    }

    public MappingType(Mapping mapping, boolean isInput){super("Mapping", "Mapping", mapping, Type.MAPPING, isInput);}
    public MappingType(Mapping mapping, String name, boolean isInput){super(name, name.toLowerCase().replace('_',' '), mapping, Type.MAPPING, isInput);}
    @Override
    public String getPresentableString() {
        return "car: "+getData().getCar().getData()+"\ncdr: "+getData().getCdr().getData();
    }
}
