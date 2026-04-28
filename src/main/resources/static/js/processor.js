window.currentTemplateId = null;

async function processWYSIWYG() {
	//Validamos antes de guardar
	if (!confirm('Esta totalmente seguro de que desea guardar o actualizar lod datos de esta plantilla?'))
		return;

	const formData = new FormData();
	let htmlVerb = "POST";

	const id = Number(window.currentTemplateId);

	if (Number.isInteger(id) && id > 0) {
		htmlVerb = "PATCH";
		formData.append("id", id);
	}

	formData.append("templateName", document.getElementById("templateName").value);
	formData.append("templateSubject", document.getElementById("templateSubject").value);	
	formData.append("description", document.getElementById("description").value);
	formData.append("sendFrequency", document.getElementById("sendFrequency").value);
	formData.append("dateTimeSending", document.getElementById("scheduleDateTime").value);
	formData.append("repeatEachTimeAt", document.getElementById("dailyTime").value);
	if (document.getElementById("sendFrequency").value === "DAILY")
		formData.append("repeatLimitType", document.getElementById("repeatLimitType").value);
	formData.append("repeatQuantity", document.getElementById("repeatCount").value);
	if (document.getElementById("dailyTime").value.trim()!=='')
		formData.append("repeatEndDate", document.getElementById("endDate").value + getTimePart(document.getElementById("dailyTime").value));
	formData.append("emailList", document.getElementById("recipients").value);
	formData.append("htmlInput", document.getElementById("htmlInput").value);

	const files = document.getElementById("imageUpload").files;

	for (let i = 0; i < files.length; i++) {
		formData.append("images", files[i]);
	}

	if (!isValidWYSIWYGFormDataBeforePersist())
		return;

	try {
		const response = await fetch("/api/v1/wysiwyg", {
			method: htmlVerb,
			body: formData
		});

		const result = await response.json();

		console.log(result);

		if (response.ok) {
			alert(result.message);
		} else {
			alert(result.message);
		}

	} catch (error) {
		console.error(error);
		alert("Ocurrió un error inesperado al guardar la plantilla");
	}
}


// =====================================================
// DOM READY INIT
// =====================================================
document.addEventListener("DOMContentLoaded", function () {

	if (editMode && viewDTO) {

		// =========================
		// 1. FORM
		// =========================
		window.currentTemplateId = viewDTO.id ? Number(viewDTO.id) : null;
		document.getElementById("templateName").value = viewDTO.templateName || "";
		document.getElementById("description").value = viewDTO.description || "";
		document.getElementById("sendFrequency").value = viewDTO.sendFrequency || "";
		document.getElementById("scheduleDateTime").value = viewDTO.dateTimeSending || "";
		document.getElementById("dailyTime").value = viewDTO.repeatEachTimeAt || "";
		document.getElementById("repeatLimitType").value = viewDTO.repeatLimitType || "";
		document.getElementById("repeatCount").value = viewDTO.repeatQuantity || "";
		document.getElementById("endDate").value = viewDTO.repeatEndDate || "";
		document.getElementById("recipients").value = viewDTO.emailList || "";


		// =========================
		// 2. HTML
		// =========================
		document.getElementById("htmlInput").value = viewDTO.htmlInput || "";

		// =========================
		// 3. UI EVENTS
		// =========================
		document.getElementById("sendFrequency")
			.dispatchEvent(new Event("change"));

		document.getElementById("repeatLimitType")
			.dispatchEvent(new Event("change"));

		document.getElementById("repeatCount").value = viewDTO.repeatQuantity || "";
		document.getElementById("endDate").value = viewDTO.repeatEndDate || "";
		// =========================
		// 4. IMAGE BRIDGE (CLAVE 🔥)
		// =========================
		window.uploadedImages = {};
		if (viewDTO.images) {
			window.uploadedImages = { ...(viewDTO.images || {}) };
		}

		// =========================
		// 5. PREVIEW
		// =========================
		updatePreview();

		// =========================
		// 6. THUMBNAILS (SIN DELAY, SIN HACKS)
		// =========================
		const imageList = document.getElementById("imageList");

		if (viewDTO.images) {
			imageList.innerHTML = "";

			for (const fileName in window.uploadedImages) {
				const img = document.createElement("img");
				img.src = window.uploadedImages[fileName];
				img.style.width = "100px";
				img.title = fileName;
				imageList.appendChild(img);
			}
		}
	}
});


// Funcion para limpiar la forma despues de guardar los datos
function clearForm() {
	document.getElementById("templateName").value = '';
	document.getElementById("description").value = '';
	document.getElementById("sendFrequency").value = 'I';
	document.getElementById("scheduleDateTime").value = '';
	document.getElementById("dailyTime").value = '';
	document.getElementById("repeatLimitType").value = 'UNLIMITED';
	document.getElementById("repeatCount").value = '';
	document.getElementById("endDate").value = '';
	document.getElementById("recipients").value = '';
	document.getElementById("htmlInput").value = '';

	// archivos también
	document.getElementById("imageUpload").value = '';
}


async function loginUser(event) {

    event.preventDefault();

    const username =
        document.getElementById("username").value.trim();

    const password =
        document.getElementById("password").value.trim();

    if (username === "" || password === "") {
        alert("Debe capturar usuario y password.");
        return;
    }

    const reqData = {
        username: username,
        password: password
    };

    const response = await doFetch(
        "POST",
        "/auth/v1/log-in",
        reqData
    );

    if (response === null) {
        alert("No fue posible conectar con el servidor.");
        return;
    }

    if (response.status === "success") {

        // Ya no guardar JWT en localStorage.
        // El backend lo guardará en cookie HttpOnly.

        window.location.href = "/welcome";

    } else {
        alert(response.message);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector("form");
    form.addEventListener("submit", loginUser);
});