var cal;//2
document.addEventListener("DOMContentLoaded", function() {
    initCalendar(professionalLbl.dataset.professionalid)
    /*var dateLimit = new Date();
    dateLimit.setDate(dateLimit.getDate() + 30);
    var calendarEl = document.getElementById('calendar');
    cal = new FullCalendar.Calendar(calendarEl, {
        initialView: 'timeGridWeek',
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
            right: "timeGridWeek"
        },
        events: function(fetchInfo, successCallback, failureCallback) {
            // Convertir a formato YYYY-MM-DD sin zona horaria
            let start = fetchInfo.start.toISOString().split("T")[0];
            let end = fetchInfo.end.toISOString().split("T")[0];

            let url = `/turnos/booking/request/slots/appts?professionalId=${professionalLbl.dataset.professionalid}&start=${start}&end=${end}`;

            console.log("Llamando a la API:", url);

            fetch(url)
                .then(response => response.json())
                .then(data => {
                    // Suponiendo que el backend devuelve `isLoggedIn`
                    let isLoggedIn = data.isLoggedIn;

                    let events = data.events.map(event => ({
                        ...event,
                        extendedProps: { isLoggedIn } // Agregar info extra a cada evento
                    }));

                    successCallback(events);
                })
                .catch(error => failureCallback(error));
        },
        eventClick: async function(info) {
            let event = info.event;
            if (!event.extendedProps.isLoggedIn) {
                $('#openLoginModal').modal('show');
                return;
            }
            var fechaInicio = info.event.start.toISOString(); // Convierte a formato ISO 8601
            var fechaFin = info.event.end ? info.event.end.toISOString() : null; // Opcional si existe



            await fetch(`/turnos/booking/preconfirm`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ start: fechaInicio, end: fechaFin})
            })
                .then(response => response.json())  // Convertir la respuesta a JSON
                .then(async data => {
                    if (data.redirectUrl) {
                        // Redirigir directamente a la página de confirmación
                        //window.location.href = "/booking/confirmation";
                        await modalAlert("success", "Turno confirmado correctamente", () => {
                            window.location.href = data.redirectUrl; // Redirecciona solo si el usuario confirma
                        });
                    } else {
                        alert(data.message);
                    }
                })
                .catch(error => {
                    console.error("Error en la solicitud:", error);
                    alert("Ocurrió un error al verificar el evento.");
                });

        }
    });
    cal.render();*/

    initLoginModal();

});







