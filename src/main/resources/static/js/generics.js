async function doFetch(reqMethod, url, reqData) {
  const fetchOptions = {
    method: reqMethod,
    headers: {
      'Accept': "application/json",
      'Content-Type': "application/json",
    }
  };

  // Solo manda body si no es GET
  if (reqMethod !== 'GET') {
    fetchOptions.body = JSON.stringify(reqData);
  }

  try {
    const response = await fetch(url, fetchOptions);
	console.log(response);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    return data;

  } catch (error) {
    console.error('Error:', error);
    return null;
  }
}


function openGenericWindow(url, w, h) {
    let x = 0;
    let y = 0;
    let win = '';
    
    // Si w o h son 0, ajusta a tamaño completo de la pantalla
    if (w === 0 || h === 0) {
        w = screen.width;
        h = screen.height;
        // Establece las opciones para abrir la ventana de tamaño completo
        win = `width=${w},height=${h},top=${y},left=${x},scrollbars=yes,resizable=no`;
    } else {
        // Si las dimensiones no son 0, abre la ventana centrada
        x = (window.screen.width / 2) - (w / 2);
        y = (window.screen.height / 2) - (h / 2);
        win = `width=${w},height=${h},top=${y},left=${x},scrollbars=yes,resizable=no`;
    }
	
	// Genera un nombre único para cada ventana usando la fecha y hora
	const windowName = 'popupWindow_' + new Date().getTime();

    // Abre la ventana
    let popup = window.open(url, windowName, win);
    
    // Asegúrate de que la ventana haya sido abierta
    if (popup) {
		window.close();
	}else{
        alert('Por favor, permite las ventanas emergentes para este sitio.');
    }

    return false; // Previene que el enlace se siga como navegación normal
}

