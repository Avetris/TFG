using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
using System.Threading;
using System.Runtime.InteropServices;
using UnityEngine.SceneManagement;

public class Login : MonoBehaviour
{
	string user = "";
	string password = "";

	Sync sync;

	string error = "";

	int error_sincro = 0;

	string cargando1 = "Despertando", cargando2 = "";

	Rect windowRect;

	GUISkin windowSkin, sincronizando, campos, botonSkin;

	bool logueando = false;
	bool cuenta = false;

	Texture2D fondo;

	// Use this for initialization
	void Start ()
	{
		cargando1 = Diccionario.getPalabra ("Despertando");
		if (cargando1 == null || cargando1.Trim ().Length == 0)
			cargando1 = "Despertando";
		cargando2 = "";
		sync = Sync.getSync ();
		int id_usuario = GestorSQLite.getGestorSQLite ().obtenerUsuarioAcual ();
		windowRect = new Rect (Screen.width / 4, Screen.height / 7, Screen.width / 2, 5 * Screen.height / 7);
		windowSkin = Resources.Load<GUISkin> ("GUISkin/Opciones/fondoOpcionesSkin");
		sincronizando = Resources.Load<GUISkin> ("GUISkin/Mapa/sincronizarSkin");
		campos = Resources.Load<GUISkin> ("GUISkin/Login/camposSkin");
		botonSkin = Resources.Load<GUISkin> ("GUISkin/Mapa/botonesSuperiorSkin");
		fondo = Resources.Load<Texture2D> ("Images/fondo_login");
		if (id_usuario > 0 && !Constantes.getErrorDatos ()) {
			cuenta = true;
			try {
				sync.setId_Usuario (id_usuario);
				Thread t = new Thread (new ThreadStart (sync.sincronizacion));
				t.Start ();
			} catch (Exception e) {
				Debug.Log (e);
				SceneManager.LoadScene ("MapaScene");				
			}
			logueando = true;
		} else if (Constantes.getErrorDatos ()) {
			error = Diccionario.getPalabra ("Datos Incorrectos");
		}
		InvokeRepeating ("cambioCargando", 1f, 1f);
	}
	
	// Update is called once per frame
	void Update ()
	{	
		if (logueando) {
			error_sincro = sync.getErrorCode ();
			if (error_sincro == 1) {
				SceneManager.LoadScene ("MapaScene");
			} else if (error_sincro == -1) {
				if (cuenta) {
					SceneManager.LoadScene ("MapaScene");					
				} else {
					error = Diccionario.getPalabra ("Internet");
					logueando = false;
				}
			} else if (error_sincro == -2) {
				error = Diccionario.getPalabra ("Login Incorrecto");
				logueando = false;
			} else if (error_sincro == -3) {
				error = Diccionario.getPalabra ("Datos Incorrectos");
				logueando = false;
			} 
		}
	}

	void OnGUI ()
	{
		GUI.DrawTexture (new Rect (0, 0, Screen.width, Screen.height), fondo);
		GUI.Label (new Rect (Screen.width / 5, Screen.height / 4, Screen.width / 4, Screen.height / 7), Diccionario.getPalabra ("Usuario"), campos.box);
		user = GUI.TextField (new Rect (Screen.width / 2, Screen.height / 4, Screen.width / 3, Screen.height / 7), user, 50, campos.textField);
		GUI.Label (new Rect (Screen.width / 5, 3 * Screen.height / 7, Screen.width / 4, Screen.height / 7), Diccionario.getPalabra ("Contrasena"), campos.box);
		password = GUI.PasswordField (new Rect (Screen.width / 2, 3 * Screen.height / 7, Screen.width / 3, Screen.height / 7), password, '*', 50, campos.textField);
		if (GUI.Button (new Rect (Screen.width / 4, 4 * Screen.height / 6, Screen.width / 2, Screen.height / 7), Diccionario.getPalabra ("Entrar"), botonSkin.box)) {
			logueando = true;
			sync.setUsuario (user);
			sync.setContrasena (password);
			Thread t = new Thread (new ThreadStart (sync.login));
			t.Start ();
		}
		if (!error.Equals ("")) {
			GUI.Box (new Rect (Screen.width / 4, Screen.height / 5, Screen.width / 2, Screen.height / 21), error, campos.label);
		}
		if (logueando) {
			GUI.Window (1, windowRect, window, "", windowSkin.box);
		}
	}

	void window (int windowID)
	{
		GUI.Box (new Rect (windowRect.width / 5, windowRect.height / 5, 3 * windowRect.width / 5, 3 * windowRect.height / 5), cargando1 + cargando2, sincronizando.box);
	}

	void cambioCargando ()
	{
		if (logueando) {
			if (cargando2 == null) {
				cargando2 = "";
			} else if (cargando2.Equals ("")) {
				cargando2 = ".";
			} else if (cargando2.Equals (".")) {
				cargando2 = "..";

			} else if (cargando2.Equals ("..")) {
				cargando2 = "...";

			} else {
				cargando2 = "";
			}
		}
	}
}
