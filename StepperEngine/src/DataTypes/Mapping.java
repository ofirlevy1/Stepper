package DataTypes;

public class Mapping {
    DataType car;
    DataType cdr;

    public Mapping(DataType car, DataType cdr){
        this.car=car;
        this.cdr=cdr;
    }

    public DataType getCar() {
        return car;
    }

    public DataType getCdr() {
        return cdr;
    }

    public void setCar(DataType car) {
        this.car = car;
    }

    public void setCdr(DataType cdr) {
        this.cdr = cdr;
    }
}


