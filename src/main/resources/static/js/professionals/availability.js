var table;
$(document).ready(function(){
    initDataTable('tblAvailability');
});

document.addEventListener("DOMContentLoaded", function() {

    fetch(`/turnos/professionals/configCalendar`)
        .then(response => response.json())
        .then(config => {
            var calendarEl = document.getElementById('calendar');

            var calendar = new FullCalendar.Calendar(calendarEl, {
                initialView: 'timeGridWeek',
                locale: 'es',
                slotMinTime: config.startTime, // Configurable desde backend
                slotMaxTime: config.endTime,   // Configurable desde backend
                height: 600, // Ajusta la altura a 600px
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

                    let url = `/turnos/professionals/availability/appts?professionalId=${profesionalId.value}&start=${start}&end=${end}`;

                    console.log("Llamando a la API:", url);

                    fetch(url)
                        .then(response => response.json())
                        .then(data => successCallback(data))
                        .catch(error => failureCallback(error));
                }
            });

            calendar.render();
        });

});
