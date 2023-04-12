package DataTypes;

public class Mapping {
    StepDataType car;
    StepDataType cdr;

    public Mapping(StepDataType car, StepDataType cdr){
        this.car=car;
        this.cdr=cdr;
    }

    public StepDataType getCar() {
        return car;
    }

    public StepDataType getCdr() {
        return cdr;
    }

    public void setCar(StepDataType car) {
        this.car = car;
    }

    public void setCdr(StepDataType cdr) {
        this.cdr = cdr;
    }
}


