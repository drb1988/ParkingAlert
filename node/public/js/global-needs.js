var qWERTY = 0;
var prevArrMarkers = '',
	prevArr = '',
	theArr = '',
	typeOfMap = false
    ;
var chart;
$("input#ajaxDate").change(function(){
  makeAjaxCall();
});
function makeAjaxCall() {
	var selectedDate = $("input[name=daterange]").val() ? $("input[name=daterange]").val() : false;
    console.log("selected date: "+selectedDate);

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
          console.log("MapAjaxCallback\n", result);
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
        // alert("Status: " + textStatus); alert("Error: " + errorThrown);
         console.log("MapAjaxCallback" + "\nStatus: " + textStatus+"\nError: " + errorThrown);
         // result = [];
         // if(circle)
         // {
         //   for(var i=0; i<=30; i++) {
         //     result.push({
         //       lat: String(parseFloat(circle.getCenter().lat()) + parseFloat((Math.random() * (0.002 - 0.001) + 0.001).toFixed(4))),
         //       lng: String(parseFloat(circle.getCenter().lng()) + parseFloat((Math.random() * (0.002 - 0.001) + 0.001).toFixed(4)))
         //     })
         //   }
         // }
         // if(prevArrMarkers) {
         //   console.log(prevArrMarkers);
         //   for (var i = 0; i < prevArrMarkers.length; i++)
         //     prevArrMarkers[i].setMap(null);
         //   prevArrMarkers= '';
         // }
         //
         // if(typeOfMap)
         //   prevArrMarkers = reinitMarker(result);
         // else
         //   reinit(result);
         //
         // if(prevArr) {
         //   prevArr = theArr;
         //   theArr = result;
         // }
         // else {
         //   prevArr = result;
         // }
         // console.log("Circle map fake result: ", result);
      }   
      });

      console.log("json pentru useri: ", json);

      $.ajax({
        async: true,
        type: "POST",
        url: "/web-routes/UsersAjaxCallback", 
        data: json,
        success: function(result) {
          console.log("UsersAjaxCallback\n", result);
          // firstNameArray = ['Ion', 'Gigel', 'Ardelean', 'Agarici', 'Borsa'];
          // lastNameNameArray = ['Ana', 'Maria', 'Alexandra', 'Diana', 'Don'];
          // ciryArray = ['Oradea', 'Cluj-Napoca', 'Arad', 'Alesd', 'Marghita'];
          // result = [];
          // for(var i=0; i<=15; i++)
          //   result.push(
          //       {
          //         first_name: firstNameArray[Math.floor(Math.random() * ciryArray.length)],
          //         last_name: lastNameNameArray[Math.floor(Math.random() * ciryArray.length)],
          //         email: firstNameArray[Math.floor(Math.random() * ciryArray.length)]+'_'+lastNameNameArray[Math.floor(Math.random() * ciryArray.length)]+'@alert.com',
          //         city: ciryArray[Math.floor(Math.random() * ciryArray.length)],
          //         is_banned: Math.random() >= 0.5
          //       }
          //   );
          // console.log("fake result: ", result);
          // console.log("merge users result in ajax", result);
          // $(function () {
            var content = '';
            for (var i = 0; i < result.length; i++) {
              content += '<tr>';
              content += '<td>' + result[i].first_name + ' ' + result[i].last_name + '</td>';
              content += '<td>' + result[i].email + '</td>';
              var displayCity = result[i].city ? result[i].city : ' ';
              content += '<td>' + displayCity + '</td>';
              var displayText = result[i].is_banned ? 'Unban' : 'Ban',
                  displayType = result[i].is_banned ? 'btn-warning' : 'btn-danger';
              content += '<td><a href="javascript: ConfirmDialog("' + result[i]._id + '", "' + result[i].is_banned + '")" class="btn btn-xs ' + displayType + '">' + displayText + '</a></td>';
              content += '</tr>';
            }
              // <a href="javascript: ConfirmDialog("5808767737a04d27c0d4a7fc", "false")" class="btn btn-xs btn-danger"></a>
            // }
            $('#dataTables-content tbody').html(content);
          // });

        },
        error: function(XMLHttpRequest, textStatus, errorThrown) { 
          // alert("Status: " + textStatus); alert("Error: " + errorThrown);
          console.log("UsersAjaxCallback" + "\nStatus: " + textStatus+"\nError: " + errorThrown);
          // firstNameArray = ['Ion', 'Gigel', 'Ardelean', 'Agarici', 'Borsa'];
          // lastNameNameArray = ['Ana', 'Maria', 'Alexandra', 'Diana', 'Don'];
          // ciryArray = ['Oradea', 'Cluj-Napoca', 'Arad', 'Alesd', 'Marghita'];
          // result = [];
          // for(var i=0; i<=15; i++)
          //   result.push(
          //       {
          //         _id: "FAKEID21",
          //         first_name: firstNameArray[Math.floor(Math.random() * ciryArray.length)],
          //         last_name: lastNameNameArray[Math.floor(Math.random() * ciryArray.length)],
          //         email: firstNameArray[Math.floor(Math.random() * ciryArray.length)]+'_'+lastNameNameArray[Math.floor(Math.random() * ciryArray.length)]+'@alert.com',
          //         city: ciryArray[Math.floor(Math.random() * ciryArray.length)],
          //         is_banned: Math.random() >= 0.5
          //       }
          //   );
          // console.log("fake result: ", result);
          // var content = '';
          // for (var i = 0; i < result.length; i++) {
          //   content += '<tr>';
          //   content += '<td>' + result[i].first_name +' '+ result[i].last_name + '</td>';
          //   content += '<td>' + result[i].email + '</td>';
          //   var displayCity = result[i].city ? result[i].city : ' ';
          //   content += '<td>' + displayCity + '</td>';
          //   var displayText= result[i].is_banned ? 'Unban' : 'Ban',
          //       displayType = result[i].is_banned ? 'btn-warning' : 'btn-danger';
          //   content += '<td><a href="javascript: ConfirmDialog("'+result[i]._id +'", "'+result[i].is_banned+'")" class="btn btn-xs '+displayType+'">'+displayText+'</a></td>';
          //   content += '</tr>';
          //
          //   // <a href="javascript: ConfirmDialog("5808767737a04d27c0d4a7fc", "false")" class="btn btn-xs btn-danger"></a>
          // }
          // $('#dataTables-content tbody').html(content);
        }   
      });

      $.ajax({
        async: true,
        type: "POST",
        url: "/web-routes/StatisticsAjaxCallback",
        data: json,
        success: function(result) {
          console.log("StatisticsAjaxCallback\n", result);
          chart.series[0].setData(result.positive_feedback);
          chart.series[1].setData(result.negative_feedback);
          chart.series[2].setData(result.no_feedback);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
          console.log("StatisticsAjaxCallback" + "\nStatus: " + textStatus+"\nError: " + errorThrown);
          // alert(errMsg);
          // var result = {
          //   positive_feedback: [],
          //   negative_feedback: [],
          //   no_feedback: []
          // };
          // for(var i=1; i<=30; i++) {
          //   result.positive_feedback.push([Date.UTC(2016, 10, i), Math.floor(Math.random() * (10 - 0 + 1)) + 0]);
          //   result.negative_feedback.push([Date.UTC(2016, 10, i), Math.floor(Math.random() * (10 - 0 + 1)) + 0]);
          //   result.no_feedback.push([Date.UTC(2016, 10, i), Math.floor(Math.random() * (10 - 0 + 1)) + 0]);
          // }
          // chart.series[0].setData(result.positive_feedback);
          // chart.series[1].setData(result.negative_feedback);
          // chart.series[2].setData(result.no_feedback);
          // console.log("StatisticsAjaxCallback without response, not my problem.");
          // console.log("Fake result", result);
        }
      });




}