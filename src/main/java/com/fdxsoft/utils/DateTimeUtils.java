package com.fdxsoft.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
	
	public static String DescriptiveFormat(String date) {
		if(date.trim().equals("")) {
			return null;
		}
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
		LocalDateTime localDateTime = LocalDateTime.parse(date, inputFormatter);
		
		// Formatear a salida deseada
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' hh:mm a");

		String formattedDate = localDateTime.format(outputFormatter)
			    .replace("AM", "A.M.")
			    .replace("PM", "P.M.");
		
		return formattedDate;
	}
}
