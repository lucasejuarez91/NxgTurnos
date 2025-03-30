$(document).ready(function(){
   console.log("Init calendarModalCreate functions!");
    $('#headerModalCreate').on('shown.bs.modal',function(){
        $('#headerModalCreate #professional').on('select2:select', function(){
            let professional = $('#professional').select2('data')[0].id.split('/')[2];
            let min = $('#headerModalCreate #companyLbl').data().min;
            let max = $('#headerModalCreate #companyLbl').data().max;
            initCalendar(professional, 'calendar', min, max);
        });
    });
});