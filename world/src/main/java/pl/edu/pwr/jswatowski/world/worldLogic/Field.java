package pl.edu.pwr.jswatowski.world.worldLogic;

import java.util.List;

public class Field {
    private MachineModel machine;
    private List<Integer> plants;

    public Field(MachineModel machine) {
        this.machine = machine;
    }

    void seedPlant() {

    }

    void harvestPlants() {

    }

    public MachineModel getMachine() {
        return machine;
    }

    public List<Integer> getPlants() {
        return plants;
    }
}
