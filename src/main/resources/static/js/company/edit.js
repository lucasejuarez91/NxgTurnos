var table;
$(document).ready(function(){

    initDataTable('tblProfessionals');
    var companyId = $('.btnUpdate').data('id');

    $('input, select, textarea').on('change', function(){
        $(this).addClass('changed');
    });

    $('.btnUpdate').on('click', async function(){
        loader();
        let id = $(this).data('id');
        await manageEntity("companies", id, await getChanges())
    });

    /* Professionals */
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
    /* End Professionals */

    $('#btnUploadAvatar').on('click', function() {
        fileInput.click();
    });

    $('#fileInput').on('change', async function(e) {
        //const fileInput = document.getElementById('fileInput');
        const file = e.currentTarget.files[0];
        if (file) {
            let resp = await uploadImage(file);
            if(!resp.error){

                const response = await fetch(e.currentTarget.dataset.url, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: resp.message
                });
                if (!response.ok) {
                    modalAlert("error", `Error al actualizar: ${response.statusText}`);
                    throw new Error(`Error al actualizar: ${response.statusText}`);
                }
                await mixinAlert("success", "Actualizado", reload);
            }
        }
    });

    $('#btnUploadSignature').on('click', function() {
        fileInputSignature.click();
    });

    $('#fileInputSignature').on('change', async function(e) {
        //const fileInput = document.getElementById('fileInput');
        const file = e.currentTarget.files[0];
        if (file) {
            let resp = await uploadImage(file);
            if(!resp.error){
                const response = await fetch(`/companies/${companyId}/updateSignature`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: resp.message
                });
                if (!response.ok) {
                    modalAlert("error", `Error al actualizar: ${response.statusText}`);
                    throw new Error(`Error al actualizar: ${response.statusText}`);
                }
                await mixinAlert("success", "Actualizado", reload);
            }
        }
    });

});

