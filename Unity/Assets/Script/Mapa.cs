using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System;
using UnityEngine.SceneManagement;
using System.Threading;

public class Mapa : MonoBehaviour
{
	Dictionary<string, object[]>[] botones;
	Rect[] rectMapas;
	Texture2D[] imagenMapas;
	GUISkin botonesSkin, fondoBlanco, fondoOpciones, btnOpciones, botonesSuperior, sincronizando;
	int[] id_mapas;
	int[] pos_mapas;
	Texture niebla, muralla;
	Vector2 origen;
	public Vector2 scrollPosition = Vector2.zero;

	List<object[]> premios;

	int botonActivado;

	Vector2 scrollViewVector;

	ScrollRect scroll;

	GestorSQLite gestorSQLite;

	Rect[] rectMenuSuperior, rectPremios, rectOpciones, rectSincronizando;

	Constantes constantes = Constantes.getConstantes ();

	string cargando;

	// Use this for initialization
	void Start ()
	{

		botonActivado = 0;
		gestorSQLite = GestorSQLite.getGestorSQLite ();
		constantes = Constantes.getConstantes ();
		gestionarMapas ();
		getPremios ();
		getOpciones ();
		niebla = Resources.Load<Texture> ("Images/niebla");
		muralla = Resources.Load<Texture> ("Images/muralla");
		scrollPosition = new Vector2 (0, -1 * rectMapas [rectMapas.Length - 1].y);
		if (Constantes.getSincronizar ()) {
			Constantes.setSincronizar (false);
			botonActivado = 3;
			cargando = "";
			try {
				Sync sync = Sync.getSync ();
				Thread t = new Thread (new ThreadStart (sync.sincronizacion));
				t.Start ();
			} catch (Exception e) {
				Debug.Log (e);
				this.botonActivado = 0;
			}
		}
		InvokeRepeating ("cambioCargando", 1f, 1f);
	}

	void gestionarMapas ()
	{
		int[][] historial = gestorSQLite.obtenerHistorialMapas ();
		id_mapas = new int[3];
		pos_mapas = new int[3];
		rectMapas = new Rect[4];
		rectMenuSuperior = new Rect[2];
		rectMenuSuperior [0] = new Rect (Screen.width / 19, Screen.height / 25, 3 * Screen.width / 19, 2 * Screen.height / 20);
		rectMenuSuperior [1] = new Rect (15 * Screen.width / 19, Screen.height / 25, 3 * Screen.width / 19, 2 * Screen.height / 20);
		for (int i = 0; i < rectMapas.Length; i++) {
			rectMapas [i] = new Rect (0, -1 * i * Screen.height, Screen.width, Screen.height);
		}
		for (int i = 0; i < id_mapas.Length; i++)
			id_mapas [i] = -1;
		for (int i = 0, j = 0; i < historial.Length && j < id_mapas.Length; i++) {
			if (historial [i] [1] != pos_mapas [0] && historial [i] [1] != pos_mapas [1] && historial [i] [1] != pos_mapas [2]) {
				id_mapas [j] = historial [i] [0];
				pos_mapas [j] = historial [i] [1];
				j++;
			}
		}
		imagenMapas = gestorSQLite.obtenerMapas (Mathf.RoundToInt (rectMapas [0].width), Mathf.RoundToInt (rectMapas [0].height), id_mapas);
		botones = gestorSQLite.obtenerBotones (rectMapas [0].width, rectMapas [0].height, pos_mapas);
		botonesSkin = Resources.Load<GUISkin> ("GUISkin/Mapa/botonesSkin");
		botonesSuperior = Resources.Load<GUISkin> ("GUISkin/Mapa/botonesSuperiorSkin");
		sincronizando = Resources.Load<GUISkin> ("GUISkin/Mapa/sincronizarSkin");
		rectSincronizando = new Rect[2];
		rectSincronizando [0] = new Rect (Screen.width / 4, Screen.height / 7, Screen.width / 2, 5 * Screen.height / 7);
		rectSincronizando [1] = new Rect (rectSincronizando [0].width / 5, rectSincronizando [0].height / 5, 3 * rectSincronizando [0].width / 5, 3 * rectSincronizando [0].height / 5);
	}

	void getPremios ()
	{
		fondoBlanco = Resources.Load<GUISkin> ("GUISkin/SopaDeLetras/cuadroSkin");
		rectPremios = new Rect[4];
		rectPremios [0] = new Rect (Screen.width / 2 - 4 * Screen.width / 10, Screen.height / 2 - 4 * Screen.height / 10, 4 * Screen.width / 5, 4 * Screen.height / 5);
		rectPremios [1] = new Rect (rectPremios [0].width / 2 - 2 * rectPremios [0].width / 6, rectPremios [0].height / 12, 2 * rectPremios [0].width / 3, rectPremios [0].height / 12);
		rectPremios [2] = new Rect (0, rectPremios [0].height / 6, rectPremios [0].width, 4 * rectPremios [0].height / 6);
		rectPremios [3] = new Rect (rectPremios [0].width / 2 - 2 * rectPremios [0].width / 6, 11 * rectPremios [0].height / 13, 2 * rectPremios [0].width / 3, rectPremios [0].height / 12);
		premios = gestorSQLite.obtenerTodosPremios (Mathf.FloorToInt (rectPremios [2].width / 3));
	}

	void getOpciones ()
	{
		rectOpciones = new Rect[14];
		rectOpciones [0] = new Rect (0, 0, Screen.width, Screen.height); 
		rectOpciones [1] = new Rect (Screen.width / 3, Screen.height / 10, Screen.width / 3, 8 * Screen.height / 10);
		rectOpciones [2] = new Rect (rectOpciones [1].width / 2 - rectOpciones [1].width / 3, rectOpciones [1].height / 12, 2 * rectOpciones [1].width / 3, 2f * rectOpciones [1].height / 11);
		rectOpciones [3] = new Rect (rectOpciones [1].width / 4, 3f * rectOpciones [1].height / 11, rectOpciones [1].width / 2, 1.5f * rectOpciones [1].height / 10);
		rectOpciones [4] = new Rect (rectOpciones [1].width / 4, 5 * rectOpciones [1].height / 11, rectOpciones [1].width / 2, 1.5f * rectOpciones [1].height / 10);
		rectOpciones [5] = new Rect (rectOpciones [1].width / 4, 7 * rectOpciones [1].height / 11, rectOpciones [1].width / 2, 1.5f * rectOpciones [1].height / 10);
		rectOpciones [6] = new Rect (rectOpciones [1].width / 2 - rectOpciones [1].width / 3, 9 * rectOpciones [1].height / 11, 2 * rectOpciones [1].width / 3, 1.5f * rectOpciones [1].height / 10);
		GUISkin[] skins = Resources.LoadAll<GUISkin> ("GUISkin/Opciones");
		for (int i = 0; i < skins.Length; i++) {
			if (skins [i].name == "botonOpcionesSkin") {
				skins [i].button.fontSize = (int)Mathf.Min (rectOpciones [5].width, rectOpciones [5].height) / 3;
				skins [i].box.fontSize = (int)Mathf.Min (rectOpciones [4].width, rectOpciones [4].height) / 2;
				btnOpciones = skins [i];
			} else if (skins [i].name == "fondoOpcionesSkin") {
				fondoOpciones = skins [i];
			}
		}

	}
	
	// Update is called once per frame
	void Update ()
	{
		if (botonActivado == 0)
			tocar ();
		else if (botonActivado == 3 && Sync.getSync ().getErrorCode () != 0) {
			botonActivado = 0;
		} else if (botonActivado == 4) {
			if (Sync.getSync ().getErrorCode () == 1) {
				SceneManager.LoadScene ("LoginScene");
			} else if (Sync.getSync ().getErrorCode () == -1) {
				botonActivado = 0;
			}
		}
	}

	void OnGUI ()
	{
		if (imagenMapas != null) {
			scrollPosition = GUI.BeginScrollView (new Rect (0, 0, Screen.width, Screen.height), scrollPosition, new Rect (0, rectMapas [rectMapas.Length - 1].y, Screen.width, Screen.height * rectMapas.Length), false, false, GUIStyle.none, GUIStyle.none);
			for (int i = 0; i < rectMapas.Length - 1; i++) {
				GUI.BeginGroup (rectMapas [i]);		
				GUI.DrawTexture (new Rect (0, 0, Screen.width, Screen.height), imagenMapas [i]);
				if (i != 0)
					GUI.DrawTexture (new Rect (0, Screen.height - 50, Screen.width, 50), muralla);

				if (botonActivado < 2) {
					for (int j = 0; botones [i].ContainsKey ("botones_" + j); j++) {
						if (botones [i] ["botones_" + j] [2].Equals ("Actual")) {
							if (GUI.Button ((Rect)botones [i] ["botones_" + j] [1], "", botonesSkin.box)) {								
								GameObject game = Resources.Load<GameObject> ("Prefab/Minijuegos/" + constantes.getMinijuego ((int)botones [i] ["botones_" + j] [3]));
								game = Instantiate (game, this.transform.position, game.transform.rotation);
								game.GetComponent<Minijuego> ().setInformacion ((int)botones [i] ["botones_" + j] [3], id_mapas [i], pos_mapas [i], (int)botones [i] ["botones_" + j] [0]);
								Destroy (this.gameObject);
							}
						} else if (botones [i] ["botones_" + j] [2].Equals ("Futuro")) {
							GUI.Box ((Rect)botones [i] ["botones_" + j] [1], "", botonesSkin.label);
						} else if (botones [i] ["botones_" + j] [2].Equals ("Pasado")) {
							GUI.Box ((Rect)botones [i] ["botones_" + j] [1], "", botonesSkin.button);
						}

					}
				}
				GUI.EndGroup ();
			} 
			GUI.BeginGroup (rectMapas [rectMapas.Length - 1]);	
			GUI.DrawTexture (new Rect (0, 0, Screen.width, Screen.height), niebla);
			GUI.DrawTexture (new Rect (0, Screen.height - muralla.height, Screen.width, muralla.height), muralla);
			GUI.EndGroup ();
			GUI.EndScrollView ();
			if (botonActivado == 0) {
				if (GUI.Button (rectMenuSuperior [0], Diccionario.getPalabra ("Opciones"), botonesSuperior.box)) {
					botonActivado = 1;
				}
				if (GUI.Button (rectMenuSuperior [1], Diccionario.getPalabra ("Premios"), botonesSuperior.box)) {
					botonActivado = 2;
				}
			}
			switch (botonActivado) {
			case 1:
				GUI.Box (rectOpciones [0], "", fondoOpciones.button);
				GUI.Window (1, rectOpciones [1], verOpciones, "", fondoOpciones.box);
				break;
			case 2:
				GUI.Box (rectOpciones [0], "", fondoOpciones.button);
				GUI.Window (2, rectPremios [0], verPremios, "", fondoBlanco.box);
				break;
			case 3:
				GUI.Box (rectOpciones [0], "", fondoOpciones.button);
				GUI.Window (3, rectSincronizando [0], verSincronizar, "", fondoOpciones.box);
				break;
			case 4:
				GUI.Box (rectOpciones [0], "", fondoOpciones.button);
				GUI.Window (3, rectSincronizando [0], verDesconectar, "", fondoOpciones.box);
				break;
			}
		}
	}

	void verOpciones (int windowID)
	{
		GUI.Box (rectOpciones [2], Diccionario.getPalabra ("OPCIONES"), btnOpciones.box);
		if (GUI.Button (rectOpciones [3], Diccionario.getPalabra ("Castellano"), btnOpciones.button)) {
			PlayerPrefs.SetString ("idioma", "castellano");
		}
		if (GUI.Button (rectOpciones [4], Diccionario.getPalabra ("Euskera"), btnOpciones.button)) {
			PlayerPrefs.SetString ("idioma", "euskera");
		}
		if (GUI.Button (rectOpciones [5], Diccionario.getPalabra ("Continuar"), btnOpciones.button)) {
			botonActivado = 0;
		}
		if (GUI.Button (rectOpciones [6], Diccionario.getPalabra ("Desconectar"), btnOpciones.button)) {
			botonActivado = 4;
			cargando = "";
			Sync sync = Sync.getSync ();
			sync.setErrorCode (0);
			new Thread (new ThreadStart (sync.actualizarNube)).Start ();
		}
	}

	void verPremios (int windowID)
	{
		GUI.Box (rectPremios [1], "<html><color=#000000>" + Diccionario.getPalabra ("PREMIOS") + "</color></html>", btnOpciones.box);
		int contx = 0;
		int conty = 0;
		for (int i = 0; i < premios.Count; i++) {
			if (premios [i] [2].Equals ("Si")) {
				GUI.DrawTexture (new Rect (rectPremios [2].x + (contx) * rectPremios [2].width / 3, rectPremios [2].y + (conty) * rectPremios [2].width / 3, rectPremios [2].width / 3, rectPremios [2].width / 6), (Texture2D)premios [i] [1]);
			} else {
				GUI.DrawTexture (new Rect (rectPremios [2].x + (contx) * rectPremios [2].width / 3, rectPremios [2].y + (conty) * rectPremios [2].width / 3, rectPremios [2].width / 3, rectPremios [2].width / 6), (Texture2D)oscurecer (premios [i] [1]));
			}
			contx++;
			if (contx >= 3) {
				contx = 0;
				conty++;
			}
		}
		if (GUI.Button (rectPremios [3], Diccionario.getPalabra ("Atras"), btnOpciones.button)) {
			botonActivado = 0;
		}
	}

	void verSincronizar (int windowID)
	{
		GUI.Box (rectSincronizando [1], Diccionario.getPalabra ("Cargando_Energia") + cargando, sincronizando.box);
	}

	void verDesconectar (int windowID)
	{
		GUI.Box (rectSincronizando [1], Diccionario.getPalabra ("Desconectando") + cargando, sincronizando.box);
	}


	//control de touch
	void tocar ()
	{
		foreach (Touch touch in Input.touches) {
			if (touch.phase == TouchPhase.Moved) {
				scrollPosition.y += touch.deltaPosition.y * 4;
			}
		}
	}

	object oscurecer (object campo)
	{
		if (campo.GetType () == typeof(Texture2D)) {
			
			Color[] color = ((Texture2D)campo).GetPixels ();
			for (int i = 0; i < color.Length; i++) {
				if (color [i].a > 0 || color [i].r > 0 || color [i].b > 0) {
					color [i].r = 0f;
					color [i].g = 0f;
					color [i].b = 0f;
				}
			}
			((Texture2D)campo).SetPixels (color);
			((Texture2D)campo).Apply ();
			return campo;

		} else if (campo.GetType () == typeof(String)) {
			return "<color=silver>" + campo.ToString () + "</color>";
		}
		return null;
	}

	void cambioCargando ()
	{
		if (botonActivado == 4 || botonActivado == 3) {
			if (cargando.Equals ("")) {
				cargando = ".";
			} else if (cargando.Equals (".")) {
				cargando = "..";

			} else if (cargando.Equals ("..")) {
				cargando = "...";

			} else {
				cargando = "";
			}
		}
	}
}
