package alanrdgz;

//Robocode Semana i :D.
//Karla Fabiola Ramirez Martinez.
//Alejandro Torices Oliva.
//Alan Giovanni Rodriguez Camacho.

//La estrategia de nuestor robot es bastante simple, busca enemigos y se dedica a girar mientras los ataca de forma circular.

import java.awt.Color;
import java.util.Random;


import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

public class PequeTeemonio extends AdvancedRobot {
	private Estado estado;
	private ScannedRobotEvent eventoUltimo;
	private int potencia=1;
	private double giroCanon;
	private double giroRadar;
	private Random rnd = new Random();
	private int constanteF=99999;
	private int choques;
	//Este estado se concentra en detectar que debe priorizar la huida cuando es necesario.
	private boolean estadoH;
	
	private void Inicializar() {
		Random random = new Random();
		estado=Estado.GIRANDO;	
		//Esto nos deja mover a voluntad el radar y el arma por separado.
		setAdjustRadarForGunTurn(true); 
		setAdjustGunForRobotTurn(true);
		//Esto le da los colores a nuestro robot.
		Color rojo = new Color(200+random.nextInt(50), 100+random.nextInt(100), 10+random.nextInt(10));
		setColors(rojo, Color.yellow, rojo);
		choques = 0;
	}
	//Run donde se ejecutara todo lo que hace el robot
	public void run() {	
			Inicializar();
		while (true)
			switch (estado) {
			case GIRANDO:
				//Todo el tiempo checa si esta a punto de chocar con la pared
				AlmostHWall();
				estadoH=false;
				setTurnRadarRight(constanteF);
				setTurnGunRight(constanteF);
				setTurnRight(constanteF);
				setAhead(10);
				setTurnLeft(10);
				execute();
				break;
			//Aqui ajusta el angulo cuando encuentra un enemigo.
			case AJUSTANDO:
				turnLeft(-eventoUltimo.getBearing());	
				estado=Estado.DISPARANDO;//Incondicional.
				break;	
			//En este metodo se dedica a disparar y al mismo tiempo no deja de mover.
			case DISPARANDO:
				AlmostHWall();
				//Se dispone a girar el cañon a donde esta el enemigo mientras sigue moviendose.
				setTurnRadarRight((giroRadar));
			    setTurnGunRight((giroCanon));
				fire(potencia);
				setAhead(rnd.nextInt(90));
				setTurnRight(rnd.nextInt(20));
				execute();
				break;
			//El estado de huida y escape son similares, sin embargo el estado de huida es un poco menos agresivo y ocurre mas seguido.
			case HUIDA:
				//Se reposiciona con algunos valores random.
				setBack(rnd.nextInt(35));
				setTurnRight(rnd.nextInt(30));
				ahead(100);
				estado=Estado.GIRANDO;
				break;
			//Caso especial donde busca reponerse del muro.
			case REPOSICION_MURO:
				estadoH=false;
				back(100);
				estado=Estado.GIRANDO;
				break;
			//Estado similar a huida pero mas evasivo.
			case ESCAPE:
				setAhead(rnd.nextInt(100));
				setTurnLeft(rnd.nextInt(60));
				estado=Estado.GIRANDO;
			//Esatdo en el que si etecta que nuestro robot choca a otro mas de 2 veces se dirige hacia atras evitando mas colisiones.
			case ENEMIGO:
				setTurnRight(180);
				if(choques>2) {
					ahead(100);
				}else {
					back(100);
				}
				execute();
				choques = 0;
				estado = Estado.GIRANDO;
				break;
			default:
				break;
			}
	}
	
	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		//Esto fija el punto donde debe apuntar para apuntarle al enemigo que tiene enfrente.
		this.giroRadar = event.getBearing() + getHeading() - getRadarHeading();
		this.giroCanon = event.getBearing() + getHeading() - getGunHeading();
		//Este if sirve para saber si el enemigo se encuentra almenos a 300 pixeles le dispararemos, si no seguira realizando las demas acciones.
		if (event.getDistance()<300) {
			estado=Estado.DISPARANDO;
		} else {
			return;
		}
	}
	@Override
	public void onBulletHit(BulletHitEvent event) { //Cuando golpea con una bala aumenta la potencia.
		if (potencia<3) {
			potencia++;
		}
		else
			doNothing();
	}
	
	@Override
	public void onBulletMissed(BulletMissedEvent event) { //Cuando no golpea con la bala reinicia la potencia.
		potencia=1;
		estado=Estado.GIRANDO;
	}
	
	@Override
	public void onHitByBullet(HitByBulletEvent event) { //Cuando es golpeado por una bala decide como proceder dependiendo de la vida.
		estadoH=true;
		if (getEnergy()<=20) {
			estado=Estado.ESCAPE;
		}
		else if (getEnergy()<=50) {
			estado=Estado.HUIDA;
		}
	}
	
	@Override
	public void onHitRobot(HitRobotEvent event) { //Llama al estado de ENEMIGO cuando colisiona con un enemigo.
		choques++;
		estado = Estado.ENEMIGO;
	}
	
	@Override
	public void onHitWall(HitWallEvent event) { //Llama al estado de reposicion cuando golpea con una pared.
		estadoH=true;	
		estado=Estado.REPOSICION_MURO;
	}
	
	public void AlmostHWall(){
		System.out.println("Si esta llegando al estado de AlmosHWall :)");
		System.out.println("la XMax :)" + getBattleFieldWidth()  );
		System.out.println("la Y :)" + this.getY() );
		System.out.println("la X :)" + this.getX() );
		
		if(( this.getX()<=80||this.getX()>=(getBattleFieldWidth()-80)||this.getY()<=80||this.getY()>=(getBattleFieldHeight()-80) ) && !estadoH ) {
			System.out.println("Si entra al if de AlmosHWall O W O");
			estado=Estado.REPOSICION_MURO;
		}
		
	}
	
	private enum Estado{
		GIRANDO,
		DISPARANDO,
		AJUSTANDO,
		HUIDA,
		ESCAPE,
		REPOSICION_MURO,
		ENEMIGO
	}

}