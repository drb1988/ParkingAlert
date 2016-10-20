var qWERTY = 0;
var prevArrMarkers = '',
	prevArr = '',
	theArr = '',
	typeOfMap = false;

function makeAjaxCall() {
	var selectedDate = $("input[name=daterange]").val() ? $("input[name=daterange]").val() : false;
    console.log("selected date: "+selectedDate);
    if(selectedDate && selectedDate!="2016-01-01 01:00:00 - 2016-12-30 24:00:00") {
      var date = selectedDate.split(" - ");
      var startDateTime = date[0],
          endDateTime = date[1];
      var json = {
        "startDateTime": startDateTime, 
        "endDateTime" : endDateTime
      };
      if(polygon) {
      	json.polygon = [];
        var vertices = polygon.getPath();
        for (var i =0; i < vertices.getLength(); i++) {
          var xy = vertices.getAt(i);
          console.log("typeeeee: "+ typeof xy.lat());
          json.polygon.push([xy.lat(), xy.lng()]);
        }
      }

      if(rectangle) {
      	json.polygon = [];
      	var vertices = rectangle.getBounds();
        console.log("vertices:",vertices);
        json.polygon.push([vertices.getSouthWest().lat(), vertices.getNorthEast().lng()]);
        json.polygon.push([vertices.getNorthEast().lat(), vertices.getNorthEast().lng()]);
        json.polygon.push([vertices.getNorthEast().lat(), vertices.getSouthWest().lng()]);
        json.polygon.push([vertices.getSouthWest().lat(), vertices.getSouthWest().lng()]);

        // console.log('getNorthEast lng: ',  rectangle.getBounds().getNorthEast().lng());
        // console.log('getSouthWest lat: ',  rectangle.getBounds().getSouthWest().lat());
        // console.log('getSouthWest lng: ',  rectangle.getBounds().getSouthWest().lng());
      }
      if(circle) {
      	json.circle = {
      		center: {
      			lat: circle.getCenter().lat(),
      			lng: circle.getCenter().lng()
      		},
      		radius: circle.getRadius()/63781
      	};
      }
      // json.type = circle ? "circle" : rectangle ? "rectangle" : polygon ? "polygon" : false;
      // json.value = circle ? "crc" : rectangle ? rectangle.getBounds() : polygon ? polygon.getPath() : false;
// console.log('getNorthEast lat: ',  rectangle.getBounds().getNorthEast().lat());
        // console.log('getNorthEast lng: ',  rectangle.getBounds().getNorthEast().lng());
        // console.log('getSouthWest lat: ',  rectangle.getBounds().getSouthWest().lat());
        // console.log('getSouthWest lng: ',  rectangle.getBounds().getSouthWest().lng());
      // make ajax call to the server
      console.log("map in ajax ", json);
      $.ajax({
        async: true,
        type: "POST",
        url: "/web-routes/MapAjaxCallback", 
        data: json,
        success: function(result) {
          console.log("merge map result in ajax");
          console.log("map result", result);
          // $("#div1").text("The map has changed"); // alert user 
          // reinit(result);
          if(prevArrMarkers) {
            console.log(prevArrMarkers);
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
       },
       error: function(XMLHttpRequest, textStatus, errorThrown) { 
        alert("Status: " + textStatus); alert("Error: " + errorThrown); 
    }   
      });
    }
}