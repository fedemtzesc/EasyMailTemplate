package com.fdxsoft.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Cryptography {

	public static void main(String...args) {
		
		String in = "Calibre3006";
		String out = encrypt(in);
		
		System.out.println("Cadena a encriptar: " + in);
		System.out.println("Cadena encriptada :" + out);
		String hacenMatch = validate(in,out)?"SI":"NO";
		System.out.println("Corresponde " + out + " => a " + "'" + in + "' ?? -> " + hacenMatch);
	}
	
	public static String encrypt(String inStr) {
		BCryptPasswordEncoder crypter= new BCryptPasswordEncoder();
		return crypter.encode(inStr);
	}
	
	public static boolean validate(String inStr, String outStr) {
		BCryptPasswordEncoder crypter= new BCryptPasswordEncoder();
		return crypter.matches(inStr, outStr);
	}
	
	
}
