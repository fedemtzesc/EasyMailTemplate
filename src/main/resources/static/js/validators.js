function isValidWYSIWYGFormDataBeforePersist() {
	let msg = '';

	const data = {
		templateName: document.getElementById("templateName"),
		description: document.getElementById("description"),
		sendFrequency: document.getElementById("sendFrequency"),
		dateTimeSending: document.getElementById("scheduleDateTime"),
		repeatEachTimeAt: document.getElementById("dailyTime"),
		repeatLimitType: document.getElementById("repeatLimitType"),
		repeatQuantity: document.getElementById("repeatCount"),
		repeatEndDate: document.getElementById("endDate"),
		recipients: document.getElementById("recipients"),
		htmlInput: document.getElementById("htmlInput")
	};


	//Primero validamos los inputs que tienen que guardarse si o si
	if (data.templateName.value.trim() === '')
		msg += '- El nombre de la plantilla no puede estar vacio. Tiene que asignarle un nombre.\n';
	if (data.description.value.trim() === '')
		msg += '- La descripcion de la plantilla no puede estar vacia. Tiene que asignarle una breve explicacion.\n';
	if (data.htmlInput.value.trim() === '')
		msg += '- El contenido HTML de su plantilla no puede ir vacio. Tiene que capturarlo.\n';

	//Ahora validamos por cada valor del primer combobox que se refiere a la frecuencia de envio
	switch (data.sendFrequency.value) {
		case 'IMMEDIATE': //Envio Inmediate
			console.log('Envio inmediato solo requiere nombre de plantilla y descripcion');
			//Limpio los campos que no se requieren
			break;
		case 'SCHEDULED': //Envio Scheduled
			if (!isValidISODateTime(data.dateTimeSending.value.trim()))
				msg += '- Tiene que especificar una fecha con hora de envio validas.\n';
			break;
		case 'DAILY': //Envio Daily, que se repite todos los dias a partir de la fecha de creacion, a cierta hora y por cierta cantidad de veces o por terminacion de fecha
			if (data.repeatEachTimeAt.value.trim() === '') {
				msg += '- Al haber elegido envio diario tiene que especificar la hora exacta en que se haran los envios diariamente.\n';
			} else {
				if (!isValidISODateTime(data.repeatEachTimeAt.value.trim()))
					msg += '- Tiene que especificar fecha y hora de envio iniciales que sean validas.\n';
			}
			switch (data.repeatLimitType.value) {
				case 'UNLIMITED':
					console.log('UNLIMITED: En este caso no tengo que validar nada...');
					break;
				case 'QUANTITY':
					if (!/^\d+$/.test(data.repeatQuantity.value.trim())) {
						msg += '- Al haber elegido envio diario, con repeticion por dia, o cantidad de veces. La cantidad de veces o días debe ser un número entero sin decimales.\n';
					}
					break;
				case 'END_DATE':
					if (!isValidISODate(data.repeatEndDate.value.trim()))
						msg += '- Ha elegido envio diario, con fecha de terminacion, pero la fecha de terminacion es invalida!\n';
					break;
			}
			break;
	}

	if (data.sendFrequency.value !== 'IMMEDIATE')
		msg += getRecipentValidationMsg(data.recipients.value);

	if (msg.trim() === '')
		return true;
	else {
		alert(msg);
		return false;
	}

}

function getRecipentValidationMsg(emailRecipent) {
	let msg = '';

	if (emailRecipent.trim() === '') {
		msg += '- Tiene que especificar al menos un email o nombres de listas de emails que seran el destinatario o destinatarios del correo.\n';
	} else {
		const input = emailRecipent.trim();
		const hasComma = input.includes(",");
		const hasSemicolon = input.includes(";");
		if (hasComma && hasSemicolon) {
			msg += '- No puede mezclar comas y punto y coma como separadores para los emails o nombres de listas de email.\n';
		} else {
			let separator = ",";
			if (hasSemicolon) {
				separator = ";";
			}
			const emailsArray = input
				.split(separator)
				.map(e => e.trim())
				.filter(e => e !== "");
			const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
			const listRegex = /^[a-zA-Z0-9_]+$/;
			const invalidArray = emailsArray.filter(e =>
				!emailRegex.test(e) && !listRegex.test(e)
			);

			if (invalidArray.length > 0) {
				msg += `- Los siguientes elementos no son válidos: ${invalidArray.join(", ")}\n`;
			}
			// Filtrar solo válidos (emails o listas)
			const validArray = emailsArray.filter(e =>
				emailRegex.test(e) || listRegex.test(e)
			);
			// Contar
			const emailCount = validArray.filter(e => emailRegex.test(e)).length;
			const listCount = validArray.filter(e => listRegex.test(e)).length;
			if (emailCount > 10 || listCount > 5) {
				msg += '- \nHas excedido el límite permitido (máximo 10 correos y 5 listas). ' +
					'\nSi necesitas agregar más de 10 correos, te recomendamos crear una ' +
					'lista de distribución para gestionarlos de forma más eficiente.' +
					'\nPuedes apoyarte con tu administrador de correos para configurarla fácilmente.';
			}
		}
	}
	return msg;
}