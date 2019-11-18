using UnityEngine;
using System.Collections;
using MySql.Data;
using MySql.Data.MySqlClient;
using System.Data;
using System;
using UnityEngine.Networking.NetworkSystem;
using System.Threading;
using System.Reflection;
using System.Collections.Generic;
using System.Data.Common;
using System.Data.SqlTypes;
using System.Security.Cryptography;
using System.Xml.Xsl;
using System.Text;


public class Sync
{

	static Sync miSync = new Sync ();

	GestorSQLite sqlite;

	MySqlConnection conexion = null;
	string conexionBD2 = "Server=galan.ehu.eus;Port=3306;Database=Xavelez012_TFG;UID=Xavelez012;PASSWORD=mc3FY6xMZyEV96r2;Connection Timeout=5";

	Action[] sinc;

	static int error_code = 0;
	static int id_usuario = 0;

	string nombre = "", contrasena = "";

	string[] nombre_tablas = new string [] {
		"usuario",
		"contenido",
		"grupo",
		"minijuegos",
		"grupo_minijuegos",
		"premios",
		"fondos",
		"mapas",
		"botones",
		"historial_mapas"
	};

	Dictionary<string, List<Dictionary<string, object>>> list;

	// Use this for initialization
	private Sync ()
	{
		sqlite = GestorSQLite.getGestorSQLite ();
		error_code = 0;
		id_usuario = 0;
		conexion = new MySqlConnection (conexionBD2);
		sinc = new Action[] {
			() => sqlite.sincronizarUsuario (), 
			() => sqlite.sincronizarContenido (),
			() => sqlite.sincronizarGrupo (),
			() => sqlite.sincronizarMinijuego (),
			() => sqlite.sincronizarGrupoMinijuego (),
			() => sqlite.sincronizarPremios (),
			() => sqlite.sincronizarFondos (),
			() => sqlite.sincronizarMapas (),
			() => sqlite.sincronizarBotones (),
			() => sqlite.sincronizarHistorial_Mapas ()
		};
		list = new Dictionary<string, List<Dictionary<string, object>>> ();
	}

	public static Sync getSync ()
	{
		if (miSync == null) {
			miSync = new Sync ();
		}
		return miSync;
	}

	public int getErrorCode ()
	{
		return error_code;
	}

	public void setErrorCode (int code)
	{
		error_code = code;
	}

	public void conectar ()
	{
		try {
			if (conexion == null) {
				conexion = new MySqlConnection (conexionBD2);		
			}
			if (conexion.State == ConnectionState.Closed || conexion.State == ConnectionState.Broken) {
				conexion.Open ();
			}
		} catch (Exception e) {
			Debug.Log (e);
			error_code = -1;
		}
	}

	public void desconectar ()
	{
		if (conexion != null && conexion.State != ConnectionState.Closed) {
			conexion.Close ();
		}
	}

	public void setId_Usuario (int id_usuarioAux)
	{
		id_usuario = id_usuarioAux;
	}

	public void setUsuario (string nombre)
	{
		this.nombre = nombre;
	}

	public void setContrasena (string contrasena)
	{
		this.contrasena = contrasena;
	}

	public void login ()
	{
		try {
			error_code = 0;
			conectar ();
			contrasena = SHA1.Hash (contrasena).ToLower ();
			using (MySqlCommand cmd = new MySqlCommand ("SELECT LOGIN(@nombre,@contrasena)", conexion)) {
				cmd.Parameters.AddWithValue ("@nombre", nombre);
				cmd.Parameters.AddWithValue ("@contrasena", contrasena);
				cmd.Prepare ();
				cmd.ExecuteNonQuery (); 
				using (MySqlDataReader reader = cmd.ExecuteReader ()) {
					if (reader.Read ()) {
						id_usuario = reader.GetInt32 (0);
						Debug.Log (id_usuario);
					}
					if (id_usuario < 1) {
						error_code = id_usuario;
					}
					reader.Close ();
				}
				cmd.Dispose ();
			}
			setContrasena (null);
			setUsuario (null);
			desconectar ();
		} catch (Exception e) {
			Debug.Log ("ERROR: " + e.Message);
			Debug.Log ("CONEXION: " + conexion.Site);
			error_code = -1;
			desconectar ();
		}
		if (id_usuario > 0) {
			sincronizacion ();
		}
	}

	public void sincronizacion ()
	{
		List<Thread> threads = new List<Thread> ();
		error_code = 0;

		try {
			conectar ();
			if (conexion.State == ConnectionState.Open) {
				String fechaSincro = sqlite.obtenerFechaSincro ();
				String fechaFondos = sqlite.obtenerFechaImagen ("Fondos");
				String fechaMapas = sqlite.obtenerFechaImagen ("Mapas");

				actualizarHistorial_Mapa ();
				actualizarHistorial_Juego ();
				actualizarPremios ();
				sqlite.conectar ();
				sqlite.comenzarTransaccion ();
				sincronizacionRemota (fechaSincro, fechaFondos, fechaMapas);
				DateTime fechaActual = DateTime.Now;
				sqlite.setDatosASincronizar (list);
				if (fechaSincro == null && !Constantes.getErrorDatos ()) {
					sqlite.cambioUsuario ();
				}
				for (int i = 0; i < sinc.Length && error_code == 0; i++) {
					if (list.ContainsKey (nombre_tablas [i]) && list [nombre_tablas [i]].Count > 0) {
						Thread t = new Thread (new ThreadStart (sinc [i]));
						t.Start ();
						threads.Add (t);	
					} else {
						if (!nombre_tablas [i].Equals ("historial_mapas") && !nombre_tablas [i].Equals ("historial_juego") && fechaSincro == null) {
							switch (nombre_tablas [i]) {
							case "mapas":
								if (fechaMapas == null)
									error_code = -3;
								break;
							case "fondos":
								if (fechaFondos == null)
									error_code = -3;
								break;
							default:
								error_code = -3;
								break;
							}
						}
					}
				}
				while (threads.Count > 0) {
					for (int i = 0; i < threads.Count; i++) {
						if (!threads [i].IsAlive) {
							threads.Remove (threads [i]);
							i--;
						}
					}
					if (error_code != 0) {
						while (threads.Count > 0) {
							if (!threads [0].IsAlive) {
								threads [0].Abort ();
								threads.Remove (threads [0]);
							}
						}
					}
				}
				if (error_code == 0) {
					Constantes.reiniciarErrorDatos ();
					if (sqlite.actualizarFechas (id_usuario, fechaActual)) {
						sqlite.commit ();
						error_code = 1;
					} else {
						sqlite.rollback ();
						error_code = -1;
					}
				} else {
					sqlite.rollback ();
				}
				GestorSQLite.getGestorSQLite ().desconectar ();
			}
			desconectar ();
		} catch (Exception e) {
			Debug.Log (e.Message);
			error_code = -1;
			desconectar ();
			sqlite.rollback ();
			GestorSQLite.getGestorSQLite ().desconectar ();
		}
	}

	private void sincronizacionRemota (String tiempo, String tiempoFondos, String tiempoMapas)
	{
		try {
			list.Clear ();
			int k = 0;
			List<Dictionary<String, object>> aux = new List<Dictionary<String, object>> ();
			List<MySqlParameter> parameters = new List<MySqlParameter> ();
			if (id_usuario > 0) {	
				parameters.Add (new MySqlParameter ("@id_usuario", id_usuario));
				String[] sql = null;
				String[] sqlImagenes = new String[3];
				if (tiempo == null) {
					sql = new String[] {
						"SELECT N_ID_NINO, N_NOMBRE, N_IDIOMA_JUEGO FROM NINO WHERE N_ID_NINO = @id_usuario;",

						"SELECT ID_CONTENIDO, CASTELLANO, EUSKERA, IMAGEN, 'A' AS ACCION FROM V_CONTENIDO WHERE ID_NINO = @id_usuario AND ACCION NOT IN ('B');",

						"SELECT ID_GRUPO, SOLUCION, OPCION, 'A' AS ACCION FROM V_GRUPOS WHERE ID_NINO = @id_usuario AND ACCION NOT IN ('B');",

						"SELECT ID_MINIJUEGO, NOMBRE, DESCRIPCION, MAXIMO, MINIMO, PUNTUACION_MAXIMA, 'A' AS ACCION FROM V_MINIJUEGOS WHERE ID_NINO = @id_usuario AND ACCION NOT IN ('B');",

						"SELECT ID_MINIJUEGO, ID_GRUPO, 'A' AS ACCION FROM V_GRUPO_MINIJUEGOS WHERE ACCION NOT IN ('B');",

						"SELECT ID_PREMIO, NOMBRE, IMAGEN, CONSEGUIDO FROM V_PREMIOS WHERE ID_NINO = @id_usuario;",

						"SELECT ID_MAPA, ID_BOTON, POSICION_X, POSICION_Y, WIDTH, HEIGHT, 'A' AS ACCION FROM V_BOTONES_MAPA;",

						"SELECT HM_ID_MINIJUEGO, HM_ID_MAPA, HM_POS_MAPA, HM_ID_BOTON, HM_ESTADO, HM_FECHA FROM HISTORIAL_MAPAS WHERE HM_ID_NINO = @id_usuario;"
					};
				} else {
					MySqlParameter p = new MySqlParameter ("@tiempo", tiempo);
					p.DbType = DbType.DateTime;
					p.Value = tiempo;
					p.ParameterName = "@tiempo";
					parameters.Add (p);
					sql = new String[] {
						"SELECT N_ID_NINO, N_NOMBRE, N_IDIOMA_JUEGO FROM NINO WHERE N_ID_NINO = @id_usuario;",	
														
						"SELECT ID_CONTENIDO, CASTELLANO, EUSKERA, IMAGEN, ACCION FROM V_CONTENIDO WHERE ID_NINO = @id_usuario AND FECMOD > CONVERT(@tiempo,DATETIME);",

						"SELECT ID_GRUPO, SOLUCION, OPCION, 'A' AS ACCION FROM V_GRUPOS WHERE ID_NINO = @id_usuario AND FECMOD > CONVERT(@tiempo,DATETIME);",

						"SELECT ID_MINIJUEGO, NOMBRE, DESCRIPCION, MAXIMO, MINIMO, PUNTUACION_MAXIMA, ACCION FROM V_MINIJUEGOS WHERE ID_NINO = @id_usuario AND FECMOD > CONVERT(@tiempo,DATETIME);",

						"SELECT ID_MINIJUEGO, ID_GRUPO, ACCION FROM V_GRUPO_MINIJUEGOS WHERE FECMOD > CONVERT(@tiempo,DATETIME);",

						"SELECT ID_PREMIO, NOMBRE, IMAGEN, CONSEGUIDO FROM V_PREMIOS WHERE ID_NINO = @id_usuario AND FECMOD > CONVERT(@tiempo,DATETIME);",

						"SELECT ID_MAPA, ID_BOTON, POSICION_X, POSICION_Y, WIDTH, HEIGHT, ACCION FROM V_BOTONES_MAPA WHERE FECMOD > CONVERT(@tiempo,DATETIME);",

						"SELECT HM_ID_MINIJUEGO, HM_ID_MAPA, HM_POS_MAPA, HM_ID_BOTON, HM_ESTADO, HM_FECHA FROM HISTORIAL_MAPAS WHERE HM_ID_NINO = @id_usuario AND HM_FECHA > CONVERT(@tiempo,DATETIME);"
					};
				}
				if (tiempoFondos != null) {
					sqlImagenes [0] = "SELECT F_ID_FONDO AS ID_FONDO, F_IMAGEN AS IMAGEN FROM FONDO WHERE F_FECMOD > CONVERT(@tiempo_fondo, DATETIME);";
					parameters.Add (new MySqlParameter ("@tiempo_fondo", tiempoFondos));
				} else {
					sqlImagenes [0] = "SELECT F_ID_FONDO AS ID_FONDO, F_IMAGEN AS IMAGEN FROM FONDO;";
				}
				if (tiempoMapas != null) {
					sqlImagenes [1] = "SELECT MA_ID_MAPA AS ID_MAPA, MA_IMAGEN AS IMAGEN FROM MAPA WHERE MA_FECMOD > CONVERT(@tiempo_mapa, DATETIME);";
					parameters.Add (new MySqlParameter ("@tiempo_mapa", tiempoMapas));
				} else {
					sqlImagenes [1] = "SELECT MA_ID_MAPA AS ID_MAPA, MA_IMAGEN AS IMAGEN FROM MAPA;";
				}
				StringBuilder SQL = new StringBuilder ("");
				for (int i = 0; i < sql.Length; i++) {
					SQL.Append (sql [i] + " ");
					if (i == 5) {
						SQL.Append (sqlImagenes [0] + " " + sqlImagenes [1] + " ");
					}
				}
				Debug.Log ("SQL: ");
				Debug.Log (SQL.ToString ());
				using (MySqlCommand cmd = new MySqlCommand (SQL.ToString (), conexion)) {
					cmd.Parameters.AddRange (parameters.ToArray ());
					cmd.Prepare ();
					cmd.ExecuteNonQuery (); 
					using (MySqlDataReader reader = cmd.ExecuteReader ()) {
						do {
							while (reader.Read ()) {
								aux.Add (new Dictionary<String, object> ());
								for (int i = 0; i < reader.FieldCount; i++) {									
									if (reader.IsDBNull (i)) {
										aux [aux.Count - 1].Add (reader.GetName (i), null);
									} else {
										aux [aux.Count - 1].Add (reader.GetName (i), reader.GetString (i));
									}
								}
							}
							list.Add (nombre_tablas [k], aux);
							Debug.Log (nombre_tablas [k]);
							aux = new List<Dictionary<String, object>> ();
							k++;
						} while (reader.NextResult ());
						reader.Close ();
					}
					cmd.Dispose ();
				}
			} else {
				error_code = -1;
			}
			error_code = 0;
		} catch (Exception e) {
			Debug.Log (e);
			error_code = -1;
		} 
	}

	public void actualizarNube ()
	{
		error_code = 0;
		conectar ();
		actualizarHistorial_Juego ();
		actualizarHistorial_Mapa ();
		actualizarPremios ();
		desconectar ();
		if (error_code == 0) {
			sqlite.reiniciar ();
			error_code = 1;
		}
	}

	public void actualizarHistorial_Juego ()
	{
		if (error_code != -1) {
			try {
				List<Dictionary<string, object>> historial_juegos = sqlite.obtenerHistorial_Juegos ();
				StringBuilder insert = new StringBuilder ("REPLACE INTO HISTORIAL_JUEGO (HJ_ID_NINO, HJ_ID_MINIJUEGO, HJ_FECHA, HJ_COMPLETO, HJ_VIDAS, HJ_ERRORES, HJ_TIEMPO)");
				insert.Append (" VALUES (@id_usuario, @id_minijuego, @fecha, @completo, @vidas, @errores, @tiempo);");
				if (historial_juegos == null) {
					error_code = -1;
				} else {
					int usu = sqlite.obtenerUsuarioAcual ();
					for (int i = 0; i < historial_juegos.Count; i++) {
						using (MySqlCommand cmdMysql = new MySqlCommand (insert.ToString (), conexion)) {
							cmdMysql.Parameters.AddWithValue ("@id_usuario", usu);
							cmdMysql.Parameters.AddWithValue ("@id_minijuego", historial_juegos [i] ["id_minijuego"]);
							cmdMysql.Parameters.AddWithValue ("@fecha", historial_juegos [i] ["fecha"]);
							cmdMysql.Parameters.AddWithValue ("@completo", historial_juegos [i] ["completo"]);
							cmdMysql.Parameters.AddWithValue ("@vidas", historial_juegos [i] ["vidas"]);
							cmdMysql.Parameters.AddWithValue ("@errores", historial_juegos [i] ["errores"]);
							cmdMysql.Parameters.AddWithValue ("@tiempo", historial_juegos [i] ["tiempo"]);

							cmdMysql.Prepare ();
							cmdMysql.ExecuteNonQuery (); 		
							cmdMysql.Dispose ();								
						}
					}
				}			

			} catch (Exception e) {
				Debug.Log (e.Message);
				error_code = -1;
			}
		}

	}

	public void actualizarHistorial_Mapa ()
	{
		if (error_code != -1) {
			try {
				List<Dictionary<string, object>> historial_mapas = sqlite.obtenerHistorial_Mapas_Completo ();
				if (historial_mapas == null) {
					error_code = -1;
				} else {
					int usu = sqlite.obtenerUsuarioAcual ();
					for (int i = 0; i < historial_mapas.Count; i++) {
						using (MySqlCommand cmdMysql = new MySqlCommand ("SINCRONIZAR_HISTORIAL_MAPA", conexion)) {
							cmdMysql.CommandType = CommandType.StoredProcedure;
							cmdMysql.Parameters.AddWithValue ("@p_id_usuario", usu);
							cmdMysql.Parameters.AddWithValue ("@p_id_mapa", historial_mapas [i] ["id_mapa"]);
							cmdMysql.Parameters.AddWithValue ("@p_pos_mapa", historial_mapas [i] ["pos_mapa"]);
							cmdMysql.Parameters.AddWithValue ("@p_id_minijuego", historial_mapas [i] ["id_minijuego"]);
							cmdMysql.Parameters.AddWithValue ("@p_id_boton", historial_mapas [i] ["id_boton"]);
							cmdMysql.Parameters.AddWithValue ("@p_estado", historial_mapas [i] ["estado"]);
							cmdMysql.Parameters.Add ("@po_agregado", MySqlDbType.Int32).Direction = ParameterDirection.Output;

							cmdMysql.Prepare ();
							cmdMysql.ExecuteNonQuery ();
							cmdMysql.Dispose ();								
						}
					}
				}

			} catch (Exception e) {
				Debug.Log (e.Message);
				error_code = -1;
			}
		}
	}

	public void actualizarPremios ()
	{
		if (error_code != -1) {
			try {	
				List<Int32> premios = sqlite.obtenerPremios ();
				int usu = sqlite.obtenerUsuarioAcual ();
				string insert = "REPLACE INTO PREMIOS_USUARIOS (PU_ID_PREMIO, PU_ID_NINO, PU_CONSEGUIDO, PU_FECMOD) VALUES (@id_premio, @id_usuario, 'Si', SYSDATE());";
				if (premios == null) {
					error_code = -1;
				} else {
					foreach (Int32 idPremio in premios) {
						using (MySqlCommand cmdMysql = new MySqlCommand (insert, conexion)) {
							cmdMysql.Parameters.AddWithValue ("@id_premio", idPremio);
							cmdMysql.Parameters.AddWithValue ("@id_usuario", usu);
							cmdMysql.Prepare ();
							cmdMysql.ExecuteNonQuery (); 		
							cmdMysql.Dispose ();								
						}
					}
				}			
			} catch (Exception e) {
				Debug.Log (e.Message);
				error_code = -1;
			}
		}

	}
}
