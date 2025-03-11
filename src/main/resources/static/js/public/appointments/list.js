var table;
$(document).ready(function(){
    //loader();
    initDataTable('tblAppts');

    $('.btnEdit').on('click', function(){
        window.location.href = $(this).data('url');
    });

    $('.btnDelete').on('click', async function(){
        let id = $(this).data('id');
        const respuesta = await modalConfirmation(`¿Está seguro de cancelar este turno?`);
        if(respuesta)
            await manageEntity("appointments", id, {status: false, apptStatus: '/appointmentStatus/2'})
    });

    $('.btnQR').on('click', function(){
        document.getElementById("qrcode").innerHTML = '';
        $('#qrTurnoModal').modal('show');
        let jsonData = { name: "Juan", age: 30, job: "Developer" };
        let jsonString = JSON.stringify(jsonData);

        new QRCode(document.getElementById("qrcode"), {
            text: jsonString,  // Pasamos el JSON como string
            width: 300,
            height: 300
        });
    });

    $('#btnModalCalendar').on('click', function(){
        fetch(`/professionals/configCalendar?companyId=${loggedUserCompany.dataset.value}`)
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

                        let url = `/company/availability/slots?start=${start}&end=${end}`;

                        console.log("Llamando a la API:", url);

                        fetch(url)
                            .then(response => response.json())
                            .then(data => successCallback(data))
                            .catch(error => failureCallback(error));
                    }
                });

                calendar.render();
            });
       $('#modalCalendar').modal('show')
    });
});

