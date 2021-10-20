package net;

public class Player {
    private String name;
    private int id;
    private String ipAddr;
    private int port;

    private Role role;
    private PlayerType type;
    private int score;

    public Player(String name, PlayerType type , int port, Role role, int score, int id) {
        this.name = name;
        this.type = type;
        this.port = port;
        this.role = role;
        this.score = score;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public int getPort() {
        return port;
    }

    public Role getRole() {
        return role;
    }

    public PlayerType getType() {
        return type;
    }

    public int getScore() {
        return score;
    }
}

