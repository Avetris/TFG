using UnityEngine;
using System.Collections;
using UnityEngine.EventSystems;
using System.Collections.Generic;
using System;

public class Parejas : Minijuego
{
	int tamanoX, tamanoY;

	int tiempoAux;

	int[][] pulsado;
	bool[][] mostrado;

	Dictionary<String, object>[][] campos;

	Rect[][] rect;

	GUISkin ficha;

	// Use this for initialization
	protected override void Start ()
	{
		base.Start ();
		obtenerDatos ();
		referencia ();
		desordenar ();	
	}

	void referencia ()
	{
		rect = new Rect[tamanoX][];
		for (int i = 0; i < tamanoX; i++) {
			rect [i] = new Rect[tamanoY];
			for (int j = 0; j < tamanoY; j++) {
				rect [i] [j] = new Rect (width / (2 + tamanoX) * (i + 1), height / (2 + 2 * tamanoY) * (2 * j + 3), width / (2 + tamanoX), 2 * height / (2 + 2 * tamanoY));
			}
		}
		ficha = Resources.Load<GUISkin> ("GUISkin/Parejas/casillasSkin");
	}

	void obtenerDatos ()
	{
		object[][] listaDatos = gestorSQLite.obtenerParejas (base.id_minijuego, Int32.Parse (base.datos ["maximo"]), Int32.Parse (base.datos ["minimo"]));
		bool imagen = true;
		if (listaDatos [0] [1] == null) {
			imagen = false;
		}
		int tamano = listaDatos.Length * 2;	
		if (Mathf.Sqrt (tamano) % 1 == 0) {
			tamanoX = (int)Mathf.Sqrt (tamano);
			tamanoY = (int)Mathf.Sqrt (tamano);
		} else {
			int numSqrt = (int)Mathf.Sqrt (tamano);
			int numDiv = Mathf.CeilToInt ((float)tamano / (float)numSqrt);
			tamanoX = Mathf.Max (numSqrt, numDiv);
			tamanoY = Mathf.Min (numSqrt, numDiv);
		}
		campos = new Dictionary<String, object>[tamano / 2][];
		for (int i = 0; i < campos.Length; i++) {
			campos [i] = new Dictionary<String, object>[2];
			campos [i] [0] = new Dictionary<String, object> ();
			campos [i] [1] = new Dictionary<String, object> ();
			campos [i] [0].Add ("Valor", listaDatos [i] [0]);
			campos [i] [0].Add ("Identificador", i);
			if (imagen) {
				campos [i] [1].Add ("Valor", listaDatos [i] [1]);
			} else {
				campos [i] [1].Add ("Valor", listaDatos [i] [0]);
			}
			campos [i] [1].Add ("Identificador", i);
		}
		pulsado = new int[2][];
		for (int i = 0; i < pulsado.Length; i++) {
			pulsado [i] = new int[2];
			pulsado [i] [0] = -1;
			pulsado [i] [1] = -1;
		}
	}

	void desordenar ()
	{	
		Dictionary<String, object>[][] aux = new Dictionary<String, object>[tamanoX][]; 
		List<int[]> posiciones = new List<int[]> ();
		for (int i = 0; i < campos.Length; i++) {
			for (int j = 0; j < campos [i].Length; j++) {
				posiciones.Add (new int[]{ i, j });
			}
		}
		mostrado = new bool[aux.Length][];
		int[] num;
		for (int i = 0; i < aux.Length; i++) {
			aux [i] = new Dictionary<String, object>[tamanoY];
			mostrado [i] = new bool[aux [i].Length];
			for (int j = 0; j < aux [i].Length; j++) {
				num = posiciones [UnityEngine.Random.Range (0, posiciones.Count)];
				if (campos [num [0]] [num [1]] ["Valor"] != null) {
					aux [i] [j] = new Dictionary<String, object> ();
					aux [i] [j].Add ("Valor", campos [num [0]] [num [1]] ["Valor"]);
					aux [i] [j].Add ("Identificador", campos [num [0]] [num [1]] ["Identificador"]);
					if (aux [i] [j] ["Valor"].GetType () == typeof(Texture2D)) {
						TextureScale.Point ((Texture2D)aux [i] [j] ["Valor"], (int)rect [i] [j].width - 20, (int)rect [i] [j].height - 20);
					}
					campos [num [0]] [num [1]] ["Valor"] = null;
					mostrado [i] [j] = false;
				}
				posiciones.Remove (num);
			}
		}
		campos = aux;
	}

	// Update is called once per frame
	void Update ()
	{
		if (pulsado [0] [0] != -1 && pulsado [1] [0] != -1) {
			if (base.tiempo > tiempoAux + 100) {
				reiniciar ();
			}
		}
	}

	protected override void OnGUI ()
	{
		base.OnGUI ();
		if (!base.opcionesOn) {
			for (int i = 0; i < campos.Length; i++) {
				for (int j = 0; j < campos [i].Length; j++) {
					if (mostrado [i] [j]) {
						if (campos [i] [j] ["Valor"].GetType () == typeof(String)) {
							GUI.Box (rect [i] [j], campos [i] [j] ["Valor"].ToString (), ficha.button);
						} else {
							GUI.Box (rect [i] [j], (Texture2D)campos [i] [j] ["Valor"], ficha.box);
						}
					} else {
						if (comprobarPulsado (i, j)) {
							if (campos [i] [j] ["Valor"].GetType () == typeof(String)) {
								if (GUI.Button (rect [i] [j], campos [i] [j] ["Valor"].ToString (), ficha.button)) {
									pulsar (i, j);
								}
							} else {
								if (GUI.Button (rect [i] [j], (Texture2D)campos [i] [j] ["Valor"], ficha.box)) {
									pulsar (i, j);
								}
							}
						} else {							
							if (GUI.Button (rect [i] [j], "", ficha.button)) {
								pulsar (i, j);
							}							
						}
					}
				}
			}
		}
	}

	bool comprobarPulsado (int pI, int pJ)
	{
		bool esta = false;
		if (pulsado [0] [0] == pI && pulsado [0] [1] == pJ) {
			esta = true;
		} else if (pulsado [1] [0] == pI && pulsado [1] [1] == pJ) {
			esta = true;
		} else {
			esta = false;
		}
		return esta;
	}

	void pulsar (int pI, int pJ)
	{
		if (pulsado [0] [0] == -1 || pulsado [0] [1] == -1 || pulsado [1] [0] == -1 || pulsado [1] [1] == -1) {
			if (pulsado [0] [0] == pI && pulsado [0] [1] == pJ) {
				pulsado [0] [0] = -1;
				pulsado [0] [1] = -1;
			} else if (pulsado [1] [0] == pI && pulsado [1] [1] == pJ) {
				pulsado [1] [0] = -1;
				pulsado [1] [1] = -1;
			} else if (pulsado [0] [0] == -1) {
				pulsado [0] [0] = pI;
				pulsado [0] [1] = pJ;
			} else if (pulsado [1] [0] == -1) {
				pulsado [1] [0] = pI;
				pulsado [1] [1] = pJ;
			}
			if (pulsado [0] [0] != -1 && pulsado [1] [0] != -1) {
				if (comprobarCorrecto ()) {
					mostrado [pulsado [0] [0]] [pulsado [0] [1]] = true;
					mostrado [pulsado [1] [0]] [pulsado [1] [1]] = true;
					reiniciar ();
					if (comprobarCompleto ()) {
						base.terminarJuego ();
					}
				} else {
					base.restarPuntuacion ();
					tiempoAux = base.tiempo;
				}
			}
		}
	}

	bool comprobarCorrecto ()
	{
		bool correcto = false;
		int val1 = (int)campos [pulsado [0] [0]] [pulsado [0] [1]] ["Identificador"];
		int val2 = (int)campos [pulsado [1] [0]] [pulsado [1] [1]] ["Identificador"];
		if (val1 == val2) {
			correcto = true;
			campos [pulsado [0] [0]] [pulsado [0] [1]] ["Valor"] = base.oscurecer (campos [pulsado [0] [0]] [pulsado [0] [1]] ["Valor"]);
			campos [pulsado [1] [0]] [pulsado [1] [1]] ["Valor"] = base.oscurecer (campos [pulsado [1] [0]] [pulsado [1] [1]] ["Valor"]);
		}
		return correcto;
	}

	bool comprobarCompleto ()
	{
		bool completo = true;
		for (int i = 0; i < tamanoX && completo; i++) {
			for (int j = 0; j < tamanoY && completo; j++) {
				if (!mostrado [i] [j])
					completo = false;
			}
		}
		return completo;
	}

	void reiniciar ()
	{
		pulsado [0] [0] = -1;
		pulsado [0] [1] = -1;
		pulsado [1] [0] = -1;
		pulsado [0] [1] = -1;
	}
}
