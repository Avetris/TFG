using UnityEngine;
using System.Collections;
using System.Text;

//mysql
using MySql.Data;
using MySql.Data.MySqlClient;

//sqlite
using System.Data;
using System;
using System.Data.SqlClient;
using UnityEngine.Networking.NetworkSystem;
using System.Threading;
using System.Data.SqlTypes;
using System.Reflection;
using System.ComponentModel;
using System.IO;
using System.Security.Principal;
using System.Collections.Generic;
using Mono.Data.SqliteClient;
using System.Data.Common;
using UnityEngine.Networking;
using UnityEngine.Rendering;
using System.Globalization;
using System.ComponentModel.Design;

public class GestorSQLite
{
	static GestorSQLite miGestorBD = new GestorSQLite ();

	//sqlite
	string rutaAndroid = Application.persistentDataPath + "/database.sqlite";
	string rutaOrdenador = Application.streamingAssetsPath + "/database.sqlite";
	string rutaConexion = "";

	DbTransaction transaction;

	//Path to database.
	static SqliteConnection conexion;

	Func<string[], bool>[] sinc;

	Dictionary<string, List<Dictionary<string, object>>> listASincronizar;

	// Use this for initialization
	private GestorSQLite ()
	{
		#if UNITY_EDITOR
		rutaConexion = rutaOrdenador;
		#elif UNITY_ANDROID
		rutaConexion = rutaAndroid;
		#endif
			
		if (!File.Exists (rutaConexion)) {
			Debug.Log ("CREANDO BASE DE DATOS");
			WWW loadDB = new WWW ("jar:file://" + Application.dataPath + "!/assets/database.sqlite");
			while (!loadDB.isDone) {
			}
			Debug.Log (loadDB.ToString ());
			// then save to Application.persistentDataPath
			File.WriteAllBytes (rutaConexion, loadDB.bytes);
		} 
		rutaConexion = "URI=file:" + rutaConexion;
		conexion = new SqliteConnection (rutaConexion);
	}

	public static GestorSQLite getGestorSQLite ()
	{
		if (miGestorBD == null) {
			miGestorBD = new GestorSQLite ();
		}
		return miGestorBD;
	}

	public void conectar ()
	{
		try {
			if (conexion == null || conexion.ConnectionString == null) {
				conexion = new SqliteConnection (rutaConexion);
				conexion.Open ();
			} else if (conexion.State == ConnectionState.Closed || conexion.State == ConnectionState.Broken) {
				conexion.Open ();
			}
		} catch (Exception e) {
			Debug.Log (e);
		}
	}

	public void desconectar ()
	{
		try {
			if (conexion != null && conexion.State != ConnectionState.Closed && transaction == null) {
				conexion.Close ();
				conexion.Dispose ();
			}
		} catch (Exception e) {
			Debug.Log (e);
		}
	}

	public void setDatosASincronizar (Dictionary<string, List<Dictionary<string, object>>> datos)
	{
		listASincronizar = datos;
	}

	public void comenzarTransaccion ()
	{
		conectar ();
		if (transaction == null) {
			transaction = conexion.BeginTransaction ();
		}
	}

	public void commit ()
	{
		if (transaction != null) {
			transaction.Commit ();
			transaction = null;
		}
	}

	public void rollback ()
	{
		if (transaction != null) {
			transaction.Rollback ();
			transaction = null;
		}
	}


	public int obtenerUsuarioAcual ()
	{
		conectar ();
		int id_usuario = 0;
		try {
			String selectCount = "SELECT U_ID_USUARIO FROM USUARIO;";
			using (SqliteCommand cmd = new SqliteCommand (selectCount, conexion)) {
				using (SqliteDataReader reader = cmd.ExecuteReader ()) {
					if (reader.Read ()) {
						id_usuario = reader.GetInt32 (0);
					}
				}								
			}
			desconectar ();
			return id_usuario;
		} catch (Exception e) {
			desconectar ();
			Debug.Log (e);
			return 0;
		}
	}

	public String obtenerFechaSincro ()
	{
		conectar ();
		String fechaSincro = null;
		try {
			String selectCount = "SELECT DATETIME(U_ULT_SINCRO) FROM USUARIO;";
			using (SqliteCommand cmd = new SqliteCommand (selectCount, conexion)) {
				using (SqliteDataReader reader = cmd.ExecuteReader ()) {
					if (reader.Read ()) {
						fechaSincro = reader.GetString (0);
					} else {
						return null;
					}
				}								
			}
			desconectar ();
			return fechaSincro;
		} catch (Exception e) {
			desconectar ();
			Debug.Log (e);
			return null;
		}
	}

	public void reiniciar ()
	{
		conectar ();
		comenzarTransaccion ();
		try {
			using (SqliteCommand cmd = new SqliteCommand ("DELETE FROM USUARIO;", conexion)) {
				cmd.ExecuteNonQuery ();
			}
			using (SqliteCommand cmd = new SqliteCommand ("DELETE FROM CONTENIDO;", conexion)) {
				cmd.ExecuteNonQuery ();
			}
			using (SqliteCommand cmd = new SqliteCommand ("DELETE FROM MINIJUEGO;", conexion)) {
				cmd.ExecuteNonQuery ();
			}
			using (SqliteCommand cmd = new SqliteCommand ("DELETE FROM GRUPO;", conexion)) {
				cmd.ExecuteNonQuery ();
			}
			using (SqliteCommand cmd = new SqliteCommand ("DELETE FROM GRUPO_MINIJUEGOS;", conexion)) {
				cmd.ExecuteNonQuery ();
			}
			using (SqliteCommand cmd = new SqliteCommand ("DELETE FROM HISTORIAL_MAPAS;", conexion)) {
				cmd.ExecuteNonQuery ();
			}
			using (SqliteCommand cmd = new SqliteCommand ("DELETE FROM HISTORIAL_JUEGO;", conexion)) {
				cmd.ExecuteNonQuery ();
			}
			using (SqliteCommand cmd = new SqliteCommand ("DELETE FROM PREMIO;", conexion)) {
				cmd.ExecuteNonQuery ();
			}
			commit ();
			desconectar ();
		} catch (Exception e) {
			rollback ();
			desconectar ();
			Debug.Log (e);
		}
	}

	public void cambioUsuario ()
	{
		conectar ();
		string[] deletes = new string[] {
			"DELETE FROM USUARIO",
			"DELETE FROM PREMIO",
			"DELETE FROM HISTORIAL_JUEGO",
			"DELETE FROM HISTORIAL_MAPAS",
			"DELETE FROM GRUPO",
			"DELETE FROM MINIJUEGO",
			"DELETE FROM GRUPO_MINIJUEGOS"
		};
		foreach (string sql in deletes) {
			using (SqliteCommand cmd = new SqliteCommand (sql, conexion)) {
				cmd.ExecuteNonQuery ();
				cmd.Dispose ();					
			}
		}
		desconectar ();
	}

	public String obtenerFechaImagen (String tabla)
	{
		conectar ();
		String fechaImagen = null;
		try {
			String selectCount = "SELECT TI_FECMOD FROM TIEMPOS_IMAGENES WHERE TI_NOMBRE_TABLA = :nombre_tabla;";
			using (SqliteCommand cmd = new SqliteCommand (selectCount, conexion)) {
				cmd.Parameters.Add ("nombre_tabla", tabla);
				using (SqliteDataReader reader = cmd.ExecuteReader ()) {
					if (reader.Read ()) {
						fechaImagen = reader.GetString (0);
					}
				}								
			}
			desconectar ();
			return fechaImagen;
		} catch (Exception e) {
			desconectar ();
			Debug.Log (e);
			return fechaImagen;
		}
	}

	public bool actualizarFechas (int id_usuario, DateTime fecha)
	{
		try {
			conectar ();
			String update = "UPDATE USUARIO SET U_ULT_SINCRO = :fecha WHERE U_ID_USUARIO = :id_usuario;";
			using (SqliteCommand cmd = new SqliteCommand (update, conexion)) {
				cmd.Parameters.Add ("id_usuario", id_usuario);
				cmd.Parameters.Add ("fecha", fecha.ToString ("yyyy-MM-dd HH:mm:ss"));
				cmd.ExecuteNonQuery ();
				cmd.Dispose ();
			}
			String[] tablas = new String[]{ "Fondos", "Mapas" };
			String insert = "INSERT OR REPLACE INTO TIEMPOS_IMAGENES (TI_NOMBRE_TABLA, TI_FECMOD) VALUES (:nombre_tabla, :fecha);";
			for (int i = 0; i < tablas.Length; i++) {
				using (SqliteCommand cmd = new SqliteCommand (insert, conexion)) {
					cmd.Parameters.Add ("nombre_tabla", tablas [i]);
					cmd.Parameters.Add ("fecha", fecha.ToString ("yyyy-MM-dd HH:mm:ss"));
					cmd.ExecuteNonQuery ();
					cmd.Dispose ();									
				}
			}
			desconectar ();
			return true;
		} catch (Exception e) {
			Debug.Log (e);
			desconectar ();
			return false;
		}
	}

	public void sincronizarUsuario ()
	{
		conectar ();
		List<Dictionary<string, object>> usuario = listASincronizar ["usuario"];
		try {
			String sqlUsuario = "INSERT OR REPLACE INTO USUARIO (U_ID_USUARIO, U_NOMBRE, U_IDIOMA_JUEGO, U_ULT_SINCRO) VALUES (:id_usuario, :nombre, :idioma_juego, DATETIME(CURRENT_TIMESTAMP, 'localtime'))";
			if (usuario.Count > 0) {
				using (SqliteCommand cmd = new SqliteCommand (sqlUsuario, conexion)) {
					cmd.Parameters.Add (new SqliteParameter ("id_usuario", usuario [0] ["N_ID_NINO"]));
					cmd.Parameters.Add (new SqliteParameter ("nombre", usuario [0] ["N_NOMBRE"]));
					cmd.Parameters.Add (new SqliteParameter ("idioma_juego", usuario [0] ["N_IDIOMA_JUEGO"]));
					cmd.ExecuteNonQuery ();
					cmd.Dispose ();
				}
			}
			desconectar ();
		} catch (Exception e) {
			Debug.Log (e);
			desconectar ();
			Sync.getSync ().setErrorCode (-1);
		}		
	}

	public void sincronizarContenido ()
	{
		List<Dictionary<string, object>> contenido = listASincronizar ["contenido"];
		Debug.Log ("Contenido: " + contenido.Count);
		try {		
			conectar ();
			String sqlDelete = "DELETE FROM CONTENIDO WHERE C_ID_CONTENIDO = :id_contenido";
			String sqlInsert = "INSERT OR REPLACE INTO CONTENIDO (C_ID_CONTENIDO, C_CASTELLANO, C_EUSKERA, C_IMAGEN) VALUES (:id_contenido, :castellano, :euskera, :imagen)";
			for (int i = 0; i < contenido.Count; i++) {
				if (conexion.State == ConnectionState.Executing) {
					i--;
				} else {
					if (Constantes.quitarNull (contenido [i] ["ACCION"]).Equals ("B")) {	
						using (SqliteCommand cmd = new SqliteCommand (sqlDelete, conexion)) {
							cmd.Parameters.Add (new SqliteParameter ("id_contenido", contenido [i] ["ID_CONTENIDO"]));
							cmd.Dispose ();
						}
					} else {
						if (!Constantes.quitarNull (contenido [i] ["ID_CONTENIDO"]).Equals ("")) {
							using (SqliteCommand cmd = new SqliteCommand (sqlInsert, conexion)) {
								cmd.Parameters.Add (new SqliteParameter ("id_contenido", contenido [i] ["ID_CONTENIDO"]));
								cmd.Parameters.Add (new SqliteParameter ("castellano", contenido [i] ["CASTELLANO"]));
								cmd.Parameters.Add (new SqliteParameter ("euskera", contenido [i] ["EUSKERA"]));
								cmd.Parameters.Add (new SqliteParameter ("imagen", Constantes.getConstantes ().obtenerImagen (contenido [i] ["IMAGEN"])));
								cmd.ExecuteNonQuery ();
								cmd.Dispose ();
							}
						}
					}
				}
			}

			Debug.Log ("Fin Contenido");
			desconectar ();
		} catch (Exception e) {
			Debug.Log (e);
			desconectar ();
			Sync.getSync ().setErrorCode (-1);
		}
	}

	public void sincronizarGrupo ()
	{
		List<Dictionary<string, object>> grupo = listASincronizar ["grupo"];
		Debug.Log ("Grupo: " + grupo.Count);
		try {
			conectar ();
			String sqlGrupoDelete = "DELETE FROM GRUPO WHERE G_ID_GRUPO = :id_grupo";

			String sqlGrupo = "INSERT OR REPLACE INTO GRUPO (G_ID_GRUPO, G_CONTENIDO_SOLUCION, G_CONTENIDO_OPCION) VALUES (:id_grupo, :id_contenido_solucion, :id_contenido_opcion)";
			for (int i = 0; i < grupo.Count; i++) {
				if (conexion.State == ConnectionState.Executing) {
					i--;
				} else {
					if (Constantes.quitarNull (grupo [i] ["ACCION"]).Equals ("B")) {
						if (!Constantes.quitarNull (grupo [i] ["ID_GRUPO"]).Equals ("")) {
							using (SqliteCommand cmd = new SqliteCommand (sqlGrupoDelete, conexion)) {
								cmd.Parameters.Add (new SqliteParameter ("id_grupo", grupo [i] ["ID_GRUPO"]));
								cmd.ExecuteNonQuery ();
								cmd.Dispose ();
							}
						}
					} else if (!Constantes.quitarNull (grupo [i] ["ID_GRUPO"]).Equals ("") && !Constantes.quitarNull (grupo [i] ["SOLUCION"]).Equals ("")
					           && !Constantes.quitarNull (grupo [i] ["OPCION"]).Equals ("")) {
						using (SqliteCommand cmd = new SqliteCommand (sqlGrupo, conexion)) {
							cmd.Parameters.Add (new SqliteParameter ("id_grupo", grupo [i] ["ID_GRUPO"]));
							cmd.Parameters.Add (new SqliteParameter ("id_contenido_solucion", grupo [i] ["SOLUCION"]));
							cmd.Parameters.Add (new SqliteParameter ("id_contenido_opcion", grupo [i] ["OPCION"]));
							cmd.ExecuteNonQuery ();
							cmd.Dispose ();
						}
					}
				}			
			}
			Debug.Log ("Fin Grupo");
			desconectar ();
		} catch (Exception e) {
			Debug.Log (e);
			desconectar ();
			Sync.getSync ().setErrorCode (-1);
		}
	}

	public void sincronizarMinijuego ()
	{
		List<Dictionary<string, object>> minijuego = listASincronizar ["minijuegos"];
		Debug.Log ("Minijuego: " + minijuego.Count);
		try {
			conectar ();
			String sqlMinijuegoDelete = "DELETE FROM MINIJUEGO WHERE M_ID_MINIJUEGO = :id_minijuego; DELETE FROM GRUPO_MINIJUEGOS WHERE GM_ID_MINIJUEGO = :id_minijuego";

			String sqlMinijuego = "INSERT OR REPLACE INTO MINIJUEGO (M_ID_MINIJUEGO, M_NOMBRE, M_DESCRIPCION, M_MAXIMO, M_MINIMO, M_PUNTUACION_MAXIMA) VALUES (:id_minijuego, :nombre, :descripcion, :maximo, :minimo, :puntuacion_maxima)";
			for (int i = 0; i < minijuego.Count; i++) {
				if (!Constantes.quitarNull (minijuego [i] ["ID_MINIJUEGO"]).Equals ("")) {
					if (conexion.State == ConnectionState.Executing) {
						i--;
					} else {
						if (Constantes.quitarNull (minijuego [i] ["ACCION"]).Equals ("B")) {
							using (SqliteCommand cmd = new SqliteCommand (sqlMinijuegoDelete, conexion)) {
								cmd.Parameters.Add (new SqliteParameter ("id_minijuego", minijuego [i] ["ID_MINIJUEGO"]));
								cmd.ExecuteNonQuery ();
								cmd.Dispose ();
							}
						} else if (!Constantes.quitarNull (minijuego [i] ["NOMBRE"]).Equals ("")) {
							using (SqliteCommand cmd = new SqliteCommand (sqlMinijuego, conexion)) {
								cmd.Parameters.Add (new SqliteParameter ("id_minijuego", minijuego [i] ["ID_MINIJUEGO"]));
								cmd.Parameters.Add (new SqliteParameter ("nombre", minijuego [i] ["NOMBRE"]));
								cmd.Parameters.Add (new SqliteParameter ("descripcion", minijuego [i] ["DESCRIPCION"]));
								cmd.Parameters.Add (new SqliteParameter ("maximo", minijuego [i] ["MAXIMO"]));
								cmd.Parameters.Add (new SqliteParameter ("minimo", minijuego [i] ["MINIMO"]));
								cmd.Parameters.Add (new SqliteParameter ("puntuacion_maxima", minijuego [i] ["PUNTUACION_MAXIMA"]));
								cmd.ExecuteNonQuery ();
								cmd.Dispose ();
							}
						}
					}
				}
			}
			Debug.Log ("Fin Minijuego");
			desconectar ();
		} catch (Exception e) {
			Debug.Log (e.Message);
			desconectar ();
			Sync.getSync ().setErrorCode (-1);
		}
	}

	public void sincronizarGrupoMinijuego ()
	{
		List<Dictionary<string, object>> grupo_minijuego = listASincronizar ["grupo_minijuegos"];
		Debug.Log ("Grupo Minijuego: " + grupo_minijuego.Count);
		try {
			conectar ();
			String sqlMinijuegoContenidoDelete = "DELETE FROM GRUPO_MINIJUEGOS WHERE GM_ID_MINIJUEGO = :id_minijuego AND GM_ID_GRUPO = :id_grupo";
			String sqlMinijuegoContenido = "INSERT OR REPLACE INTO GRUPO_MINIJUEGOS (GM_ID_MINIJUEGO, GM_ID_GRUPO) VALUES (:id_minijuego, :id_grupo)";
			for (int i = 0; i < grupo_minijuego.Count; i++) {
				if (!Constantes.quitarNull (grupo_minijuego [i] ["ID_MINIJUEGO"]).Equals ("") && !Constantes.quitarNull (grupo_minijuego [i] ["ID_GRUPO"]).Equals ("")) {
					if (conexion.State == ConnectionState.Executing) {
						i--;
					} else {
						if (Constantes.quitarNull (grupo_minijuego [i] ["ACCION"]).Equals ("B")) {
							using (SqliteCommand cmd = new SqliteCommand (sqlMinijuegoContenidoDelete, conexion)) {
								cmd.Parameters.Add (new SqliteParameter ("id_minijuego", grupo_minijuego [i] ["ID_MINIJUEGO"]));
								cmd.Parameters.Add (new SqliteParameter ("id_grupo", grupo_minijuego [i] ["ID_GRUPO"]));
								cmd.ExecuteNonQuery ();
								cmd.Dispose ();
							}
						} else {
							using (SqliteCommand cmd = new SqliteCommand (sqlMinijuegoContenido, conexion)) {
								cmd.Parameters.Add (new SqliteParameter ("id_minijuego", grupo_minijuego [i] ["ID_MINIJUEGO"]));
								cmd.Parameters.Add (new SqliteParameter ("id_grupo", grupo_minijuego [i] ["ID_GRUPO"]));
								cmd.ExecuteNonQuery ();
								cmd.Dispose ();
							}
						}
					}
				}
			}
			Debug.Log ("Fin Grupo Minijuego");
			desconectar ();
		} catch (Exception e) {
			Debug.Log (e.Message);
			desconectar ();
			Sync.getSync ().setErrorCode (-1);
		}
	}

	public void sincronizarFondos ()
	{
		List<Dictionary<string, object>> fondos = listASincronizar ["fondos"];
		Debug.Log ("Fondos: " + fondos.Count);
		try {
			conectar ();
			String sqlFondo = "INSERT OR REPLACE INTO FONDO (F_ID_FONDO, F_IMAGEN) VALUES (:id_fondo, :imagen)";
			for (int i = 0; i < fondos.Count; i++) {
				if (!Constantes.quitarNull (fondos [i] ["ID_FONDO"]).Equals ("") && !Constantes.quitarNull (fondos [i] ["IMAGEN"]).Equals ("")) {
					if (conexion.State == ConnectionState.Executing) {
						i--;
					} else {
						using (SqliteCommand cmd = new SqliteCommand (sqlFondo, conexion)) {
							cmd.Parameters.Add (new SqliteParameter ("id_fondo", fondos [i] ["ID_FONDO"]));
							cmd.Parameters.Add (new SqliteParameter ("imagen", Constantes.getConstantes ().obtenerImagen (fondos [i] ["IMAGEN"])));
							cmd.ExecuteNonQuery ();
							cmd.Dispose ();
						}
					}
				}
			}
			Debug.Log ("Fin Fondos");
			desconectar ();
		} catch (Exception e) {
			Debug.Log (e.Message);
			desconectar ();
			Sync.getSync ().setErrorCode (-1);
		}
	}

	public void sincronizarMapas ()
	{
		List<Dictionary<string, object>> mapas = listASincronizar ["mapas"];
		Debug.Log ("Mapas: " + mapas.Count);
		try {
			conectar ();
			String sqlMapa = "INSERT OR REPLACE INTO MAPA (MA_ID_MAPA, MA_IMAGEN) VALUES (:id_mapa, :imagen)";
			for (int i = 0; i < mapas.Count; i++) {
				if (!Constantes.quitarNull (mapas [i] ["ID_MAPA"]).Equals ("") && !Constantes.quitarNull (mapas [i] ["IMAGEN"]).Equals ("")) {
					if (conexion.State == ConnectionState.Executing) {
						i--;
					} else {
						using (SqliteCommand cmd = new SqliteCommand (sqlMapa, conexion)) {
							cmd.Parameters.Add (new SqliteParameter ("id_mapa", mapas [i] ["ID_MAPA"]));
							cmd.Parameters.Add (new SqliteParameter ("imagen", Constantes.getConstantes ().obtenerImagen (mapas [i] ["IMAGEN"])));
							cmd.ExecuteNonQuery ();
							cmd.Dispose ();
						}
					}
				}
			}
			Debug.Log ("Fin Mapas");
			desconectar ();
		} catch (Exception e) {
			Debug.Log (e.Message);
			desconectar ();
			Sync.getSync ().setErrorCode (-1);
		}
	}

	public void sincronizarBotones ()
	{
		List<Dictionary<string, object>> botones = listASincronizar ["botones"];
		Debug.Log ("Botones: " + botones.Count);
		try {
			conectar ();
			String sqlBotones = "INSERT OR REPLACE INTO BOTONES_MAPA (BM_ID_MAPA, BM_ID_BOTON, BM_POSICION_X, BM_POSICION_Y, BM_WIDTH, BM_HEIGHT) VALUES (:id_mapa, :id_boton, :posicion_x, :posicion_y, :width, :height)";
			String sqlBotonesDelete = "DELETE FROM BOTONES_MAPA WHERE BM_ID_MAPA = :id_mapa AND BM_ID_BOTON = :id_boton";
			String sqlBotonesDeleteHistorial = "DELETE FROM HISTORIAL_MAPAS WHERE HM_ID_MAPA = :id_mapa AND HM_ID_BOTON = :id_boton";
			for (int i = 0; i < botones.Count; i++) {
				if (!Constantes.quitarNull (botones [i] ["ID_MAPA"]).Equals ("") && !Constantes.quitarNull (botones [i] ["ID_BOTON"]).Equals ("")) {
					if (Constantes.quitarNull (botones [i] ["ID_MAPA"]).Equals ("B")) {
						using (SqliteCommand cmd = new SqliteCommand (sqlBotonesDelete, conexion)) {
							cmd.Parameters.Add (new SqliteParameter ("id_mapa", botones [i] ["ID_MAPA"]));
							cmd.Parameters.Add (new SqliteParameter ("id_boton", botones [i] ["ID_BOTON"]));
							cmd.ExecuteNonQuery ();
							cmd.Dispose ();
						}
						using (SqliteCommand cmd = new SqliteCommand (sqlBotonesDeleteHistorial, conexion)) {
							cmd.Parameters.Add (new SqliteParameter ("id_mapa", botones [i] ["ID_MAPA"]));
							cmd.Parameters.Add (new SqliteParameter ("id_boton", botones [i] ["ID_BOTON"]));
							cmd.ExecuteNonQuery ();
							cmd.Dispose ();
						}
					} else {
						if (!Constantes.quitarNull (botones [i] ["POSICION_X"]).Equals ("")
						    && !Constantes.quitarNull (botones [i] ["POSICION_Y"]).Equals ("") && !Constantes.quitarNull (botones [i] ["WIDTH"]).Equals ("") && !Constantes.quitarNull (botones [i] ["HEIGHT"]).Equals ("")) {					
							using (SqliteCommand cmd = new SqliteCommand (sqlBotones, conexion)) {
								cmd.Parameters.Add (new SqliteParameter ("id_mapa", botones [i] ["ID_MAPA"]));
								cmd.Parameters.Add (new SqliteParameter ("id_boton", botones [i] ["ID_BOTON"]));
								cmd.Parameters.Add (new SqliteParameter ("posicion_x", botones [i] ["POSICION_X"]));
								cmd.Parameters.Add (new SqliteParameter ("posicion_y", botones [i] ["POSICION_Y"]));
								cmd.Parameters.Add (new SqliteParameter ("width", botones [i] ["WIDTH"]));
								cmd.Parameters.Add (new SqliteParameter ("height", botones [i] ["HEIGHT"]));
								cmd.ExecuteNonQuery ();
								cmd.Dispose ();
							}
						}
					}
				}

			}
			Debug.Log ("Fin Botones");
			desconectar ();
		} catch (Exception e) {
			Debug.Log (e.Message);
			desconectar ();
			Sync.getSync ().setErrorCode (-1);
		}
	}

	public void sincronizarPremios ()
	{
		List<Dictionary<string, object>> premios = listASincronizar ["premios"];
		Debug.Log ("Premios: " + premios.Count);
		try {
			conectar ();
			String sql = "INSERT OR REPLACE INTO PREMIO (PR_ID_PREMIO, PR_NOMBRE, PR_IMAGEN, PR_CONSEGUIDO, PR_FECMOD) VALUES (:id_premio, :nombre,  :imagen, :conseguido, DATETIME(CURRENT_TIMESTAMP, 'localtime'));";
			for (int i = 0; i < premios.Count; i++) {
				if (!Constantes.quitarNull (premios [i] ["ID_PREMIO"]).Equals ("") && !Constantes.quitarNull (premios [i] ["NOMBRE"]).Equals ("")
				    && !Constantes.quitarNull (premios [i] ["IMAGEN"]).Equals ("") && !Constantes.quitarNull (premios [i] ["CONSEGUIDO"]).Equals ("")) {
					if (conexion.State == ConnectionState.Executing) {
						i--;
					} else {
						using (SqliteCommand cmd = new SqliteCommand (sql, conexion)) {
							cmd.Parameters.Add (new SqliteParameter ("id_premio", premios [i] ["ID_PREMIO"]));
							cmd.Parameters.Add (new SqliteParameter ("nombre", premios [i] ["NOMBRE"]));
							cmd.Parameters.Add (new SqliteParameter ("imagen", Constantes.getConstantes ().obtenerImagen (premios [i] ["IMAGEN"])));
							cmd.Parameters.Add (new SqliteParameter ("conseguido", premios [i] ["CONSEGUIDO"]));
							cmd.ExecuteNonQuery ();
							cmd.Dispose ();
						}
					}
				}
			}
			Debug.Log ("Fin Premios");
			desconectar ();
		} catch (Exception e) {
			Debug.Log (e.GetBaseException ());
			desconectar ();
			Sync.getSync ().setErrorCode (-1);
		}
	}

	public void sincronizarHistorial_Mapas ()
	{
		List<Dictionary<string, object>> historial = listASincronizar ["historial_mapas"];
		Debug.Log ("Historial Mapas: " + historial.Count);
		try {
			conectar ();
			String sql = "INSERT OR REPLACE INTO HISTORIAL_MAPAS (HM_ID_MINIJUEGO, HM_ID_MAPA, HM_POS_MAPA, HM_ID_BOTON, HM_ESTADO, HM_FECHA) VALUES (:id_minijuego, :id_mapa, :pos_mapa, :id_boton, :estado, :fecha);";
			for (int i = 0; i < historial.Count; i++) {
				if (!Constantes.quitarNull (historial [i] ["HM_ID_MINIJUEGO"]).Equals ("") && !Constantes.quitarNull (historial [i] ["HM_ID_MAPA"]).Equals ("")
				    && !Constantes.quitarNull (historial [i] ["HM_POS_MAPA"]).Equals ("") && !Constantes.quitarNull (historial [i] ["HM_ID_BOTON"]).Equals ("")) {
					if (conexion.State == ConnectionState.Executing) {
						i--;
					} else {
						using (SqliteCommand cmd = new SqliteCommand (sql, conexion)) {
							cmd.Parameters.Add (new SqliteParameter ("id_minijuego", historial [i] ["HM_ID_MINIJUEGO"]));
							cmd.Parameters.Add (new SqliteParameter ("id_mapa", historial [i] ["HM_ID_MAPA"]));
							cmd.Parameters.Add (new SqliteParameter ("pos_mapa", historial [i] ["HM_POS_MAPA"]));
							cmd.Parameters.Add (new SqliteParameter ("id_boton", historial [i] ["HM_ID_BOTON"]));
							cmd.Parameters.Add (new SqliteParameter ("estado", historial [i] ["HM_ESTADO"]));
							cmd.Parameters.Add (new SqliteParameter ("fecha", DateTime.Parse (historial [i] ["HM_FECHA"].ToString ())));
							cmd.ExecuteNonQuery ();
							cmd.Dispose ();
						}	
					}				
				}
			}
			Debug.Log ("Fin Historial Mapas");
			desconectar ();
		} catch (Exception e) {
			Debug.Log (e);
			desconectar ();
			Sync.getSync ().setErrorCode (-1);
		}
	}

	public int[][] obtenerHistorialMapas ()
	{
		int count = 0;
		int[] pos_mapa = new int[3];
		try {
			while (conexion.State == ConnectionState.Executing) {
			}
			conectar ();
			transaction = conexion.BeginTransaction ();
			comprobarMinijuegosQuitados ();
			String selectCount = "SELECT HM_POS_MAPA FROM V_MAPAS GROUP BY HM_POS_MAPA ORDER BY HM_POS_MAPA ASC LIMIT 0,3;";
			using (SqliteCommand cmd = new SqliteCommand (selectCount, conexion)) {
				using (SqliteDataReader reader = cmd.ExecuteReader ()) {
					while (reader.Read ()) {
						pos_mapa [count] = reader.GetInt32 (0);
						count++;
					}
					reader.Close ();
				}					
				cmd.Dispose ();
			}
			while (count != 3) {
				if (count == 0) {
					pos_mapa [count] = anadirHistorialMapas (true);
				} else {
					pos_mapa [count] = anadirHistorialMapas (false);
				}
				count++;
			}
			using (SqliteCommand cmd = new SqliteCommand ("SELECT COUNT(1) FROM V_MAPAS WHERE HM_POS_MAPA IN (:pos_mapa1, :pos_mapa2, :pos_mapa3) AND HM_ESTADO = 'Actual'", conexion)) {
				cmd.Parameters.Add (new SqliteParameter ("pos_mapa1", pos_mapa [0]));
				cmd.Parameters.Add (new SqliteParameter ("pos_mapa2", pos_mapa [1]));
				cmd.Parameters.Add (new SqliteParameter ("pos_mapa3", pos_mapa [2]));				
				using (SqliteDataReader reader = cmd.ExecuteReader ()) {
					if (reader.Read ()) {
						if (reader.GetInt32 (0) == 0) {
							using (SqliteCommand cmd2 = new SqliteCommand ("UPDATE HISTORIAL_MAPAS SET HM_ESTADO = 'Actual', HM_FECHA = DATETIME(CURRENT_TIMESTAMP, 'localtime') WHERE HM_POS_MAPA = :pos_mapa AND HM_ESTADO = 'Futuro'", conexion)) {
								cmd2.Parameters.Add (new SqliteParameter ("pos_mapa", pos_mapa [0]));	
								cmd2.ExecuteNonQuery ();
								cmd2.Dispose ();
							}
						}
					}
					reader.Close ();
				}					
				cmd.Dispose ();
			}
			int[][] resultado = null;
			String countTotal = "SELECT COUNT(1) FROM HISTORIAL_MAPAS WHERE HM_POS_MAPA IN (:pos_mapa1, :pos_mapa2, :pos_mapa3);";
			String selectTotal = "SELECT HM_ID_MAPA, HM_POS_MAPA, HM_ID_BOTON, HM_ID_MINIJUEGO FROM HISTORIAL_MAPAS WHERE HM_POS_MAPA IN (:pos_mapa1, :pos_mapa2, :pos_mapa3) ORDER BY HM_POS_MAPA;";
			using (SqliteCommand cmd = new SqliteCommand (countTotal, conexion)) {
				cmd.Parameters.Add (new SqliteParameter ("pos_mapa1", pos_mapa [0]));
				cmd.Parameters.Add (new SqliteParameter ("pos_mapa2", pos_mapa [1]));
				cmd.Parameters.Add (new SqliteParameter ("pos_mapa3", pos_mapa [2]));
				using (SqliteDataReader reader = cmd.ExecuteReader ()) {
					if (reader.Read ()) {
						resultado = new int[reader.GetInt32 (0)][];
					}
					reader.Close ();
				}					
				cmd.Dispose ();							
			}
			using (SqliteCommand cmd = new SqliteCommand (selectTotal, conexion)) {
				cmd.Parameters.Add (new SqliteParameter ("pos_mapa1", pos_mapa [0]));
				cmd.Parameters.Add (new SqliteParameter ("pos_mapa2", pos_mapa [1]));
				cmd.Parameters.Add (new SqliteParameter ("pos_mapa3", pos_mapa [2]));
				using (SqliteDataReader reader = cmd.ExecuteReader ()) {
					for (int i = 0; i < resultado.Length && reader.Read (); i++) {
						resultado [i] = new int[4];
						for (int j = 0; j < resultado [i].Length; j++) {
							resultado [i] [j] = reader.GetInt32 (j);
						}
					}
					reader.Close ();
				}					
				cmd.Dispose ();							
			}
			if (resultado.Length > 0 && resultado [0].Length > 0) {
				commit ();
			} else {
				rollback ();
			}
			desconectar ();
			return resultado;
		} catch (Exception e) {
			Debug.Log (e.Message);
			if (transaction != null) {
				rollback ();
			}
			desconectar ();
			Constantes.setErrorDatos ();
			return new int[0][];
		}
	}

	private int anadirHistorialMapas (bool actual)
	{
		int mapa = 0;
		int[] botones = null;
		int pos_mapa = 1;
		try {
			conectar ();
			using (SqliteCommand cmd = new SqliteCommand ("SELECT IFNULL(MAX(HM_POS_MAPA), 0)+1 FROM HISTORIAL_MAPAS", conexion)) {
				using (SqliteDataReader reader = cmd.ExecuteReader ()) {
					if (reader.Read ()) {
						pos_mapa = reader.GetInt32 (0);
					}
					reader.Close ();
				}
				cmd.Dispose ();	
			}	
			String selectMapas = "SELECT MA_ID_MAPA FROM MAPA ORDER BY RANDOM() LIMIT 0,1;";
			using (SqliteCommand cmd = new SqliteCommand (selectMapas, conexion)) {
				using (SqliteDataReader reader = cmd.ExecuteReader ()) {
					if (reader.Read ()) {
						mapa = reader.GetInt32 (0);
					}
					reader.Close ();
				}	
				cmd.Dispose ();									
			}
			String countBotones = "SELECT COUNT(BM_ID_BOTON) FROM BOTONES_MAPA WHERE BM_ID_MAPA = :id_mapa;";
			String selectBotones = "SELECT BM_ID_BOTON FROM BOTONES_MAPA WHERE BM_ID_MAPA = :id_mapa;";
			using (SqliteCommand cmd = new SqliteCommand (countBotones, conexion)) {
				cmd.Parameters.Add (new SqliteParameter ("id_mapa", mapa));
				using (SqliteDataReader reader = cmd.ExecuteReader ()) {
					if (reader.Read ()) {
						botones = new int[reader.GetInt32 (0)];
					}
					reader.Close ();
				}	
				cmd.Dispose ();									
			}
			using (SqliteCommand cmd = new SqliteCommand (selectBotones, conexion)) {
				cmd.Parameters.Add (new SqliteParameter ("id_mapa", mapa));
				using (SqliteDataReader reader = cmd.ExecuteReader ()) {
					for (int i = 0; i < botones.Length && reader.Read (); i++) {
						botones [i] = reader.GetInt32 (0);
					}
					reader.Close ();
				}	
				cmd.Dispose ();									
			}
			StringBuilder selectInsercion = new StringBuilder ("INSERT INTO HISTORIAL_MAPAS (HM_ID_MINIJUEGO, HM_ID_MAPA, HM_POS_MAPA, HM_ID_BOTON, HM_ESTADO, HM_FECHA) ");
			selectInsercion.Append ("SELECT M_ID_MINIJUEGO, :id_mapa, :pos_mapa, :id_boton, :estado, DATETIME(CURRENT_TIMESTAMP, 'localtime') FROM V_MINIJUEGOS ORDER BY RANDOM() LIMIT 1;");
			for (int i = 0; i < botones.Length; i++) {
				using (SqliteCommand cmd = new SqliteCommand (selectInsercion.ToString (), conexion)) {
					cmd.Parameters.Add (new SqliteParameter ("id_mapa", mapa));
					cmd.Parameters.Add (new SqliteParameter ("pos_mapa", pos_mapa));
					cmd.Parameters.Add (new SqliteParameter ("id_boton", botones [i]));
					if (actual) {
						cmd.Parameters.Add (new SqliteParameter ("estado", "Actual"));	
					} else {
						cmd.Parameters.Add (new SqliteParameter ("estado", "Futuro"));
					}

					cmd.ExecuteNonQuery ();
					cmd.Dispose ();					
				}
			}
			desconectar ();
		} catch (Exception e) {
			Debug.Log (e);
			desconectar ();
			Constantes.setErrorDatos ();
		}
		return pos_mapa;
	}

	private void comprobarMinijuegosQuitados ()
	{
		try {
			conectar ();
			List<int[]> lista = new List<int[]> ();
			using (SqliteCommand cmd = new SqliteCommand ("SELECT HM_POS_MAPA, HM_ID_MAPA, HM_ID_BOTON FROM HISTORIAL_MAPAS WHERE HM_ID_MINIJUEGO NOT IN (SELECT M_ID_MINIJUEGO FROM V_MINIJUEGOS) OR HM_ESTADO = 'Futuro' AND HM_ESTADO IN ('Futuro', 'Actual')", conexion)) {
				using (SqliteDataReader reader = cmd.ExecuteReader ()) {
					while (reader.Read ()) {
						int[] l = new int[3];
						l [0] = reader.GetInt32 (0);
						l [1] = reader.GetInt32 (1);
						l [2] = reader.GetInt32 (2);
						lista.Add (l);
					}
					reader.Close ();
				}	
				cmd.Dispose ();											
			}
			for (int i = 0; i < lista.Count; i++) {
				String update = "UPDATE HISTORIAL_MAPAS SET HM_ID_MINIJUEGO = (SELECT M_ID_MINIJUEGO FROM V_MINIJUEGOS ORDER BY RANDOM() LIMIT 1) WHERE HM_POS_MAPA = :pos_mapa AND HM_ID_MAPA = :id_mapa AND HM_ID_BOTON = :id_boton";
				using (SqliteCommand cmd = new SqliteCommand (update, conexion)) {
					cmd.Parameters.Add (new SqliteParameter ("pos_mapa", lista [i] [0]));
					cmd.Parameters.Add (new SqliteParameter ("id_mapa", lista [i] [1]));
					cmd.Parameters.Add (new SqliteParameter ("id_boton", lista [i] [2]));
					cmd.ExecuteNonQuery ();
					cmd.Dispose ();
				}
			}
			desconectar ();
		} catch (Exception e) {
			Debug.Log (e.Message);
			desconectar ();
			Constantes.setErrorDatos ();
		}
	}

	public Texture2D[] obtenerMapas (int width, int height, int[] id_mapas)
	{
		String select = "SELECT MA_IMAGEN FROM MAPA WHERE MA_ID_MAPA = :id_mapa;";
		Texture2D[] mapas = new Texture2D[id_mapas.Length];
		conectar ();
		for (int i = 0; i < id_mapas.Length; i++) {
			using (SqliteCommand cmd = new SqliteCommand (select, conexion)) {
				cmd.Parameters.Add (new SqliteParameter ("id_mapa", id_mapas [i]));
				using (SqliteDataReader reader = cmd.ExecuteReader ()) {
					if (reader.Read ()) {
						mapas [i] = new Texture2D (width, height);
						mapas [i].LoadImage ((byte[])reader.GetValue (0));
						mapas [i].Apply ();

					}
					reader.Close ();
				}

				cmd.Dispose ();							
			}
		}
		desconectar ();
		foreach (Texture2D mapa in mapas) {
			if (mapa == null) {
				Constantes.setErrorDatos ();
				return null;
			}

		}
		return mapas;
	}

	public Dictionary<string, object[]>[] obtenerBotones (float width, float height, int[] pos_mapas)
	{
		String select = "SELECT BM_ID_BOTON, BM_POSICION_X, BM_POSICION_Y, BM_WIDTH, BM_HEIGHT, HM_ESTADO, HM_ID_MINIJUEGO FROM V_BOTONES WHERE HM_POS_MAPA = :pos_mapa;";
		Dictionary<string, object[]>[] botones = new Dictionary<string, object[]>[pos_mapas.Length];
		object[] aux;
		conectar ();
		float w = width / 1280f;
		float h = height / 720f;
		for (int i = 0; i < pos_mapas.Length; i++) {
			botones [i] = new Dictionary<string, object[]> ();
			using (SqliteCommand cmd = new SqliteCommand (select, conexion)) {
				cmd.Parameters.Add (new SqliteParameter ("pos_mapa", pos_mapas [i]));
				using (SqliteDataReader reader = cmd.ExecuteReader ()) {
					for (int j = 0; reader.Read (); j++) {
						aux = new object[4];
						aux [0] = reader.GetInt32 (0);
						aux [1] = new Rect (reader.GetFloat (1) * w, reader.GetFloat (2) * h, reader.GetFloat (3) * w, reader.GetFloat (4) * h);
						aux [2] = reader.GetString (5);
						aux [3] = reader.GetInt32 (6);
						botones [i].Add ("botones_" + j, aux);
					}
					reader.Close ();
				}
				cmd.Dispose ();							
			}
		}
		desconectar ();
		return botones;
	}

	public bool insertarPuntuacion (int id_minijuego, int id_mapa, int pos_mapa, int id_boton, bool completo, int vidas, int errores, int tiempo)
	{
		DbTransaction transaccion = null;
		try {
			conectar ();
			System.Text.StringBuilder intert = new  System.Text.StringBuilder ("INSERT INTO HISTORIAL_JUEGO ");
			intert.Append ("(HJ_ID_MINIJUEGO, HJ_COMPLETO, HJ_VIDAS, HJ_ERRORES, HJ_TIEMPO, HJ_FECHA) ");
			intert.Append ("VALUES (:id_minijuego, :completo, :vidas, :errores, :tiempo, DATETIME(CURRENT_TIMESTAMP, 'localtime'));");
			using (SqliteCommand cmd = new SqliteCommand (intert.ToString (), conexion)) {
				cmd.Parameters.Add ("id_minijuego", id_minijuego);
				cmd.Parameters.Add ("vidas", vidas);
				cmd.Parameters.Add ("errores", errores);
				cmd.Parameters.Add ("tiempo", tiempo);
				if (completo) {
					cmd.Parameters.Add ("completo", "S");
				} else {
					cmd.Parameters.Add ("completo", "N");
				}
				cmd.ExecuteNonQuery ();
				cmd.Dispose ();
			}
			if (completo) {
				StringBuilder update = new  StringBuilder ("UPDATE HISTORIAL_MAPAS ");
				update.Append ("SET HM_ESTADO = 'Pasado', HM_FECHA = DATETIME(CURRENT_TIMESTAMP, 'localtime') ");
				update.Append ("WHERE HM_ID_MAPA = :id_mapa AND HM_POS_MAPA = :pos_mapa AND HM_ID_MINIJUEGO = :id_minijuego AND HM_ID_BOTON = :id_boton;");
				transaccion = conexion.BeginTransaction ();
				using (SqliteCommand cmd = new SqliteCommand (update.ToString (), conexion)) {
					cmd.Parameters.Add ("id_minijuego", id_minijuego);
					cmd.Parameters.Add ("pos_mapa", pos_mapa);
					cmd.Parameters.Add ("id_mapa", id_mapa);
					cmd.Parameters.Add ("id_boton", id_boton);
					cmd.ExecuteNonQuery ();
					cmd.Dispose ();
				}
				bool mapa_completo = true;
				StringBuilder selectLast = new StringBuilder ("SELECT COUNT(1)  FROM HISTORIAL_MAPAS ");
				selectLast.Append ("WHERE HM_POS_MAPA = :pos_mapa AND HM_ESTADO = 'Actual';");
				using (SqliteCommand cmd = new SqliteCommand (selectLast.ToString (), conexion)) {
					cmd.Parameters.Add ("pos_mapa", pos_mapa);
					using (SqliteDataReader reader = cmd.ExecuteReader ()) {
						if (reader.Read ()) {
							if (reader.GetInt32 (0) > 0) {
								mapa_completo = false;
							} else {
								mapa_completo = true;
							}
						} 
						reader.Close ();
					}						
					cmd.Dispose ();
				}
				if (mapa_completo) {
					StringBuilder selectSiguiente = new  System.Text.StringBuilder ("SELECT HM_POS_MAPA ");
					selectSiguiente.Append ("FROM HISTORIAL_MAPAS ");
					selectSiguiente.Append ("WHERE HM_POS_MAPA > :pos_mapa ");
					selectSiguiente.Append ("ORDER BY HM_POS_MAPA ASC LIMIT 0, 1;");

					System.Text.StringBuilder updateBoton = new  System.Text.StringBuilder ("UPDATE HISTORIAL_MAPAS ");
					updateBoton.Append ("SET HM_ESTADO = 'Actual', HM_FECHA = DATETIME(CURRENT_TIMESTAMP, 'localtime') ");
					updateBoton.Append ("WHERE HM_POS_MAPA = :pos_mapa;");
					using (SqliteCommand cmd = new SqliteCommand (selectSiguiente.ToString (), conexion)) {
						cmd.Parameters.Add ("pos_mapa", pos_mapa);
						using (SqliteDataReader reader = cmd.ExecuteReader ()) {
							if (reader.Read ()) {
								pos_mapa = reader.GetInt32 (0);
							} 
							using (SqliteCommand cmd2 = new SqliteCommand (updateBoton.ToString (), conexion)) {
								cmd2.Parameters.Add (new SqliteParameter ("pos_mapa", pos_mapa));
								cmd2.ExecuteNonQuery ();
								cmd2.Dispose ();
							}
							reader.Close ();
						}						
						cmd.Dispose ();
					}
				}
				transaccion.Commit ();
			}
			desconectar ();
			return true;
		} catch (Exception e) {
			Debug.Log (e);
			if (transaccion != null) {
				transaccion.Rollback ();
			}
			desconectar ();
			return false;
		}
	}

	public Texture2D obtenerPremioRandom (int width, int height)
	{
		int id_premio = -1;
		Texture2D premio = null;
		try {
			conectar ();
			using (SqliteCommand cmd = new SqliteCommand ("SELECT PR_ID_PREMIO, PR_IMAGEN FROM PREMIO ORDER BY RANDOM() LIMIT 1;", conexion)) {
				using (SqliteDataReader reader = cmd.ExecuteReader ()) {
					if (reader.Read ()) {
						id_premio = reader.GetInt32 (0);
						premio = new Texture2D (width, height);
						premio.LoadImage ((byte[])reader.GetValue (1));
						premio.Apply ();
					} 
					reader.Close ();
				}						
				cmd.Dispose ();
			}
			if (id_premio > 0 && premio != null) {
				using (SqliteCommand cmd = new SqliteCommand ("UPDATE PREMIO SET PR_CONSEGUIDO = 'Si', PR_FECMOD = DATETIME(CURRENT_TIMESTAMP, 'localtime') WHERE PR_ID_PREMIO = :id_premio;", conexion)) {
					cmd.Parameters.Add ("id_premio", id_premio);
					cmd.ExecuteNonQuery ();	
					cmd.Dispose ();
				}
			}
			desconectar ();
		} catch (Exception e) {
			Debug.Log (e);
			desconectar ();
		}
		return premio;
	}

	public Dictionary<String, String> getMinijuego (int id_minijuego)
	{
		conectar ();
		Dictionary<String, String> datos = new Dictionary<String, String> ();
		String select = "SELECT M_NOMBRE, M_DESCRIPCION, M_MAXIMO, M_MINIMO, M_PUNTUACION_MAXIMA FROM MINIJUEGO WHERE M_ID_MINIJUEGO = :id_minijuego";

		using (SqliteCommand cmd = new SqliteCommand (select, conexion)) {
			cmd.Parameters.Add ("id_minijuego", id_minijuego);
			using (SqliteDataReader reader = cmd.ExecuteReader ()) {
				if (reader.Read ()) {
					datos.Add ("nombre", reader.GetValue (0).ToString ());
					datos.Add ("descripcion", reader.GetValue (1).ToString ());
					datos.Add ("maximo", reader.GetValue (2).ToString ());
					datos.Add ("minimo", reader.GetValue (3).ToString ());
					datos.Add ("puntuacion_maxima", reader.GetValue (4).ToString ());
				}
				reader.Close ();
			}
			cmd.Dispose ();
		}
		desconectar ();
		return datos;
	}

	public List<Dictionary<string, object>> obtenerHistorial_Mapas_Completo ()
	{
		List<Dictionary<string, object>> historial_mapas = new List<Dictionary<string, object>> ();
		try {
			conectar ();
			string select = "SELECT HM_ID_MAPA, HM_POS_MAPA, HM_ID_MINIJUEGO, HM_ID_BOTON, HM_ESTADO, HM_FECHA FROM V_HISTORIAL_MAPAS";
			using (SqliteCommand cmd = new SqliteCommand (select, conexion)) {
				using (SqliteDataReader reader = cmd.ExecuteReader ()) {
					for (int i = 0; reader.Read (); i++) {
						historial_mapas.Add (new Dictionary<string, object> ());
						historial_mapas [i].Add ("id_mapa", reader.GetValue (0));
						historial_mapas [i].Add ("pos_mapa", reader.GetValue (1));
						historial_mapas [i].Add ("id_minijuego", reader.GetValue (2));
						historial_mapas [i].Add ("id_boton", reader.GetValue (3));
						historial_mapas [i].Add ("estado", reader.GetValue (4));
						historial_mapas [i].Add ("fecha", reader.GetValue (5));
					}
					reader.Close ();
				}	
				cmd.Dispose ();	
			}
			desconectar ();
			return historial_mapas;
		} catch (Exception e) {
			Debug.Log (e);
			return null;
		}
	}

	public List<Dictionary<string, object>> obtenerHistorial_Juegos ()
	{
		List<Dictionary<string, object>> historial_juegos = new List<Dictionary<string, object>> ();
		try {
			conectar ();
			string select = "SELECT HJ_ID_MINIJUEGO, HJ_COMPLETO, HJ_VIDAS, HJ_ERRORES, HJ_TIEMPO, HJ_FECHA FROM V_HISTORIAL_JUEGO";
			using (SqliteCommand cmd = new SqliteCommand (select, conexion)) {
				using (SqliteDataReader reader = cmd.ExecuteReader ()) {
					for (int i = 0; reader.Read (); i++) {
						historial_juegos.Add (new Dictionary<string, object> ());
						historial_juegos [i].Add ("id_minijuego", reader.GetValue (0));
						historial_juegos [i].Add ("completo", reader.GetValue (1));
						historial_juegos [i].Add ("vidas", reader.GetValue (2));
						historial_juegos [i].Add ("errores", reader.GetValue (3));
						historial_juegos [i].Add ("tiempo", reader.GetValue (4));
						historial_juegos [i].Add ("fecha", reader.GetValue (5));
					}
					reader.Close ();
				}	
				cmd.Dispose ();	
			}
			desconectar ();
			return historial_juegos;
		} catch (Exception e) {
			Debug.Log (e.Message);
			return null;
		}
	}

	public List<Int32>  obtenerPremios ()
	{
		List<Int32> premios = new List<Int32> ();
		try {
			conectar ();
			string select = "SELECT PR_ID_PREMIO, PR_CONSEGUIDO FROM PREMIO WHERE PR_FECMOD >= (SELECT U_ULT_SINCRO FROM USUARIO)";
			using (SqliteCommand cmd = new SqliteCommand (select, conexion)) {
				using (SqliteDataReader reader = cmd.ExecuteReader ()) {
					for (int i = 0; reader.Read (); i++) {
						if (reader.GetString (1).Equals ("Si"))
							premios.Add (reader.GetInt32 (0));
					}
					reader.Close ();
				}	
				cmd.Dispose ();	
			}
			desconectar ();
			return premios;
		} catch (Exception e) {
			Debug.Log (e.Message);
			desconectar ();
			return null;
		}
	}

	public List<object[]>  obtenerTodosPremios (int width)
	{
		conectar ();
		List<object[]> premios = new List<object[]> ();
		try {
			string select = "SELECT PR_NOMBRE, PR_IMAGEN, PR_CONSEGUIDO FROM PREMIO";
			using (SqliteCommand cmd = new SqliteCommand (select, conexion)) {
				using (SqliteDataReader reader = cmd.ExecuteReader ()) {
					for (int i = 0; reader.Read (); i++) {
						object[] o = new object[3];
						o [0] = reader.GetString (0);
						o [2] = reader.GetString (2);
						Texture2D t = new Texture2D (width, width);
						t.LoadImage ((byte[])reader.GetValue (1));
						t.Apply ();
						o [1] = t;
						premios.Add (o);
					}
					reader.Close ();
				}	
				cmd.Dispose ();	
			}
			desconectar ();
			return premios;
		} catch (Exception e) {
			Debug.Log (e.Message);
			desconectar ();
			return null;
		}
	}

	public Texture2D obtenerFondo (int width, int height)
	{
		conectar ();
		Texture2D fondo = null;
		using (SqliteCommand cmd = new SqliteCommand ("SELECT F_IMAGEN FROM FONDO ORDER BY RANDOM() LIMIT 1", conexion)) {
			using (SqliteDataReader reader = cmd.ExecuteReader ()) {
				if (reader.Read ()) {
					fondo = new Texture2D (width, height);
					fondo.LoadImage ((byte[])reader.GetValue (0));
					fondo.Apply ();
				}
				reader.Close ();
			}
			cmd.Dispose ();
		}
		desconectar ();
		if (fondo == null) {
			Constantes.setErrorDatos ();
		}
		return fondo;
	}

	public object[][] obtenerSopaDeLetras (int id, int maximo, int minimo)
	{
		conectar ();
		List<object[]> datosAux = new List<object[]> ();
		String select = "SELECT TEXTO_SOLUCION, IMAGEN_SOLUCION FROM V_CONTENIDO WHERE GM_ID_MINIJUEGO = :id_minijuego AND TEXTO_SOLUCION IS NOT NULL GROUP BY G_ID_GRUPO ORDER BY RANDOM();";
		using (SqliteCommand cmd = new SqliteCommand (select, conexion)) {
			cmd.Parameters.Add (new SqliteParameter ("id_minijuego", id));
			using (SqliteDataReader reader = cmd.ExecuteReader ()) {
				while (reader.Read ()) {
					object[] fila = new object[2];
					fila [0] = reader.GetString (0).Trim ().ToUpper ().Replace (" ", "").Replace ("Á", "A").Replace ("É", "E").Replace ("Í", "I").Replace ("Ó", "O").Replace ("Ú", "U");
					fila [1] = reader.GetValue (1);
					datosAux.Add (fila);
				}
				reader.Close ();
			}
			cmd.Dispose ();
		}
		object[][] datos = null;
		if (datosAux.Count == 0) {
			Constantes.setErrorDatos ();
		} else {
			bool imagen = true;
			int cantidad = Mathf.Min (maximo, datosAux.Count);
			if (cantidad % 2 != 0) {
				cantidad -= 1;
			}
			if (cantidad > minimo) {
				cantidad = Mathf.Max (cantidad, minimo + UnityEngine.Random.Range (0, ((cantidad - minimo) / 2) + 1) * 2);
			}
			datos = new object[cantidad][];
			for (int i = 0; i < datos.Length; i++) {
				datos [i] = datosAux [i];
				if (datos [i] [1] == null || datos [i] [1].ToString ().Length == 0) {
					imagen = false;
				}
			}
			for (int i = 0; i < datos.Length; i++) {
				if (!imagen) {
					datos [i] [1] = null;
				} else {
					Texture2D t = new Texture2D (1, 1);
					t.LoadImage ((byte[])datos [i] [1]);
					t.Apply ();
					datos [i] [1] = t;					
				}
			}
		}
		desconectar ();
		return datos;
	}

	public object[][] obtenerParejas (int id, int maximo, int minimo)
	{
		conectar ();
		List<object[]> datosAux = new List<object[]> ();
		String select = "SELECT TEXTO_SOLUCION, IMAGEN_SOLUCION FROM V_CONTENIDO WHERE GM_ID_MINIJUEGO = :id_minijuego AND TEXTO_SOLUCION IS NOT NULL GROUP BY G_ID_GRUPO ORDER BY RANDOM();";
		using (SqliteCommand cmd = new SqliteCommand (select, conexion)) {
			cmd.Parameters.Add (new SqliteParameter ("id_minijuego", id));
			using (SqliteDataReader reader = cmd.ExecuteReader ()) {
				while (reader.Read ()) {
					object[] fila = new object[2];
					fila [0] = reader.GetString (0);
					fila [1] = reader.GetValue (1);
					datosAux.Add (fila);
				}
				reader.Close ();
			}
			cmd.Dispose ();
		}
		object[][] datos = null;
		if (datosAux.Count == 0) {
			Constantes.setErrorDatos ();
		} else {
			bool imagen = true;
			maximo = Mathf.Min (maximo, datosAux.Count);
			datos = new object[UnityEngine.Random.Range (minimo, maximo)][];
			if (datos.Length == 13 || datos.Length == 11 || datos.Length == 9 || datos.Length == 7 || datos.Length == 5) {
				datos = new object[datos.Length - 1][];
			}
			for (int i = 0; i < datos.Length; i++) {
				datos [i] = datosAux [i];
				if (datos [i] [1] == null || datos [i] [1].ToString ().Length == 0) {
					imagen = false;
				}
			}
			for (int i = 0; i < datos.Length; i++) {
				if (!imagen) {
					datos [i] [1] = null;
				} else {
					Texture2D t = new Texture2D (1, 1);
					t.LoadImage ((byte[])datos [i] [1]);
					t.Apply ();
					datos [i] [1] = t;					
				}
			}
		}
		desconectar ();
		return datos;
	}

	public string[] obtenerBusca (int id)
	{
		conectar ();
		string[] datos = new string[2];
		StringBuilder select = new StringBuilder ("SELECT TEXTO_SOLUCION, TEXTO_OPCION FROM V_CONTENIDO WHERE GM_ID_MINIJUEGO = :id_minijuego AND TEXTO_SOLUCION IS NOT NULL");
		select.Append (" AND TEXTO_OPCION IS NOT NULL GROUP BY G_ID_GRUPO ORDER BY RANDOM() LIMIT 0,1;");
		using (SqliteCommand cmd = new SqliteCommand (select.ToString (), conexion)) {
			cmd.Parameters.Add (new SqliteParameter ("id_minijuego", id));
			using (SqliteDataReader reader = cmd.ExecuteReader ()) {
				if (reader.Read ()) {
					datos [0] = reader.GetString (0);
					datos [1] = reader.GetString (1);					
				}
				reader.Close ();
			}
			cmd.Dispose ();
		}
		desconectar ();
		return datos;
	}

	public object[][]  obtenerPalabrasIncompletas (int id, int maximo, int minimo)
	{
		conectar ();
		List<object[]> datosAux = new List<object[]> ();
		StringBuilder select = new StringBuilder ("SELECT TEXTO_SOLUCION, IMAGEN_SOLUCION, ");
		select.Append ("(SELECT TEXTO_OPCION FROM V_CONTENIDO VC1 WHERE VC1.G_ID_GRUPO = VC.G_ID_GRUPO ORDER BY RANDOM()) AS TEXTO_OPCION ");
		select.Append ("FROM V_CONTENIDO VC WHERE GM_ID_MINIJUEGO = :id_minijuego AND IMAGEN_SOLUCION IS NOT NULL AND TEXTO_SOLUCION IS NOT NULL ");
		select.Append (@"AND TEXTO_OPCION IS NOT NULL AND TEXTO_OPCION LIKE '%\_%' ESCAPE '\' ");
		select.Append ("GROUP BY G_ID_GRUPO ORDER BY RANDOM(); ");
		using (SqliteCommand cmd = new SqliteCommand (select.ToString (), conexion)) {
			cmd.Parameters.Add (new SqliteParameter ("id_minijuego", id));
			using (SqliteDataReader reader = cmd.ExecuteReader ()) {
				while (reader.Read ()) {
					object[] fila = new object[3];
					fila [0] = reader.GetString (0).Trim ().ToUpper ().Replace (" ", "").Replace ("Á", "A").Replace ("É", "E").Replace ("Í", "I").Replace ("Ó", "O").Replace ("Ú", "U");
					Texture2D t = new Texture2D (1, 1);
					t.LoadImage ((byte[])reader.GetValue (1));
					t.Apply ();
					fila [1] = t;	
					fila [2] = reader.GetString (2).Trim ().ToUpper ().Replace (" ", "").Replace ("Á", "A").Replace ("É", "E").Replace ("Í", "I").Replace ("Ó", "O").Replace ("Ú", "U");
					datosAux.Add (fila);					
				}
				reader.Close ();
			}
			cmd.Dispose ();
		}
		object[][] datos = null;
		if (datosAux.Count > 0) {
			maximo = Mathf.Min (maximo, datosAux.Count);
			datos = new object[UnityEngine.Random.Range (minimo, maximo)][];
			for (int i = 0; i < datos.Length; i++) {
				datos [i] = datosAux [i];
			}
		} else {
			Constantes.setErrorDatos ();
		}
		desconectar ();
		return datos;
	}

	public List<Dictionary<string, List<object>>>  obtenerSeleccion (int id, int maximo, int minimo)
	{
		conectar ();
		Dictionary<int, Dictionary<String, List<object>>> datosAux = new Dictionary<int, Dictionary<String, List<object>>> ();
		StringBuilder select = new StringBuilder ("SELECT G_ID_GRUPO, ID_CONTENIDO_SOLUCION, TEXTO_SOLUCION, ID_CONTENIDO_OPCION, IMAGEN_OPCION FROM V_CONTENIDO ");
		select.Append ("WHERE GM_ID_MINIJUEGO = :id_minijuego AND TEXTO_SOLUCION IS NOT NULL AND IMAGEN_OPCION IS NOT NULL ");
		select.Append ("ORDER BY RANDOM(); ");
		using (SqliteCommand cmd = new SqliteCommand (select.ToString (), conexion)) {
			cmd.Parameters.Add (new SqliteParameter ("id_minijuego", id));
			using (SqliteDataReader reader = cmd.ExecuteReader ()) {
				while (reader.Read ()) {
					int id_grupo = reader.GetInt32 (0);
					int id_contenido_solucion = reader.GetInt32 (1);
					int id_contenido_opcion = reader.GetInt32 (3);
					if (!datosAux.ContainsKey (id_grupo)) {
						datosAux.Add (id_grupo, new Dictionary<String, List<object>> ());
						datosAux [id_grupo].Add ("Opciones", new List<object> ());
					}
					if (datosAux [id_grupo] ["Opciones"].Count < 3
					    || (datosAux [id_grupo] ["Opciones"].Count == 3 && datosAux [id_grupo].ContainsKey ("Solucion"))
					    || (datosAux [id_grupo] ["Opciones"].Count == 3 && id_contenido_opcion == id_contenido_solucion)) {
						Texture2D t = new Texture2D (1, 1);
						t.LoadImage ((byte[])reader.GetValue (4));
						t.Apply ();
						datosAux [id_grupo] ["Opciones"].Add (t);
					} 
					if (id_contenido_opcion == id_contenido_solucion) {
						datosAux [id_grupo].Add ("Enunciado", new List<object> ());
						datosAux [id_grupo] ["Enunciado"].Add (reader.GetString (2));
						datosAux [id_grupo].Add ("Solucion", new List<object> ());
						datosAux [id_grupo] ["Solucion"].Add (datosAux [id_grupo] ["Opciones"].Count - 1);
					}												
				}
				reader.Close ();
			}
			cmd.Dispose ();
		}
		List<Dictionary<String,List<object>>> datos = new List<Dictionary<string, List<object>>> ();
		if (datosAux.Count > 0) {
			maximo = Mathf.Min (maximo, datosAux.Count);
			int total = UnityEngine.Random.Range (minimo, maximo);
			datos.AddRange (datosAux.Values);
			datos.RemoveRange (total, (datos.Count - total));
		} else {
			Constantes.setErrorDatos ();
		}
		desconectar ();
		return datos;
	}

	public List<Dictionary<string, List<object>>>  obtenerFraseCorrecta (int id, int maximo, int minimo)
	{
		conectar ();
		Dictionary<int, Dictionary<String, List<object>>> datosAux = new Dictionary<int, Dictionary<String, List<object>>> ();
		StringBuilder select = new StringBuilder ("SELECT G_ID_GRUPO, ID_CONTENIDO_SOLUCION, IMAGEN_SOLUCION, TEXTO_SOLUCION, ID_CONTENIDO_OPCION, TEXTO_OPCION FROM V_CONTENIDO ");
		select.Append ("WHERE GM_ID_MINIJUEGO = :id_minijuego AND TEXTO_OPCION IS NOT NULL AND IMAGEN_SOLUCION IS NOT NULL ");
		select.Append ("ORDER BY RANDOM(); ");
		using (SqliteCommand cmd = new SqliteCommand (select.ToString (), conexion)) {
			cmd.Parameters.Add (new SqliteParameter ("id_minijuego", id));
			using (SqliteDataReader reader = cmd.ExecuteReader ()) {
				while (reader.Read ()) {
					int id_grupo = reader.GetInt32 (0);
					int id_contenido_solucion = reader.GetInt32 (1);
					int id_contenido_opcion = reader.GetInt32 (4);
					if (!datosAux.ContainsKey (id_grupo)) {
						datosAux.Add (id_grupo, new Dictionary<String, List<object>> ());
						datosAux [id_grupo].Add ("Opciones", new List<object> ());
					}
					if (datosAux [id_grupo] ["Opciones"].Count < 3
					    || (datosAux [id_grupo] ["Opciones"].Count == 3 && datosAux [id_grupo].ContainsKey ("Solucion"))
					    || (datosAux [id_grupo] ["Opciones"].Count == 3 && id_contenido_opcion == id_contenido_solucion)) {
						datosAux [id_grupo] ["Opciones"].Add (reader.GetValue (5));
					} 
					if (id_contenido_opcion == id_contenido_solucion) {
						datosAux [id_grupo].Add ("Enunciado", new List<object> ());
						Texture2D t = new Texture2D (1, 1);
						t.LoadImage ((byte[])reader.GetValue (2));
						t.Apply ();
						datosAux [id_grupo] ["Enunciado"].Add (t);
						datosAux [id_grupo].Add ("Solucion", new List<object> ());
						datosAux [id_grupo] ["Solucion"].Add (datosAux [id_grupo] ["Opciones"].Count - 1);
					}												
				}
				reader.Close ();
			}
			cmd.Dispose ();
		}
		desconectar ();
		List<Dictionary<String,List<object>>> datos = new List<Dictionary<string, List<object>>> ();
		if (datosAux.Count > 0) {
			maximo = Mathf.Min (maximo, datosAux.Count);
			int total = UnityEngine.Random.Range (minimo, maximo);
			datos.AddRange (datosAux.Values);
			datos.RemoveRange (total, (datos.Count - total));
		} else {
			Constantes.setErrorDatos ();
		}
		return datos;
	}

	public String obtenerIdioma ()
	{
		String idioma = "espanol";
		try {
			conectar ();
			using (SqliteCommand cmd = new SqliteCommand ("SELECT U_IDIOMA_JUEGO FROM USUARIO;", conexion)) {
				using (SqliteDataReader reader = cmd.ExecuteReader ()) {
					if (reader.Read ()) {
						idioma = reader.GetString (0);
					}
					reader.Close ();
				}
			}
			desconectar ();
		} catch (Exception e) {
			Debug.Log (e.GetBaseException ());
			desconectar ();
		}
		return idioma;
	}
}