var table;
$(document).ready(function(){
    //loader();
    //initDataTable('tblAppts');
    var groupColumn = 0;
    var table = $('#tblAppts').DataTable({
        columnDefs: [{ visible: false, targets: groupColumn }],
        order: [[groupColumn, 'asc']],
        displayLength: 25,
        drawCallback: function (settings) {
            var api = this.api();
            var rows = api.rows({ page: 'current' }).nodes();
            var last = null;

            api.column(groupColumn, { page: 'current' })
                .data()
                .each(function (group, i) {
                    if (last !== group) {
                        $(rows)
                            .eq(i)
                            .before(
                                '<tr class="group"><td colspan="5">' +
                                group +
                                '</td></tr>'
                            );

                        last = group;
                    }
                });
        }
    });

    $('.btnEdit').on('click', function(){
        window.location.href = $(this).data('url');
    });

    $('.btnDelete').on('click', async function(){
        let id = $(this).data('id');
        const respuesta = await modalConfirmation(`¿Está seguro de cancelar el turno? <br><strong>Se le enviará al cliente una notificación.</strong>`);
        if(respuesta)
            await manageEntity("appointments", id, {status: false})
    });
});

