$(document).ready(function() {
  togleIsDaily();
  togleIsMultiple();
  initDatePicker();
  initTinyMCE();
});



function togleIsDaily() {
  $('.js_isDaily').on('click', function(){
    console.log('test');
    $('#js_daily-start-date').toggleClass('hidden');
  });
}
function togleIsMultiple() {
  $('.js_isMultiple').on('click', function(){
    console.log('test is multiple');
    $('#js_multiple').toggleClass('hidden');
  });
}

function initDatePicker() {
  $("#datepicker").datepicker({
    dateFormat: "yy-mm-dd"
  });
  $("#datepickerStart").datepicker({
    dateFormat: "yy-mm-dd"
  });
  $("#datepickerEnd").datepicker({
    dateFormat: "yy-mm-dd"
  });
}

function initTinyMCE() {
  tinymce.init({
    selector: '.tiniymce',
    height: 500,
    plugins: [
      "advlist autolink lists link image charmap print preview anchor",
      "searchreplace visualblocks code fullscreen",
      "insertdatetime media table contextmenu paste imagetools"
    ],
    toolbar: "insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image",
    imagetools_cors_hosts: ['www.tinymce.com', 'codepen.io'],
    content_css: [
      '//fast.fonts.net/cssapi/e6dc9b99-64fe-4292-ad98-6974f93cd2a2.css',
      '//www.tinymce.com/css/codepen.min.css'
    ]
  });
}
