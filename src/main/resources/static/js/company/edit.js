var table;
$(document).ready(function(){

    $('input, select, textarea').on('change', function(){
        $(this).addClass('changed');
    });

    $('.btnUpdate').on('click', async function(){
        loader();
        let id = $(this).data('id');
        await manageEntity("companies", id, await getChanges())
    });

});

