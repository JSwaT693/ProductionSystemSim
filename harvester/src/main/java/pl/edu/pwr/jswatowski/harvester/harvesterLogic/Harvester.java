package pl.edu.pwr.jswatowski.harvester.harvesterLogic;

import pl.edu.pwr.jswatowski.shared.MachineBase;
import pl.edu.pwr.jswatowski.shared.MachineType;

import java.io.IOException;

public class Harvester extends MachineBase {
    public Harvester(String worldIp, int worldPort, int serverPort) {
        super(worldIp, worldPort, serverPort);
    }

    protected void handleClient(String[] messageItems) throws IOException {
        if (messageItems[0].equals("harvest")) {
            System.out.println("Harvest complete");
        } else {
            super.handleClient(messageItems);
        }
    }

    @Override
    protected MachineType getRole() {
        return MachineType.HARVESTER;
    }

    public void harvest() {
        var message = "harvest," + id;
        sendMessage(worldIp, worldPort, message);
    }
}
