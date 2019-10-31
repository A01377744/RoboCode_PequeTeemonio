package pruebasRoboticas;

import java.awt.Color;
import robocode.util.Utils;
import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;

import java.util.Random;

public class PequeTemmonio2 extends AdvancedRobot {
	private Estado estado;
	public static final int BORDE = 70;
	private int giro= 120;
	private double giroRadar;
	private double giroCanon;
	private double potencia;
	private EnemyBot[] enemy;
	private static int nextEnemy ;
	private int direction;
	private double giroRobot;

	
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
				crash();
				setTurnRadarRightRadians(99999);
				setTurnGunRightRadians(99999);
				setTurnRightRadians(99999);
				setAhead(60);
				turnLeft(20);
				
				
				execute();
				break;
			case ACERCANDOSE_DISPARANDO:
				setTurnRadarRightRadians(Utils.normalRelativeAngle(giroRadar));
				setTurnGunRightRadians(Utils.normalRelativeAngle(giroCanon));
				setTurnRight(getTarget().getBearing());
				setAhead(getTarget().getDistance()-100);
				setFire(potencia);
				execute();
				if(getTarget().getDistance()<120) {
					estado = Estado.RODEANDO_DISPARANDO;
				}
				break;
			case RODEANDO_DISPARANDO:
				out.println(direction);
				setTurnRadarRightRadians(Utils.normalRelativeAngle(giroRadar));
			    setTurnGunRightRadians(Utils.normalRelativeAngle(giroCanon));
				setTurnRight(90); 
			    setBack(100);
			    setFire(potencia);
			    execute();
				break;
			case EJECUTANDO:
				setTurnRightRadians(0);
				
				turnRight(giroRobot);
				setAhead(getTarget().getDistance()+50);
				if(getTarget().getEnergy()<50) {
					setFire(1);
				}else {
					setFire(3);
				}
				execute();
				if(getTarget().getDistance()>200) {
					estado = Estado.ACERCANDOSE_DISPARANDO;
				}
				break;
			case ESQUIVANDO:
				
				turnRight(90);
				back(50);
				execute();
				
				estado = Estado.RASTREANDO;
				
				break;
			case HUYENDO:		
				
				setBack(random.nextInt(35));
				setTurnRight(random.nextInt(30));
				
				ahead(100);
			
				estado=Estado.RASTREANDO;
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
		
		double angulo = this.getHeading() + event.getBearing();
		double xEnemigo = getX() + event.getDistance() * Math.sin(Math.toRadians(angulo));
		double yEnemigo = getY() + event.getDistance() * Math.cos(Math.toRadians(angulo));
		
		int x = includes(event.getName());
		if(x<0) {
			enemy[nextEnemy] = new EnemyBot(event, event.getName(), event.getEnergy(), event.getVelocity(), event.getDistance(), event.getBearing(),
					xEnemigo, yEnemigo);
			nextEnemy++;
		}else {
			enemy[x].update(event, event.getEnergy(), event.getVelocity(), event.getDistance(), event.getBearing(), xEnemigo, yEnemigo);
		}

		
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
	
	public void onHitByBullet(HitByBulletEvent event) {
		estado = Estado.ESQUIVANDO;
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
	
	public void crash() {
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
	}
	
	//sorter, para establecer prioridades y saber en quien concentrar el ataque
	public static <T extends Comparable<? super T>> int selectionSort(T[] a){
        int min = 0;
        T temp;
        for(int i=0; i< nextEnemy; i++){
            min = i;
            for(int j=i+1; j<nextEnemy;j++){
                if(a[j].compareTo(a[min]) <0){
                    min = j;
                }
            }
            if (min != i){
                temp = a[min];
                a[min] = a[i];
                a[i] = temp;
            }
        }
        return min;
    }
	
	private ScannedRobotEvent getTarget(){
		Integer[] targetEnergy = new Integer[7];
		for(int e = 0; e<nextEnemy; e++){
			if(enemy[e].alive) {
				targetEnergy[e] = (int)enemy[e].energy;
			}else {
				targetEnergy[e]=100;
			}
		}
		int target = selectionSort(targetEnergy);
		this.giroRadar = enemy[target].event.getBearingRadians() + getHeadingRadians() - getRadarHeadingRadians();
		this.giroCanon = enemy[target].event.getBearingRadians() + getHeadingRadians() - getGunHeadingRadians();
		this.giroRobot = enemy[target].event.getBearingRadians() + getHeadingRadians() - getHeadingRadians();

		

		return enemy[target].event;
		
	}
	
}
