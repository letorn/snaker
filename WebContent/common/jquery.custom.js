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
	if ($.parser) {
		$.extend($.fn.validatebox.defaults.rules, {
			headcount: {
				validator: function(value, param) {
					return (/^\d+$/.test(value) || value == -1) && value != 0;
				},
				message: '请输入招聘人数，如：5'
			},
			age: {
				validator: function(value, param) {
					return (/^\d+-\d+$/.test(value) || value == -1) && value.length <= 6;
				},
				message: '请输入年龄段，如：20-25'
			},
			salary: {
				validator: function(value, param) {
					return /^\d+$/.test(value) || /^\d+-\d+$/.test(value) || value == -1;
				},
				message: '请输入薪资范围或固定薪资，如：8000-9999或9999'
			}
		});
	}
})(jQuery);