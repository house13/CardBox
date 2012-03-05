$(function() {
	var game = $("#game"),
		resize = function() {
			game.width($(window).outerWidth() - 40)
			game.height($(window).outerHeight() - 20);
		};

	// Resize the applet on page launch
	resize();

	// Setup event handler for window resize to call resize() above
	$(window).on("resize", function() {
		resize();
	});

	// Circle navigation links when clicked on
	$("nav ul li a").click(function(){
		$("nav ul li a").removeClass("selected");
		$(this).addClass("selected");
	});
});