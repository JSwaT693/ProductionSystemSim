package pl.edu.pwr.jswatowski.world.worldLogic;

import pl.edu.pwr.jswatowski.shared.MachineType;
import pl.edu.pwr.jswatowski.shared.SocketServerBase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class World extends SocketServerBase {
    private final List<MachineModel> machines = new ArrayList<>();
    private int clientID = 0;
    private final Field[][] board = new Field[5][5];

    public World() throws IOException {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                board[i][j] = new Field();
            }
        }
        startServer(5000);
        startGrowingProcess();
    }

    protected void handleClient(String[] messageItems) {
        switch (messageItems[0]) {
            case "register" ->
                    register(messageItems[1], Integer.parseInt(messageItems[2]), MachineType.valueOf(messageItems[3].toUpperCase()));
            case "unregister" -> unregister(Integer.parseInt(messageItems[1]));
            case "move" -> move(Integer.parseInt(messageItems[1]));
            case "seed" -> seed(Integer.parseInt(messageItems[1]));
            case "harvest" -> harvest(Integer.parseInt(messageItems[1]));
        }
    }

    public void register(String host, int port, MachineType role) {
        var machine = new MachineModel(++clientID, host, port, role);
        machines.add(machine);
        var message = "register," + machine.getId();
        sendMessage(host, port, message);
        addToBoard(machine);

        System.out.println("Registered " + role + " from: " + host + ":" + port);
    }

    public synchronized void unregister(int id) {
        var machine = getMachine(id);
        if (machine != null) {
            machines.remove(machine);
            board[machine.getX()][machine.getY()].setMachine(null);
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
                    moveMachine(machine, x + 1, y);
                    return;
                }
            } else {
                //Normalne poruszanie sie w lewo
                if (x > 0 && !board[x - 1][y].isMachine()) {
                    moveMachine(machine, x - 1, y);
                    return;
                }
            }
        } else {
            //Analogicznie do Seedera tylko kierunki to gora i dol
            if (machine.getDirection() == Directions.UP) {
                if (y < 4 && !board[x][y + 1].isMachine()) {
                    moveMachine(machine, x, y + 1);
                    return;
                }
            } else {
                if (y > 0 && !board[x][y - 1].isMachine()) {
                    moveMachine(machine, x, y - 1);
                    return;
                }
            }
        }
        var message = "move," + board[x][y].getPlants();
        sendMessage(machine.getIp(), machine.getPort(), message);
    }

    private synchronized void moveMachine(MachineModel machine, int newX, int newY) {
        var x = machine.getX();
        var y = machine.getY();
        machine.setX(newX);
        machine.setY(newY);
        board[x][y].setMachine(null);
        board[newX][newY].setMachine(machine);
        var message = "move," + board[newX][newY].getPlants();
        sendMessage(machine.getIp(), machine.getPort(), message);
        System.out.println("Move to: " + newX + ", " + newY);
    }

    public synchronized void seed(int id) {
        var seeder = getMachine(id);
        board[seeder.getX()][seeder.getY()].seedPlant();
        var message = "seed,";
        sendMessage(seeder.getIp(), seeder.getPort(), message);
    }

    public synchronized void harvest(int id) {
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
            var seeders = machines.stream()
                    .filter(item -> item.getRole().equals((MachineType.SEEDER)))
                    .toList();
            var seedersY = new ArrayList<Integer>();
            for (int i = 0; i < seeders.size() - 1; i++) {
                seedersY.add(seeders.get(i).getY());
            }
            do {
                y = random.nextInt(0, 5);
                x = random.nextInt(0, 5);
            } while (seedersY.contains(y) || board[x][y].isMachine());
            machine.setY(y);
            machine.setX(x);
            board[x][y].setMachine(machine);
        } else {
            var harvesters = machines.stream()
                    .filter(item -> item.getRole().equals((MachineType.HARVESTER)))
                    .toList();
            var harvestersX = new ArrayList<Integer>();
            for (int i = 0; i < harvesters.size() - 1; i++) {
                harvestersX.add(harvesters.get(i).getX());
            }
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

    public void startGrowingProcess() {
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override  public synchronized void run() {
                for (Field[] fields : board) {
                    for (Field field : fields) {
                        field.grow();
                    }
                }
            }
        },0, 5, TimeUnit.SECONDS);
    }

    public Field[][] getBoard() {
        return board;
    }
}

