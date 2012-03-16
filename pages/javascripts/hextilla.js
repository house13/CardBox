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

    // Fade out errors and status messages
    var timeoutMs = 5000;
    if ($("#top")[0]) {
        setTimeout(function() {
            $("#top").fadeOut();
        }, timeoutMs);
    }

	// Setup event handler for window resize to call resize() above
	$(window).on("resize", function() {
		resize();
	});

	// Circle navigation links when clicked on
	$("nav ul li a").click(function(){
		$("nav ul li a").removeClass("selected");
		$(this).addClass("selected");
	});

    $("#delete-data").submit(function() {
        if (confirm("Are you sure you want me to forget your account data?")) {
             return true;
        }
        else {
            // Don't submit form
            return false;
        }
    });
});
