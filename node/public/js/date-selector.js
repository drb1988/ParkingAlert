var quickSelect = false; 

$( ".map-cosen" ).click(function() {
	console.log("merge");
	$(this).addClass(' active');
	$( ".chart-cosen" ).removeClass(' active');
  $( ".users-cosen" ).removeClass(' active');
	// $( ".chart-container" ).addClass(' content-hidden hidden');
	// $( "#map-content" ).removeClass(' content-hidden hidden');
  $( ".chart-container" ).fadeOut(1000);
  $( "#map-content" ).fadeIn(1000);
});

$( ".chart-cosen" ).click(function() {
	console.log("merge .chart-cosen");
	$(this).addClass(' active');
	$( ".map-cosen" ).removeClass(' active');
  $( ".users-cosen" ).removeClass(' active');

  $( "#map-content" ).fadeOut();
  $( ".chart-container" ).removeClass(' content-hidden');
  $( ".chart-container" ).fadeIn(1000);
	// $( ".chart-container" ).removeClass(' content-hidden hidden');
	// $( "#map-content" ).addClass(' content-hidden hidden');
});

$( ".users-cosen" ).click(function() {
  console.log("merge");
  $(this).addClass(' active');
  $( ".chart-cosen" ).removeClass(' active');
  $( ".map-cosen" ).removeClass(' active');
  // $( ".chart-container" ).addClass(' content-hidden hidden');
  // $( "#map-content" ).removeClass(' content-hidden hidden');
  $( ".chart-container" ).fadeOut(1000);
  $( "#map-content" ).fadeOut(1000);
});


$( "input[name='daterange']" ).change(function() {
  // Check input( $( this ).val() ) for validity here
  console.log($( "input[name='daterange']" ).val());
  });
  $(function() {
  $('input[name="daterange"]').change(function(){
    console.log("daterange changed");
    makeAjaxCall();
  });
  $('input[name="daterange"]').daterangepicker({
  timePicker: true,
  timePickerIncrement: 30,
  locale: {
  format: 'YYYY-MM-DD HH:mm:00'
  },
  ranges: {
  'Today': [moment().subtract(1, 'days'), moment()],
  'Yesterday': [moment().subtract(2, 'days'), moment().subtract(1, 'days')],
  'The last 7 days': [moment().subtract(6, 'days'), moment()],
  'Last 30 days': [moment().subtract(29, 'days'), moment()],
  'This month': [moment().startOf('month'), moment().endOf('month')],
  'Last month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
  }
  });
  $(window).scroll(function() {
  if ($('input[name="daterange"]').length) {
  $('input[name="daterange"]').daterangepicker("close");
  }
  });
  });


