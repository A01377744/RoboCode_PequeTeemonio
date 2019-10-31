package pruebasRoboticas;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.ScannedRobotEvent;
import java.util.Random;


public class teemoneitor extends AdvancedRobot {
	private Estado estado;
	public static final int BORDE = 70;
	private int giro= 180;
	private double giroRadar;
	private double giroCanon;
	private double giroRobot;
	private double potencia;
	private ScannedRobotEvent eventoEnemigo;
	private EnemyBot[] enemy;
	private int nextEnemy ;

	
	private enum Estado{
		RASTREANDO,
		ACERCANDOSE_DISPARANDO,
		RODEANDO_DISPARANDO,
		HUYENDO,
		EJECUTANDO,
		ESQUIVANDO, 
		POTENCIANDO
		
	}
	
	public void run(){
		Random random = new Random();
		setAdjustRadarForGunTurn(true); 
		setAdjustGunForRobotTurn(true);
		inicializar();
		
		while(true) {
			//out.println(estado);
			Color rojo = new Color(200+random.nextInt(50), 100+random.nextInt(100), 10+random.nextInt(10));
			setColors(rojo, Color.yellow, rojo);
			
			switch(estado) {
			case RASTREANDO:
				setTurnRadarRightRadians(99999);
				setTurnGunRightRadians(99999);
				setTurnRight(99999);
				setAhead(60);
				turnLeft(20);
				
				if(getX()<BORDE){
					if(getHeading()>270) {
						turnRight(giro);
						turnLeft(0);
					}else{
						turnLeft(giro);
						turnRight(0);

					}					

				}if(getX()>getBattleFieldWidth()-BORDE) {
					if(getHeading()<giro) {
						turnLeft(giro);
						turnRight(0);
					}else {
						turnRight(giro);
						turnLeft(0);
					}
					
				}if(getY()<BORDE) {
					if(getHeading()>180) {
						turnRight(giro);
						turnLeft(0);
					}else {
						turnLeft(giro);
						turnRight(0);
					}

				}if(getY()>getBattleFieldHeight()-BORDE){
					if(getHeading()>270) {
						turnLeft(giro);
						turnRight(0);
					}else {
						turnRight(giro);	
						turnLeft(0);
					}

				}
				execute();
				break;
			case ACERCANDOSE_DISPARANDO:
				setTurnRadarRight(giroRadar);
			    setTurnGunRight(giroCanon);
			    setTurnRight(giroRobot);
				setFire(potencia);
				setAhead(200);
				execute();
				if(eventoEnemigo.getDistance()<100) {
					estado = Estado.RODEANDO_DISPARANDO;
				}
				break;
			case RODEANDO_DISPARANDO:
				setTurnRadarRight(giroRadar);
			    setTurnGunRight(giroCanon);
			    turnRight(getRadarHeading()+90); 
			    setBack(100);
			    setFire(potencia);
			    execute();
				break;
			default:
				doNothing();
			}
			
			
			
		}
	}
	
	public void inicializar() {
		estado = Estado.RASTREANDO;
		potencia = 1;
		enemy = new EnemyBot[7];
		nextEnemy=0;

	}
	
	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		double angulo = this.getHeading() + event.getBearing();
		double xEnemigo = getX() + event.getDistance() * Math.sin(Math.toRadians(angulo));
		double yEnemigo = getY() + event.getDistance() * Math.cos(Math.toRadians(angulo));
		
		int x = includes(event.getName());
		if(x<0) {
			enemy[nextEnemy] = new EnemyBot(event.getName(), event.getEnergy(), event.getVelocity(), event.getDistance(), event.getBearing(),
					xEnemigo, yEnemigo);
			nextEnemy++;
		}else {
			enemy[x].update(event.getEnergy(), event.getVelocity(), event.getDistance(), event.getBearing(), xEnemigo, yEnemigo);
		}
		out.println(enemy[x]);
		
		eventoEnemigo = event;
		this.giroRadar = event.getBearing() + getHeading() - getRadarHeading();
		this.giroCanon = event.getBearing() + getHeading() - getGunHeading();
		this.giroRobot = event.getBearing() + getHeading() - getHeading();
		if(!(eventoEnemigo.getDistance()<100)) {
			estado = Estado.ACERCANDOSE_DISPARANDO;
		}
	}
	
	@Override
	public void onBulletHit(BulletHitEvent event) {
		if (potencia < 3) {
			potencia ++;
		}
	}

	@Override
	public void onBulletMissed(BulletMissedEvent event) {
		setFire(0);
		execute();
		potencia = 1;
		estado = Estado.RASTREANDO;

	}

	private int includes(String name) {
		if(nextEnemy ==0){
			return -1;
		}
		for(int i = 0; i<nextEnemy; i++) {
			if(name.equals(enemy[i].nombre)) {
				return i;
			}
		}
		return -1;
	}
}
