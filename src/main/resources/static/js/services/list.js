var table;
$(document).ready(function(){
    //loader();
    initDataTable('tblServices');

    loadHtmlOnModal(document.getElementById('createBtn'));

    $('.btnEdit').on('click', function(){
        window.location.href = $(this).data('url');
    });

    $('.btnAvailability').on('click', function(){
        window.location.href = $(this).data('url');
    });

    $('.btnChangeStatus').on('click', async function(){
        let id = $(this).data('id');
        const respuesta = await modalConfirmation(i18next.t("title.modal.confirm.recover", {'entity': i18next.t("entity.service")}));
        if (respuesta) {
            await manageEntity("services", id, {status: true})
        }
    });

    $('.btnDelete').on('click', async function(){
        let id = $(this).data('id');
        const respuesta = await modalConfirmation(i18next.t("title.modal.confirm.delete", {'entity': i18next.t("entity.service")}));
        if(respuesta)
            await manageEntity("services", id, {status: false})
    });

});

