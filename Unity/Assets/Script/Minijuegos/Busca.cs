using UnityEngine;
using System.Collections;
using System;

public class Busca : Minijuego
{
	int tamanoX = 7, tamanoY = 7;

	string[] posibles;
	string correcto;

	string[][] valores;
	bool[][] pulsado;

	GUISkin skin;

	Rect[][] rect;
	Rect rectCorrecto;

	// Use this for initialization
	protected override void Start ()
	{
		base.Start ();
		obtenerDatos ();
		referencia ();
	}

	void referencia ()
	{
		skin = Resources.Load<GUISkin> ("GUISkin/Busca/casillasSkin");
		rect = new Rect[tamanoX][];
		rectCorrecto = new Rect (2 * width / 5, alturaTitulo + base.height / (tamanoY + 4), width / 5, base.height / (tamanoY + 4));
		for (int i = 0; i < rect.Length; i++) {
			rect [i] = new Rect[tamanoY];
			for (int j = 0; j < rect [i].Length; j++) {
				rect [i] [j] = new Rect ((i + 1) * width / (tamanoX + 2), alturaTitulo + (j + 3) * base.height / (tamanoY + 4), width / (tamanoX + 2), base.height / (tamanoY + 4));
			}
		}

	}

	void obtenerDatos ()
	{
		tamanoX = UnityEngine.Random.Range (Int32.Parse (base.datos ["minimo"]), Int32.Parse (base.datos ["maximo"]));
		tamanoY = UnityEngine.Random.Range (Int32.Parse (base.datos ["minimo"]), Int32.Parse (base.datos ["maximo"]));
		posibles = gestorSQLite.obtenerBusca (base.id_minijuego);
		correcto = posibles [UnityEngine.Random.Range (0, 2)];
		valores = new string[tamanoX][];
		pulsado = new bool[tamanoX][];
		for (int i = 0; i < tamanoX; i++) {
			valores [i] = new string[tamanoY];
			pulsado [i] = new bool[tamanoY];
			for (int j = 0; j < tamanoY; j++) {
				valores [i] [j] = posibles [UnityEngine.Random.Range (0, posibles.Length)];
				pulsado [i] [j] = false;
			}
		}		
	}

	protected override void OnGUI ()
	{
		base.OnGUI ();
		if (!base.opcionesOn) {
			GUI.Box (rectCorrecto, correcto, skin.box);
			for (int i = 0; i < tamanoX; i++) {
				for (int j = 0; j < tamanoY; j++) {
					if (pulsado [i] [j]) {						
						GUI.Box (rect [i] [j], valores [i] [j], correcto.Equals (valores [i] [j]) ? skin.label : skin.textField);
					} else {
						if (GUI.Button (rect [i] [j], valores [i] [j], skin.button)) {
							pulsado [i] [j] = true;
							if (correcto.Equals (valores [i] [j])) {
								if (comprobarCompleto ()) {
									base.terminarJuego ();
								}
							} else {
								base.restarPuntuacion ();
							}
						}
					}
				}
			}
		}
	}

	bool comprobarCompleto ()
	{
		bool completo = true;
		for (int i = 0; i < tamanoX && completo; i++) {
			for (int j = 0; j < tamanoY && completo; j++) {
				if (!pulsado [i] [j] && valores [i] [j].Equals (correcto)) {
					completo = false;
				}
			}
		}
		return completo;
	}
}
