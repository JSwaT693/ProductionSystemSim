package pl.edu.pwr.jswatowski.world.worldLogic;

import pl.edu.pwr.jswatowski.shared.MachineType;

public class MachineModel {
    private final int id;
    private final String ip;
    private final int port;
    private final MachineType role;
    private int x;
    private int y;
    private Directions direction;

    public MachineModel(int id, String ip, int port, MachineType role) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.role = role;
        if (role == MachineType.SEEDER) {
            direction = Directions.DOWN;
        } else {
            direction = Directions.RIGHT;
        }
    }

    public int getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public MachineType getRole() {
        return role;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Directions getDirection() {
        return direction;
    }

    public void setDirection(Directions direction) {
        this.direction = direction;
    }
}
