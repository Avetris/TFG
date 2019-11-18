using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using UnityEngine.Networking.NetworkSystem;
using System;
using System.ComponentModel;
using System.Media;
using System.Data;

public class SopaDeLetras : Minijuego
{
	int ySize, xSize;
	int tamano;

	Vector2 origen = new Vector2 (0, 0);
	int[][] recorridos;
	int[] actual;

	Rect[][] rectList;
	Rect[] rectPalabras;
	Rect[] rectCuadroPalabras;
	GUISkin[][] skinMatrix;
	GUISkin[] skinList;
	GUISkin palabrasSkin, fondoSopa;
	string[][] texto;
	bool[][] conPalabra;

	char[] letrasOpciones = new char[] {
		'A',
		'B',
		'C',
		'D',
		'E',
		'F',
		'G',
		'H',
		'I',
		'J',
		'K',
		'L',
		'M',
		'N',
		'O',
		'P',
		'Q',
		'R',
		'S',
		'T',
		'U',
		'V',
		'W',
		'X',
		'Y',
		'Z'
	};
	string[] listaPalabras;
	object[] listaMostrar;

	bool[] palabrasHechas;

	// Use this for initialization
	protected override void Start ()
	{
		base.Start ();
		obtenerDatos ();
		referencias ();
		inicializarTamanos ();
		inicializarMatrices ();
		inicializarPalabras ();
		rellenarSopa ();
		actual = new int[2];
		actual [0] = -1;
		actual [1] = -1;
	}

	void referencias ()
	{
		fondoSopa = Resources.Load<GUISkin> ("GUISkin/SopaDeLetras/cuadroSkin");
		skinList = Resources.LoadAll<GUISkin> ("GUISkin/SopaDeLetras/Sopa");
		palabrasSkin = Resources.Load<GUISkin> ("GUISkin/SopaDeLetras/palabrasSkin");
		skinList [0].box.fontSize = Mathf.Min (tamano * xSize, tamano * ySize) / 20;
		skinList [1].box.fontSize = Mathf.Min (tamano * xSize, tamano * ySize);
		skinList [2].box.fontSize = Mathf.Min (tamano * xSize, tamano * ySize);	
	}

	void obtenerDatos ()
	{
		object[][] listaDatos = gestorSQLite.obtenerSopaDeLetras (base.id_minijuego, Int32.Parse (base.datos ["maximo"]), Int32.Parse (base.datos ["minimo"]));
		List<String> posibles = new List<String> ();
		posibles.Add ("texto");
		if (listaDatos [0] [1] != null) {
			posibles.Add ("imagen");
		}

		listaPalabras = new string[listaDatos.Length];
		int selec = 0;
		switch (posibles [UnityEngine.Random.Range (0, posibles.Count)]) {
		case "texto":
			selec = 0;
			listaMostrar = new String[listaDatos.Length];
			break;
		case "imagen":
			selec = 1;
			listaMostrar = new Texture2D[listaDatos.Length];
			break;
		}
		for (int i = 0; i < listaMostrar.Length; i++) {
			listaMostrar [i] = listaDatos [i] [selec];
			listaPalabras [i] = listaDatos [i] [0].ToString ();
		}
	}
		
	// Update is called once per frame
	void Update ()
	{
		tocar ();
	}

	protected override void OnGUI ()
	{
		base.OnGUI ();
		if (!base.opcionesOn) {
			GUI.Box (new Rect (xSize * 3, ySize * 5, tamano * xSize, tamano * ySize), "", fondoSopa.box);

			for (int i = 0; i < tamano; i++) {
				for (int j = 0; j < tamano; j++) {
					GUI.Box (rectList [i] [j], texto [i] [j], skinMatrix [i] [j].box);
				}
			}

			GUI.Box (rectCuadroPalabras [0], "", fondoSopa.box);
			GUI.Box (rectCuadroPalabras [1], "", fondoSopa.box);

			for (int i = 0; i < rectPalabras.Length; i++) {
				if (listaMostrar [0].GetType () == typeof(String)) {
					GUI.Box (rectPalabras [i], listaMostrar [i].ToString (), palabrasSkin.box);
				} else if (listaMostrar [0].GetType () == typeof(Texture2D)) {
					GUI.Box (rectPalabras [i], (Texture2D)listaMostrar [i], palabrasSkin.box);
				}
			}
		}
	}

	void inicializarTamanos ()
	{
		tamano = 0;
		for (int i = 0; i < listaPalabras.Length; i++) {
			if (listaPalabras [i].Length > tamano - 1) {
				tamano = listaPalabras [i].Length + 1;
			}
		}
		xSize = Screen.width / (tamano + 6);
		ySize = Screen.height / (tamano + 6);
	}

	void inicializarMatrices ()
	{
		rectList = new Rect[tamano][];
		skinMatrix = new GUISkin[tamano][];
		texto = new string[tamano][];
		conPalabra = new bool[tamano][];
		recorridos = new int[tamano][];
		for (int i = 0; i < tamano; i++) {	
			rectList [i] = new Rect[tamano];
			skinMatrix [i] = new GUISkin[tamano];
			conPalabra [i] = new bool[tamano];
			texto [i] = new string[tamano];
			for (int j = 0; j < tamano; j++) {
				rectList [i] [j] = new Rect (xSize * (i + 3), ySize * (j + 5), xSize, ySize);
				skinMatrix [i] [j] = skinList [0];
				conPalabra [i] [j] = false;
			}
			recorridos [i] = new int[2];
			recorridos [i] [0] = -1;
			recorridos [i] [1] = -1;
		}
	}


	void inicializarPalabras ()
	{
		rectPalabras = new Rect[listaPalabras.Length];
		palabrasHechas = new bool[listaPalabras.Length];
		rectCuadroPalabras = new Rect[2];
		for (int i = 0; i < palabrasHechas.Length; i++) {
			palabrasHechas [i] = false;
		}
		int t = 5, pos = 1;
		int tamanoMax = 0;
		rectCuadroPalabras [0] = new Rect (xSize / 3, ySize * t, xSize * 2 + xSize / 3, ySize * tamano);
		rectCuadroPalabras [1] = new Rect (xSize / 2 + xSize * (tamano + 3), ySize * t, xSize * 2 + xSize / 3, ySize * tamano);
		for (int i = 0; i < rectPalabras.Length; i++) {
			if (listaPalabras [i].Length > tamanoMax)
				tamanoMax = listaPalabras [i].Length;
			if (i == rectPalabras.Length / 2)
				pos = 1;
			if (i < rectPalabras.Length / 2) {
				rectPalabras [i] = new Rect (rectCuadroPalabras [0].x, rectCuadroPalabras [0].y + rectCuadroPalabras [0].height / (rectPalabras.Length + 1) * pos, rectCuadroPalabras [0].width, rectCuadroPalabras [0].height / rectPalabras.Length);
			} else {
				rectPalabras [i] = new Rect (rectCuadroPalabras [1].x, rectCuadroPalabras [1].y + rectCuadroPalabras [1].height / (rectPalabras.Length + 1) * pos, rectCuadroPalabras [1].width, rectCuadroPalabras [1].height / rectPalabras.Length);
			}
			pos += 2;
			//Cambiamos el tamano de las imagenes, para que ocupen el cuadro
			if (listaMostrar [i].GetType () == typeof(Texture2D)) {
				TextureScale.Point ((Texture2D)listaMostrar [i], (int)rectPalabras [i].width, (int)rectPalabras [i].height);
			}
		}	
		palabrasSkin.box.fontSize = (int)Mathf.Max (rectPalabras [0].width, rectPalabras [0].height) / tamanoMax;

	}

	//introduce todas las palabras
	void meterPalabras ()
	{
		int x, y;
		int direccion;
		bool puesta;
		for (int k = 0; k < listaPalabras.Length; k++) {
			puesta = false;
			while (!puesta) {
				x = UnityEngine.Random.Range (0, tamano);
				y = UnityEngine.Random.Range (0, tamano);
				if (texto [x] [y] == null || texto [x] [y].Equals ("")) {
					puesta = true;
					direccion = UnityEngine.Random.Range (0, 4);
					puesta = comprobarVacio (direccion, x, y, listaPalabras [k]);
				} else if (texto [x] [y] != null) {
					if (texto [x] [y].Equals (listaPalabras [k] [0] + "")) {
						puesta = true;
						direccion = UnityEngine.Random.Range (0, 4);
						puesta = comprobarVacio (direccion, x, y, listaPalabras [k]);
					}
				}
			}
		}
	}

	void rellenarSopa ()
	{
		meterPalabras ();
		for (int i = 0; i < tamano; i++) {	
			for (int j = 0; j < tamano; j++) {
				if (texto [i] [j] == null || texto [i] [j].Equals ("")) {
					texto [i] [j] = letrasOpciones [UnityEngine.Random.Range (0, letrasOpciones.Length)] + "";
				}
			}
		}
	}

	//Compueba longitud y si esta vacio por donde la palabra y la mete
	bool comprobarVacio (int pDireccion, int pX, int pY, string pPalabra)
	{
		int tamanoPalabra = pPalabra.Length;
		bool puesta = true;
		switch (pDireccion) {
		case 0://arriba
			if (tamanoPalabra <= pY) {
				for (int cont = 0, j = pY; j > pY - tamanoPalabra && puesta; j--, cont++) {
					if (texto [pX] [j] != null && !texto [pX] [j].Equals ("")) {
						if (!texto [pX] [j].Equals (pPalabra [cont] + "")) {
							puesta = false;
						}
					}
				}
				if (puesta) {
					bool fin = false;
					for (int k = 0; k < tamanoPalabra && !fin; k++) {
						if (texto [pX] [pY - k] == null || texto [pX] [pY - k].Equals ("")) {
							texto [pX] [pY - k] = pPalabra [k] + "";
						}
					}
				}
			} else {
				puesta = false;
			}
			break;
		case 1://abajo
			if (tamanoPalabra <= tamano - pY - 1) {
				for (int cont = 0, j = pY; j < tamano && j <= tamanoPalabra + pY - 1 && puesta; j++, cont++) {
					if (texto [pX] [j] != null && !texto [pX] [j].Equals ("")) {
						if (!texto [pX] [j].Equals (pPalabra [cont] + "")) {//mal
							puesta = false;
						}
					}
				}
				if (puesta) {
					for (int k = 0; k < tamanoPalabra; k++) {
						if (texto [pX] [pY + k] == null || texto [pX] [pY + k].Equals ("")) {
							texto [pX] [pY + k] = pPalabra [k] + "";
						}
					}
				}
			} else {
				puesta = false;
			}
			break;
		case 2://izquierda
			if (tamanoPalabra <= pX) {
				for (int cont = 0, i = pX; i > pX - tamanoPalabra && puesta; i--, cont++) {
					if (texto [i] [pY] != null && !texto [i] [pY].Equals ("")) {
						if (!texto [i] [pY].Equals (pPalabra [cont] + "")) {
							puesta = false;
						}
					}
				}
				if (puesta) {
					for (int k = 0; k < tamanoPalabra; k++) {
						if (texto [pX - k] [pY] == null || texto [pX - k] [pY].Equals ("")) {
							texto [pX - k] [pY] = pPalabra [k] + "";
						}
					}
				}
			} else {
				puesta = false;
			}
			break;
		case 3://derecha
			if (tamanoPalabra <= tamano - pX - 1) {
				for (int cont = 0, i = pX; i <= tamanoPalabra + pX - 1 && puesta; i++, cont++) {
					if (texto [i] [pY] != null && !texto [i] [pY].Equals ("")) {
						if (!texto [i] [pY].Equals (pPalabra [cont] + "")) {
							puesta = false;
						}
					}
				}
				if (puesta) {
					for (int k = 0; k < tamanoPalabra; k++) {
						if (texto [pX + k] [pY] == null || texto [pX + k] [pY].Equals ("")) {
							texto [pX + k] [pY] = pPalabra [k] + "";
						}
					}
				}
			} else {
				puesta = false;
			}
			break;
		}
		return puesta;
	}

	//control de touch
	void tocar ()
	{
		foreach (Touch touch in Input.touches) {
			if (touch.phase == TouchPhase.Began) {
				origen = new Vector2 (touch.position.x, Screen.height - touch.position.y);
				presionar (touch.position.x, Screen.height - touch.position.y);
			} else if (touch.phase == TouchPhase.Moved) {
				if (touch.position != origen) {
					if (!comprobarMovimiento (touch.position.x, Screen.height - touch.position.y)) {
						origen = new Vector2 (touch.position.x, Screen.height - touch.position.y);
						presionar (touch.position.x, Screen.height - touch.position.y);
					}
				}
			} else if (touch.phase == TouchPhase.Ended) {
				comprobar ();
			}
		}
	}

	//comprueba que se ha movido de casilla
	bool comprobarMovimiento (float positionX, float positionY)
	{	
		bool mismo = true;
		bool encontrado = false;
		int i = 1;
		int j = 1;
		int jAux = 1;
		while (!encontrado) {
			if (i >= tamano || j >= tamano) {
				
				return true;
			} else if (positionX < rectList [i] [j].position.x && positionY < rectList [i] [j].position.y) {
				encontrado = true;
				i--;
				j--;
			} else {
				if (positionY >= rectList [i] [j].position.y) {
					jAux++;
				}
				if (positionX >= rectList [i] [j].position.x) {
					i++;
				}	
				j = jAux;
			}
			if (i == tamano & j == tamano) {
				if (positionX < (rectList [i - 1] [j - 1].position.x + xSize) && positionY < (rectList [i - 1] [j - 1].position.y + ySize)) {
					encontrado = true;
					i--;
					j--;
				}
			} else if (i == tamano) {
				if (positionX < (rectList [i - 1] [j].position.x + xSize) && positionY < (rectList [i - 1] [j].position.y + ySize)) {
					encontrado = true;
					i--;
					j--;
				}
			} else if (j == tamano) {
				if (positionX < (rectList [i] [j - 1].position.x + xSize) && positionY < (rectList [i] [j - 1].position.y + ySize)) {
					encontrado = true;
					i--;
					j--;
				}
			}
		}
		if (actual [0] == i && actual [1] == j) {
			mismo = true;
		} else {
			mismo = false;
		}
		return mismo;
	}

	//acciones al presionar una casilla
	void presionar (float positionX, float positionY)
	{
		int i = 0;
		int j = 0;
		bool fuera = false;
		float rectSize = rectList [i + 1] [0].position.x;
		if (positionX < rectList [0] [0].position.x || positionX > (rectList [tamano - 1] [0].position.x + xSize)) {
			fuera = true;
		}
		if (!fuera) {			
			while (rectSize < positionX && i < tamano - 1) {			
				i++;
				if (i < tamano - 1) {
					rectSize = rectList [i + 1] [0].position.x;
				}
			}
			if (positionY < rectList [0] [0].position.y || positionY > (rectList [0] [tamano - 1].position.y + ySize)) {
				fuera = true;
			}
			if (!fuera) {
				rectSize = rectList [i] [j + 1].position.y;
				while (rectSize < positionY && j < tamano - 1) {
					j++;
					if (j < tamano - 1) {
						rectSize = rectList [i] [j + 1].position.y;
					}
				}
				if (recorridos [0] [0] == -1 || recorridos [0] [1] == -1) {					
					anadir (i, j);
					actual [0] = i;	
					actual [1] = j;
					if (!conPalabra [recorridos [0] [0]] [recorridos [0] [1]]) {
						skinMatrix [i] [j] = skinList [1];		
					}
				} else {
					int posicion = posicionSiContiene (i, j);
					if (posicion != -1) {
						int[] ultimos = ultimo ();
						if (recorridos [posicion + 1] [0] == ultimos [0] && recorridos [posicion + 1] [1] == ultimos [1]) {
							eliminarUltimo ();
							actual [0] = i;
							actual [1] = j;	
						}
					} else if (mismaDireccion (i, j)) {						
						anadir (i, j);	
						actual [0] = i;	
						actual [1] = j;
						if (!conPalabra [i] [j]) {
							skinMatrix [i] [j] = skinList [1];		
						}
					}
				}
			}
		}
	}

	//devuelve la posicion en caso de estar en los seleccionados
	int posicionSiContiene (int pI, int pJ)
	{
		bool fin = false;
		int k = 0;
		while (!fin) {
			if (k >= tamano) {
				return -1;
			} else if (recorridos [k] [0] == -1 || recorridos [k] [1] == -1) {
				return -1;
			} else if (recorridos [k] [0] == pI && recorridos [k] [1] == pJ) {
				fin = true;
			} else {
				k++;
			}
		}
		return k;
	}

	//añade a los seleccionados
	void anadir (int pI, int pJ)
	{
		int k = 0;
		bool fin = false;
		while (!fin) {
			if (recorridos [k] [0] != -1 && recorridos [k] [1] != -1) {
				k++;
			} else {
				recorridos [k] [0] = pI;
				recorridos [k] [1] = pJ;
				fin = true;
			}
		}
	}

	//comprueba si va en la misma direccion (horizontal o vertical)
	bool mismaDireccion (int pI, int pJ)
	{
		int[] ultimos = ultimo ();
		bool misma = false;
		if (recorridos [1] [0] == -1 || recorridos [1] [1] == -1) {
			misma = true;
		} else if (recorridos [0] [0] == recorridos [1] [0] && ultimos [0] == pI) {
			//vertical
			if (recorridos [0] [1] > recorridos [1] [1] && ultimos [1] == pJ + 1) { //arriba
				misma = true;
			} else if (recorridos [0] [1] < recorridos [1] [1] && ultimos [1] == pJ - 1) {//abajo
				misma = true;
			} else if (ultimos [1] == pJ) {//atras
				misma = false;
				eliminarUltimo ();
			} else {
				misma = false;
			}				
		} else if (recorridos [0] [1] == recorridos [1] [1] && ultimos [1] == pJ) {
			//horizontal
			if (recorridos [0] [0] < recorridos [1] [0] && ultimos [0] == pI - 1) {//derecha
				misma = true;
			} else if (recorridos [0] [0] > recorridos [1] [0] && ultimos [0] == pI + 1) {//izquierda
				misma = true;
			} else if (ultimos [0] == pI) {//atras
				misma = false;
				eliminarUltimo ();
			} else {
				misma = false;
			}
		}
		return misma;
	}

	//devuelve la i y j del ultimo elmento seleccionado
	int[] ultimo ()
	{
		bool fin = false;
		int i = 1;
		while (!fin) {
			if (i >= recorridos.Length) {
				fin = true;
			} else if (recorridos [i] [0] == -1) {
				fin = true;
			} else {
				i++;
			}
		}
		return recorridos [i - 1];
	}

	//elimina el ultimo elemento seleccionado
	void eliminarUltimo ()
	{
		bool fin = false;
		int i = 0;
		while (!fin) {
			if (recorridos [i] [0] == -1 || recorridos [i] [1] == -1) {
				if (i == 0) {
					fin = true;
				} else {					
					skinMatrix [recorridos [i - 1] [0]] [recorridos [i - 1] [1]] = skinList [0];	
					recorridos [i - 1] [0] = -1;
					recorridos [i - 1] [1] = -1;
					fin = true;
				}
			} else {
				i++;
			}
		}
	}

	//comprueba si lo seleccionado es correcto
	void comprobar ()
	{
		string palabra = "";
		bool esta = false;
		for (int i = 0; i < recorridos.Length; i++) {
			if (recorridos [i] [0] != -1 && recorridos [i] [1] != -1) {
				if (!conPalabra [recorridos [i] [0]] [recorridos [i] [1]]) {
					skinMatrix [recorridos [i] [0]] [recorridos [i] [1]] = skinList [0];
				}
				palabra += texto [recorridos [i] [0]] [recorridos [i] [1]];
			}
		}
		esta = estaPalabra (palabra);

		for (int i = 0; i < recorridos.Length; i++) {
			if (recorridos [i] [0] != -1 && recorridos [i] [1] != -1) {
				if (!esta) {
					if (!conPalabra [recorridos [i] [0]] [recorridos [i] [1]]) {
						skinMatrix [recorridos [i] [0]] [recorridos [i] [1]] = skinList [0];
					} else {
						skinMatrix [recorridos [i] [0]] [recorridos [i] [1]] = skinList [2];

					}
				} else {
					skinMatrix [recorridos [i] [0]] [recorridos [i] [1]] = skinList [2];
					conPalabra [recorridos [i] [0]] [recorridos [i] [1]] = true;
				}
				recorridos [i] [0] = -1;
				recorridos [i] [1] = -1;
			}	
		}
		actual [0] = -1;
		actual [1] = -1;
		if (haTerminado ()) {
			base.terminarJuego ();
		}
	}

	bool estaPalabra (string palabra)
	{
		bool esta = false;
		for (int i = 0; i < listaPalabras.Length && !esta; i++) {
			if (palabra.Equals (listaPalabras [i]) && !palabrasHechas [i]) {
				palabrasHechas [i] = true;
				esta = true;
				listaMostrar [i] = base.oscurecer (listaMostrar [i]);
			}
		}
		if (!esta) {
			if (palabra.Length > 1) {
				base.restarPuntuacion ();
			}
		}
		return esta;
	}

	bool haTerminado ()
	{
		bool terminado = true;
		for (int i = 0; i < palabrasHechas.Length && terminado; i++) {
			terminado = palabrasHechas [i];
		}
		return terminado;
	}
}
