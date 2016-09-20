$(document).ready(function() {
  togleIsDaily();
  togleIsMultiple();
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

