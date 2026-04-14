function isValidDateYYYYMMDD(dateStr) {
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

// Blanquea los inputs que le indiquemos en una lista de este formato ['inputId_01','inputId_02',...'inputId_n']
function resetFields(ids) {
	ids.forEach(id => document.getElementById(id).value = '');
}
