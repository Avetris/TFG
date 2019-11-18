using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
using System.IO;
using I18N.Common;
using System.Net;
using UnityEngine.Networking.NetworkSystem;
using UnityEngine.SceneManagement;

public class Constantes
{
	static Constantes misConstantes = new Constantes ();

	static Dictionary<int, string> minijuegos;

	static bool errorDatos = false;

	static bool sincronizar = false;

	private Constantes ()
	{
		minijuegos = new Dictionary<int, string> () {
			{ 1, "Busca" },
			{ 2, "FraseCorrecta" },
			{ 3, "LaberintoErroneo" },
			{ 4, "PalabrasIncompletas" },
			{ 5, "Parejas" },
			{ 6, "Selecciona" },
			{ 7, "SopaDeLetras" }
		};
	}

	public static void setErrorDatos ()
	{
		errorDatos = true;
		SceneManager.LoadScene ("LoginScene");
	}

	public static void setSincronizar (bool sincro)
	{
		sincronizar = sincro;
	}

	public static bool getSincronizar ()
	{
		return sincronizar;
	}

	public static void reiniciarErrorDatos ()
	{
		errorDatos = false;
	}

	public static bool getErrorDatos ()
	{
		return errorDatos;
	}

	public static Constantes getConstantes ()
	{
		if (misConstantes == null) {
			misConstantes = new Constantes ();
		}
		return misConstantes;
	}

	public string getMinijuego (int id)
	{
		return minijuegos [id];
	}

	public static object quitarNull (object texto)
	{
		if (texto == null) {
			texto = "";
		}
		return texto;
	}

	public byte[] obtenerImagen (object objectData)
	{
		if (objectData != null) {
			string data = objectData.ToString ();
			if (data.Length > 0) {
				WebClient client = new WebClient ();
				byte[] image = client.DownloadData (data);
				return image;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
