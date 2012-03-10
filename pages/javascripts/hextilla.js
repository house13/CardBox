$(function() {
	var game = $("#game"),
		originalGame = {
			width: game.width(),
			height: game.height()
		},
		resize = function() {
			var adjusted = false;
			if ($(window).outerWidth() < originalGame.width) {
				game.width($(window).outerWidth() - 20);
				adjusted = true;
			}
			if ($(window).outerHeight() < originalGame.height) {
				game.height($(window).outerHeight() - 20);
				adjusted = true;
			}
			if (!adjusted) {
				if (game.width() !== originalGame.width || game.height() !== originalGame.height) {
					game.width(originalGame.width);
					game.height(originalGame.height);
				}
			}
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
