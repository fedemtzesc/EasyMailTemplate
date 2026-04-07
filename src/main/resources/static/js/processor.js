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

        if (!response.ok) {
            throw new Error("Error al guardar la plantilla");
        }

        const result = await response.json();

        console.log(result);
        alert("Plantilla guardada correctamente");

    } catch (error) {
        console.error(error);
        alert("Ocurrió un error al guardar la plantilla");
    }
}
