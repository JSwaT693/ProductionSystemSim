package pl.edu.pwr.jswatowski.seeder.seederLogic;

import pl.edu.pwr.jswatowski.shared.MachineBase;
import pl.edu.pwr.jswatowski.shared.MachineType;

import java.io.*;


public class Seeder extends MachineBase {

    public Seeder(String worldIp, int worldPort, int serverPort) {
        super(worldIp, worldPort, serverPort);
    }

    protected void handleClient(String[] messageItems) throws IOException {
        if (messageItems[0].equals("seed")) {
            System.out.println("Seed complete");
        } else {
            super.handleClient(messageItems);
        }
    }

    @Override
    protected MachineType getRole() {
        return MachineType.SEEDER;
    }

    public void seed() {
        var message = "seed," + id;
        sendMessage(worldIp, worldPort, message);
    }
}
