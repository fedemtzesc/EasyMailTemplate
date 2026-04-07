// Variables
let htmlInput = document.getElementById('htmlInput');
let previewFrame = document.getElementById('previewFrame');
let handle = document.getElementById('handle');
let leftPane = document.getElementById('leftPane');
let rightPane = document.getElementById('rightPane');
let configSection = document.getElementById('configSection'); // Contenedor de scroll
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

// Objeto para almacenar las imágenes subidas (Nombre de archivo: Base64 URL)
const uploadedImages = {};

// === LÓGICA DEL MODAL ===
function openDocModal() {
  docModal.style.display = 'flex'; // Usar flex para centrar el contenido
}

function closeDocModal() {
  docModal.style.display = 'none';
}

// Cerrar el modal al hacer clic en el overlay (fuera del contenido)
docModal.addEventListener('click', (event) => {
  if (event.target === docModal) {
    closeDocModal();
  }
});
// =========================


// Redimension vertical
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

// Subida de imágenes
const imageUpload = document.getElementById('imageUpload');
const imageList = document.getElementById('imageList');

imageUpload.addEventListener("change", () => {
  imageList.innerHTML = "";
  // Limpiar el objeto de imágenes cargadas antes de volver a llenarlo
  for (const key in uploadedImages) {
    if (uploadedImages.hasOwnProperty(key)) {
      delete uploadedImages[key];
    }
  }

  [...imageUpload.files].forEach(file => {
    const reader = new FileReader();

    // Almacenar el nombre del archivo para usarlo como clave
    const fileName = file.name;
    console.log(file);
    reader.onload = e => {
      const imageUrl = e.target.result;

      // 1. ALMACENAR la URL Base64 para usarla en el preview
      uploadedImages[fileName] = imageUrl;

      // 2. Mostrar la miniatura
      const img = document.createElement("img");
      img.src = imageUrl;
      img.title = fileName; // Mostrar el nombre del archivo al pasar el ratón
      imageList.appendChild(img);

      // 3. Actualizar el preview para que la imagen se vea inmediatamente
      updatePreview();

      // 4. Mueve el scroll hasta abajo para mostrar la imagen
      configSection.scrollTop = configSection.scrollHeight;
    };
    reader.readAsDataURL(file);
  });
});


// LÓGICA DE VISIBILIDAD DE FRECUENCIA PRINCIPAL (dailyTime, scheduleDateTime)
sendFrequency.addEventListener('change', () => {
  // Ocultar todos los campos condicionales por defecto
  dailyTime.style.display = 'none';
  scheduleDateTime.style.display = 'none';
  dailyLimitContainer.classList.add('hidden');

  // Ocultar los campos de límite interior
  repeatCount.style.display = 'none';
  recipients.style.display = 'none';
  lblRecipients.style.display = 'none';
  endDate.style.display = 'none';


  if (sendFrequency.value === 'D') {
    // 1. Mostrar el control de tiempo diario y el contenedor de límite
    dailyTime.style.display = 'inline-block';
    recipients.style.display = 'inline-block';
    lblRecipients.style.display = 'inline-block';
    dailyLimitContainer.classList.remove('hidden');

    // 2. Dar foco y abrir el selector del tiempo diario
    dailyTime.focus();
    if (dailyTime.showPicker) {
      dailyTime.showPicker();
    }

    // Cambiar el foco al select de límite de repetición
    setTimeout(() => {
      repeatLimitType.focus();
      handleRepeatLimitChange(); // Asegurar que el campo de límite inicial se muestre
    }, 100);


  } else if (sendFrequency.value === 'S') {
    // 1. Mostrar el control de datetime-local
    scheduleDateTime.style.display = 'inline-block';
    recipients.style.display = 'inline-block';
    lblRecipients.style.display = 'inline-block';

    // 2. Dar foco y abrir el selector
    scheduleDateTime.focus();

    if (scheduleDateTime.showPicker) {
      scheduleDateTime.showPicker();
    }
  }
});


// LÓGICA DE VISIBILIDAD DEL LÍMITE DE REPETICIÓN (count, endDate)
function handleRepeatLimitChange() {
  // Ocultar ambos inputs antes de mostrar el correcto
  repeatCount.style.display = 'none';
  recipients.style.display = 'none';
  lblRecipients.style.display = 'none';
  endDate.style.display = 'none';

  if (repeatLimitType.value === 'QUANTITY') {
    // Mostrar el campo de cantidad de repeticiones
    repeatCount.style.display = 'inline-block';
    repeatCount.focus(); // Dar foco al campo de cantidad

  } else if (repeatLimitType.value === 'END_DATE') {
    // Mostrar el campo de fecha de término
    endDate.style.display = 'inline-block';
    endDate.focus(); // Dar foco al campo de fecha

    // Abrir el date picker automáticamente si es soportado
    if (endDate.showPicker) {
      endDate.showPicker();
    }
  }

  recipients.style.display = 'inline-block';
  lblRecipients.style.display = 'inline-block';
}

// Escuchar cambios en el selector de tipo de límite
repeatLimitType.addEventListener('change', handleRepeatLimitChange);


// Guardar plantilla
document.getElementById('saveTemplate').addEventListener('click', () => {
  const templateName = document.getElementById('templateName').value.trim();
  const htmlContent = htmlInput.value;
  const jsonExample = document.getElementById('jsonRequestExample').value.trim();
  const frequency = sendFrequency.value;

  let time = '';
  let repeatLimit = { type: 'none', value: null };

  if (frequency === 'S') {
    time = scheduleDateTime.value;
  } else if (frequency === 'D') {
    time = dailyTime.value;

    // Obtener los datos del límite de repetición
    const limitType = repeatLimitType.value;
    repeatLimit.type = limitType;

    if (limitType === 'count') {
      repeatLimit.value = repeatCount.value;
    } else if (limitType === 'date') {
      repeatLimit.value = endDate.value;
    }
  }

  console.log({
    templateName,
    htmlContent,
    jsonExample,
    frequency,
    time,
    repeatLimit
  });

  alert(`Plantilla "${templateName}" lista para enviar al servidor`);
});

// Preview (Modificado para reemplazar rutas de imagen)
function updatePreview() {
  let htmlCode = htmlInput.value;

  // 1. Iterar sobre las imágenes subidas
  for (const fileName in uploadedImages) {
    if (uploadedImages.hasOwnProperty(fileName)) {
      const base64Url = uploadedImages[fileName];

      // 2. Construir la ruta de producción que queremos reemplazar
      const standardPath = `img/${fileName}`;

      // 3. Crear una expresión regular para encontrar todas las ocurrencias de esa ruta
      // Usamos 'g' para reemplazo global y escapamos caracteres especiales como '.'
      const regex = new RegExp(standardPath.replace(/[-\/\\^$+?.()|[\]{}]/g, '\\$&'), 'g');

      // 4. Reemplazar la ruta de producción por la URL Base64 SOLO para el preview
      htmlCode = htmlCode.replace(regex, base64Url);
    }
  }

  const frameDoc = previewFrame.contentDocument || previewFrame.contentWindow.document;
  frameDoc.open();
  frameDoc.write(htmlCode);
  frameDoc.close();
}

// Maximizar con doble clic
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

// Cargar archivo HTML desde filesystem
const loadHTMLBtn = document.getElementById('loadHTML');
const fileHTMLInput = document.getElementById('fileHTMLInput');

loadHTMLBtn.addEventListener('click', () => fileHTMLInput.click());

fileHTMLInput.addEventListener('change', () => {
  const file = fileHTMLInput.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = e => {
      htmlInput.value = e.target.result;
      // Llamar a updatePreview() para cargar el contenido y aplicar el reemplazo de imágenes
      updatePreview();
    };
    reader.readAsText(file);
  }
});