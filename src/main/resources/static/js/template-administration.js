document.addEventListener("DOMContentLoaded", function () {

    const searchInput = document.getElementById("templateSearch");

    const table = new Tabulator("#templateTable", {
        ajaxURL: "/api/v1/wysiwyg/all",
        ajaxConfig: "GET",

        pagination: true,
        paginationMode: "remote",
        paginationSize: 5,
        paginationSizeSelector: [5, 10, 20, 50],

        layout: "fitColumns",
        rowHeight: null,
        responsiveLayout: "collapse",
        placeholder: "No se encontraron plantillas registradas",

        ajaxURLGenerator: function (url, config, params) {
            const searchValue = searchInput.value || "";
            return `${url}?page=${params.page}&size=${params.size}&search=${encodeURIComponent(searchValue)}`;
        },

        // Mapea los nombres de tu backend al formato que Tabulator espera
        paginationDataReceived: {
            last_page: "lastPage",
            data: "data"
        },

        ajaxResponse: function (url, params, response) {
            // Asegurarse de que data sea siempre un array
            response.data = Array.isArray(response.data) ? response.data : [];
            // Asegurar lastPage
            response.last_page = response.lastPage && response.lastPage > 0 ? response.lastPage : 1;
            return response;
        },

        ajaxError: function (xhr, textStatus, errorThrown) {
            console.error("Tabulator AJAX Error:", textStatus, errorThrown);
        },

        columns: [
            {
                title: "Nombre Plantilla",
                field: "templateName",
                headerHozAlign: "center"
            },
            {
                title: "Frecuencia de Envío",
                field: "sendFrequencyDescription",
                headerHozAlign: "center"
            },
            {
                title: "Detalle del Envío",
                field: "repeatLimitDescription",
                headerHozAlign: "center"
            },
            {
                title: "Acciones",
                hozAlign: "center",
                headerHozAlign: "center",
                headerSort: false,
                widthShrink: 0,
                width: 120,
                formatter: function () {
                    return `
					<div class="d-flex justify-content-center align-items-center gap-1">
		                <button class="btn btn-sm btn-primary edit-btn" style="padding: 1px 6px; line-height: 1.2;">
		                    <i class="bi bi-pencil-square" style="font-size: 0.75rem;"></i>
		                </button>
		                <button class="btn btn-sm btn-danger delete-btn" style="padding: 1px 6px; line-height: 1.2;">
		                    <i class="bi bi-trash" style="font-size: 0.75rem;"></i>
		                </button>
		            </div>
                    `;
                },
                cellClick: function (e, cell) {
                    const rowData = cell.getRow().getData();

                    if (e.target.closest(".edit-btn")) {
                        console.log("Editar plantilla:", rowData);
                        alert(`Editar plantilla: ${rowData.templateName}`);
						alert('Editanto plantilla con id: ' + rowData.id);
                        window.location.href = `/wysiwyg?id=${rowData.id}`;
                    }

                    if (e.target.closest(".delete-btn")) {
                        console.log("Eliminar plantilla:", rowData);
                        const confirmDelete = confirm(`¿Deseas eliminar la plantilla "${rowData.templateName}"?`);
                        if (confirmDelete) {
                            alert(`Eliminar plantilla ID: ${rowData.id}`);
                            /*
                            fetch(`/api/v1/wysiwyg/${rowData.id}`, { method: "DELETE" })
                                .then(resp => resp.json())
                                .then(result => {
                                    alert(result.message);
                                    cell.getTable().replaceData();
                                })
                                .catch(err => {
                                    console.error(err);
                                    alert("Ocurrió un error al eliminar la plantilla");
                                });
                            */
                        }
                    }
                }
            }
        ]
    });

    // 🔍 Búsqueda remota
    searchInput.addEventListener("input", function () {
        table.setPage(1); // Reinicia a la primera página
        //table.replaceData(); // Vuelve a cargar la data desde el backend
    });

});