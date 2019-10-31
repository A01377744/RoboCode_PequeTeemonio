package pruebasRoboticas;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

public class PequenioTeemonio extends AdvancedRobot {
	
	private Estado estado;
	private ScannedRobotEvent eventoUltimo;
	private HitByBulletEvent eventoGolpe;
	private HitRobotEvent eventoColision;
	private HitWallEvent eventoChoque;
	private int potencia, dirMovimiento;
	private double previousEnergy, posicionX, posicionY, giroRadar, giroCanon, giroRobot;
	private HitWallEvent pared;
	private static final int LARGO = 600;
	private static final int ANCHO = 800;
	
	@Override
	public void run() {
		setAdjustRadarForGunTurn(true); 
		setAdjustGunForRobotTurn(true);
		
		inicializar();
		while(true) {
			switch (estado) {
			case GIRANDO:
				setTurnRight(20);	
				setTurnGunRight(20);
				setTurnRadarRight(20);
				setAhead(50);
				execute();
				break;
			case AJUSTANDO:
				turnRight(eventoUltimo.getBearing());
				estado = Estado.DISPARANDO; //INCONDICIONAL
				break;
			case DISPARANDO:
				setTurnRadarRight(giroRadar);
			    setTurnGunRight(giroCanon);
				setFire(potencia);
				setAhead(20);
				execute();
				break;
			case POTENCIANDO:
				if (potencia < 3) {
					potencia ++;
				}
				estado = Estado.AVANZANDO_DISPARANDO;
				break;
			case AVANZANDO_DISPARANDO:
				setAhead(eventoUltimo.getDistance());
				setFire(potencia);
				estado = Estado.DISPARANDO;
				execute();
				break;
//			case ANALIZANDO:
//				double energia = getEnergy();
//				
//				if (energia<50) {
//					out.println("huye");
//					estado = Estado.ESCAPANDO;	
//				} else {
//					estado = Estado.DISPARANDO;
//				}
//				previousEnergy = getEnergy();
//				break;
			case ESCAPANDO:
				setTurnRight(20);
				back(100);
				execute();
				estado = Estado.GIRANDO;
				previousEnergy = getEnergy();
				break;
			case REPOSICION:
				if (eventoChoque.getBearing() > -90 && eventoChoque.getBearing() <= 90) {
			           setBack(100);
			           execute();
			       } else {
			           setAhead(100);
			           execute();
			       }
				estado = Estado.GIRANDO;	
				
			case REPOSICION2:
				if (eventoColision.getBearing() > -90 && eventoColision.getBearing() <= 90) {
			           setBack(100);
			           execute();
			       } else {
			           setAhead(100);
			           execute();
			       }
				estado = Estado.GIRANDO;
			case ESQUIVANDO:
				turnRight(90);
				setBack(50);
				execute();
				if(previousEnergy==getEnergy()){
					estado=Estado.GIRANDO;
				}
				previousEnergy = getEnergy();
				break;
				
			default:
				doNothing();
				break;
			}
		}
	}
	
	//TRANSICIONES
	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		this.giroRadar = event.getBearing() + getHeading() - getRadarHeading();
		this.giroCanon = event.getBearing() + getHeading() - getGunHeading();
		this.giroRobot = event.getBearing() + getHeading() - getHeading();
		estado = Estado.AJUSTANDO;
		eventoUltimo = event;
	}
	
	@Override
	public void onBulletMissed(BulletMissedEvent event) {
		estado = Estado.GIRANDO;
		setFire(0);
		execute();
		potencia = 1;
	}
	
	@Override
	public void onBulletHit(BulletHitEvent event) {
		estado = Estado.POTENCIANDO;
	}
	
	@Override
	public void onHitByBullet(HitByBulletEvent event) {
		eventoGolpe = event;
		estado = Estado.ESQUIVANDO;
		if(getEnergy()<50) {
			estado = Estado.ESCAPANDO;
		}
	}
	
	@Override
	public void onHitWall(HitWallEvent event) {
		eventoChoque = event;
		estado = Estado.REPOSICION;
	}
	
	@Override
	public void onHitRobot(HitRobotEvent event) {
		eventoColision = event;
		estado = Estado.REPOSICION2;

	}

	
	
	//INICIALIZAR
	private void inicializar() {
		previousEnergy = 100;
		estado = Estado.GIRANDO;
		setColors(Color.RED, Color.ORANGE, Color.YELLOW);
		potencia = 1;
		posicionX = getX();
		posicionY = getY();
	}


	private enum Estado{
		GIRANDO,
		DISPARANDO,
		AJUSTANDO,
		AVANZANDO_DISPARANDO,
		POTENCIANDO,
		ESCAPANDO,
		ANALIZANDO,
		DAÑORECIBIDO,
		REPOSICION, 
		ESQUIVANDO, 
		REPOSICION2
		
	}
	
}