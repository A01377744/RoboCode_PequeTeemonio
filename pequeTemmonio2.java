package pruebasRoboticas;

import java.awt.Color;
import robocode.util.Utils;
import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;

import java.util.Random;

public class pequeTemmonio2 extends AdvancedRobot {
	private Estado estado;
	public static final int BORDE = 70;
	private int giro= 180;
	private double giroRadar;
	private double giroCanon;
	private double potencia;
	private ScannedRobotEvent eventoEnemigo;
	private EnemyBot[] enemy;
	private int nextEnemy ;
	private int direction;

	
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
			out.println(estado);
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
				setTurnRadarRight(Utils.normalRelativeAngleDegrees(giroRadar));
				setTurnGunRight(Utils.normalRelativeAngleDegrees(giroCanon));
				setAhead(eventoEnemigo.getDistance()-100);
				setTurnRight(eventoEnemigo.getBearing());
				setFire(potencia);
				execute();
				if(eventoEnemigo.getDistance()<120) {
					estado = Estado.RODEANDO_DISPARANDO;
				}
				break;
			case RODEANDO_DISPARANDO:
				out.println(direction);
				setTurnRadarRight(Utils.normalRelativeAngleDegrees(giroRadar));
			    setTurnGunRight(Utils.normalRelativeAngleDegrees(giroCanon));
			    if(direction>0) {
				    turnRight(eventoEnemigo.getBearing()+90); 
			    }else {
			    	turnLeft(eventoEnemigo.getBearing()+90);
			    }
			    setBack(100*direction);
			    setFire(potencia);
			    execute();
				break;
			case EJECUTANDO:
				setTurnRight(eventoEnemigo.getBearing());
				setAhead(eventoEnemigo.getDistance()+50);
				if(eventoEnemigo.getEnergy()<20) {
					setFire(0);
				}else {
					setFire(3);
				}
				execute();
				if(eventoEnemigo.getDistance()>200) {
					estado = Estado.ACERCANDOSE_DISPARANDO;
				}
				break;
			default:
				doNothing();
				break;
			}
			
			
			
		}
	}
	
	public void inicializar() {
		estado = Estado.RASTREANDO;
		potencia = 1;
		enemy = new EnemyBot[7];
		nextEnemy=0;
		direction = 1;

	}
	
	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		eventoEnemigo = event;
		
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
		
		this.giroRadar = event.getBearing() + getHeading() - getRadarHeading();
		this.giroCanon = event.getBearing() + getHeading() - getGunHeading();
		if(getEnergy()<60&&(event.getEnergy()>getEnergy())||event.getEnergy()>50) {
			estado = Estado.ACERCANDOSE_DISPARANDO;
		}else{
			estado = Estado.EJECUTANDO;
		}

	}
	
	@Override
	public void onHitWall(HitWallEvent event) {
		direction = direction*-1;
	}
	
	@Override
	public void onHitRobot(HitRobotEvent event) {
		direction= direction*-1;
	}
	
	@Override
	public void onBulletHit(BulletHitEvent event) {
		if (potencia < 3) {
			potencia ++;
		}
	}

	@Override
	public void onBulletMissed(BulletMissedEvent event) {
		potencia = 1;
		estado = Estado.RASTREANDO;

	}
	
	@Override
	public void onRobotDeath(RobotDeathEvent event) {
		int x = includes(event.getName());
		enemy[x].alive = false;
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
	
	@Override
	public void onWin(WinEvent event) {
		for(int i =0; i<4; i++) {
			setTurnRadarLeft(360);
			turnLeft(20);
			setTurnRadarRight(360);
			turnRight(20);
		}
	}
	
	
//	private double getObjective() {
//		
//		double dx = p.getX() - this.getX();
//		double dy = p.getY() - this.getY();
//		return Math.toDegrees(Math.atan2(dx, dy));
//	}
}
