$(document).ready(function() {
  activeAjaxMap();
  activeAjaxChart();
});

function activeAjaxMap()  {
  var prevArrMarkers = '',
      prevArr = '',
      theArr = '',
      typeOfMap = false;

  $( "#reinitMarker" ).click(function() {
    typeOfMap = !typeOfMap;

    if(prevArrMarkers) 
      for (var i = 0; i < prevArrMarkers.length; i++) 
        prevArrMarkers[i].setMap(null);
  
    if(typeOfMap) prevArrMarkers = theArr==''? reinitMarker(prevArr) : reinitMarker(theArr);
    else
      if(theArr && prevArr) reinit(theArr);
      else reinit(prevArr);

  });

  $("select#ajaxDate, input#ajaxDate").change(function(){
    console.log('changed');
    var selectedDate = $("input[name=date]").val() ? $("input[name=date]").val() : false,
        selectedStartHour = $("select[name=start_hour]").val() ? $("select[name=start_hour]").val() : false,
        selectedEndHour = $("select[name=end_hour]").val() ? $("select[name=end_hour]").val() : false;

    if(selectedDate && selectedStartHour && selectedEndHour) {
      var json = {
        date: selectedDate, 
        start_hour : selectedStartHour, 
        end_hour: selectedEndHour
      };
      // make ajax call to the server
      $.ajax({
        async: true,
        type: "POST",
        url: "/heatmaps_ajax_map", 
        data: json,
        success: function(result) {
          $("#div1").text("The map has changed"); // alert user 

          if(prevArrMarkers) {
            for (var i = 0; i < prevArrMarkers.length; i++) 
              prevArrMarkers[i].setMap(null);
            prevArrMarkers= '';
          }

          if(typeOfMap)
            prevArrMarkers = reinitMarker(result);
          else
            reinit(result);

          if(prevArr) {
            prevArr = theArr;
            theArr = result;
          }
          else {
            prevArr = result;
          }
       }
      });
    }
  });
}

function activeAjaxChart()  {
  
}