(function($) {
	$.encodeHtml = function(value) {
		return value ? $('<div />').text(value).html() : '';
	}
	$.decodeHtml = function(value) {
		return value ? $('<div />').html(value).text() : '';
	}
})(jQuery);