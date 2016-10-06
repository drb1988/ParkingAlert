$.getScript("/./js/global-needs.js", function(){
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

  $("input#ajaxDate").change(function(){
    console.log('changed');
    var selectedDate = $("input[name=daterange]").val() ? $("input[name=daterange]").val() : false;
    console.log("selected date: "+selectedDate);
    if(selectedDate && selectedDate!="2016-01-01 01:00:00 - 2016-12-30 24:00:00") {
      var date = selectedDate.split(" - ");
      var startDateTime = date[0],
          endDateTime = date[1];
      var json = {
        selectedDate: selectedDate, 
        endDateTime : endDateTime,
        circle: {
          is_defined: circle ? true : false,
          value: {
            radius: circle ? circle.getRadius() : null,
            center: {
              lat: circle ? circle.getCenter().lat() : null,
              lng: circle ? circle.getCenter().lng() : null
            }
          }
        },
        rectangle: {
          is_defined: rectangle ? true : false,
          value: {
            NorthEast: {
              lat: rectangle ? rectangle.getBounds().getNorthEast().lat() : null,
              lng: rectangle ? rectangle.getBounds().getNorthEast().lng() : null
            },
            SouthWest: {
              lat: rectangle ? rectangle.getBounds().getSouthWest().lat() : null,
              lng: rectangle ? rectangle.getBounds().getSouthWest().lng() : null
            }
          }
        }
      };
      // make ajax call to the server
      console.log("map in ajax ",json);
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
});
function activeAjaxChart()  {
  
}