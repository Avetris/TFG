using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System;

public class FraseCorrecta : Minijuego
{
	List<Dictionary<string, List<object>>> listaDatos;
	int[] seleccion, mal;

	GUISkin skin;

	int pos = 0;

	Rect[] rectTexto, rectBotones;
	Rect rectImagen, rectFondo;
	// Use this for initialization
	protected override void Start ()
	{
		base.Start ();
		obtenerDatos ();
		referencia ();
	}

	void referencia ()
	{
		rectTexto = new Rect[listaDatos.Count];
		rectFondo = new Rect (4.5f * width / 10, alturaTitulo + base.height / 8, 4.5f * width / 10, 6 * base.height / 8);
		for (int i = 0; i < rectTexto.Length; i++) {
			rectTexto [i] = new Rect (rectFondo.x + 10, rectFondo.y + i * rectFondo.height / 4, 4.5f * width / 10, rectFondo.height / 5);
		}
		rectImagen = new Rect (width / 10, rectFondo.y, 3 * width / 10, rectFondo.height);
		rectBotones = new Rect [2];
		rectBotones [0] = new Rect (width / 5, rectFondo.y + rectFondo.height, width / 4, 2 * (Screen.height - rectFondo.y - rectFondo.height) / 3);
		rectBotones [1] = new Rect (3 * width / 5, rectFondo.y + rectFondo.height, width / 4, 2 * (Screen.height - rectFondo.y - rectFondo.height) / 3);
		skin = Resources.Load<GUISkin> ("GUISkin/FraseCorrecta/skin");
		skin.box.fontSize = (int)Mathf.Min (rectTexto [0].width, rectTexto [0].height) / 5;
		skin.label.fontSize = (int)Mathf.Min (rectTexto [0].width, rectTexto [0].height) / 5;
		skin.textArea.fontSize = (int)Mathf.Min (rectTexto [0].width, rectTexto [0].height) / 5;
		for (int i = 0; i < listaDatos.Count; i++) {
			TextureScale.Point ((Texture2D)listaDatos [i] ["Enunciado"] [0], Mathf.FloorToInt (rectImagen.width), Mathf.FloorToInt (rectImagen.height));
		}
	}

	void obtenerDatos ()
	{
		listaDatos = gestorSQLite.obtenerFraseCorrecta (base.id_minijuego, Int32.Parse (base.datos ["maximo"]), Int32.Parse (base.datos ["minimo"]));
		seleccion = new int[listaDatos.Count];
		mal = new int[listaDatos.Count];
		for (int i = 0; i < listaDatos.Count; i++) {
			seleccion [i] = -1;
			mal [i] = -1;
		}
		pos = 0;
	}

	protected override void OnGUI ()
	{
		base.OnGUI ();
		if (!base.opcionesOn) {
			GUI.Box (rectFondo, "", skin.window);
			for (int i = 0; i < listaDatos [pos] ["Opciones"].Count; i++) {
				GUILayout.BeginArea (rectTexto [i]);
				GUILayout.BeginHorizontal ();
				if (mal [pos] == i) {
					GUILayout.Box (i + ".", skin.textArea, GUILayout.ExpandWidth (false));
					if (GUILayout.Button (listaDatos [pos] ["Opciones"] [i].ToString (), skin.textArea, GUILayout.ExpandWidth (false))) {
						seleccion [pos] = -1;
					}
				} else {						
					if (seleccion [pos] == i) {
						GUILayout.Box (i + ".", skin.label, GUILayout.ExpandWidth (false));
						if (GUILayout.Button (listaDatos [pos] ["Opciones"] [i].ToString (), skin.label, GUILayout.ExpandWidth (false))) {
							seleccion [pos] = -1;
							mal [pos] = -1;
						}
					} else {
						GUILayout.Box (i + ".", skin.box, GUILayout.ExpandWidth (false));
						if (GUILayout.Button (listaDatos [pos] ["Opciones"] [i].ToString (), skin.box, GUILayout.ExpandWidth (false))) {
							seleccion [pos] = i;
							mal [pos] = -1;
						}
					}
				}
				GUILayout.EndHorizontal ();
				GUILayout.EndArea ();
			}
			GUI.Box (rectImagen, (Texture2D)listaDatos [pos] ["Enunciado"] [0], skin.textField);
			if (pos == listaDatos.Count - 1) {
				if (comprobarCompleto () == -1) {
					if (GUI.Button (rectBotones [1], Diccionario.getPalabra ("Terminar"), skin.button)) {
						pos = comprobarCorrecto ();
						if (pos == -1) {
							base.terminarJuego ();
							pos = 0;
						} else {
							base.restarPuntuacion ();

						}
					}
				}
			} else {
				if (GUI.Button (rectBotones [1], Diccionario.getPalabra ("Siguiente"), skin.button)) {
					pos++;
				}
			}
			if (pos != 0) {
				if (GUI.Button (rectBotones [0], Diccionario.getPalabra ("Anterior"), skin.button)) {
					pos--;
				}
			} else {
				GUI.Box (rectBotones [0], Diccionario.getPalabra ("Anterior"), skin.button);
			}
		}
	}

	int comprobarCorrecto ()
	{
		pos = -1;
		for (int i = 0; i < mal.Length; i++) {
			mal [i] = -1;
		}
		for (int i = 0; i < seleccion.Length; i++) {
			if (seleccion [i] == -1) {
				if (pos == -1)
					pos = i;
			} else {
				if (seleccion [i] != (int)listaDatos [i] ["Solucion"] [0]) {
					mal [i] = seleccion [i];
					seleccion [i] = -1;
					if (pos == -1)
						pos = i;
				}
			}
		}
		return pos;
	}

	int comprobarCompleto ()
	{
		int completo = -1;
		for (int i = 0; i < seleccion.Length && completo == -1; i++) {
			if (seleccion [i] == -1) {
				completo = i;
			}
		}
		return completo;
	}
}
