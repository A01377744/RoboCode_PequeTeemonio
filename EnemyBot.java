package pruebasRoboticas;

import robocode.ScannedRobotEvent;

public class EnemyBot {
	public String nombre;
	public double energy, velocity, distance, bearing, x , y;
	public ScannedRobotEvent event;
	public boolean alive;
	
	public EnemyBot(ScannedRobotEvent event, String nombre, double energy, double velocity, double distance, double bearing, double x, double y) {
		this.event = event;
		this.nombre = nombre;
		this.energy = energy;
		this.velocity = velocity;
		this.bearing = bearing;
		this.distance = distance;
		this.x = x;
		this.y = y;
		this.alive = true;
	}
	
	public void update(ScannedRobotEvent event, double energy, double velocity, double distance, double bearing, double x, double y) {
		this.event = event;
		this.energy = energy;
		this.velocity = velocity;
		this.bearing = bearing;
		this.distance = distance;
		this.x = x;
		this.y = y;
	}

		@Override
		public String toString() {
			return "nombre " + nombre + ", alive " + alive+ ", coordenadas " + '(' + x + ',' + y + ')' + ", energía " 
		+ energy + ", distancia " + distance + ", velocidad " + velocity + ", direccion " + bearing ;
		}
}
