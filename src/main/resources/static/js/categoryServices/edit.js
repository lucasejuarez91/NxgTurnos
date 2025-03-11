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

});

