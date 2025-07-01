$(document).ready(function(){
    $('select').select2({theme: 'bootstrap-5'});
    $('input, select').on('change', function(){
        $(this).addClass('changed');
    });

    $('.btnUpdate').on('click', async function(){
        loader();
        let id = $(this).data('id');
        await manageEntity("appointments", id, await getChanges())
    });

    initCalendar($('#professional').select2('data')[0].element.dataset.id, $('#service').select2('data')[0].element.dataset.id);

    $('#professional,#service').on('change', function(){
        initCalendar($('#professional').select2('data')[0].element.dataset.id, $('#service').select2('data')[0].element.dataset.id);
    });
    //$('#professional').on('change', function(){
        /*initCalendar($('#professional').select2('data')[0].element.dataset.id, document.getElementById('serviceLbl').dataset.serviceid, 'calendar',
            document.getElementById('companyLbl').dataset.min, document.getElementById('companyLbl').dataset.max, async (e) => {
                const respuesta = await modalConfirmation(`¿Está seguro de re-coordinar el turno del <strong>${moment(scheduledDate.value).format('DD-MM-YYYY HH:mm')}</strong> para 
                                    el <strong>${moment(e.event.start).format('DD-MM-YYYY HH:mm')}</strong>? <br>
                                    <label class="alert alert-primary fade show mt-3" role="alert">
                                        Se le enviará una notificación al cliente.
                                    </label>`);
                if(respuesta){
                    let fechaInicio = toLocalISOString(e.event.start); // Convierte a formato ISO 8601
                    let fechaFin = e.event.end ? toLocalISOString(e.event.end) : null; // Opcional si existe
                    await manageEntity("appointments", document.getElementById('bkg').value, {scheduledDateStart: fechaInicio, scheduledDateEnd: fechaFin})
                }
            });*/
    //})
});

