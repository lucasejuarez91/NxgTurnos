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

