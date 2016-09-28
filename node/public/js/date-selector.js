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