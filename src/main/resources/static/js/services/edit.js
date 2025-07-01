$(document).ready(function(){

    $('input, select').on('change', function(){
        $(this).addClass('changed');
    });

    $('.btnUpdate').on('click', async function(){
        loader();
        let id = $(this).data('id');
        await manageEntity("items", id, await getChanges())
    });

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

});

