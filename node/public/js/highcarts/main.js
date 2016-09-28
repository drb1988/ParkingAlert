$(document).ready(function () {
  var chart = new Highcharts.Chart({
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
          color: Highcharts.getOptions().colors[1]
        }
      },
      title: {
        text: 'Persoane care nu au ajuns la timp.',
        style: {
          color: Highcharts.getOptions().colors[1]
        }
      }
    }, { // Secondary yAxis
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
      x: 120,
      verticalAlign: 'top',
      y: 100,
      floating: true,
      backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
    },
    series: [{
      name: 'Au ajuns',
      type: 'column',
      yAxis: 1,
      data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ,0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
      tooltip: {
        valueSuffix: ''
      }
    }, {
      name: 'Nu au ajuns',
      type: 'spline',
      data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ,0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
      tooltip: {
      valueSuffix: ''
      }
    }]
  });

  $("input#ajaxDate").change(function(){
    var selectedDate = $("input[name=daterange]").val() ? $("input[name=daterange]").val() : false;
    console.log("data daterange val: "+selectedDate);
    if(selectedDate) {
      var date = selectedDate.split(" - ");
      var startDateTime = date[0],
          endDateTime = date[1];
      var json = {
        selectedDate: selectedDate, 
        endDateTime : endDateTime
      };
      chart.setTitle({text: "Statistica din data: "+selectedDate});
      $.ajax({
        async: true,
        type: "POST",
        url: "/chart_ajax", 
        data: json,
        success: function(result) {
          chart.series[0].setData(result.positive_feedback);
          chart.series[1].setData(result.negative_feedback);
       },
       failure: function (errMsg) {
        alert(errMsg);
      }
      });
    }

    //chart.series[0].setData([129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4, 29.9, 71.5, 106.4] ); // Persoane care au ajuns la timp
    //chart.series[1].setData([129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4, 29.9, 71.5, 106.4] ); // Persoane care nu au ajuns la timp
  })

});
