$(document).ready(function(){
   initDataTable('tblItems');
   $('#item').select2({theme: 'bootstrap-5'});

   $('#formAddItems').on('submit', async function(e){
       e.preventDefault();
       // Crear un objeto FormData a partir del formulario
       let formData = new FormData(this);

       // Usar Fetch para enviar el formulario al backend
       fetch(getContextPath() + '/appointments/addItemsToBooking', {
           method: 'POST',
           body: formData,
       })
           .then(response => response.text()) // Asumiendo que el backend responde con JSON
           .then(async data => {
               //console.log('Respuesta del backend:', data);
               // Aquí puedes hacer algo con la respuesta, como mostrar un mensaje de éxito
               await mixinAlert('success', 'Item agregado', () => {
                   window.location.reload();
               });
               //$('#tblItems').html(data);
               //initDataTable('tblItems');
               //refreshFragment(getContextPath() + '/booking/refreshTotalizers', '#totalizers')
           })
           .catch(error => {
               console.error('Error al enviar los datos:', error);
           });
   })

});


function createGoogleCalendarEvent(button) {
    // Obtener los datos almacenados en los atributos data-* del botón
    const startDate = button.getAttribute('data-startdate');
    const endDate = button.getAttribute('data-enddate');
    const title = 'Turno en ' + button.getAttribute('data-title');
    const professional = button.getAttribute('data-professional');
    const description = `Servicio de ${button.getAttribute('data-description')} con ${professional}`;
    const location = button.getAttribute('data-location');

    const googleCalendarURL = `https://www.google.com/calendar/render?action=TEMPLATE` +
        `&text=${encodeURIComponent(title)}` +
        `&details=${encodeURIComponent(description)}` +
        `&location=${encodeURIComponent(location)}` +
        `&dates=${formatDateForGoogleCalendar(startDate)}/${formatDateForGoogleCalendar(endDate)}`;

    // Redirige al usuario a Google Calendar para agregar el evento
    window.open(googleCalendarURL, '_blank');
}

// Función para formatear las fechas al formato de Google Calendar (YYYYMMDDTHHmmssZ)
function formatDateForGoogleCalendar(date) {
    // Asegúrate de que la fecha esté en formato adecuado (yyyyMMddTHHmmssZ)
    const formattedDate = new Date(date);
    const year = formattedDate.getUTCFullYear();
    const month = String(formattedDate.getUTCMonth() + 1).padStart(2, '0');
    const day = String(formattedDate.getUTCDate()).padStart(2, '0');
    const hours = String(formattedDate.getUTCHours()).padStart(2, '0');
    const minutes = String(formattedDate.getUTCMinutes()).padStart(2, '0');
    const seconds = String(formattedDate.getUTCSeconds()).padStart(2, '0');

    return `${year}${month}${day}T${hours}${minutes}${seconds}Z`;
}

function createAppleCalendarEvent(button) {
    // Obtener los datos almacenados en los atributos data-* del botón
    const startDate = button.getAttribute('data-startdate');
    const endDate = button.getAttribute('data-enddate');
    const title = 'Turno en ' + button.getAttribute('data-title');
    const professional = button.getAttribute('data-professional');
    const description = `Servicio de ${button.getAttribute('data-description')} con ${professional}`;
    const location = button.getAttribute('data-location');

    // Formatear las fechas en el formato adecuado para iCalendar
    const start = formatDateForICalendar(startDate);
    const end = formatDateForICalendar(endDate);

    // Crear el contenido del archivo .ics con los saltos de línea CRLF
    const icsContent = `BEGIN:VCALENDAR\r\nVERSION:2.0\r\nPRODID:-//Apple Inc.//NONSGML iCal 5.0//EN\r\nBEGIN:VEVENT\r\nUID:${new Date().getTime()}@example.com\r\nDTSTAMP:${start}\r\nDTSTART:${start}\r\nDTEND:${end}\r\nSUMMARY:${title}\r\nDESCRIPTION:${description}\r\nLOCATION:${location}\r\nEND:VEVENT\r\nEND:VCALENDAR\r\n`;

    // Crear un Blob para el archivo .ics
    const blob = new Blob([icsContent], { type: 'text/calendar' });

    // Crear un enlace para descargar el archivo .ics
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = `${title}.ics`; // Nombre del archivo
    link.click();
}

function formatDateForICalendar(date) {
    // Asegúrate de que la fecha esté en formato adecuado (yyyyMMddTHHmmssZ)
    const formattedDate = new Date(date);
    const year = formattedDate.getUTCFullYear();
    const month = String(formattedDate.getUTCMonth() + 1).padStart(2, '0');
    const day = String(formattedDate.getUTCDate()).padStart(2, '0');
    const hours = String(formattedDate.getUTCHours()).padStart(2, '0');
    const minutes = String(formattedDate.getUTCMinutes()).padStart(2, '0');
    const seconds = String(formattedDate.getUTCSeconds()).padStart(2, '0');

    return `${year}${month}${day}T${hours}${minutes}${seconds}Z`;
}

/*Items*/
function autocompleteItem(element) {
    // Obtener la opción seleccionada
    let selectedOption = element.selectedOptions[0];

    // Obtener el valor del atributo 'data-price'
    let itemPrice = selectedOption ? selectedOption.dataset.price : null;

    // Obtener el input donde se mostrará el precio
    let priceInputElement = document.getElementById('price');

    // Asignar el valor al input
    if (priceInputElement && itemPrice) {
        priceInputElement.value = itemPrice;
    }
}


