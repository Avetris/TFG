using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;

[System.Serializable]
public class Diccionario
{
	static Dictionary<string, string>[] diccionario;

	private static void getDiccionario ()
	{
		diccionario = new Dictionary<string, string>[2];
		diccionario [0] = new Dictionary<string, string> ();
		diccionario [0].Add ("OPCIONES", "OPCIONES");
		diccionario [0].Add ("Castellano", "Castellano");
		diccionario [0].Add ("Euskera", "Euskera");
		diccionario [0].Add ("Continuar", "Continuar");
		diccionario [0].Add ("Desconectar", "Desconectar");
		diccionario [0].Add ("Premios", "Premios");
		diccionario [0].Add ("Opciones", "Opciones");
		diccionario [0].Add ("Sincronizar", "Sincronizar");
		diccionario [0].Add ("Cargando_Energia", "Recuperando Energía");
		diccionario [0].Add ("Comprobar", "Comprobar");
		diccionario [0].Add ("Despertando", "Despertando");
		diccionario [0].Add ("Datos Incorrectos", "Datos incorrectos. Comuniquelo a la Logopeda");
		diccionario [0].Add ("Internet", "Conectese a Internet y vuelva a intentarlo");
		diccionario [0].Add ("Login Incorrecto", "Usuario o contraseña incorrectos");
		diccionario [0].Add ("Usuario", "Usuario");
		diccionario [0].Add ("Contrasena", "Contraseña");
		diccionario [0].Add ("Entrar", "Entrar");
		diccionario [0].Add ("SIN TITULO", "SIN TITULO");
		diccionario [0].Add ("PAUSA", "PAUSA");
		diccionario [0].Add ("OBJETIVOS", "OBJETIVOS");
		diccionario [0].Add ("Abandonar", "Abandonar");
		diccionario [0].Add ("Reiniciar", "Reiniciar");
		diccionario [0].Add ("Sin llave", "¡No has conseguido la llave! Vuelve a intentarlo para poder abrir la puerta");
		diccionario [0].Add ("Con llave", "¡Has conseguido la llave!");
		diccionario [0].Add ("Con Premio", "¡Has conseguido un premio!");
		diccionario [0].Add ("Terminar", "Terminar");
		diccionario [0].Add ("Siguiente", "Siguiente");
		diccionario [0].Add ("Anterior", "Anterior");
		diccionario [0].Add ("Atras", "Atrás");
		diccionario [0].Add ("PREMIOS", "PREMIOS");
		diccionario [0].Add ("Desconectando", "Desconectando");

		diccionario [1] = new Dictionary<string, string> ();
		diccionario [1].Add ("OPCIONES", "AUKERAK");
		diccionario [1].Add ("Castellano", "Gaztelaina");
		diccionario [1].Add ("Euskera", "Euskera");
		diccionario [1].Add ("Continuar", "Jarraitu");
		diccionario [1].Add ("Desconectar", "Deskonektatu");
		diccionario [1].Add ("Premios", "Premioak");
		diccionario [1].Add ("Opciones", "Aukerak");
		diccionario [1].Add ("Sincronizar", "Sinkronizatu");
		diccionario [1].Add ("Cargando_Energia", "Energia Berreskuratzen");
		diccionario [1].Add ("Comprobar", "Egiaztatu");
		diccionario [1].Add ("Despertando", "Esnatzen");
		diccionario [1].Add ("Datos Incorrectos", "Datu okerrak. Ezan zuren logopedaro");
		diccionario [1].Add ("Internet", "Internetera konectatu eta saiatu berriro");
		diccionario [1].Add ("Login Incorrecto", "Erabiltzaile edo pasahitza okerrak");
		diccionario [1].Add ("Usuario", "Erabiltzaile");
		diccionario [1].Add ("Contrasena", "Pasahitza");
		diccionario [1].Add ("Entrar", "Sartu");
		diccionario [1].Add ("SIN TITULO", "TITULORIK GABE");
		diccionario [1].Add ("PAUSA", "PAUSU");
		diccionario [1].Add ("OBJETIVOS", "HELBURUAK");
		diccionario [1].Add ("Abandonar", "Abandonatu");
		diccionario [1].Add ("Reiniciar", "Berrekin");
		diccionario [1].Add ("Sin llave", "Ez duzu giltrza lortu! Saiatu berriro atea irekitzeko");
		diccionario [1].Add ("Con llave", "Giltza lortu duzu!");
		diccionario [1].Add ("Con Premio", "Premio bat lortu duzu!");
		diccionario [1].Add ("Terminar", "Amaitu");
		diccionario [1].Add ("Siguiente", "Hurrengoa");
		diccionario [1].Add ("Anterior", "Aurrekoa");
		diccionario [1].Add ("Atras", "Atzera");
		diccionario [1].Add ("PREMIOS", "PREMIOAK");
		diccionario [1].Add ("Desconectando", "Deskonektatzen");
	}

	public static string getPalabra (string palabra)
	{
		if (diccionario == null)
			getDiccionario ();

		String idioma = PlayerPrefs.GetString ("idioma");
		if (idioma == null || idioma.Equals ("")) {
			idioma = "castellano";
		}
		if (idioma.Equals ("euskera")) {
			if (diccionario [1].ContainsKey (palabra)) {
				return diccionario [1] [palabra];
			} else {
				return "";
			}
		} else {
			if (diccionario [0].ContainsKey (palabra)) {
				return diccionario [0] [palabra];
			} else {
				return "";
			}
		}
	}
}
