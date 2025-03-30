$(document).ready(async function(){
    $('input, select').on('change', function(){
        $(this).addClass('changed');
    });

    $('.btnUpdate').on('click', async function(){
        loader();
        let id = $(this).data('id');
        await manageEntity("professionals", id, await getChanges())
    });

    $('#specialities').select2({theme: 'bootstrap-5'});
})