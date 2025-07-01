var table;
$(document).ready(function(){

    //initDataTable('tblProfessionals');
    var companyId = $('.btnUpdate').data('id');

    $('input, select, textarea').on('change', function(){
        $(this).addClass('changed');
    });

    $('.btnUpdate').on('click', async function(){
        loader();
        let id = $(this).data('id');
        await manageEntity("companies", id, await getChanges())
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

function reloadAddressFrame(){
    let address = $('#address').val();
    $('.gmap_iframe').attr('src', 'https://maps.google.com/maps?width=600&height=400&hl=en&q='+address+'&t=&z=17&ie=UTF8&iwloc=B&output=embed')
}

