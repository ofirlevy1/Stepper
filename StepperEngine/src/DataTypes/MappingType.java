package DataTypes;

public class MappingType extends DataType<Mapping>{

    public MappingType(Mapping mapping){super("Mapping", "Mapping", false, mapping);}
    @Override
    public String getPresentableString() {
        return "car: "+data.getCar()+"\ncdr: "+data.getCdr();
    }
}
