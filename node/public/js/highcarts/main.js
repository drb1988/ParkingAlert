$.getScript("/./js/global-needs.js", function(){
  console.log("qWERTY: "+qWERTY);

  var chart;
$(document).ready(function () {
  chart = new Highcharts.Chart({
    chart: {
      zoomType: 'xy',
      renderTo: 'container'
    },
      title: {
      text: 'Statistica'
    },
    subtitle: {
      text: ''
    },
    xAxis: [{
      categories: ['1:00', '2:00', '3:00', '4:00', '5:00', '6:00',
      '7:00', '8:00', '9:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00',
      '17:00', '18:00', '19:00', '20:00', '21:00', '22:00', '23:00', '24:00'],
      crosshair: true
    }],
    yAxis: [{ // Primary yAxis
      labels: {
          format: '{value}',
          style: {
              color: Highcharts.getOptions().colors[2]
          }
      },
      title: {
          text: 'Notificari fara raspuns.',
          style: {
              color: Highcharts.getOptions().colors[2]
          }
      },
      opposite: true
    },
    { // Primary yAxis
      labels: {
        format: '{value}',
        style: {
          color: Highcharts.getOptions().colors[1]
        }
      },
      title: {
        text: 'Persoane care nu au ajuns la timp.',
        style: {
          color: Highcharts.getOptions().colors[1]
        }
      }
    }, 
    { // Secondary yAxis
      title: {
        text: 'Persoane care au ajuns la timp.',
        style: {
          color: Highcharts.getOptions().colors[0]
        }
      },
      labels: {
        format: '{value}',
        style: {
          color: Highcharts.getOptions().colors[0]
        }
      },
      opposite: true
    }],
    tooltip: {
      shared: true
    },
    legend: {
      layout: 'vertical',
      align: 'left',
      x: 50,
      verticalAlign: 'top',
      y: 20,
      floating: true,
      backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
    },
    series: [{
      name: 'Notificari fara raspuns',
      type: 'spline',
      yAxis: 0,
      data: [],
      tooltip: {
        valueSuffix: ''
      },
      backgroundColor: 'red'

      },{
      name: 'Nu au ajuns',
      type: 'spline',
      yAxis: 1,
      data: [],
      tooltip: {
        valueSuffix: ''
      }
      }, {
        name: 'Au ajuns',
        type: 'spline',
        data: [],
        yAxis: 2,
        tooltip: {
          valueSuffix: '',
          
        }
      }]
  });

  $("input#ajaxDate").change(function(){
    var selectedDate = $("input[name=daterange]").val() ? $("input[name=daterange]").val() : false;
    console.log("data daterange val: "+selectedDate);
    qWERTY=5;
    if(selectedDate) {
      var date = selectedDate.split(" - ");
      var startDateTime = date[0],
          endDateTime = date[1];
      var json = {
        selectedDate: selectedDate, 
        endDateTime : endDateTime
      }
      json.type = circle ? "circle" : rectangle ? "rectangle" : polygon ? "polygon" : false;
      json.value = circle ? "crc" : rectangle ? rectangle.getBounds() : polygon ? "polygon.getPath()" : false;
      // console.log("circle", circle);
      // console.log("rectangle", rectangle);
      // console.log("polygon", polygon);
      // if(circle)
      //   json.circle= {
      //     value: {
      //       radius: circle ? circle.getRadius() : null,
      //       center: {
      //         lat: circle ? circle.getCenter().lat() : null,
      //         lng: circle ? circle.getCenter().lng() : null
      //       }
      //     }
      //   }
      // if(rectangle)
      //   json.rectangle= {
      //     value: {
      //       NorthEast: {
      //         lat: rectangle ? rectangle.getBounds().getNorthEast().lat() : null,
      //         lng: rectangle ? rectangle.getBounds().getNorthEast().lng() : null
      //       },
      //       SouthWest: {
      //         lat: rectangle ? rectangle.getBounds().getSouthWest().lat() : null,
      //         lng: rectangle ? rectangle.getBounds().getSouthWest().lng() : null
      //       }
      //     }
      //   };
      // if(polygon) 
      //   json.value = polygon.getPath();
      console.log("json pentru stat",json);
      chart.setTitle({text: "Statistica pentru perioada : "+selectedDate});
      $.ajax({
        async: true,
        type: "POST",
        url: "/chart_ajax", 
        data: json,
        success: function(result) {
          chart.series[0].setData(result.positive_feedback);
          chart.series[1].setData(result.negative_feedback);
          chart.series[2].setData(result.without_feedback);
       },
       failure: function (errMsg) {
        alert(errMsg);
      }
      });
    }
  })

  $("input[name='checkbox_1']").change(function () {
    showHideSerie(0);
  });
  $("input[name='checkbox_2']").change(function () {
    showHideSerie(1);
  });
  $("input[name='checkbox_3']").change(function () {
    showHideSerie(2);
  });
  function showHideSerie(orderNumber) {
    if(chart.series[orderNumber].visible) 
      chart.series[orderNumber].hide();
    else
      chart.series[orderNumber].show();
  }

});

});