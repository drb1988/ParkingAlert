var quickSelect = false; 

$( ".map-cosen" ).click(function() {
	console.log("merge");
	$(this).addClass(' active');
	$( ".chart-cosen" ).removeClass(' active');
	$( ".chart-container" ).addClass(' content-hidden hidden');
	$( "#map-content" ).removeClass(' content-hidden hidden');
});

$( ".chart-cosen" ).click(function() {
	console.log("merge .chart-cosen");
	$(this).addClass(' active');
	$( ".map-cosen" ).removeClass(' active');
	$( ".chart-container" ).removeClass(' content-hidden hidden');
	$( "#map-content" ).addClass(' content-hidden hidden');
});

$( "input[name='daterange']" ).change(function() {
  // Check input( $( this ).val() ) for validity here
  console.log($( "input[name='daterange']" ).val());
  });
  $(function() {
  $('input[name="daterange"]').daterangepicker({
  timePicker: true,
  timePickerIncrement: 30,
  locale: {
  format: 'YYYY-MM-DD HH:mm:00'
  },
  ranges: {
  'Azi': [moment(), moment()],
  'Ieri': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
  'Ultimele 7 zile': [moment().subtract(6, 'days'), moment()],
  'Ultimele 30 de zile': [moment().subtract(29, 'days'), moment()],
  'Aceasta luna': [moment().startOf('month'), moment().endOf('month')],
  'Luna trecuta': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
  }
  });
  $(window).scroll(function() {
  if ($('input[name="daterange"]').length) {
  $('input[name="daterange"]').daterangepicker("close");
  }
  });
  });