package at.renehollander.placeplayground.util;

import org.joml.Matrix4d;
import org.joml.Vector3d;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Camera {

    private Vector3d position;
    private double pitch;
    private double yaw;

    private Matrix4d viewMatrix;

    public Camera(Vector3d position, double pitch, double yaw) {
        this.position = position;
        this.pitch = pitch;
        this.yaw = yaw;

        this.viewMatrix = new Matrix4d();
    }

    public Camera(Vector3d position) {
        this(position, 0, 0);
    }

    public Camera() {
        this(new Vector3d(), 0, 0);
    }

    public void forwards(double distance) {
        moveFromLook(0, 0, -distance);
    }

    public void backwards(double distance) {
        moveFromLook(0, 0, distance);
    }

    public void left(double distance) {
        moveFromLook(-distance, 0, 0);
    }

    public void right(double distance) {
        moveFromLook(distance, 0, 0);
    }

    public void up(double distance) {
        position.y += distance;
    }

    public void down(double distance) {
        position.y -= distance;
    }

    public void pitch(double angle) {
        if (angle != 0) {
            if (pitch + angle >= -Mathd.HALF_PI && pitch + angle <= Mathd.HALF_PI) {
                pitch += angle;
            } else if (pitch + angle < -Mathd.HALF_PI) {
                pitch = -Mathd.HALF_PI;
            } else if (pitch + angle > Mathd.HALF_PI) {
                pitch = Mathd.HALF_PI;
            }
        }
    }

    public void yaw(double angle) {
        if (angle != 0) {
            if (yaw + angle >= Mathd.TWO_PI) {
                yaw = yaw + angle - Mathd.TWO_PI;
            } else if (yaw + angle < 0) {
                yaw = Mathd.TWO_PI - yaw + angle;
            } else {
                yaw += angle;
            }
        }
    }

    public Matrix4d getViewMatrix() {
        viewMatrix.identity();
        viewMatrix.rotate(pitch, 1, 0, 0);
        viewMatrix.rotate(yaw, 0, 1, 0);
        viewMatrix.translate(-position.x, -position.y, -position.z);
        return viewMatrix;
    }

    public Vector3d getPosition() {
        return position;
    }

    public double getPitch() {
        return pitch;
    }

    public double getYaw() {
        return yaw;
    }

    @Override
    public String toString() {
        return "Camera{" +
                "position=" + position +
                ", pitch=" + pitch +
                ", yaw=" + yaw +
                '}';
    }

    public void moveFromLook(double dx, double dy, double dz) {
        position.z += dx * cos(yaw - Mathd.HALF_PI) + dz * cos(yaw);
        position.x -= dx * sin(yaw - Mathd.HALF_PI) + dz * sin(yaw);
        position.y += dy * sin(pitch - Mathd.HALF_PI) + dz * sin(pitch);
    }
}
