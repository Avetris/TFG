using UnityEngine;
using System.Collections;
using System.Text.RegularExpressions;
using System;
using System.Collections.Generic;

public class PalabrasIncompletas : Minijuego
{
	Dictionary<String, object[]>[] campos;

	Dictionary<String, bool> mal = new Dictionary<String, bool> ();

	string[] letras = new string[] {
		"_",
		"A",
		"B",
		"C",
		"D",
		"E",
		"F",
		"G",
		"H",
		"I",
		"J",
		"K",
		"L",
		"M",
		"N",
		"Ñ",
		"O",
		"P",
		"Q",
		"R",
		"S",
		"T",
		"U",
		"V",
		"W",
		"X",
		"Y",
		"Z"
	};
	int[] seleccionado;

	string letra = "";


	GUISkin skin, botonSkin;

	Rect botonRect;
	Rect[][] rectLetras;
	Rect[] rectImagenes, rectPalabras, rectBotonesLetras;
	// Use this for initialization
	protected override void Start ()
	{
		base.Start ();
		obtenerDatos ();
		referencia ();
	}

	void referencia ()
	{
		char[][] listaPalabras = new char[campos.Length][];
		for (int i = 0; i < campos.Length; i++) {
			listaPalabras [i] = new char[campos [i] ["Mostrar"].Length];
			Array.Copy (campos [i] ["Mostrar"], listaPalabras [i], campos [i] ["Mostrar"].Length);
		}
		int xmax = 3, ymax = 2;
		if (campos.Length > 8) {
			ymax = 2;
		} else {
			ymax = 2;
		}
		xmax = listaPalabras.Length / ymax;
		Rect[][] aux = new Rect[xmax][];
		//Rect de auxiliar de imagenes y palabras completas
		for (int i = 0; i < aux.Length; i++) {
			aux [i] = new Rect[ymax * 2];
			for (int j = 0; j < aux [i].Length; j++) {
				if (j % 2 == 0) {
					aux [i] [j] = new Rect ((3 + i * 4) * width / (5 + xmax * 4), alturaTitulo + (j * 2 + 1) * base.height / (3 + ymax * 4f), 3f * width / (3 + xmax * 4), 2 * base.height / (3 + ymax * 4f));
				} else {
					aux [i] [j] = new Rect ((3 + i * 4) * width / (5 + xmax * 4), alturaTitulo + (j * 2 + 1) * base.height / (3 + ymax * 4f), 3f * width / (3 + xmax * 4), base.height / (3 + ymax * 4f));
				}
			}
		}
		rectLetras = new Rect[listaPalabras.Length][];
		Rect rectActual;
		int k = 0, t = 1;
		char[] palabraActual;
		//Rect de letras
		for (int i = 0; i < rectLetras.Length; i++) {
			rectLetras [i] = new Rect[listaPalabras [i].Length];
			rectActual = aux [k] [t];
			t += 2;
			if (t == ymax * 2 || t > ymax * 2) {
				k++;
				t = 1;
			}
			palabraActual = listaPalabras [i];
			for (int j = 0; j < rectLetras [i].Length; j++) {
				rectLetras [i] [j] = new Rect (rectActual.x + j * rectActual.width / (palabraActual.Length + 1), rectActual.y, rectActual.width / (palabraActual.Length + 1), rectActual.height);
			}
		}

		rectImagenes = new Rect[listaPalabras.Length];
		rectPalabras = new Rect[listaPalabras.Length];

		//Rect de imagenes
		int contIm = 0, contPal = 0;
		for (int i = 0; i < aux.Length; i++) {
			for (int j = 0; j < aux [i].Length; j++) {
				if (j % 2 == 0) {
					if (contIm < rectImagenes.Length) {
						rectImagenes [contIm] = aux [i] [j];
						contIm++;
					}
				} else {
					if (contPal < rectPalabras.Length) {
						rectPalabras [contPal] = aux [i] [j];
						contPal++;
					}
				}
			}
		}

		//Rect de botones de letras
		rectBotonesLetras = new Rect[letras.Length];
		int alt = 0;
		for (int i = 0; i < letras.Length; i++) {
			if (i < letras.Length / 2) {
				rectBotonesLetras [i] = new Rect (width / (5 + xmax * 8), alturaTitulo + (alt + 1) * base.height / (letras.Length / 2 + 2), 3 * width / (3 + xmax * 8), base.height / (letras.Length / 2 + 2));
			} else {
				rectBotonesLetras [i] = new Rect ((2 + xmax * 4) * width / (5 + xmax * 4) + width / (5 + xmax * 8), alturaTitulo + (alt + 1) * base.height / (letras.Length / 2 + 2), 3 * width / (3 + xmax * 8), base.height / (letras.Length / 2 + 2));
			}
			alt++;
			if (alt >= letras.Length / 2) {
				alt = 0;
			}
		}
		botonRect = new Rect (width / 4, alturaTitulo + (ymax * 4 + 1) * base.height / (3 + ymax * 4f), width / 2, base.height / (3 + ymax * 2f));
		skin = Resources.Load<GUISkin> ("GUISkin/PalabrasIncompletas/skin");
		botonSkin = Resources.Load<GUISkin> ("GUISkin/PalabrasIncompletas/botonSkin");
		botonSkin.button.fontSize = (int)Mathf.Min (botonRect.width, botonRect.height) / 3;
		skin.box.fontSize = (int)Mathf.Min (rectLetras [0] [0].width, rectLetras [0] [0].height) / 2;
		skin.toggle.fontSize = (int)Mathf.Min (rectLetras [0] [0].width, rectLetras [0] [0].height) / 2;		
		skin.button.fontSize = (int)Mathf.Min (rectLetras [0] [0].width, rectLetras [0] [0].height) / 2;	
		skin.label.fontSize = (int)Mathf.Min (rectLetras [0] [0].width, rectLetras [0] [0].height) / 2;

	}

	void obtenerDatos ()
	{
		object[][] listaDatos = gestorSQLite.obtenerPalabrasIncompletas (base.id_minijuego, Int32.Parse (base.datos ["maximo"]), Int32.Parse (base.datos ["minimo"]));
		campos = new Dictionary<string, object[]>[listaDatos.Length];
		for (int i = 0; i < campos.Length; i++) {
			campos [i] = new Dictionary<string, object[]> ();
			object[] aux = new object[listaDatos [i] [0].ToString ().ToCharArray ().Length];
			Array.Copy (listaDatos [i] [0].ToString ().ToCharArray (), aux, listaDatos [i] [0].ToString ().ToCharArray ().Length);
			campos [i].Add ("Palabra", aux);
			campos [i].Add ("Imagen", new object[]{ listaDatos [i] [1] });
			aux = new object[listaDatos [i] [2].ToString ().ToCharArray ().Length];
			Array.Copy (listaDatos [i] [2].ToString ().ToCharArray (), aux, listaDatos [i] [2].ToString ().ToCharArray ().Length);
			campos [i].Add ("Mostrar", aux);
		}
		seleccionado = new int[2];
		seleccionado [0] = -1;
		seleccionado [1] = -1;

		for (int i = 0; i < campos.Length; i++) {
			object[] relleno = new object[campos [i] ["Mostrar"].Length];
			for (int j = 0; j < relleno.Length; j++) {
				if (campos [i] ["Mostrar"] [j].ToString ().Equals ("_")) {
					relleno [j] = true;
				} else {
					relleno [j] = false;
				}
			}
			campos [i].Add ("Relleno", relleno);
		}
	}

	protected override void OnGUI ()
	{
		base.OnGUI ();
		if (!base.opcionesOn) {
			for (int i = 0; i < rectPalabras.Length; i++) {
				GUI.Box (rectPalabras [i], "", skin.button);
				GUI.Box (rectImagenes [i], (Texture2D)campos [i] ["Imagen"] [0], skin.button);
				for (int j = 0; j < rectLetras [i].Length; j++) {
					letra = campos [i] ["Mostrar"] [j].ToString ();
					if ((bool)campos [i] ["Relleno"] [j]) {
						if (seleccionado [0] == i && seleccionado [1] == j) {
							if (GUI.Button (rectLetras [i] [j], campos [i] ["Mostrar"] [j].ToString (), skin.box)) {
								seleccionado [0] = -1;
								seleccionado [1] = -1;
							}
						} else {
							if (mal.ContainsKey (i + ", " + j) && mal [i + ", " + j]) {
								if (GUI.Button (rectLetras [i] [j], campos [i] ["Mostrar"] [j].ToString (), skin.label)) {
									seleccionado [0] = i;
									seleccionado [1] = j;
								}
							} else {
								if (GUI.Button (rectLetras [i] [j], campos [i] ["Mostrar"] [j].ToString (), skin.toggle)) {
									seleccionado [0] = i;
									seleccionado [1] = j;
								}
							}
						}
					} else {
						GUI.Box (rectLetras [i] [j], letra, skin.button);
					}
				}
			}
			if (seleccionado [0] != -1 && seleccionado [1] != -1) {
				for (int i = 0; i < rectBotonesLetras.Length; i++) {
					if (GUI.Button (rectBotonesLetras [i], letras [i], botonSkin.button)) {					
						campos [seleccionado [0]] ["Mostrar"] [seleccionado [1]] = letras [i].ToCharArray () [0]; 
						if (mal.ContainsKey (seleccionado [0] + ", " + seleccionado [1]) && mal [seleccionado [0] + ", " + seleccionado [1]]) {
							mal [seleccionado [0] + ", " + seleccionado [1]] = false;
						}
					}
				}
			}
			if (GUI.Button (botonRect, "COMPROBAR", botonSkin.box)) {
				seleccionado [0] = -1;
				seleccionado [1] = -1;
				comprobarCorrecto ();
				if (mal.Count == 0) {
					base.terminarJuego ();
				} else {					
					base.restarPuntuacion ();
				}
			}
		}
	}

	public void comprobarCorrecto ()
	{
		mal.Clear ();
		for (int i = 0; i < campos.Length; i++) {
			for (int j = 0; j < campos [i] ["Palabra"].Length; j++) {
				if (!campos [i] ["Mostrar"] [j].ToString ().Equals (campos [i] ["Palabra"] [j].ToString ())) {
					mal.Add (i + ", " + j, true);
				}
			}
		} 
	}
}

