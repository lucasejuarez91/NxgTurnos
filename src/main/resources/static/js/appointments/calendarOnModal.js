var cal;
var loaded = false;
$('#modalCalendar').on("shown.bs.modal", function() {
    console.log("Dibujando calendario...");
    if(!loaded){
        let seconds = 10;
        drawCalendar();
        updateCountdown(seconds);
        setInterval(function() {drawCalendar(cal.view.type)}, seconds * 1000);
        loaded = true;
    }
});

function updateCountdown(seconds) {
    const countdownElement = document.getElementById("countdown");
    countdownElement.textContent = `Actualiza en ${seconds} segundos...`;
    if (seconds > 1) {
        seconds--;
    } else {
        seconds = 10;
    }
    setTimeout(updateCountdown, 1000, seconds);
}

function drawCalendar(initialView = 'timeGridWeek'){
    var dateLimit = new Date();
    dateLimit.setDate(dateLimit.getDate() + 30);
    var calendarEl = document.getElementById('calendar');
    cal = new FullCalendar.Calendar(calendarEl, {
        initialView: initialView,
        locale: 'es',
        firstDay: new Date().getDay(),
        slotMinTime: document.getElementById('companyLbl').dataset.minstarttime, // Configurable desde backend
        slotMaxTime: document.getElementById('companyLbl').dataset.maxendtime,   // Configurable desde backend
        validRange: {
            start: new Date(),  // Evita fechas pasadas
            end: dateLimit
        },
        height: '600px',
        aspectRatio: 9,
        contentHeight: 300,
        allDaySlot: false,
        headerToolbar: {
            left: "prev,next today",
            center: "title",
            right: "timeGridWeek,dayGridDay,dayGridMonth"
        },
        views: {
            timeGridWeek: { // name of view
                displayEventTime: false
                // other view-specific options here
            }
        },
        events: function (fetchInfo, successCallback, failureCallback) {
            // Convertir a formato YYYY-MM-DD sin zona horaria
            let start = fetchInfo.start.toISOString().split("T")[0];
            let end = fetchInfo.end.toISOString().split("T")[0];

            let url = `/turnos/booking/request/slots/allAppts`;

            console.log("Llamando a la API:", url);

            fetch(url)
                .then(response => response.json())
                .then(data => {
                    // Suponiendo que el backend devuelve `isLoggedIn`
                    let isLoggedIn = data.isLoggedIn;

                    let events = data.events.map(event => ({
                        ...event,
                        extendedProps: {isLoggedIn} // Agregar info extra a cada evento
                    }));

                    successCallback(events);
                })
                .catch(error => failureCallback(error));
        },
        eventClick: function(info) {
            let eventObj = info.event;
            /*let date = moment(eventObj.start).format('DD-MM-YYYY HH:mm');
            let service = eventObj.extendedProps.service ? `Servicio: ${eventObj.extendedProps.service}` : '';
            modalAlert('info', `<p>Turno: ${eventObj.extendedProps.isBusy ? 'Ocupado' : 'Disponible'}
                                                <br>Profesional: ${eventObj.extendedProps.prof}
                                                 <br>Fecha/Hora: ${date}
                                                 <br>${service}
                                               <p>` );*/
            // Verificar si el evento tiene un template asignado
            let templateId = eventObj.extendedProps.swalTemplate;
            let dateStart = moment(eventObj.start).format('DD-MM-YYYY HH:mm');
            let dateEnd = moment(eventObj.end).format('DD-MM-YYYY HH:mm');
            let service = eventObj.extendedProps.service ? `<p><strong>Servicio:</strong> ${eventObj.extendedProps.service}</p>` : '';
            let available = eventObj.extendedProps.isBusy ? eventObj.title : 'Disponible';
            if (templateId) {
                Swal.fire({
                    html: `
                <p>${available}</p>
                ${service}
                <p><strong th:text="#{'title.datetime'}">Inicio:</strong> ${dateStart}</p>
                <p><strong th:text="#{'title.datetime'}">Fin:</strong> ${dateEnd}</p>
            `,
                    icon: "info",
                    toast: true
                });
            }
        }
    });
    cal.render();
}