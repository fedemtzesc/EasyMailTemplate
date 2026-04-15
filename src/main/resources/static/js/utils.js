function isValidISODate(dateStr) {
	if (!dateStr || typeof dateStr !== 'string') return false;

	const value = dateStr.trim();
	if (value === '') return false;

	const dateRegex = /^\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])$/;
	if (!dateRegex.test(value)) return false;

	const [year, month, day] = value.split('-').map(Number);

	const date = new Date(year, month - 1, day);

	return (
		date.getFullYear() === year &&
		date.getMonth() === month - 1 &&
		date.getDate() === day
	);
}


function isValidISODateTime(dateStr) {
	if (!dateStr || typeof dateStr !== 'string') return false;

	const value = dateStr.trim();
	if (value === '') return false;

	const dateRegex = /^\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])T([01]\d|2[0-3]):([0-5]\d)$/;
	if (!dateRegex.test(value)) return false;

	// Separar fecha y hora
	const [datePart, timePart] = value.split('T');
	const [year, month, day] = datePart.split('-').map(Number);
	const [hour, minute] = timePart.split(':').map(Number);

	// Crear fecha completa
	const date = new Date(year, month - 1, day, hour, minute);

	// Validación completa
	return (
		date.getFullYear() === year &&
		date.getMonth() === month - 1 &&
		date.getDate() === day &&
		date.getHours() === hour &&
		date.getMinutes() === minute
	);
}



// Blanquea los inputs que le indiquemos en una lista de este formato ['inputId_01','inputId_02',...'inputId_n']
function resetFields(ids) {
	ids.forEach(id => document.getElementById(id).value = '');
}
