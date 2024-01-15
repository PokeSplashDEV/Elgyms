package org.pokesplash.elgyms.gym;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * Config for positions of a gym leader, challenger or spectator.
 */
public class Position {
	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;
	private Identifier world;

	public Position() {
		x = 0;
		y = 0;
		z = 0;
		yaw = 0;
		pitch = 0;
		world = null;
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

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
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

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public Identifier getWorld() {
		return world;
	}

	public void setWorld(Identifier world) {
		this.world = world;
	}
}
