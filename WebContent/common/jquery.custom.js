(function($) {
	$.encodeHtml = function(html) {
		return html ? $('<div />').text(html).html() : '';
	}
	$.decodeHtml = function(str) {
		return str ? $('<div />').html(str).text() : '';
	}
	$.encodeJson = function(obj, indent) {
		return obj ? JSON.stringify(obj, null, indent) : '';
	}
	$.decodeJson = function(str) {
		return str ? JSON.parse(str) : {};
	}
})(jQuery);