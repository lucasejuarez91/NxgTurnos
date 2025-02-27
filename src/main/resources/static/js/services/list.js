var table;
$(document).ready(function(){
    //loader();
    initDataTable('tblServices');

    loadHtmlOnModal(document.getElementById('createServicesBtn'));
    //loader(false);

    $('#modalCreate').on('shown.bs.modal', function() {

    });
});

