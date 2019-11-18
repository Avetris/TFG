using UnityEngine;
using System.Collections;
using System.Threading;
using System.Xml.Linq;
using System;
using System.Collections.Generic;

public abstract class Minijuego: MonoBehaviour
{
	protected GestorSQLite gestorSQLite;
	protected bool opcionesOn = true;
	protected bool descripcion = true;
	protected bool terminado = false;

	protected int id_minijuego, id_boton, id_mapa, pos_mapa;

	protected float width, height, alturaTitulo;

	protected string titulo = "";

	GUISkin titleSkin, fondoOpciones, btnOpciones, btnSonido, btnMusica, btnPausa, toast, puntuacionImagen, fin;

	Texture2D fondo;
	int[] puntuacion_restante;
	int puntuacion;

	int error;

	Texture2D premio = null;

	Rect[] rectOpciones, rectFin;

	protected int tiempo = 0;

	protected Dictionary<String, String> datos;

	protected bool mostrarToast = false;
	int tiempoToastActual = 0, tiempoToastVer = 150;

	protected virtual void Start ()
	{	
		gestorSQLite = GestorSQLite.getGestorSQLite ();
		terminado = false;
		datos = gestorSQLite.getMinijuego (id_minijuego);
		referenciasOpciones ();
		importarOpciones ();
		if (tiempo == 0) {
			InvokeRepeating ("aumentarTiempo", 0f, 0.001f);
		} else {
			tiempo = 0;
		}
	}

	void referenciasOpciones ()
	{
		rectOpciones = new Rect[14];
		rectOpciones [0] = new Rect (0, 0, Screen.width, Screen.height); 
		rectOpciones [1] = new Rect (Screen.width / 3, Screen.height / 12, Screen.width / 3, Screen.height / 9); //title
		rectOpciones [2] = new Rect (5 * Screen.width / 6, Screen.height / 10, Screen.width / 10, Screen.height / 9);
		rectOpciones [3] = new Rect (Screen.width / 3, Screen.height / 10, Screen.width / 3, 8 * Screen.height / 10);
		rectOpciones [4] = new Rect (rectOpciones [3].width / 4, rectOpciones [3].height / 12, rectOpciones [3].width / 2, 2f * rectOpciones [3].height / 11);
		rectOpciones [5] = new Rect (rectOpciones [3].width / 4, 3f * rectOpciones [3].height / 11, rectOpciones [3].width / 2, 1.5f * rectOpciones [3].height / 10);
		rectOpciones [6] = new Rect (rectOpciones [3].width / 4, 5 * rectOpciones [3].height / 11, rectOpciones [3].width / 2, 1.5f * rectOpciones [3].height / 10);
		rectOpciones [7] = new Rect (rectOpciones [3].width / 4, 7 * rectOpciones [3].height / 11, rectOpciones [3].width / 2, 1.5f * rectOpciones [3].height / 10);
		rectOpciones [8] = new Rect (rectOpciones [3].width / 2 - rectOpciones [3].width / 12, 9 * rectOpciones [3].height / 11, rectOpciones [3].width / 6, 1.5f * rectOpciones [3].height / 10);
		rectOpciones [9] = new Rect (rectOpciones [1].x + rectOpciones [1].width + rectOpciones [2].width / 3, rectOpciones [2].y, rectOpciones [2].width / 2, rectOpciones [2].height / 2);
		rectOpciones [10] = new Rect (Screen.width / 4, Screen.height / 4, 2 * Screen.width / 4, 2 * Screen.height / 4);
		rectOpciones [11] = new Rect (rectOpciones [10].width / 6, rectOpciones [10].height / 8, 2 * rectOpciones [10].width / 3, rectOpciones [10].height / 8);
		rectOpciones [12] = new Rect (rectOpciones [10].width / 8, 2 * rectOpciones [10].height / 8, 3 * rectOpciones [10].width / 4, 4 * rectOpciones [10].height / 8);
		rectOpciones [13] = new Rect (rectOpciones [10].width / 4, 6 * rectOpciones [10].height / 8, rectOpciones [10].width / 2, rectOpciones [10].height / 6);

		rectFin = new Rect[5];
		rectFin [0] = new Rect (Screen.width / 2 - Screen.width / 3, Screen.height / 10, 2 * Screen.width / 3, 8 * Screen.height / 10);
		rectFin [1] = new Rect (rectFin [0].width / 2 - rectFin [0].width / 3, rectFin [0].height / 8, 2 * rectFin [0].width / 3, rectFin [0].height / 8);
		rectFin [2] = new Rect (rectFin [0].width / 2 - rectFin [0].width / 3, rectFin [0].height / 8, 2 * rectFin [0].width / 3, 3 * rectFin [0].height / 8);
		rectFin [3] = new Rect (rectFin [0].width / 2 - rectFin [0].width / 4, 2 * rectFin [0].height / 8, rectFin [0].width / 2, 4 * rectFin [0].height / 8);
		rectFin [4] = new Rect (rectFin [0].width / 2 - rectFin [0].width / 6, 6 * rectFin [0].height / 8, rectFin [0].width / 3, rectFin [0].height / 8);
		width = Screen.width;
		alturaTitulo = rectOpciones [1].height + rectOpciones [1].y;
		height = Screen.height - alturaTitulo;
	}



	protected void inicializarPuntuacion (int cantidad)
	{
		puntuacion_restante = new int[3];
		for (int i = 0; i < puntuacion_restante.Length; i++) {
			puntuacion_restante [i] = cantidad / 3;
		}
		puntuacion = cantidad;
	}

	void importarOpciones ()
	{
		GUISkin[] skins = Resources.LoadAll<GUISkin> ("GUISkin/Opciones");
		for (int i = 0; i < skins.Length; i++) {
			if (skins [i].name == "pausaSkin") {
				btnPausa = skins [i];
			} else if (skins [i].name == "botonOpcionesSkin") {
				skins [i].button.fontSize = (int)Mathf.Min (rectOpciones [5].width, rectOpciones [5].height) / 3;
				skins [i].box.fontSize = (int)Mathf.Min (rectOpciones [4].width, rectOpciones [4].height) / 2;
				btnOpciones = skins [i];
			} else if (skins [i].name == "fondoOpcionesSkin") {
				fondoOpciones = skins [i];
			}
		}
		if (datos != null && datos.ContainsKey ("nombre")) {
			titulo = datos ["nombre"];
		} else {
			titulo = Diccionario.getPalabra ("SIN TITULO");
		}
		if (datos != null && datos.ContainsKey ("puntuacion_maxima")) {
			inicializarPuntuacion (Int32.Parse (datos ["puntuacion_maxima"]));
		} else {
			inicializarPuntuacion (6);
		}
		titleSkin = Resources.Load<GUISkin> ("GUISkin/Opciones/titleSkin");
		titleSkin.box.fontSize = (int)Mathf.Max (rectOpciones [1].width / titulo.Length, rectOpciones [1].height / titulo.Length);
		fondo = gestorSQLite.obtenerFondo (Screen.width, Screen.height);
		puntuacionImagen = Resources.Load<GUISkin> ("GUISkin/Opciones/llaveSkin");
		toast = Resources.Load<GUISkin> ("GUISkin/Opciones/toastSkin");
		fin = Resources.Load<GUISkin> ("GUISkin/Opciones/premioSkin");
	}

	void aumentarTiempo ()
	{
		if (!opcionesOn && !terminado && !descripcion) {
			tiempo++;
		}
		if (mostrarToast && tiempoToastActual > 0) {
			tiempoToastActual--;
			if (tiempoToastActual <= 0) {
				mostrarToast = false;
			}
		}
	}

	protected virtual void OnGUI ()
	{
		GUI.DrawTexture (rectOpciones [0], fondo);
		if (!opcionesOn && !terminado) {
			GUI.Box (rectOpciones [1], titulo, titleSkin.box);
			if (GUI.Button (rectOpciones [2], "", btnPausa.button)) {
				opcionesOn = !opcionesOn;
			}
			if (GUI.Button (rectOpciones [9], "", btnPausa.box)) {
				opcionesOn = !opcionesOn;
				descripcion = !descripcion;
			}
			for (int i = 0; i < puntuacion_restante.Length; i++) {			
				if (puntuacion_restante [i] != 0) {
					GUI.Box (new Rect ((1 + i) * Screen.width / 15, Screen.height / 10, Screen.width / 15, Screen.height / 9), "", puntuacionImagen.box);
				}
			}
		} else if (opcionesOn) {
			GUI.Box (rectOpciones [0], "", fondoOpciones.button);
			if (descripcion) {
				GUI.Window (2, rectOpciones [10], verDescripcion, "", fondoOpciones.box);
			} else {
				GUI.Window (1, rectOpciones [3], opciones, "", fondoOpciones.box);
			}
		} else if (terminado) {
			GUI.Box (rectOpciones [0], "", fondoOpciones.button);
			GUI.Window (3, rectFin [0], verFin, "", fondoOpciones.box);
		}
	}

	protected void activarToast ()
	{
		mostrarToast = true;
		tiempoToastActual = tiempoToastVer;
	}

	protected void verToast (String texto)
	{
		if (mostrarToast) {
			GUI.Label (new Rect (rectOpciones [1].x, 7 * Screen.height / 9, rectOpciones [1].width, rectOpciones [1].height), texto, toast.box);
		}
	}

	void opciones (int windowID)
	{
		GUI.Box (rectOpciones [4], Diccionario.getPalabra ("PAUSA"), btnOpciones.box);
		if (GUI.Button (rectOpciones [5], Diccionario.getPalabra ("Continuar"), btnOpciones.button)) {
			opcionesOn = !opcionesOn;
		}
		if (GUI.Button (rectOpciones [6], Diccionario.getPalabra ("Reiniciar"), btnOpciones.button)) {
			reiniciarNivel ();
		}
		if (GUI.Button (rectOpciones [7], Diccionario.getPalabra ("Abandonar"), btnOpciones.button)) {
			Constantes.setSincronizar (true);
			Instantiate (Resources.Load<GameObject> ("Prefab/Mapa"));
			Destroy (this.gameObject);
		}
	}

	void verDescripcion (int windowID)
	{
		GUI.Box (rectOpciones [11], Diccionario.getPalabra ("OBJETIVOS"), btnOpciones.label);
		GUI.Box (rectOpciones [12], datos ["descripcion"], btnOpciones.textField);
		if (GUI.Button (rectOpciones [13], Diccionario.getPalabra ("Continuar"), btnOpciones.button)) {
			opcionesOn = !opcionesOn;
			descripcion = !descripcion;
		}
	}

	void verFin (int windowID)
	{
		if (puntuacion == 0) {
			GUI.Box (rectFin [2], Diccionario.getPalabra ("Sin llave"), btnOpciones.label);
		} else {
			GUI.Box (rectFin [1], Diccionario.getPalabra ("Con llave"), btnOpciones.label);
			if (premio != null) {
				GUIContent contenido = new GUIContent ();
				contenido.text = Diccionario.getPalabra ("Con Premio");
				contenido.image = premio;
				GUI.Box (rectFin [3], contenido, fin.box);
			}
		}
		if (GUI.Button (rectFin [4], Diccionario.getPalabra ("Continuar"), btnOpciones.button)) {
			Constantes.setSincronizar (true);
			Instantiate (Resources.Load<GameObject> ("Prefab/Mapa"));
			Destroy (this);
		}
	}

	protected void restarPuntuacion ()
	{
		bool hecho = false;
		for (int i = puntuacion_restante.Length - 1; i > -1 && !hecho; i--) {
			if (puntuacion_restante [i] > 0) {
				puntuacion_restante [i]--;
				puntuacion--;
				hecho = true;
			}
		}
		error++;
	}

	protected void terminarJuego ()
	{	
		terminado = true;
		bool completo = true;
		if (puntuacion == 0) {
			completo = false;
		}
		int vidas = 0;
		for (int i = puntuacion_restante.Length - 1; i > -1; i--) {
			if (puntuacion_restante [i] > 0)
				vidas++;
		}
		gestorSQLite.insertarPuntuacion (id_minijuego, id_mapa, pos_mapa, id_boton, completo, vidas, error, tiempo / 1000);
		if (completo && UnityEngine.Random.Range (0, 6) == 0) {
			premio = gestorSQLite.obtenerPremioRandom (Mathf.FloorToInt (rectFin [3].width), Mathf.FloorToInt (rectFin [3].height));
		}
	}

	protected void reiniciarNivel ()
	{
		this.gameObject.GetComponent<Minijuego> ().Start ();
		opcionesOn = false;
	}

	public void setInformacion (int id_minijuego, int id_mapa, int pos_mapa, int id_boton)
	{
		this.id_minijuego = id_minijuego;
		this.id_mapa = id_mapa;
		this.pos_mapa = pos_mapa;
		this.id_boton = id_boton;
	}

	protected object oscurecer (object campo)
	{
		if (campo.GetType () == typeof(Texture2D)) {
			
			Color[] color = ((Texture2D)campo).GetPixels ();
			for (int i = 0; i < color.Length; i++) {
				color [i].a = 0.2f;
			}
			((Texture2D)campo).SetPixels (color);
			((Texture2D)campo).Apply ();
			return campo;

		} else if (campo.GetType () == typeof(String)) {
			return "<color=silver>" + campo.ToString () + "</color>";
		}
		return null;
	}
}
