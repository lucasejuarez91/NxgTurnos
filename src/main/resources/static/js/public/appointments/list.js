var table;
$(document).ready(function() {
    loader();
    initDataTableGroup('tblAppts', 4, true, 5);

    $('.btnView, .btnReSchedule').on('click', function () {
        window.location.href = $(this).data('url');
    });

    $('.btnDelete').on('click', async function () {
        let id = $(this).data('id');
        const respuesta = await modalConfirmation(i18next.t("title.modal.confirm.delete", {'entity': i18next.t("entity.appointment")}));
        if (respuesta)
            await manageEntity("appointments", id, {status: false, apptStatus: '/appointmentStatus/2'})
    });

    $('.btnQR').on('click', function () {
        document.getElementById("qrcode").innerHTML = '';
        $('#qrTurnoModal').modal('show');
        let url = $(this).data('url');
        fetch(url)
            .then(response => response.text())
            .then(data => {
                url = data;
                new QRCode(document.getElementById("qrcode"), {
                    text: getContextPath() + `/appointments/initAppointment?code=${url}`,
                    width: 300,
                    height: 300
                });
            });
    });

    $('#btnModalCalendar').on('click', function () {
        fetch(getContextPath() + `/professionals/configCalendar?companyId=${loggedUserCompany.dataset.value}`)
            .then(response => response.json())
            .then(config => {
                const calendarEl = document.getElementById('calendar');

                const calendar = new FullCalendar.Calendar(calendarEl, {
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
                    events: function (fetchInfo, successCallback, failureCallback) {
                        // Convertir a formato YYYY-MM-DD sin zona horaria
                        let start = fetchInfo.start.toISOString().split("T")[0];
                        let end = fetchInfo.end.toISOString().split("T")[0];

                        let url = getContextPath() + `/company/availability/slots?start=${start}&end=${end}`;

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
    loader(false);
});

