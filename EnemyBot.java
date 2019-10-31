package pruebasRoboticas;

public class EnemyBot {
	public String nombre;
	public double energy, velocity, distance, bearing, x , y;
	
	public EnemyBot(String nombre, double energy, double velocity, double distance, double bearing, double x, double y) {
		this.nombre = nombre;
		this.energy = energy;
		this.velocity = velocity;
		this.bearing = bearing;
		this.distance = distance;
		this.x = x;
		this.y = y;
	}
	
	public void update(double energy, double velocity, double distance, double bearing, double x, double y) {
		this.energy = energy;
		this.velocity = velocity;
		this.bearing = bearing;
		this.distance = distance;
		this.x = x;
		this.y = y;
	}

		@Override
		public String toString() {
			return "nombre " + nombre + ", coordenadas " + '(' + x + ',' + y + ')' + ", energía " + energy + ", distancia " + distance + 
					", velocidad " + velocity + ", direccion " + bearing ;
		}
}
