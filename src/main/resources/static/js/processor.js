async function processWYSIWYG() {
	const formData = new FormData();

	formData.append("id", "");
	formData.append("templateName", document.getElementById("templateName").value);
	formData.append("description", document.getElementById("description").value);
	formData.append("sendFrequency", document.getElementById("sendFrequency").value);
	formData.append("dateTimeSending", document.getElementById("scheduleDateTime").value);
	formData.append("repeatEachTimeAt", document.getElementById("dailyTime").value);
	formData.append("repeatLimitType", document.getElementById("repeatLimitType").value);
	formData.append("repeatQuantity", document.getElementById("repeatCount").value);
	formData.append("repeatEndDate", document.getElementById("endDate").value);
	formData.append("emailList", document.getElementById("recipients").value);
	formData.append("htmlInput", document.getElementById("htmlInput").value);

	const files = document.getElementById("imageUpload").files;

	for (let i = 0; i < files.length; i++) {
		formData.append("images", files[i]);
	}
	
	if(!isValidWYSIWYGFormDataBeforePersist())
		return;

	try {
		const response = await fetch("/api/v1/wysiwyg", {
			method: "POST",
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
