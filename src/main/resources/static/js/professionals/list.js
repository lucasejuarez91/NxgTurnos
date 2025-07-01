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
        const respuesta = await modalConfirmation(i18next.t("title.modal.confirm.recover", {'entity': i18next.t("entity.professional")}));
        if (respuesta) {
            await manageEntity("professionals", id, {status: true})
        }
    });

    $('.btnDelete').on('click', async function(){
        let id = $(this).data('id');
        const respuesta = await modalConfirmation(i18next.t("title.modal.confirm.delete", {'entity': i18next.t("entity.professional")}));
        if(respuesta)
            await manageEntity("professionals", id, {status: false})
    });

    loadHtmlOnModal(document.getElementById('createBtn'));
    //loader(false);
});

