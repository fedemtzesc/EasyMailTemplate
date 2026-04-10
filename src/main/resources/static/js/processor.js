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


document.addEventListener("DOMContentLoaded", function () {

  if (editMode && viewDTO) {

    // =========================
    // 1. CAMPOS DEL FORMULARIO
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
    // 2. HTML EDITOR
    // =========================
    document.getElementById("htmlInput").value = viewDTO.htmlInput || "";

    // =========================
    // 3. FRECUENCIA UI (IMPORTANTE)
    // =========================
    sendFrequency.dispatchEvent(new Event("change"));
    repeatLimitType.dispatchEvent(new Event("change"));

    // =========================
    // 4. IMÁGENES (si ya vienen del backend)
    // =========================
    if (viewDTO.images) {
      for (const fileName in viewDTO.images) {
        uploadedImages[fileName] = viewDTO.images[fileName];
      }
    }

    // =========================
    // 5. PREVIEW FINAL
    // =========================
    updatePreview();
  }
});
