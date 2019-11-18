using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
using System.Text;
using System.Security.Cryptography;

public class SHA1
{

	public static string Hash (string input)
	{
		using (SHA1Managed sha1 = new SHA1Managed ()) {
			var hash = sha1.ComputeHash (Encoding.UTF8.GetBytes ((input)));
			var sb = new StringBuilder (hash.Length * 2);

			foreach (byte b in hash) {
				sb.Append (b.ToString ("X2"));
			}
			return sb.ToString ();
		}
	}
}
