document.addEventListener("DOMContentLoaded", function () {

    new Tabulator("#templateTable", {
        ajaxURL: "/api/v1/wysiwyg/all",
        ajaxConfig: "GET",

        pagination: true,
        paginationMode: "remote",
        paginationSize: 5,
        paginationSizeSelector: [5, 10, 20, 50],

		layout: "fitDataStretch",
		rowHeight: null,
        responsiveLayout: "collapse",
        placeholder: "No se encontraron plantillas registradas",

        ajaxURLGenerator: function (url, config, params) {
            return `${url}?page=${params.page}&size=${params.size}`;
        },

        ajaxResponse: function (url, params, response) {
            return {
                data: response.data,
                last_page: response.lastPage
            };
        },

        columns: [
            {
                title: "ID",
                field: "id",
                width: 90,
                hozAlign: "center"
            },
			{
			    title: "Template Name",
			    field: "templateName",
			    headerFilter: "input",
			    formatter: "textarea"
			},
			{
			    title: "Send Frequency",
			    field: "sendFrequencyDescription",
			    formatter: "textarea"
			},
			{
			    title: "Repeat Limit Type",
			    field: "repeatLimitDescription",
			    formatter: "textarea"
			},
            {
                title: "Acciones",
                hozAlign: "center",
                headerSort: false,
                formatter: function () {
                    return `
                        <button class="btn btn-sm btn-primary me-2">
                            <i class="bi bi-pencil-square"></i>
                        </button>
                        <button class="btn btn-sm btn-danger">
                            <i class="bi bi-trash"></i>
                        </button>
                    `;
                },
                width: 140
            }
        ]
    });

});