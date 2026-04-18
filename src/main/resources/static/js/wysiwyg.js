// Variables
let htmlInput = document.getElementById('htmlInput');
let previewFrame = document.getElementById('previewFrame');
let handle = document.getElementById('handle');
let leftPane = document.getElementById('leftPane');
let rightPane = document.getElementById('rightPane');
let configSection = document.getElementById('configSection');
let editorSection = document.getElementById('editorSection');
let isResizing = false;

// Variables de frecuencia y límite
const sendFrequency = document.getElementById('sendFrequency');
const dailyTime = document.getElementById('dailyTime');
const scheduleDateTime = document.getElementById('scheduleDateTime');
const dailyLimitContainer = document.getElementById('dailyLimitContainer');
const repeatLimitType = document.getElementById('repeatLimitType');
const repeatCount = document.getElementById('repeatCount');
const recipients = document.getElementById('recipients');
const lblRecipients = document.getElementById('lblRecipients');
const endDate = document.getElementById('endDate');

// Variables del Modal
const docModal = document.getElementById('docModal');

// Objeto para almacenar las imágenes subidas
window.uploadedImages = window.uploadedImages || {};

// =========================
// MODAL
// =========================
function openDocModal() {
	docModal.style.display = 'flex';
}

function closeDocModal() {
	docModal.style.display = 'none';
}

docModal.addEventListener('click', (event) => {
	if (event.target === docModal) {
		closeDocModal();
	}
});

// =========================
// RESIZE
// =========================
handle.addEventListener('mousedown', e => {
	isResizing = true;
	document.body.style.cursor = 'ns-resize';
});

document.addEventListener('mousemove', e => {
	if (!isResizing) return;

	let containerTop = configSection.getBoundingClientRect().top;
	let newHeight = e.clientY - containerTop;

	if (newHeight < 50) newHeight = 50;
	if (newHeight > leftPane.clientHeight - 50) newHeight = leftPane.clientHeight - 50;

	configSection.style.flex = '0 0 ' + newHeight + 'px';
	editorSection.style.flex = '1';
});

document.addEventListener('mouseup', e => {
	isResizing = false;
	document.body.style.cursor = 'default';
});

// =========================
// SUBIDA DE IMÁGENES
// =========================
const imageUpload = document.getElementById('imageUpload');
const imageList = document.getElementById('imageList');

imageUpload.addEventListener("change", () => {
	imageList.innerHTML = "";

	// limpiar estado anterior
	window.uploadedImages = {};

	const files = [...imageUpload.files];

	const MAX_SIZE = 500 * 1024; // 500KB

	// 🔥 VALIDACIÓN GLOBAL (si UNA falla, se cancela TODO)
	const invalidFile = files.find(file => file.size > MAX_SIZE);

	if (invalidFile) {
		alert(`La imagen "${invalidFile.name}" excede el límite de 500KB. No se cargará ninguna imagen.`);

		// limpiar input para evitar estado inconsistente
		imageUpload.value = "";

		return; // 🚫 ABORTA TODO EL PROCESO
	}


	files.forEach(file => {
		const reader = new FileReader();
		const fileName = file.name;

		reader.onload = e => {
			const imageUrl = e.target.result;

			// guardar en estado
			window.uploadedImages[fileName] = imageUrl;

			// crear miniatura
			const img = document.createElement("img");
			img.src = imageUrl;
			img.title = fileName;

			imageList.appendChild(img);

			// actualizar preview
			updatePreview();

			// scroll al final
			configSection.scrollTop = configSection.scrollHeight;
		};

		reader.readAsDataURL(file);
	});
});


sendFrequency.addEventListener('change', () => {

	dailyTime.style.display = 'none';
	scheduleDateTime.style.display = 'none';
	dailyLimitContainer.classList.add('hidden');

	repeatCount.style.display = 'none';
	recipients.style.display = 'none';
	lblRecipients.style.display = 'none';
	endDate.style.display = 'none';

	if (sendFrequency.value === 'DAILY') {

		dailyTime.style.display = 'inline-block';
		recipients.style.display = 'inline-block';
		lblRecipients.style.display = 'inline-block';
		dailyLimitContainer.classList.remove('hidden');

		// ❌ REMOVIDO: dailyTime.showPicker()

		setTimeout(() => {
			repeatLimitType.focus();
			handleRepeatLimitChange();
		}, 100);

	} else if (sendFrequency.value === 'SCHEDULED') {

		scheduleDateTime.style.display = 'inline-block';
		recipients.style.display = 'inline-block';
		lblRecipients.style.display = 'inline-block';

		scheduleDateTime.focus();

		// ❌ REMOVIDO: scheduleDateTime.showPicker()
	}
});

// =========================
// LÍMITE REPETICIÓN (FIX)
// =========================
function handleRepeatLimitChange() {

	repeatCount.style.display = 'none';
	recipients.style.display = 'none';
	lblRecipients.style.display = 'none';
	endDate.style.display = 'none';

	if (repeatLimitType.value === 'QUANTITY') {

		repeatCount.style.display = 'inline-block';
		repeatCount.focus();

	} else if (repeatLimitType.value === 'END_DATE') {

		endDate.style.display = 'inline-block';
		endDate.focus();

		// ❌ REMOVIDO: endDate.showPicker()
	}

	recipients.style.display = 'inline-block';
	lblRecipients.style.display = 'inline-block';
}

repeatLimitType.addEventListener('change', handleRepeatLimitChange);

// =========================
// SAVE TEMPLATE
// =========================
document.getElementById('saveTemplate').addEventListener('click', processWYSIWYG);


// =========================
// PREVIEW (SIN CAMBIOS)
// =========================
function updatePreview() {
	const images = window.uploadedImages || {};
	let htmlCode = htmlInput.value;

	for (const fileName in images) {
		if (!Object.prototype.hasOwnProperty.call(images, fileName)) continue;

		const base64Url = images[fileName];
		const standardPath = `img/${fileName}`;



		const regex = new RegExp(
			standardPath.replace(/[-\/\\^$+?.()|[\]{}]/g, '\\$&'),
			'g'
		);

		htmlCode = htmlCode.replace(regex, base64Url);
	}

	const frameDoc = previewFrame.contentDocument || previewFrame.contentWindow.document;
	frameDoc.open();
	frameDoc.write(htmlCode);
	frameDoc.close();
}

// =========================
// FULLSCREEN + LOAD HTML (SIN CAMBIOS)
// =========================
htmlInput.addEventListener('dblclick', () => toggleFullscreen(htmlInput));
previewFrame.addEventListener('dblclick', () => toggleFullscreen(previewFrame));

function toggleFullscreen(element) {
	element.classList.toggle('fullscreen');

	if (element.classList.contains('fullscreen')) {
		if (element === htmlInput) rightPane.style.display = 'none';
		if (element === previewFrame) leftPane.style.display = 'none';
	} else {
		rightPane.style.display = 'flex';
		leftPane.style.display = 'flex';
	}
}

const loadHTMLBtn = document.getElementById('loadHTML');
const fileHTMLInput = document.getElementById('fileHTMLInput');

loadHTMLBtn.addEventListener('click', () => fileHTMLInput.click());

fileHTMLInput.addEventListener('change', () => {
	const file = fileHTMLInput.files[0];

	if (file) {
		const reader = new FileReader();

		reader.onload = e => {
			htmlInput.value = e.target.result;
			updatePreview();
		};

		reader.readAsText(file);
	}
});

function resetByFrequency(frequency) {
	if (frequency === 'I') {
		resetFields(["scheduleDateTime", "dailyTime", "repeatLimitType", "repeatCount", "endDate"]);
	}

	if (frequency === 'S') {
		resetFields(["dailyTime", "repeatLimitType", "repeatCount", "endDate"]);
	}

	if (frequency === 'D') {
		resetFields(["scheduleDateTime", "repeatCount", "endDate"]);
	}
}

document.getElementById("sendFrequency").addEventListener("change", (e) => {
	resetByFrequency(e.target.value);
});


htmlInput.addEventListener('input', updatePreview);


function showCatalog() {
	alert('Showing HTML Templates Catalog!');
}

function getTemplateJSON() {
	alert('Showing Template JSON!');
}

function showStatistics() {
	alert('Showing Template Statistics!');
}


function testTemplateSending() {
	alert('Testing template sending...');
}

function exitFromWYSIWYG() {
	window.location.href = '/templates-list';
}


