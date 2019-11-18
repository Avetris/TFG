using UnityEngine;
using System.Collections;
using System;
using System.Collections.Generic;
using System.IO;

public class LaberintoErroneo : Minijuego
{
	int tamano = 8;
	int numPosibles = 3, correcto;
	int[][] posibles;

	int[] casillaComienzo;
	int[] casillaSolucion;

	bool[][] activos;

	bool[][] casillas;
	GUIContent[][] contenido;

	Texture[] imagenesPosibles;
	Texture llave, casa;
	string[] textoPosible = new string[]{ "ABAJO", "ARRIBA", "DERECHA", "IZQUIERDA" };
	int tamanoCamino;

	Rect[][] rectList;

	GUISkin skin;

	// Use this for initialization
	protected override void Start ()
	{
		base.Start ();
		obtenerDatos ();
		referencia ();
		posicionar ();
		rellenar ();
	}

	void referencia ()
	{
		rectList = new Rect[tamano][];
		for (int i = 0; i < tamano; i++) {
			rectList [i] = new Rect[tamano];
			for (int j = 0; j < tamano; j++) {
				rectList [i] [j] = new Rect (width * (i + 1) / (tamano + 2), base.alturaTitulo + height * (j + 3) / (tamano + 4), width / (tamano + 2), height / (tamano + 4));
			}
		}
		imagenesPosibles = Resources.LoadAll<Texture> ("Images/LaberintoErroneo");
		llave = Resources.Load<Texture> ("Images/llave");
		casa = Resources.Load<Texture> ("Images/casa");
		skin = Resources.Load<GUISkin> ("GUISkin/LaberintoErroneo/LaberintoSkin");
		skin.box.fontSize = (int)Mathf.Max (rectList [0] [0].width, rectList [0] [0].height) / 15;
	}

	protected override void OnGUI ()
	{
		base.OnGUI ();
		if (!base.opcionesOn) {
			for (int i = 0; i < tamano; i++) {
				for (int j = 0; j < tamano; j++) {
					if (activos [i] [j]) {
						if (GUI.Button (rectList [i] [j], contenido [i] [j], skin.box)) {
							comprobarPulsado (i, j);
						}						
					} else {
						GUI.Box (rectList [i] [j], "", skin.box);
					}
				}
			}
		}
	}


	void obtenerDatos ()
	{
		tamano = UnityEngine.Random.Range (Int32.Parse (base.datos ["minimo"]), Int32.Parse (base.datos ["maximo"]));
		tamanoCamino = UnityEngine.Random.Range (Mathf.FloorToInt (tamano * tamano / 3), Mathf.RoundToInt (tamano * tamano / 4));
		if (gestorSQLite.obtenerIdioma ().Equals ("euskera")) {
			textoPosible = new string[]{ "BEHERA", "GORA", "ESKUINA", "EZKERRA" };
		} else {
			textoPosible = new string[]{ "ABAJO", "ARRIBA", "DERECHA", "IZQUIERDA" };
		}
		casillas = new bool[tamano][];
		activos = new bool[tamano][];
		contenido = new GUIContent[tamano][];
		posibles = new int[numPosibles][];
		casillaComienzo = new int[2];
		casillaSolucion = new int[2];
		for (int i = 0; i < tamano; i++) {
			casillas [i] = new bool[tamano];
			activos [i] = new bool[tamano];
			contenido [i] = new GUIContent[tamano];
			casillaComienzo [0] = -1;
			casillaComienzo [1] = -1;
			casillaSolucion [0] = -1;
			casillaSolucion [1] = -1;
			for (int j = 0; j < tamano; j++) {
				contenido [i] [j] = new GUIContent ();
				activos [i] [j] = false;
				casillas [i] [j] = false;
			}
		}
		for (int i = 0; i < numPosibles; i++) {
			posibles [i] = new int[2];
			posibles [i] [0] = 0;
			posibles [i] [1] = 0;
		}
	}

	/// <summary>
	/// Posicionar this instance.
	/// </summary>
	///<code>Va creando el camino,y si se encuentra sin salida, vuelve a empezar</code>
	void posicionar ()
	{
		bool malFin = false, fin = false;
		int x = 0, y = tamano - 1;
		do {
			malFin = false;
			x = UnityEngine.Random.Range (0, tamano - 1);
			y = UnityEngine.Random.Range (0, tamano - 1);
			casillas [x] [y] = true;
			activos [x] [y] = true;
			casillaComienzo [0] = x;
			casillaComienzo [1] = y;
			contenido [x] [y].image = casa;
			int direccion;
			for (int pos = 0; pos < tamanoCamino && !malFin; pos++) {
				direccion = elegirEntrePosibles (casillas, x, y);	
				switch (direccion) {
				case -1://Sin camino
					malFin = true;
					break;
				case 0: //abajo				
					y++;
					break;
				case 1: //arriba
					y--;
					break;
				case 2: //derecha
					x++;
					break;
				case 3: //izquierda
					x--;
					break;
				}
				if (!malFin) {					
					if (!casillas [x] [y]) {
						if (pos + 1 != tamanoCamino) {
							contenido [x] [y].text = textoPosible [direccion];
							contenido [x] [y].image = imagenesPosibles [direccion];
							casillas [x] [y] = true;
						} else {
							casillas [x] [y] = true;
							contenido [x] [y].image = llave;
							casillaSolucion [0] = x;
							casillaSolucion [1] = y;
						}
					} else {
						malFin = true;
					}
				}
			}
			if (!malFin) {
				if (x == casillaComienzo [0] + 1 && y == casillaComienzo [1] || x == casillaComienzo [0] - 1 && y == casillaComienzo [1] || x == casillaComienzo [0] && y == casillaComienzo [1] + 1 || x == casillaComienzo [0] && y == casillaComienzo [1] - 1) {
					malFin = true;
				} else {
					fin = true;
				}
			}
			if (malFin) {
				reiniciarMatrices ();
			}
		} while(!fin);
	}

	void reiniciarMatrices ()
	{
		for (int i = 0; i < casillas.Length; i++) {
			for (int j = 0; j < casillas.Length; j++) {
				casillas [i] [j] = false;
				activos [i] [j] = false;
				contenido [i] [j] = new GUIContent ();
			}
		}
		casillaComienzo [0] = -1;
		casillaComienzo [1] = -1;
		casillaSolucion [0] = -1;
		casillaSolucion [1] = -1;
	}


	/// <summary>
	/// Elegirs the entre posibles.
	/// </summary>
	/// <code>Primero obtiene las direcciones posibles, y luego elige una</code>
	/// <returns>The entre posibles.</returns>
	/// <param name="pCamino">P camino.</param>
	/// <param name="pX">P x.</param>
	/// <param name="pY">P y.</param>
	int elegirEntrePosibles (bool[][] pCamino, int pX, int pY)
	{
		Dictionary<String, int> hash = new Dictionary<String, int> ();
		List<String> direcciones = new List<String> ();
		int numero = -1;
		if (pY != tamano - 1 && !pCamino [pX] [pY + 1]) {
			hash.Add ("Abajo", 0);
			direcciones.Add ("Abajo");
		} 
		if (pY != 0 && !pCamino [pX] [pY - 1]) {
			hash.Add ("Arriba", 1);
			direcciones.Add ("Arriba");
		}
		if (pX != tamano - 1 && !pCamino [pX + 1] [pY]) {
			hash.Add ("Derecha", 2);
			direcciones.Add ("Derecha");
		}
		if (pX != 0 && !pCamino [pX - 1] [pY]) {
			hash.Add ("Izquierda", 3);
			direcciones.Add ("Izquierda");
		}

		if (hash.Count > 0 && direcciones.Count > 0) {
			numero = hash [direcciones [UnityEngine.Random.Range (0, direcciones.Count)]];
		}
		return numero;
	}

	/// <summary>
	/// Rellenar this instance.
	/// </summary>
	///<code>Rellena el resto del tablero</code>
	void rellenar ()
	{
		int num1 = -1, num2 = -1;
		for (int i = 0; i < tamano; i++) {
			for (int j = 0; j < tamano; j++) {
				if (!casillas [i] [j]) {
					num1 = UnityEngine.Random.Range (0, 4);
					num2 = UnityEngine.Random.Range (0, 4);
					if (num1 == num2) {
						j--;
					} else {
						contenido [i] [j].text = textoPosible [num1];
						contenido [i] [j].image = imagenesPosibles [num2];

					}
				} 
			}
		}
	}

	void comprobarPulsado (int pI, int pJ)
	{
		if (casillaSolucion [0] == pI && casillaSolucion [1] == pJ) {
			base.terminarJuego ();
		} else if (casillaComienzo [0] == pI && casillaComienzo [1] == pJ) {
			if (pI != 0) {
				activos [pI - 1] [pJ] = true;
			}
			if (pJ != 0) {
				activos [pI] [pJ - 1] = true;
			}
			if (pI != tamano - 1) {
				activos [pI + 1] [pJ] = true;
			}
			if (pJ != tamano - 1) {
				activos [pI] [pJ + 1] = true;
			}
		} else {								
			int posImg = -1;
			int posTxt = -1;
			for (int i = 0; i < imagenesPosibles.Length; i++) {
				if (imagenesPosibles [i] == contenido [pI] [pJ].image) {
					posImg = i;
				}
				if (textoPosible [i].Equals (contenido [pI] [pJ].text)) {
					posTxt = i;
				}
			}
			if (posImg == posTxt && posImg > -1) {
				if (pI != 0) {
					activos [pI - 1] [pJ] = true;
				}
				if (pJ != 0) {
					activos [pI] [pJ - 1] = true;
				}
				if (pI != tamano - 1) {
					activos [pI + 1] [pJ] = true;
				}
				if (pJ != tamano - 1) {
					activos [pI] [pJ + 1] = true;
				}
			} else {
				base.restarPuntuacion ();
			}
		}
	}
}
