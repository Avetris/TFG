using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using System;
using System.Collections.Generic;

public class Seleccion : Minijuego
{
	int[] seleccion, mal;
	List<Dictionary<string, List<object>>> listaDatos;

	int pos;

	float anchuraScroll, alturaScroll;

	Vector2 scrollPosition, origen;
	bool movido;

	GUISkin skin;

	Rect rectArea, rectBotonComprobar;

	// Use this for initialization
	protected override void Start ()
	{
		base.Start ();
		scrollPosition = new Vector2 (0, 0);
		referencias ();
		obtenerDatos ();
	}

	void referencias ()
	{
		skin = Resources.Load<GUISkin> ("GUISkin/Seleccion/skin");
		anchuraScroll = Mathf.RoundToInt (Screen.width / 16);
		alturaScroll = Mathf.RoundToInt (base.height / (4 + 7));
		rectArea = new Rect (anchuraScroll, base.alturaTitulo + alturaScroll, 14 * anchuraScroll, 7 * alturaScroll);
		rectBotonComprobar = new Rect (width / 4, base.alturaTitulo + 9 * alturaScroll, width / 2, alturaScroll);
	}

	void obtenerDatos ()
	{
		listaDatos = gestorSQLite.obtenerSeleccion (base.id_minijuego, Int32.Parse (base.datos ["maximo"]), Int32.Parse (base.datos ["minimo"]));
		seleccion = new int[listaDatos.Count];
		mal = new int[listaDatos.Count];
		for (int i = 0; i < listaDatos.Count; i++) {
			seleccion [i] = -1;
			mal [i] = -1;
			for (int j = 0; j < listaDatos [i] ["Opciones"].Count; j++) {
				TextureScale.Point ((Texture2D)listaDatos [i] ["Opciones"] [j], Mathf.RoundToInt (2 * anchuraScroll), Mathf.RoundToInt (3 * alturaScroll));
			}
		}
	}

	// Update is called once per frame
	protected void Update ()
	{
		tocar ();
	}

	protected override void OnGUI ()
	{
		base.OnGUI ();
		if (!base.opcionesOn) {
			GUILayout.BeginArea (rectArea);
			scrollPosition = GUILayout.BeginScrollView (scrollPosition, GUILayout.Width (rectArea.width), 
				GUILayout.Height (rectArea.height), GUILayout.ExpandWidth (false), GUILayout.ExpandHeight (false));
			for (int i = 0; i < listaDatos.Count; i++) {
				GUILayout.BeginVertical (skin.window);
				GUILayout.Space (alturaScroll);
				GUILayout.BeginHorizontal ();
				GUILayout.Space (anchuraScroll);
				GUILayout.Box (listaDatos [i] ["Enunciado"] [0].ToString (), skin.toggle, GUILayout.Width (3 * (datos.Count - 1) * anchuraScroll), GUILayout.Height (alturaScroll));
				GUILayout.EndHorizontal ();
				GUILayout.Space (alturaScroll);
				GUILayout.BeginHorizontal ();
				GUILayout.Space (anchuraScroll);
				for (int j = 0; j < listaDatos [i] ["Opciones"].Count; j++) {
					if (seleccion [i] == j) {
						if (GUILayout.Button ((Texture2D)listaDatos [i] ["Opciones"] [j], skin.textField, GUILayout.Width (2 * anchuraScroll), GUILayout.Height (3 * alturaScroll))) {
							seleccion [i] = -1;
						}
					} else if (mal [i] == j) {
						if (GUILayout.Button ((Texture2D)listaDatos [i] ["Opciones"] [j], skin.label, GUILayout.Width (2 * anchuraScroll), GUILayout.Height (3 * alturaScroll))) {
							seleccion [i] = j;
							mal [i] = -1;
						}
					} else {
						if (GUILayout.Button ((Texture2D)listaDatos [i] ["Opciones"] [j], skin.box, GUILayout.Width (2 * anchuraScroll), GUILayout.Height (3 * alturaScroll))) {
							seleccion [i] = j;
							mal [i] = -1;
						}
					}
					GUILayout.Space (anchuraScroll);
				}
				GUILayout.EndHorizontal ();
				GUILayout.Space (alturaScroll);
				GUILayout.EndVertical ();
			}
			GUILayout.EndScrollView ();
			GUILayout.EndArea ();
			
			if (comprobarCompleto () == -1) {
				if (GUI.Button (rectBotonComprobar, Diccionario.getPalabra ("Comprobar"), skin.button)) {
					int error = comprobarErrores ();
					if (error == -1) {
						base.terminarJuego ();
					} else {
						scrollPosition.y = (rectArea.height + alturaScroll - 20) * error;
						pos = error;
						base.restarPuntuacion ();
					}
				}
			}
		}
	}

	void tocar ()
	{
		foreach (Touch touch in Input.touches) {
			if (touch.phase == TouchPhase.Began) {
				origen = new Vector2 (touch.position.x, Screen.height - touch.position.y);
				movido = false;
			} else if (touch.phase == TouchPhase.Moved) {
				if (!movido && touch.position != origen) {
					movido = true;
					if (touch.position.y < origen.y + 80 && pos < datos.Count - 1) {
						scrollPosition.y += (rectArea.height + alturaScroll - 20);
						pos++;
					} else if (touch.position.y > origen.y - 80 && pos > 0) {
						scrollPosition.y -= (rectArea.height + alturaScroll - 20);
						pos--;
					}
				}
			} else if (touch.phase == TouchPhase.Ended) {
				movido = false;
			}
		}
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

	int comprobarErrores ()
	{
		int error = -1;
		for (int i = 0; i < seleccion.Length; i++) {
			if (seleccion [i] != (int)listaDatos [i] ["Solucion"] [0]) {
				mal [i] = seleccion [i];
				seleccion [i] = -1;
				if (error == -1) {
					error = i;
				}
			}

		}
		return error;
	}
}
