document.addEventListener("DOMContentLoaded", function() {
            var calendarEl = document.getElementById('calendar');
            var calendar = new FullCalendar.Calendar(calendarEl, {
                initialView: 'timeGridWeek',
                locale: 'es',
                slotMinTime: document.getElementById('companyLbl').dataset.minstarttime, // Configurable desde backend
                slotMaxTime: document.getElementById('companyLbl').dataset.maxendtime,   // Configurable desde backend
                validRange: {
                    start: new Date()  // Evita fechas pasadas
                },
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

                    let url = `/booking/request/slots/appts?professionalId=${professionalLbl.dataset.professionalid}&start=${start}&end=${end}`;

                    console.log("Llamando a la API:", url);

                    fetch(url)
                        .then(response => response.json())
                        .then(data => successCallback(data))
                        .catch(error => failureCallback(error));
                },
                eventClick: async function(info) {
                    var fechaInicio = info.event.start.toISOString(); // Convierte a formato ISO 8601
                    var fechaFin = info.event.end ? info.event.end.toISOString() : null; // Opcional si existe
                    //let respuesta = await modalConfirmation("Esta seguro de soli");
                    //if(!respuesta){
                    //    return;
                    //}
                    await fetch('/booking/confirm', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({ fechaInicio: fechaInicio, fechaFin: fechaFin })
                    })
                        .then(response => response.json())  // Convertir la respuesta a JSON
                        .then(async data => {
                            if (!data.error) {
                                // Redirigir directamente a la página de confirmación
                                //window.location.href = "/booking/confirmation";
                                await modalAlert("success", data.message, () => {
                                    window.location.href = "/appointments/my-appointments"; // Redirecciona solo si el usuario confirma
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

            calendar.render();
});
