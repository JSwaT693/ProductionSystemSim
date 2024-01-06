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

    public World() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                board[i][j] = new Field();
            }
        }
    }

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
        if (machine == null) {
            return;
        }
        var x = machine.getX();
        var y = machine.getY();
        System.out.println("Move from: " + x + ", " + y);

        if (machine.getDirection() == Directions.RIGHT  && x >= 4) {
            machine.setDirection(Directions.LEFT);
        } else if (machine.getDirection() == Directions.LEFT && x <= 0) {
            machine.setDirection(Directions.RIGHT);
        } else if (machine.getDirection() == Directions.UP && y >= 4) {
            machine.setDirection(Directions.DOWN);
        } else if (machine.getDirection() == Directions.DOWN && y <= 0) {
            machine.setDirection(Directions.UP);
        }

        if (machine.getRole() == MachineType.SEEDER) {
            if (machine.getDirection() == Directions.RIGHT) {
                //Normalne poruszanie sie w prawo
                if (x < 4 && !board[x + 1][y].isMachine()) {
                    machine.setX(x + 1);
                    board[x][y].setMachine(null);
                    board[x + 1][y].setMachine(machine);
                    var message = "move," + board[x + 1][y].getPlants();
                    sendMessage(machine.getIp(), machine.getPort(), message);
                    System.out.println("Move to: " + machine.getX() + ", " + machine.getY());
                    return;
                }
            } else {
                //Normalne poruszanie sie w lewo
                if (x > 0 && !board[x - 1][y].isMachine()) {
                    machine.setX(x - 1);
                    board[x][y].setMachine(null);
                    board[x - 1][y].setMachine(machine);
                    var message = "move," + board[x - 1][y].getPlants();
                    sendMessage(machine.getIp(), machine.getPort(), message);
                    System.out.println("Move to: " + machine.getX() + ", " + machine.getY());
                    return;
                }
            }
        } else {
            //Analogicznie do Seedera tylko kierunki to gora i dol
            if (machine.getDirection() == Directions.UP) {
                if (y < 4 && !board[x][y + 1].isMachine()) {
                    machine.setY(y + 1);
                    board[x][y].setMachine(null);
                    board[x][y + 1].setMachine(machine);
                    var message = "move," + board[x][y + 1].getPlants();
                    sendMessage(machine.getIp(), machine.getPort(), message);
                    return;
                }
            } else {
                if (y > 0 && !board[x][y - 1].isMachine()) {
                    machine.setY(y - 1);
                    board[x][y].setMachine(null);
                    board[x][y - 1].setMachine(machine);
                    var message = "move," + board[x][y - 1].getPlants();
                    sendMessage(machine.getIp(), machine.getPort(), message);
                    return;
                }
            }
        }
        var message = "move," + board[x][y].getPlants();
        sendMessage(machine.getIp(), machine.getPort(), message);
    }

    public void seed(int id) {
        var seeder = getMachine(id);
        board[seeder.getX()][seeder.getY()].seedPlant();
        var message = "seed,";
        sendMessage(seeder.getIp(), seeder.getPort(), message);
    }

    public void harvest(int id) {
        var harvester = getMachine(id);
        board[harvester.getX()][harvester.getY()].harvestPlants();
        var message = "harvest,";
        sendMessage(harvester.getIp(), harvester.getPort(), message);
    }

    private synchronized void addToBoard(MachineModel machine) {
        var random = new Random();
        var x = -1;
        var y = -1;
        if (machine.getRole().equals(MachineType.SEEDER)) {
            var seedersY = machines.stream()
                    .filter(item -> item.getRole().equals(MachineType.SEEDER))
                    .map(MachineModel::getY)
                    .toList();
            do {
                y = random.nextInt(0, 5);
                x = random.nextInt(0, 5);
            } while (seedersY.contains(y) || board[x][y].isMachine());
            machine.setY(y);
            machine.setX(x);
            board[x][y].setMachine(machine);
        } else {
            var harvestersX = machines.stream()
                    .filter(item -> item.getRole().equals(MachineType.HARVESTER))
                    .map(MachineModel::getY)
                    .toList();
            do {
                y = random.nextInt(0, 5);
                x = random.nextInt(0, 5);
            } while (harvestersX.contains(x) || board[x][y].isMachine());
            machine.setY(y);
            machine.setX(x);
            board[x][y].setMachine(machine);
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
