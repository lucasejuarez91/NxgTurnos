var table;
$(document).ready(function(){
    //loader();
    initDataTable('tblProfessionals');

    $('.btnEdit').on('click', function(){
        window.location.href = $(this).data('url');
    });

    $('.btnAvailability').on('click', function(){
        window.location.href = $(this).data('url');
    });

    $('.btnChangeStatus').on('click', async function(){
        let id = $(this).data('id');
        const respuesta = await modalConfirmation(`¿Está seguro de recuperar este Profesional eliminado?`);
        if (respuesta) {
            await manageEntity("professionals", id, {status: true})
        }
    });

    $('.btnDelete').on('click', async function(){
        let id = $(this).data('id');
        const respuesta = await modalConfirmation(`¿Está seguro de eliminar este Profesional?`);
        if(respuesta)
            await manageEntity("professionals", id, {status: false})
    });

    loadHtmlOnModal(document.getElementById('createProfessionalBtn'));
    //loader(false);
});

