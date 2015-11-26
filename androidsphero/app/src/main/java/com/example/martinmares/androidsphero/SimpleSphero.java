package com.example.martinmares.androidsphero;

import com.orbotix.Sphero;

/**
 */
public class SimpleSphero {

    private static final int[][] DEGREE = new int[][] { {315, 0, 45},
                                                        {270, -1, 90},
                                                        {225, 180,135} };

    private Sphero sphero;
    private int velocity = 2;
    private final RingLog log;

    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;

    public SimpleSphero(RingLog log) {
        this.log = log;
    }

    private synchronized void driveSphero() {
        if (sphero == null) {
            return;
        }
        
        int vert = 0;
        int hor = 0;
        if (up) {
            vert++;
        }
        if (down) {
            vert--;
        }
        if (right) {
            hor--;
        }
        if (left) {
            hor++;
        }
        int heading = DEGREE[vert+1][hor+1];
        if (heading >= 0 && velocity > 0) {
            log.log("Direction: " + heading);
            sphero.drive(heading, ((float) velocity) / 10);
        } else {
            log.log("Direction stop");
            sphero.stop();
        }
    }

    public Sphero getSphero() {
        return sphero;
    }

    public void setSphero(Sphero sphero) {
        if (sphero == null) {
            log.log("REMOVE SPHERO!");
        } else {
            log.log("NEW SPHERO: " + sphero.getRobot().getName());
        }
        this.sphero = sphero;
        driveSphero();
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        if (velocity < 0) {
            velocity = 0;
        } else if (velocity > 10) {
            velocity = 10;
        }
        log.log("VELO: " + velocity + "0%");
        this.velocity = velocity;
    }

    public boolean isUp() {
        return up;
    }

    public synchronized void setUp(boolean up) {
        log.log("UP " + (up ? "+" : "-"));
        this.up = up;
        driveSphero();
    }

    public boolean isDown() {
        return down;
    }

    public synchronized void setDown(boolean down) {
        log.log("DOWN " + (down ? "+" : "-"));
        this.down = down;
        driveSphero();
    }

    public boolean isLeft() {
        return left;
    }

    public synchronized void setLeft(boolean left) {
        log.log("LEFT " + (left ? "+" : "-"));
        this.left = left;
        driveSphero();
    }

    public boolean isRight() {
        return right;
    }

    public synchronized void setRight(boolean right) {
        log.log("RIGHT " + (right ? "+" : "-"));
        this.right = right;
        driveSphero();
    }

    public void disconnect() {
        log.log("Disconnect sphero");
        if (sphero != null) {
            sphero.disconnect();
        }
    }

    public synchronized void resetDrive() {
        log.log("Reset drive");
        up = false;
        down = false;
        left = false;
        right = false;
        driveSphero();
    }
}
