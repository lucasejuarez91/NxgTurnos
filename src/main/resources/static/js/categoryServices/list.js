var table;
$(document).ready(function(){
    //loader();
    initDataTable('tblCategoryServices');

    loadHtmlOnModal(document.getElementById('createBtn'));

    $('.btnEdit').on('click', function(){
        window.location.href = $(this).data('url');
    });

    $('.btnChangeStatus').on('click', async function(){
        let id = $(this).data('id');
        const respuesta = await modalConfirmation(`¿Está seguro de recuperar esta Categoría eliminada?`);
        if (respuesta) {
            await manageEntity("categoryServices", id, {status: true})
        }
    });

    $('.btnDelete').on('click', async function(){
        let id = $(this).data('id');
        const respuesta = await modalConfirmation(`¿Está seguro de eliminar esta Categoria?`);
        if(respuesta)
            await manageEntity("categoryServices", id, {status: false})
    });

});

