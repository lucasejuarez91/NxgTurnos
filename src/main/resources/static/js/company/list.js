var table;
$(document).ready(function(){
    //loader();
    initDataTable('tblCompany');

    $('.btnEdit').on('click', function(){
        window.location.href = $(this).data('url');
    });

    $('.btnChangeStatus').on('click', async function(){
        let id = $(this).data('id');
        const respuesta = await modalConfirmation(`¿Está seguro de recuperar este salón eliminado?`);
        if (respuesta) {
            await manageEntity("companies", id, {status: true})
        }
    });

    $('.btnDelete').on('click', async function(){
        let id = $(this).data('id');
        const respuesta = await modalConfirmation(`¿Está seguro de cancelar de dar de baja este salón?`);
        if(respuesta)
            await manageEntity("companies", id, {status: false})
    });
});

