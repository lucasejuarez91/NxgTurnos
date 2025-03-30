var table;
$(document).ready(function(){

    initDataTable('tblServices')

    $('input, select, textarea').on('change', function(){
        $(this).addClass('changed');
    });

    $('.btnUpdate').on('click', async function(){
        loader();
        let id = $(this).data('id');
        await manageEntity("categoryServices", id, await getChanges())
    });

    $('.btnEdit').on('click', function(){
        window.location.href = $(this).data('url');
    });

    $('.btnChangeStatus').on('click', async function(){
        let id = $(this).data('id');
        const respuesta = await modalConfirmation(`¿Está seguro de recuperar este Servicio eliminado?`);
        if (respuesta) {
            await manageEntity("services", id, {status: true})
        }
    });

    $('.btnDelete').on('click', async function(){
        let id = $(this).data('id');
        const respuesta = await modalConfirmation(`¿Está seguro de eliminar este Servicio?`);
        if(respuesta)
            await manageEntity("services", id, {status: false})
    });

});

