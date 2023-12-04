package org.pokesplash.elgyms.gym;

/**
 * Config for positions of a gym leader, challenger or spectator.
 */
public class Position {
	private double x;
	private double y;
	private double z;
	private double yaw;
	private double pitch;

	public Position() {
		x = 0;
		y = 0;
		z = 0;
		yaw = 0;
		pitch = 0;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public double getYaw() {
		return yaw;
	}

	public double getPitch() {
		return pitch;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public void setYaw(double yaw) {
		this.yaw = yaw;
	}

	public void setPitch(double pitch) {
		this.pitch = pitch;
	}
}
