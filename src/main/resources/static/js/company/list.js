var table;
$(document).ready(function(){
    //loader();
    initDataTable('tblCompany');

    $('.btnEdit').on('click', function(){
        window.location.href = $(this).data('url');
    });

    $('.btnChangeStatus').on('click', async function(){
        let id = $(this).data('id');
        const respuesta = await modalConfirmation(i18next.t("title.modal.confirm.recover", {'entity': i18next.t("entity.lounge")}));
        if (respuesta) {
            await manageEntity("companies", id, {status: true})
        }
    });

    $('.btnDelete').on('click', async function(){
        let id = $(this).data('id');
        const respuesta = await modalConfirmation(i18next.t("title.modal.confirm.delete", {'entity': i18next.t("entity.lounge")}));
        if(respuesta)
            await manageEntity("companies", id, {status: false})
    });
});

