$(document).ready(function() {
  togleIsDaily();
  togleIsMultiple();
  activeAjaxMap();
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

function activeAjaxMap()  {
  var prevArrMarkers = '';
  var theArrMarkers = '';
  var prevArr = '';
  var theArr = '';
  var typeOfMap = false;
  $( "#reinitMarker" ).click(function() {
    console.log("clisk");
    console.log(typeOfMap);
    typeOfMap = !typeOfMap;
    if(typeOfMap){
      if(prevArrMarkers) clearMarkerMap(prevArrMarkers);
      if(theArr && prevArr)
        reinitMarker(theArr);
      else
        reinitMarker(prevArr);
    }
    else
      if(theArr && theArr)
        reinit(theArr);
      else
        reinit(prevArr);
  });

  $("input#ajaxDate").change(function(){
  var selectedDate = $("input[name=date]").val() ? $("input[name=date]").val() : false;
  var selectedStartHour = $("input[name=start_hour]").val() ? $("input[name=start_hour]").val() : false;
  var selectedEndHour = $("input[name=end_hour]").val() ? $("input[name=end_hour]").val() : false;
  if(selectedDate && selectedStartHour && selectedEndHour) {
  var data0 = {date: selectedDate, start_hour : selectedStartHour, end_hour: selectedEndHour};
  json = data0;//JSON.stringify(data0 ); 
  console.log(json);

  $.ajax({
  async: true,
  type: "POST",
  url: "/heatmaps_ajax_map", 
  data: json,
  success: function(result){
  $("#div1").text("The map has changed");
  if(typeOfMap)
    if(prevArrMarkers) {
      prevArrMarkers = theArrMarkers;
      theArrMarkers = reinitMarker(result)
    }
    else
      prevArrMarkers = reinitMarker(result);
  else
    if(prevArr) {
      prevArr = theArr;
      reinit(result);
      theArr = result;
    }
    else {
      prevArr = result;
      reinit(result);
    }
  }});
  }
  });
}
