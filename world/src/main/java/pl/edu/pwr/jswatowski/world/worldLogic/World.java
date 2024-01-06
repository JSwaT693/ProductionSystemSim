package pl.edu.pwr.jswatowski.world.worldLogic;

import pl.edu.pwr.jswatowski.shared.MachineType;
import pl.edu.pwr.jswatowski.shared.SocketServerBase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World extends SocketServerBase {
    private final List<MachineModel> machines = new ArrayList<>();
    private int clientID = 0;
    private final Field[][] board = new Field[5][5];

    protected void handleClient(String[] messageItems) throws IOException {
        switch (messageItems[0]) {
            case "register" ->
                    register(messageItems[1], Integer.parseInt(messageItems[2]), MachineType.valueOf(messageItems[3].toUpperCase()));
            case "unregister" -> unregister(Integer.parseInt(messageItems[1]));
            case "move" -> move(Integer.parseInt(messageItems[1]));
            case "seed" -> seed(Integer.parseInt(messageItems[1]));
            case "harvest" -> harvest(Integer.parseInt(messageItems[1]));
        }
    }

    public static void main( String[] args ) throws IOException {
        World world = new World();
        world.startServer(5000);
    }

    public void register(String host, int port, MachineType role) throws IOException {
        var machine = new MachineModel(++clientID, host, port, role);
        machines.add(machine);
        var message = "register," + machine.getId();
        sendMessage(host, port, message);
        addToBoard(machine);

        System.out.println("Registered " + role + " from: " + host + ":" + port);
    }

    public void unregister(int id) throws IOException {
        var machine = getMachine(id);
        if (machine != null) {
            machines.remove(machine);
            var message = "unregister";
            sendMessage(machine.getIp(), machine.getPort(), message);
        }
    }

    public synchronized void move(int id) {
        var machine = getMachine(id);
        if (machine != null) {
            var x = machine.getX();
            var y = machine.getY();
            if (machine.getRole() == MachineType.SEEDER) {
                if (machine.getDirection() == Directions.RIGHT) {
                    if (x < 4 && board[x + 1][y] == null) {
                        board[x][y] = null;
                        board[x + 1][y] = new Field(machine);
                        var message = "move," + board[x + 1][y].getPlants();
                        sendMessage(machine.getIp(), machine.getPort(), message);
                    } else if (x == 4 && board[x - 1][y] == null) {
                        board[x][y] = null;
                        board[x - 1][y] = new Field(machine);
                        var message = "move," + board[x - 1][y].getPlants();
                        sendMessage(machine.getIp(), machine.getPort(), message);
                    } else {
                        var message = "move," + board[x][y].getPlants();
                        sendMessage(machine.getIp(), machine.getPort(), message);
                    }
                } else {


                }
            } else {



            }
        }
    }

    public void seed(int id) {
        var seeder = getMachine(id);

    }

    public void harvest(int id) {
        var harvester = getMachine(id);

    }

    private synchronized void addToBoard(MachineModel machine) {
        var random = new Random();
        if (machine.getRole().equals(MachineType.SEEDER)) {
            var seedersY = machines.stream()
                    .filter(item -> item.getRole().equals(MachineType.SEEDER))
                    .map(MachineModel::getY)
                    .toList();
            do {
                machine.setY(random.nextInt(0, 5));
                machine.setX(random.nextInt(0, 5));
            } while (seedersY.contains(machine.getY()) || board[machine.getX()][machine.getY()] != null);
            board[machine.getX()][machine.getY()] = new Field(machine);
        } else {
            var harvestersX = machines.stream()
                    .filter(item -> item.getRole().equals(MachineType.HARVESTER))
                    .map(MachineModel::getY)
                    .toList();
            do {
                machine.setY(random.nextInt(0, 5));
                machine.setX(random.nextInt(0, 5));
            } while (harvestersX.contains(machine.getX()) || board[machine.getX()][machine.getY()] != null);
            board[machine.getX()][machine.getY()] = new Field(machine);
        }
        System.out.println("New " + machine.getRole() + " registered on field x = " + machine.getX() + " y = " + machine.getY());
        System.out.println(board[machine.getX()][machine.getY()]);
    }

    private MachineModel getMachine(int id) {
        return machines
                .stream()
                .filter(item -> id == item.getId())
                .findAny()
                .orElse(null);
    }
}
