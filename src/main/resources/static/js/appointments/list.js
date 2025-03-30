var table;
$(document).ready(function(){
    //loader();
    //initDataTable('tblAppts');
    initDataTableGroup('tblAppts', 6, true, 5);

    $('.btnEdit').on('click', function(){
        window.location.href = $(this).data('url');
    });

    $('.btnDelete').on('click', async function(){
        let id = $(this).data('id');
        const respuesta = await modalConfirmation(`¿Está seguro de cancelar el turno? <br><strong>Se le enviará al cliente una notificación.</strong>`);
        if(respuesta)
            await manageEntity("appointments", id, {status: false, apptStatus: '/appointmentStatus/2'})
    });
});

