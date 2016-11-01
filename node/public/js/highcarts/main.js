$.getScript("/./js/global-needs.js", function(){
  $(function () {

      chart = new Highcharts.Chart({
        chart: {
          zoomType: 'x',
          renderTo: 'container'
        },
        title: {
          text: 'Stats'
        },
        subtitle: {
          text: document.ontouchstart === undefined ?
              'Click and drag in the plot area to zoom in' : 'Pinch the chart to zoom in'
        },
        xAxis: {
          type: 'datetime'
        },
        yAxis: [
          {
            labels: {
              format: '{value}',
              style: {
                  color: Highcharts.getOptions().colors[0]
              }
            },
            title: {
              text: 'Positive Feedback',
              style: {
                color: Highcharts.getOptions().colors[0]
              }
            }
          }, {
            labels: {
              format: '{value}',
              style: {
                color: Highcharts.getOptions().colors[1]
              }
            },
            title: {
              text: 'Negative Feedback',
              style: {
                color: Highcharts.getOptions().colors[1]
              }
            }
          }, {
            labels: {
              format: '{value}',
              style: {
                color: Highcharts.getOptions().colors[2]
              }
            },
            title: {
              text: 'No Feedback',
              style: {
                color: Highcharts.getOptions().colors[2]
              }
            }
          }
        ],
        legend: {
          enabled: false
        },
        plotOptions: {
          area: {
            fillColor: {
              linearGradient: {
                x1: 0,
                y1: 0,
                x2: 0,
                y2: 1
              },
              stops: [
                [0, Highcharts.getOptions().colors[0]],
                [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
              ]
            },
            marker: {
              radius: 2
            },
            lineWidth: 1,
            states: {
              hover: {
                lineWidth: 1
              }
            },
            threshold: null
          }
        },

        series: [
          {
            type: 'spline',
            name: 'Persons with positive feedback',
            data: [],
            backgroundColor: Highcharts.getOptions().colors[0]
          },
          {
            type: 'spline',
            name: 'Persons with negative feedback',
            data: [],
            backgroundColor: Highcharts.getOptions().colors[1]
          },
          {
            type: 'spline',
            name: 'Persons with no feedback',
            data: [],
            backgroundColor: Highcharts.getOptions().colors[2]
          }
          ]
      });
    // $.ajax({
    //   async: true,
    //   type: "POST",
    //   url: "/web-routes/StatisticsAjaxCallback",
    //   data: [],
    //   success: function(result) {
    //     console.log("aici primul raspuns la chart:::: ", result);
    //     chart.series[0].setData(result.positive_feedback);
    //     chart.series[1].setData(result.negative_feedback);
    //     chart.series[2].setData(result.no_feedback);
    //
    //   },
    //   failure: function (errMsg) {
    //     alert(errMsg);
    //   }
    // });
  });

// $(document).ready(function () {
//   chart = new Highcharts.Chart({
//     chart: {
//       zoomType: 'xy',
//       renderTo: 'container'
//     },
//       title: {
//       text: 'Statistica'
//     },
//     subtitle: {
//       text: ''
//     },
//     xAxis: [{
//       categories: ['1:00', '2:00', '3:00', '4:00', '5:00', '6:00',
//       '7:00', '8:00', '9:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00',
//       '17:00', '18:00', '19:00', '20:00', '21:00', '22:00', '23:00', '24:00'],
//       crosshair: true
//     }],
//     yAxis: [{ // Primary yAxis
//       labels: {
//           format: '{value}',
//           style: {
//               color: Highcharts.getOptions().colors[2]
//           }
//       },
//       title: {
//           text: 'Notificari fara raspuns.',
//           style: {
//               color: Highcharts.getOptions().colors[2]
//           }
//       },
//       opposite: true
//     },
//     { // Primary yAxis
//       labels: {
//         format: '{value}',
//         style: {
//           color: Highcharts.getOptions().colors[1]
//         }
//       },
//       title: {
//         text: 'Persoane care nu au ajuns la timp.',
//         style: {
//           color: Highcharts.getOptions().colors[1]
//         }
//       }
//     },
//     { // Secondary yAxis
//       title: {
//         text: 'Persoane care au ajuns la timp.',
//         style: {
//           color: Highcharts.getOptions().colors[0]
//         }
//       },
//       labels: {
//         format: '{value}',
//         style: {
//           color: Highcharts.getOptions().colors[0]
//         }
//       },
//       opposite: true
//     }],
//     tooltip: {
//       shared: true
//     },
//     legend: {
//       layout: 'vertical',
//       align: 'left',
//       x: 70,
//       verticalAlign: 'top',
//       y: 50,
//       floating: true,
//       backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
//     },
//     series: [{
//       name: 'Notificari fara raspuns',
//       type: 'spline',
//       yAxis: 0,
//       data: [],
//       tooltip: {
//         valueSuffix: ''
//       },
//       backgroundColor: 'red'
//
//       },{
//       name: 'Nu au ajuns',
//       type: 'spline',
//       yAxis: 1,
//       data: [],
//       tooltip: {
//         valueSuffix: ''
//       }
//       }, {
//         name: 'Au ajuns',
//         type: 'spline',
//         data: [],
//         yAxis: 2,
//         tooltip: {
//           valueSuffix: '',
//
//         }
//       }]
//   });

  $("input#ajaxDate").change(function(){
    var selectedDate = $("input[name=daterange]").val() ? $("input[name=daterange]").val() : false;
    if(selectedDate) {
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
          json.polygon.push([xy.lat(), xy.lng()]);
        }
      }

      if(rectangle) {
        json.polygon = [];
        var vertices = rectangle.getBounds();
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
      chart.setTitle({text: "Statistica pentru perioada : <br>"+selectedDate});
      $.ajax({
        async: true,
        type: "POST",
        url: "/web-routes/StatisticsAjaxCallback",
        data: json,
        success: function(result) {
          console.log("result stat", result);
          chart.series[0].setData(result.positive_feedback);
          chart.series[1].setData(result.negative_feedback);
          chart.series[2].setData(result.no_feedback);
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

