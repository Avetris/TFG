package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import bd.ibatis.daos.GrupoDAO;
import bd.ibatis.daos.MinijuegoDAO;
import bd.modelo.Contenido;
import bd.modelo.Grupo;
import bd.modelo.Minijuego;

public class GestorMinijuegos extends Observable implements Runnable{
	
	private static GestorMinijuegos miGestorMinijuegos = new GestorMinijuegos();
	private MinijuegoDAO minijuegoDAO;
	private GrupoDAO grupoDAO;
	private List<Minijuego> minijuegos = null;
	private List<Grupo> grupos = null;
	private List<Grupo> gruposActuales = null;
	private Minijuego minijuegoActual;

	public final int accionCargar = 1;
	public final int accionCargarError = 2;
	public final int accionModificar = 3;
	public final int accionInsertar = 4;
	public final int accionEliminar = 5;
	public final int accionInsertarError = 6;
	public final int accionModificarError = 7;
	public final int accionEliminarError = 8;
	
	private int accion;
	private String parametroNombre;
	private String parametroDescripcion;
	private List<Integer> parametroGrupos;
	
	private GestorMinijuegos(){
		minijuegoDAO = new MinijuegoDAO();
		grupoDAO = new GrupoDAO();
	}
	
	public static GestorMinijuegos getGestorMinijuegos(){
		if(miGestorMinijuegos == null){
			miGestorMinijuegos = new GestorMinijuegos();
		}
		return miGestorMinijuegos;
	}	

	
	public String[] obtenerDatos(){
		if(minijuegoActual != null){
			return new String[]{minijuegoActual.getNombre(), minijuegoActual.getDescripcion(), String.valueOf(minijuegoActual.getTamano())};
		}else{
			return null;
		}
	}

	public Object[][] obtenerGruposMinijuego(int pos){
		Object[][] cont = null;		
		if(pos >= 0){
			minijuegoActual = minijuegos.get(pos);
			cont = new Object[minijuegoActual.getGrupos().size()+1][3];
			for(int i = 0; i < cont.length-1; i++){
				cont[i][0] = minijuegoActual.getGrupos().get(i).getIdGrupo();
				cont[i][1] = minijuegoActual.getGrupos().get(i).getNombre();
				cont[i][2] = new Boolean(false);
			}
			cont[cont.length -1][0] = null;
			cont[cont.length -1][1] = null;
			cont[cont.length -1][2] = new Boolean(false);
		}else{ 
			minijuegoActual = null;
			cont = new Object[1][3];			
			cont[cont.length-1][0] = null;
			cont[cont.length-1][1] = "";
			cont[cont.length-1][2] = new Boolean(false);			
		}		
		return cont;
	}
	
	public void setMinijuego(int pos){
		minijuegoActual = minijuegos.get(pos);
		gruposActuales = null;
		if(minijuegoActual.getTamano() > 0){
			gruposActuales = new ArrayList<>();
			for(Grupo grupo : grupos){
				if(grupo.getContenidoOpciones().size() >= minijuegoActual.getTamano()){
					boolean bien = true;
					for(int j = 0; j < minijuegoActual.getGrupos().size() && bien; j++){
						bien = minijuegoActual.getGrupos().get(j).getIdGrupo() != grupo.getIdGrupo();
					}
					if (bien) {
						Contenido sol = grupo.getContenidoSolucion();
						switch (minijuegoActual.getIdMinijuego()) {
						case 1:
							if(grupo.getContenidoSolucion().getCastellano() != null &&
									grupo.getContenidoSolucion().getEuskera() != null){
								
								if((grupo.getContenidoSolucion().getCastellano().length() <= 2
									&& FiltroTexto.soloTexto(grupo.getContenidoSolucion().getCastellano())) 
									|| (grupo.getContenidoSolucion().getEuskera().length() <= 2
										|| FiltroTexto.soloTexto(grupo.getContenidoSolucion().getEuskera()))){
									
									for(Contenido contenido : grupo.getContenidoOpciones()){
										if(contenido.getCastellano() != null &&
												contenido.getCastellano().length() > 2){
											bien = false;
										}else if(contenido.getEuskera() != null &&
												contenido.getEuskera().length() > 2){
											bien = false;											
										}else if(contenido.getCastellano() == null && contenido.getEuskera() == null){
											bien = false;
										}

									}
									if(bien) gruposActuales.add(grupo);		
								}
							}else{
								if(grupo.getContenidoSolucion().getCastellano() == null){
									if(grupo.getContenidoSolucion().getEuskera().length() <= 2
											|| FiltroTexto.soloTexto(grupo.getContenidoSolucion().getEuskera())){
									
											for(Contenido contenido : grupo.getContenidoOpciones()){
												if(contenido.getEuskera() == null || 
														contenido.getEuskera() != null &&
														contenido.getEuskera().length() > 2){
													bien = false;											
												}		
											}
											if(bien) gruposActuales.add(grupo);	
									}
								}else{
									if(grupo.getContenidoSolucion().getCastellano().length() <= 2
											|| FiltroTexto.soloTexto(grupo.getContenidoSolucion().getCastellano())){
									
											for(Contenido contenido : grupo.getContenidoOpciones()){
												if(contenido.getCastellano() == null 
														|| (contenido.getCastellano() != null &&
														contenido.getCastellano().length() > 2)){
													bien = false;
												}
		
											}
											if(bien) gruposActuales.add(grupo);	
									}
								}
								
							}
							break;
						case 2:
							if(grupo.getContenidoSolucion().getImagen() != null ){
								bien = false;
								for(Contenido contenido : grupo.getContenidoOpciones()){
									if(contenido.getCastellano() != null && sol.getCastellano() != null && contenido.getCastellano().equals(sol.getCastellano())){
										bien = true;
									}else if(contenido.getEuskera() != null && sol.getEuskera() != null && contenido.getEuskera().equals(sol.getEuskera())){
										bien = true;
									}
								}
								if(bien) gruposActuales.add(grupo);
							}	
							break;
						case 4:
							if(sol.getImagen() != null){
								if(sol.getCastellano() == null || FiltroTexto.soloTexto(sol.getCastellano())){
									
										if(sol.getEuskera() == null || FiltroTexto.soloTexto(sol.getEuskera())){
											
											for(Contenido contenido : grupo.getContenidoOpciones()){
												if(contenido.getCastellano() != null){
													if(sol.getCastellano() != null && contenido.getCastellano().length() == sol.getCastellano().length() && contenido.getCastellano().contains("_")){
														for(int j = 0; j < contenido.getCastellano().length() && bien; j++){
															if(sol.getCastellano().charAt(j) != contenido.getCastellano().charAt(j) && contenido.getCastellano().charAt(j) != '_'){
																bien = false;
															}
														}		
													}else{
														bien = false;
													}
												}
												if(contenido.getEuskera() != null){
													if(sol.getEuskera() != null && contenido.getEuskera().length() == sol.getEuskera().length() && contenido.getEuskera().contains("_")){
														for(int j = 0; j < contenido.getEuskera().length() && bien; j++){
															if(sol.getEuskera().charAt(j) != contenido.getEuskera().charAt(j) && contenido.getEuskera().charAt(j) != '_'){
																bien = false;
															}
														}		
													}else{
														bien = false;
													}
												}
											}											
											if(bien) gruposActuales.add(grupo);		
										}
								}
							}
							break;
						case 5:
							gruposActuales.add(grupo);
						case 6:
							boolean bienAux = false;
							for(Contenido contenido : grupo.getContenidoOpciones()){
								if(contenido.getImagen() == null){
									bien = false;
								}
								if(contenido.getCastellano() != null && sol.getCastellano() != null && contenido.getCastellano().equals(sol.getCastellano())){
									bienAux = true;
								}else if(contenido.getEuskera() != null && sol.getEuskera() != null && contenido.getEuskera().equals(sol.getEuskera())){
									bienAux = true;
								}
							}		
							if(bien && bienAux) gruposActuales.add(grupo);	
							break;
						case 7:
							if(FiltroTexto.soloTexto(grupo.getContenidoSolucion().getCastellano()) && 
								FiltroTexto.soloTexto(grupo.getContenidoSolucion().getEuskera())){																					
								gruposActuales.add(grupo);		
							}
							break;
						default:
							break;
						}	
					}
				}
			}
		}
	}
	
	public String[] obtenerNombresGrupos(){
		if(gruposActuales == null)
			return null;
		String[] nombres = new String[gruposActuales.size()+1];
		nombres[0] = " ";
		for(int i = 0; i < nombres.length - 1; i++) nombres[i+1] = gruposActuales.get(i).getNombre();
		return nombres;
	}
	
	public String[] obtenerNombres(){
		String[] nombres = new String[minijuegos.size()];
		for(int i = 0; i < minijuegos.size(); i++)
			nombres[i] = minijuegos.get(i).getNombre();		
		return nombres;
	}
	
	public Boolean existeNombre(String nombre){
		try{
			return minijuegoDAO.existeNombre(minijuegoActual.getIdMinijuego(), nombre);
		}catch(Exception e){
			setChanged();
			notifyObservers(accionCargarError);
			return null;
		}
	}
	
	private void modificarMinijuego(String nombre, String descripcion){
		minijuegoActual.setNombre(nombre);
		minijuegoActual.setDescripcion(descripcion);
		if(minijuegoDAO.updateMinijuego(minijuegoActual)){
			try{
				minijuegos = minijuegoDAO.getMinijuegos();
		    	setChanged();
				notifyObservers(accionModificar);
			}catch(Exception e){
				setChanged();
				notifyObservers(accionCargar);
			}
		}else{
	    	setChanged();
			notifyObservers(accionModificarError);
		}
	}
	
	private void insertarGruposMinijuego(Integer idGrupo){
		if(minijuegoDAO.insertGrupoMinijuego(minijuegoActual.getIdMinijuego(), gruposActuales.get(idGrupo).getIdGrupo())){
			try{
				minijuegos = minijuegoDAO.getMinijuegos();
		    	setChanged();
				notifyObservers(accionInsertar);		
			}catch(Exception e){
				setChanged();
				notifyObservers(accionCargar);
			}	
		}else{
	    	setChanged();
			notifyObservers(accionInsertarError);
		}
	}
	
	private void eliminarGruposMinijuego(List<Integer> idGrupos){
		if(minijuegoDAO.deleteGrupoMinijuego(minijuegoActual.getIdMinijuego(), idGrupos)){
			try{
				minijuegos = minijuegoDAO.getMinijuegos();				
				setChanged();
				notifyObservers(accionEliminar);
			}catch(Exception e){
				setChanged();
				notifyObservers(accionCargar);
			}
		}else{			
			setChanged();
			notifyObservers(accionEliminarError);
		}
	}
	
	public void setParametrosYAccion(int accion, String paramNombre, String paramDesc, List<Integer> paramGrupos){
		this.accion = accion;
		this.parametroNombre = paramNombre;
		this.parametroDescripcion = paramDesc;
		this.parametroGrupos = paramGrupos;
	}

	@Override
	public void run() {
		if(accion == accionCargar){
			try{
				minijuegos = minijuegoDAO.getMinijuegos();
				grupos = grupoDAO.getGrupos();
				setChanged();
				notifyObservers(accionCargar);		
			}catch(Exception e){
				setChanged();
				notifyObservers(accionCargar);
			}	
		}else if(accion == accionInsertar){					
			insertarGruposMinijuego(parametroGrupos.get(parametroGrupos.size()-1));		
		}else if(accion == accionModificar){
			modificarMinijuego(parametroNombre, parametroDescripcion);
		}else if(accion == accionEliminar){
			eliminarGruposMinijuego(parametroGrupos);	
		}
	}
	public static void main(String[] arg){
		System.out.println(Math.sqrt(8) );
		
	}
}
