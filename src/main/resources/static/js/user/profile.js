$(document).ready(function(){

    $('input, select, textarea').on('change', function(){
        $(this).addClass('changed');
    });

    $('#btnSaveUserData').on('click', async function(){
        loader();
        let id = $(this).data('id');
        await manageEntity("users", id, await getChanges())
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

    $('#btnChangePassword').on('click', async function(){
        loader();
        let id = $(this).data('id');
        if(newPassword.value !== renewPassword.value){
            await mixinAlert("error", "Las contraseñas no coinciden. Verifique");
            return;
        }
        await manageEntity("User", id, {currentPassword: currentPassword.value, newPassword: newPassword.value})
    });

});