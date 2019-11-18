package bd.modelo;

import java.util.Date;
import java.util.List;

public class Nino extends Usuario {

	private String idiomaJuego;
	private Date fechaNacimiento;
	private List<Sesion> sesiones;
	private List<HistorialMinijuegos> historialMinijuego;

	private Integer telefono1;
	private Integer telefono2;
	private String nombrePadre;
	private String nombreMadre;
	private String profesionPadre;
	private String profesionMadre;
	private String hermanosEdades;
	private String otrasConvivencias;
	private String motivoConsulta;
	private String entrevistados;

	private String embarazoParto;
	private String desFisico;
	private String desMotor;
	private String desOrofacial;
	private String desLenguaje;
	private String antecedentesFamiliares;
	private String datosMedicos;
	private String historialEscolar;
	private String atenFueraCentro;

	private String tipoAlimentos;
	private String alimentosPreferidos;
	private String reconoceComer;
	private String pideAlimento;
	private String biberonChupete;
	private String mastica;
	private String succionaTraga;
	private String muerdeCosas;
	private String saliva;
	private String horasSueno;
	private String intencionDormir;
	private String reconoceDormir;
	private String ronca;
	private String controlEsfinteres;
	private String avisaSucio;
	private String pideWC;
	private String reconoceRopa;
	private String necesidadCambioRopa;
	private String malestarSucio;
	private String sonarseNariz;

	private String aCargo;
	private String mayorApego;
	private String sinAtender;
	private String reconoceFamiliares;
	private String comportamientoDesconocidos;
	private String juegaNinos;
	private String relacionNinos;
	private String juegoFunional;
	private String imitaAcciones;
	private String objetosPreferidos;
	private String rechazaObjetos;
	private String juegaEnCasa;
	private String reconoceJuguetes;

	private String intencionalidadComunicativa;
	private String quienComunica;
	private String comoComunica;
	private String llamaAtencion;
	private String intenaAtencion;
	private String pideNecesita;
	private String gestoRechazo;
	private String agrado;
	private String estadosEmocion;
	private String gestoAdios;
	private String ordenesSencillas;
	private String ordenesComplejas;
	private String seEntiende;

	public Nino(Integer idNino, String usuario, String nombre, String apellidos, String contrasena,
			String idioma, String idiomaJuego, Date fechaNacimiento, Integer telefono1, Integer telefono2) {
		super(idNino, usuario, nombre, apellidos, contrasena, idioma);
		this.setFechaNacimiento(fechaNacimiento);
		this.setIdiomaJuego(idiomaJuego);
		this.setTelefono1(telefono1);
		this.setTelefono2(telefono2);
	}
	
	public Nino(Integer idNino, String nombre, String apellidos) {
		super(idNino, nombre, apellidos);
	}
	
	

	public String getIdiomaJuego() {
		return idiomaJuego;
	}

	public void setIdiomaJuego(String idiomaJuego) {
		this.idiomaJuego = idiomaJuego;
	}

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}
	
	public String getNombrePadre() {
		return nombrePadre;
	}

	public void setNombrePadre(String nombrePadre) {
		this.nombrePadre = nombrePadre;
	}

	public String getNombreMadre() {
		return nombreMadre;
	}

	public void setNombreMadre(String nombreMadre) {
		this.nombreMadre = nombreMadre;
	}

	public String getProfesionPadre() {
		return profesionPadre;
	}

	public void setProfesionPadre(String profesionPadre) {
		this.profesionPadre = profesionPadre;
	}

	public String getProfesionMadre() {
		return profesionMadre;
	}

	public void setProfesionMadre(String profesionMadre) {
		this.profesionMadre = profesionMadre;
	}

	public String getHermanosEdades() {
		return hermanosEdades;
	}

	public void setHermanosEdades(String hermanosEdades) {
		this.hermanosEdades = hermanosEdades;
	}

	public String getOtrasConvivencias() {
		return otrasConvivencias;
	}

	public void setOtrasConvivencias(String otrasConvivencias) {
		this.otrasConvivencias = otrasConvivencias;
	}

	public String getMotivoConsulta() {
		return motivoConsulta;
	}

	public void setMotivoConsulta(String motivoConsulta) {
		this.motivoConsulta = motivoConsulta;
	}

	public String getEntrevistados() {
		return entrevistados;
	}

	public void setEntrevistados(String entrevistados) {
		this.entrevistados = entrevistados;
	}

	public String getEmbarazoParto() {
		return embarazoParto;
	}

	public void setEmbarazoParto(String embarazoParto) {
		this.embarazoParto = embarazoParto;
	}

	public String getDesFisico() {
		return desFisico;
	}

	public void setDesFisico(String desFisico) {
		this.desFisico = desFisico;
	}

	public String getDesMotor() {
		return desMotor;
	}

	public void setDesMotor(String desMotor) {
		this.desMotor = desMotor;
	}

	public String getDesOrofacial() {
		return desOrofacial;
	}

	public void setDesOrofacial(String desOrofacial) {
		this.desOrofacial = desOrofacial;
	}

	public String getDesLenguaje() {
		return desLenguaje;
	}

	public void setDesLenguaje(String desLenguaje) {
		this.desLenguaje = desLenguaje;
	}

	public String getAntecedentesFamiliares() {
		return antecedentesFamiliares;
	}

	public void setAntecedentesFamiliares(String antecedentesFamiliares) {
		this.antecedentesFamiliares = antecedentesFamiliares;
	}

	public String getDatosMedicos() {
		return datosMedicos;
	}

	public void setDatosMedicos(String datosMedicos) {
		this.datosMedicos = datosMedicos;
	}

	public String getHistorialEscolar() {
		return historialEscolar;
	}

	public void setHistorialEscolar(String historialEscolar) {
		this.historialEscolar = historialEscolar;
	}

	public String getAtenFueraCentro() {
		return atenFueraCentro;
	}

	public void setAtenFueraCentro(String atenFueraCentro) {
		this.atenFueraCentro = atenFueraCentro;
	}

	public String getTipoAlimentos() {
		return tipoAlimentos;
	}

	public void setTipoAlimentos(String tipoAlimentos) {
		this.tipoAlimentos = tipoAlimentos;
	}

	public String getAlimentosPreferidos() {
		return alimentosPreferidos;
	}

	public void setAlimentosPreferidos(String alimentosPreferidos) {
		this.alimentosPreferidos = alimentosPreferidos;
	}

	public String getReconoceComer() {
		return reconoceComer;
	}

	public void setReconoceComer(String reconoceComer) {
		this.reconoceComer = reconoceComer;
	}

	public String getPideAlimento() {
		return pideAlimento;
	}

	public void setPideAlimento(String pideAlimento) {
		this.pideAlimento = pideAlimento;
	}

	public String getBiberonChupete() {
		return biberonChupete;
	}

	public void setBiberonChupete(String biberonChupete) {
		this.biberonChupete = biberonChupete;
	}

	public String getMastica() {
		return mastica;
	}

	public void setMastica(String mastica) {
		this.mastica = mastica;
	}

	public String getSuccionaTraga() {
		return succionaTraga;
	}

	public void setSuccionaTraga(String succionaTraga) {
		this.succionaTraga = succionaTraga;
	}

	public String getMuerdeCosas() {
		return muerdeCosas;
	}

	public void setMuerdeCosas(String muerdeCosas) {
		this.muerdeCosas = muerdeCosas;
	}

	public String getSaliva() {
		return saliva;
	}

	public void setSaliva(String saliva) {
		this.saliva = saliva;
	}

	public String getHorasSueno() {
		return horasSueno;
	}

	public void setHorasSueno(String horasSueno) {
		this.horasSueno = horasSueno;
	}

	public String getIntencionDormir() {
		return intencionDormir;
	}

	public void setIntencionDormir(String intencionDormir) {
		this.intencionDormir = intencionDormir;
	}

	public String getReconoceDormir() {
		return reconoceDormir;
	}

	public void setReconoceDormir(String reconoceDormir) {
		this.reconoceDormir = reconoceDormir;
	}

	public String getRonca() {
		return ronca;
	}

	public void setRonca(String ronca) {
		this.ronca = ronca;
	}

	public String getControlEsfinteres() {
		return controlEsfinteres;
	}

	public void setControlEsfinteres(String controlEsfinteres) {
		this.controlEsfinteres = controlEsfinteres;
	}

	public String getAvisaSucio() {
		return avisaSucio;
	}

	public void setAvisaSucio(String avisaSucio) {
		this.avisaSucio = avisaSucio;
	}

	public String getPideWC() {
		return pideWC;
	}

	public void setPideWC(String pideWC) {
		this.pideWC = pideWC;
	}

	public String getReconoceRopa() {
		return reconoceRopa;
	}

	public void setReconoceRopa(String reconoceRopa) {
		this.reconoceRopa = reconoceRopa;
	}

	public String getNecesidadCambioRopa() {
		return necesidadCambioRopa;
	}

	public void setNecesidadCambioRopa(String necesidadCambioRopa) {
		this.necesidadCambioRopa = necesidadCambioRopa;
	}

	public String getMalestarSucio() {
		return malestarSucio;
	}

	public void setMalestarSucio(String malestarSucio) {
		this.malestarSucio = malestarSucio;
	}

	public String getSonarseNariz() {
		return sonarseNariz;
	}

	public void setSonarseNariz(String sonarseNariz) {
		this.sonarseNariz = sonarseNariz;
	}

	public String getACargo() {
		return aCargo;
	}

	public void setACargo(String aCargo) {
		this.aCargo = aCargo;
	}

	public String getMayorApego() {
		return mayorApego;
	}

	public void setMayorApego(String mayorApego) {
		this.mayorApego = mayorApego;
	}

	public String getSinAtender() {
		return sinAtender;
	}

	public void setSinAtender(String sinAtender) {
		this.sinAtender = sinAtender;
	}

	public String getReconoceFamiliares() {
		return reconoceFamiliares;
	}

	public void setReconoceFamiliares(String reconoceFamiliares) {
		this.reconoceFamiliares = reconoceFamiliares;
	}

	public String getComportamientoDesconocidos() {
		return comportamientoDesconocidos;
	}

	public void setComportamientoDesconocidos(String comportamientoDesconocidos) {
		this.comportamientoDesconocidos = comportamientoDesconocidos;
	}

	public String getJuegaNinos() {
		return juegaNinos;
	}

	public void setJuegaNinos(String juegaNinos) {
		this.juegaNinos = juegaNinos;
	}

	public String getJuegoFunional() {
		return juegoFunional;
	}

	public void setJuegoFunional(String juegoFunional) {
		this.juegoFunional = juegoFunional;
	}

	public String getRelacionNinos() {
		return relacionNinos;
	}

	public void setRelacionNinos(String relacionNinos) {
		this.relacionNinos = relacionNinos;
	}

	public String getImitaAcciones() {
		return imitaAcciones;
	}

	public void setImitaAcciones(String imitaAcciones) {
		this.imitaAcciones = imitaAcciones;
	}

	public String getObjetosPreferidos() {
		return objetosPreferidos;
	}

	public void setObjetosPreferidos(String objetosPreferidos) {
		this.objetosPreferidos = objetosPreferidos;
	}

	public String getRechazaObjetos() {
		return rechazaObjetos;
	}

	public void setRechazaObjetos(String rechazaObjetos) {
		this.rechazaObjetos = rechazaObjetos;
	}

	public String getJuegaEnCasa() {
		return juegaEnCasa;
	}

	public void setJuegaEnCasa(String juegaEnCasa) {
		this.juegaEnCasa = juegaEnCasa;
	}

	public String getReconoceJuguetes() {
		return reconoceJuguetes;
	}

	public void setReconoceJuguetes(String reconoceJuguetes) {
		this.reconoceJuguetes = reconoceJuguetes;
	}

	public String getIntencionalidadComunicativa() {
		return intencionalidadComunicativa;
	}

	public void setIntencionalidadComunicativa(String intencionalidadComunicativa) {
		this.intencionalidadComunicativa = intencionalidadComunicativa;
	}

	public String getQuienComunica() {
		return quienComunica;
	}

	public void setQuienComunica(String quienComunica) {
		this.quienComunica = quienComunica;
	}

	public String getComoComunica() {
		return comoComunica;
	}

	public void setComoComunica(String comoComunica) {
		this.comoComunica = comoComunica;
	}

	public String getLlamaAtencion() {
		return llamaAtencion;
	}

	public void setLlamaAtencion(String llamaAtencion) {
		this.llamaAtencion = llamaAtencion;
	}

	public String getIntenaAtencion() {
		return intenaAtencion;
	}

	public void setIntenaAtencion(String intenaAtencion) {
		this.intenaAtencion = intenaAtencion;
	}

	public String getPideNecesita() {
		return pideNecesita;
	}

	public void setPideNecesita(String pideNecesita) {
		this.pideNecesita = pideNecesita;
	}

	public String getGestoRechazo() {
		return gestoRechazo;
	}

	public void setGestoRechazo(String gestoRechazo) {
		this.gestoRechazo = gestoRechazo;
	}

	public String getAgrado() {
		return agrado;
	}

	public void setAgrado(String agrado) {
		this.agrado = agrado;
	}

	public String getEstadosEmocion() {
		return estadosEmocion;
	}

	public void setEstadosEmocion(String estadosEmocion) {
		this.estadosEmocion = estadosEmocion;
	}

	public String getGestoAdios() {
		return gestoAdios;
	}

	public void setGestoAdios(String gestoAdios) {
		this.gestoAdios = gestoAdios;
	}

	public String getOrdenesSencillas() {
		return ordenesSencillas;
	}

	public void setOrdenesSencillas(String ordenesSencillas) {
		this.ordenesSencillas = ordenesSencillas;
	}

	public String getOrdenesComplejas() {
		return ordenesComplejas;
	}

	public void setOrdenesComplejas(String ordenesComplejas) {
		this.ordenesComplejas = ordenesComplejas;
	}

	public String getSeEntiende() {
		return seEntiende;
	}

	public void setSeEntiende(String seEntiende) {
		this.seEntiende = seEntiende;
	}

	public Integer getTelefono1() {
		return telefono1;
	}

	public void setTelefono1(Integer telefono1) {
		this.telefono1 = telefono1;
	}

	public Integer getTelefono2() {
		return telefono2;
	}

	public void setTelefono2(Integer telefono2) {
		this.telefono2 = telefono2;
	}

	public List<Sesion> getSesiones() {
		return sesiones;
	}

	public void setSesiones(List<Sesion> sesiones) {
		this.sesiones = sesiones;
	}

	public List<HistorialMinijuegos> getHistorialMinijuego() {
		return historialMinijuego;
	}

	public void setHistorialMinijuego(List<HistorialMinijuegos> historialMinijuego) {
		this.historialMinijuego = historialMinijuego;
	}
}
