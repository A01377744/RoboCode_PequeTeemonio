package pruebasRoboticas;

import java.awt.*;
import java.util.Random;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;
import java.util.Arrays;;

public class PequeTeemonio extends AdvancedRobot {
	//son int final. Por default la primera vale 0, la segunda 1, etc
	
	private Estado estado;
	
	private ScannedRobotEvent eventoUltimo;
	private Random random = new Random();
	private EnemyBot[] enemy;
	
	private int potencia, dirMovimiento, dirRadar, dirCannon, nextEnemy;
	private double damageIncome, previousEnergy, energyGain;
		
	@Override
	public void run() {
		setColors(Color.red,Color.orange, Color.yellow, Color.yellow, Color.yellow);
		inicializar();
		while(true) {
			switch (estado) {
			case GIRANDO:
				turnLeft(45);
				setTurnGunRight(99999);
				execute();
				break;
			case DISPARANDO:
				dirCannon = -dirCannon;
				setTurnGunRight(99999*dirCannon);
				setFire(potencia);
				execute();
				break;
			case AJUSTANDO:
				setTurnRight(eventoUltimo.getBearing()+90-30*dirMovimiento);
				estado = Estado.DISPARANDO; // INCONDICIONAL
				execute();
				break;
			case ACERCANDOSE:
				setAhead(eventoUltimo.getDistance());
				estado = Estado.DISPARANDO;
				execute();
				break;
			case POTENCIANDO:
				if(potencia<3){
					potencia++;
				}
				estado = Estado.ACERCANDOSE;
				break;
				
			case ESQUIVANDO:
				dirMovimiento = -dirMovimiento;
				setAhead((eventoUltimo.getDistance()/4+25)*dirMovimiento);
				execute();
				if(previousEnergy==getEnergy()){
					estado=Estado.GIRANDO;
				}
				break;
			case HUYENDO:
				if (eventoUltimo.getBearing() > -90 && eventoUltimo.getBearing() <= 90) {
			           setBack(100);
			           execute();
			       } else {
			           setAhead(100);
			           execute();
			       
				estado = Estado.GIRANDO;
				break;
			   }
			default:
				doNothing();
				break;
			}
		}
	}
	
	//TRANSICIONES
	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		int x = includes(event.getName());
		if(x<0) {
			enemy[nextEnemy] = new EnemyBot(event.getName(), event.getEnergy(), event.getVelocity(), event.getDistance(), event.getBearing());
			nextEnemy++;
		}else {
			enemy[x].update(event.getEnergy(), event.getVelocity(), event.getDistance(), event.getBearing());
		}
		estado = Estado.AJUSTANDO;
		eventoUltimo = event;
		
	}
	
	@Override
	public void onBulletHit(BulletHitEvent event) {
		int x = includes(event.getName());
		if(x>0){
			enemy[x].moreInfo(event.getEnergy(), event.getBullet().getX(), event.getBullet().getY());
		}
		energyGain = event.getBullet().getPower() * 2;
		estado = Estado.POTENCIANDO;
	}
	
	@Override
	public void onBulletMissed(BulletMissedEvent event) {
		estado = Estado.GIRANDO;
		potencia = 1;
	}
	
	@Override
	public void onHitRobot(HitRobotEvent event) {
		damageIncome = previousEnergy-getEnergy();
		previousEnergy = getEnergy();
		if(damageIncome>energyGain) {
			estado = Estado.HUYENDO;
		}
	}
	
	@Override
	public void onHitWall(HitWallEvent event) {
		setAhead(10);
		estado = Estado.GIRANDO;
		
	}
	

	@Override
	public void onHitByBullet(HitByBulletEvent event) {
		damageIncome = previousEnergy-getEnergy();
		previousEnergy = getEnergy();
		if(damageIncome>energyGain) {
			estado = Estado.ESQUIVANDO;
		}
	}
	
	private void inicializar() {
		enemy = new EnemyBot[7];
		estado = Estado.GIRANDO;
		potencia = 1;
		dirCannon = 1;
		dirMovimiento = 1;
		nextEnemy = 0;
		previousEnergy = 100;
	}
	
	private int includes(String name) {
		for(int i = 0; i<enemy.length; i++) {
			if(name.equals(enemy[i].nombre)) {
				return i;
			}
		}
		return -1;
	}


	private enum Estado{
		GIRANDO,
		DISPARANDO,
		AJUSTANDO,
		ACERCANDOSE,
		POTENCIANDO, 
		ESQUIVANDO,
		ALERTA,
		HUYENDO}
}
