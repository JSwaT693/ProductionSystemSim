package pl.edu.pwr.jswatowski.world.worldLogic;

import java.util.ArrayList;
import java.util.List;

public class Field {
    private MachineModel machine = null;
    private final List<Integer> plants = new ArrayList<>();

    void seedPlant() {
        plants.add(1);
    }

    void harvestPlants() {
        plants.clear();
    }

    public MachineModel getMachine() {
        return machine;
    }

    public List<Integer> getPlants() {
        return plants;
    }

    public void setMachine(MachineModel machine) {
        this.machine = machine;
    }

    public boolean isMachine() {
        return machine != null;
    }

    public void grow() {
        plants.replaceAll(plant -> plant + 1);
    }
}
