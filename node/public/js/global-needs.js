var qWERTY = 0;
var prevArrMarkers = '',
	prevArr = '',
	theArr = '',
	typeOfMap = false
    ;
var chart;
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
      console.log("json pentru useri: ", json);
      $.ajax({
        async: true,
        type: "POST",
        url: "/web-routes/UsersAjaxCallback", 
        data: json,
        success: function(result) {
          console.log("merge users result in ajax", result);
          $(function () {
            var content = '';
            for (var i = 0; i < result.length; i++) {
              content += '<tr>';
              content += '<td>' + result[i].first_name +' '+ result[i].last_name + '</td>';
              content += '<td>' + result[i].email + '</td>';
              var displayCity = result[i].city ? result[i].city : ' ';
              content += '<td>' + displayCity + '</td>';
              var displayText= result[i].is_banned ? 'Unban' : 'Ban',
                  displayType = result[i].is_banned ? 'btn-warning' : 'btn-danger';
              content += '<td><a href="javascript: ConfirmDialog("'+result[i]._id +'", "'+result[i].is_banned+'")" class="btn btn-xs '+displayType+'">'+displayText+'</a></td>';
              content += '</tr>';

              // <a href="javascript: ConfirmDialog("5808767737a04d27c0d4a7fc", "false")" class="btn btn-xs btn-danger"></a>
            }
            $('#dataTables-content tbody').html(content);
          });

        },
        error: function(XMLHttpRequest, textStatus, errorThrown) { 
          alert("Status: " + textStatus); alert("Error: " + errorThrown); 
        }   
      });
    }
  $('#SaveFigure').click(function(){
    console.log("save figure ");
    if(!polygon && !circle && !rectangle)
      alert("You need to draw a shape to save!");
    else {
      var json = {
        type: null,
        lat: null,
        lon: null,
        rad: null,
        polygonPoints: []
      };
      if(polygon) {
        json.type = 'polygon';
        var vertices = polygon.getPath();
        for (var i =0; i < vertices.getLength(); i++) {
          var xy = vertices.getAt(i);
          json.polygonPoints.push([xy.lat(), xy.lng()]);
        }
      }

      if(rectangle) {
        json.type = 'polygon';
        json.polygon = [];
        var vertices = rectangle.getBounds();
        json.polygonPoints.push([vertices.getSouthWest().lat(), vertices.getNorthEast().lng()]);
        json.polygonPoints.push([vertices.getNorthEast().lat(), vertices.getNorthEast().lng()]);
        json.polygonPoints.push([vertices.getNorthEast().lat(), vertices.getSouthWest().lng()]);
        json.polygonPoints.push([vertices.getSouthWest().lat(), vertices.getSouthWest().lng()]);
      }
      if(circle) {
        json.type = 'circle';
        json.lat = circle.getCenter().lat();
        json.log = circle.getCenter().lng();
        json.rad = circle.getRadius()/63781;
      }

      $.ajax({
        async: true,
        type: "POST",
        url: "/web-routes/SaveShape",
        data: json,
        success: function (result) {
          alert("The shape was set.");
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
          // alert("Status: " + textStatus);
          // alert("Error: " + errorThrown);
          alert("Something wrong happened. Please try again.");
        }
      });
    }
  })

}