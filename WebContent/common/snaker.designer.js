var Designer = {
	module: {
		arrow: {
			ptype: 'path',
			pattr: {
				fill: '#000000'
			}
		},
		begin: {
			ptype: 'circle',
			pattr: {
				radius: 20,
				fill: '#00FA9A'
			},
			pdata: {
				node: true,
				clazz: 'engine.module.BeginModule',
				controller: 'module/begin',
				name: '开始'
			}
		},
		end: {
			ptype: 'circle',
			pattr: {
				radius: 20,
				fill: '#F10909'
			},
			pdata: {
				node: true,
				clazz: 'engine.module.EndModule',
				name: '结束'
			}
		},
		tableinput: {
			ptype: 'rect',
			pattr: {
				width: 40,
				height: 60,
				fill: '#999999'
			},
			pdata: {
				node: true,
				clazz: 'engine.module.TableInputModule',
				view: 'module/basiclog',
				name: '表格输入',
				dataHeaders: [],
				dataRows: []
			}
		},
		tablemapper: {
			ptype: 'rect',
			pattr: {
				width: 40,
				height: 60,
				fill: '#999999'
			},
			pdata: {
				node: true,
				clazz: 'engine.module.TableMapperModule',
				view: 'module/basiclog',
				name: '表格映射',
				dataHeaders: [],
				dataMappers: []
			}
		}
	}
};

Designer.Attr = {
	editor: {
		dataHeaders: 'data-headers',
		dataRows: 'data-rows'
	}
}

Designer.init = function(tabsId, canvasId, contentId, moveableId, canvasItemClass, propformId, propboxId, editor) {
	var $tabs = $('#' + tabsId), $canvas = $('#' + canvasId), $content = $('#' + contentId), $moveable = $('#' + moveableId), $propform = $('#' + propformId), $propbox = $('#' + propboxId);
	var paper = Raphael(canvasId, $canvas.width() - 5, $canvas.height() - 5);
	var canvasOffset = $canvas.offset();
	Designer.$tabs = $tabs;
	Designer.$canvas = $canvas;
	Designer.$content = $content;
	Designer.$moveable = $moveable;
	Designer.$propform = $propform;
	Designer.$propbox = $propbox;
	Designer.paper = paper;
	Designer.canvasOffset = canvasOffset;
	Designer.editor = editor;
	
	$tabs.tabs({
		onSelect: function(title, index) {
			if (title == $canvas.panel('options').title) {
				Designer.setData(JSON.parse($content.text()));
			} else if (title == $content.panel('options').title) {
				$content.text(JSON.stringify(Designer.getData()));
			}
		}
	})
	$('.' + canvasItemClass).draggable({
		revert: true,
		proxy: 'clone',
		onStartDrag: function(e) {
			$this = $(this);
			e.data.startLeft = $this.offset().left;
			e.data.startTop = $this.offset().top - 12;
			$this.draggable('proxy').css('position', 'fixed');
		}
	});
	$canvas.droppable({
		accept: '.' + canvasItemClass,
		onDrop: function(e, source) {
			var mtype = $(source).data('mtype');
			var ele = Designer.eleCreater(mtype);
			ele.drag(Designer.eleMove, Designer.eleStart, Designer.eleStop);
			ele.click(Designer.eleClick);
		}
	});
	$propbox.propertygrid({
		onBeginEdit: function(index, row) {
			if (editor[row.name]) {
				editor[row.name].init(index, row);
				editor[row.name].setValue(row.value);
				editor[row.name].show();
			}
		},
		onEndEdit: function(index, row) {
			Designer.PropBox.updateValue(index, row);
		}
	});
	$propform.form('load', {
		name: Designer.createName()
	})
};

Designer.Arrow = {
	rhombus: 15,
	list: [],
	current: null,
	next: function() {
		if (!this.current) {
			this.current = Designer.eleCreater('arrow', 0, 0);
			this.current.drag(Designer.eleMove, Designer.eleStart, Designer.eleStop);
			this.current.toBack();
			return this.current;
		}
	},
	refresh: function(list) {
		for (var i = 0; i < list.length; i++) {
			this.current = list[i];
			this.linkTo(list[i].from, list[i].to);
		}
		this.current = null;
	},
	remove: function() {
		if (this.current) {
			this.current.remove();
			this.current = null;
		}
	},
	moveTo: function(startX, startY, stopX, stopY) {
		this.current.attr('path', this.buildPath(startX, startY, stopX, stopY));
	},
	linkTo: function(e1, e2) {
		this.current.from = e1;
		this.current.to = e2;
		var e1Mid = this.cpoint(e1);
		var e2Mid = this.cpoint(e2);
		var start = this.intersection(e1, e1Mid, e2Mid);
		var end = this.intersection(e2, e2Mid, e1Mid);
		this.moveTo(start.x, start.y, end.x, end.y);
	},
	buildPath: function(x1, y1, x2, y2, size) {
		if (size == undefined) {
			size = 8;
		}
		if (x2 == undefined || y2 == undefined) {
			return [];
		}
		var angle = Raphael.angle(x1, y1, x2, y2);
		var a45 = Raphael.rad(angle - this.rhombus);
		var a45m = Raphael.rad(angle + this.rhombus);
		var x2a = x2 + Math.cos(a45) * size;
		var y2a = y2 + Math.sin(a45) * size;
		var x2b = x2 + Math.cos(a45m) * size;
		var y2b = y2 + Math.sin(a45m) * size;
		return ["M", x1, y1, "L", x2, y2, "L", x2a, y2a, "L", x2b, y2b, "L", x2, y2];
	},
	cpoint: function(ele) {
		if (ele.type == 'circle') {
			return {
				x: ele.attr('cx'),
				y: ele.attr('cy')
			};
		} else if (ele.type == 'rect') {
			return {
				x: ele.attr('x') + ele.attr('width') / 2,
				y: ele.attr('y') + ele.attr('height') / 2
			};
		} else {
			var bbox = ele.getBBox();
			return {
				x: bbox.cx,
				y: bbox.cy
			};
		}
	},
	intersection: function(ele, start, stop) {
		if (ele.type == 'rect') {
			var p1, p2;
			var angle = Raphael.angle(stop.x, stop.y, start.x, start.y);
			var angle1 = Raphael.angle(ele.attr('x') + ele.attr('width'), ele.attr('y'), start.x, start.y);
			var angle2 = 540 - angle1;
			var angle3 = angle1 - 180;
			var angle4 = angle2 - 180;
			if (angle <= angle1 && angle >= angle2) {
				p1 = {
					x: ele.attr('x'),
					y: ele.attr('y')
				};
				p2 = {
					x: ele.attr('x') + ele.attr('width'),
					y: ele.attr('y')
				};
			} else if (angle <= angle2 && angle >= angle3) {
				p1 = {
					x: ele.attr('x'),
					y: ele.attr('y')
				};
				p2 = {
					x: ele.attr('x'),
					y: ele.attr('y') + ele.attr('height')
				};
			} else if (angle <= angle3 && angle >= angle4) {
				p1 = {
					x: ele.attr('x'),
					y: ele.attr('y') + ele.attr('height')
				};
				p2 = {
					x: ele.attr('x') + ele.attr('width'),
					y: ele.attr('y') + ele.attr('height')
				};
			} else {
				p1 = {
					x: ele.attr('x') + ele.attr('width'),
					y: ele.attr('y')
				};
				p2 = {
					x: ele.attr('x') + ele.attr('width'),
					y: ele.attr('y') + ele.attr('height')
				};
			}
			return this.overlap(p1, p2, start, stop);
		} else if (ele.type == 'circle') {
			var angle = Raphael.angle(stop.x, stop.y, start.x, start.y);
			var rad = Raphael.rad(angle);
			return {
				x: start.x + Math.cos(rad) * ele.attrs.r,
				y: start.y + Math.sin(rad) * ele.attrs.r
			};
		} else {
			var p1, p2;
			var angle = Raphael.angle(stop.x, stop.y, start.x, start.y);
			if (angle <= 360 && angle >= 270) {
				p1 = {
					x: start.x,
					y: start.y - this.rhombus
				};
				p2 = {
					x: start.x + this.rhombus * 2,
					y: start.y
				};
			} else if (angle <= 270 && angle >= 180) {
				p1 = {
					x: start.x,
					y: start.y - this.rhombus
				};
				p2 = {
					x: start.x - this.rhombus * 2,
					y: start.y
				};
			} else if (angle <= 180 && angle >= 90) {
				p1 = {
					x: start.x,
					y: start.y + this.rhombus
				};
				p2 = {
					x: start.x - this.rhombus * 2,
					y: start.y
				};
			} else {
				p1 = {
					x: start.x,
					y: start.y + this.rhombus
				};
				p2 = {
					x: start.x + this.rhombus * 2,
					y: start.y
				};
			}
			return this.overlap(p1, p2, start, stop);
		}
	},
	overlap: function(a, b, c, d) {
		var denominator = (b.y - a.y) * (d.x - c.x) - (a.x - b.x) * (c.y - d.y);
		var x = ((b.x - a.x) * (d.x - c.x) * (c.y - a.y) + (b.y - a.y) * (d.x - c.x) * a.x - (d.y - c.y) * (b.x - a.x) * c.x) / denominator;
		var y = -((b.y - a.y) * (d.y - c.y) * (c.x - a.x) + (b.x - a.x) * (d.y - c.y) * a.y - (d.x - c.x) * (b.y - a.y) * c.y) / denominator;
		return {
			x: x,
			y: y
		};
	}
}

Designer.createName = function () {
	var now = new Date(),
	year = now.getFullYear(),
	month = now.getMonth() + 1,
	day = now.getDate(),
	hour = now.getHours(),
	minute = now.getMinutes(),
	second = now.getSeconds();
	return '' +
		(year) +
		(month < 10 ? '0' + month : month) +
		(day < 10 ? '0' + day : day) +
		(hour < 10 ? '0' + hour : hour) +
		(minute < 10 ? '0' + minute : minute) +
		(second < 10 ? '0' + second : second);
}

Designer.eleCreater = function(mtype, x, y, data) {
	var paper = Designer.paper, Arrow = Designer.Arrow, canvasOffset = Designer.canvasOffset;
	if (x == undefined) {
		var ex = window.event.clientX;
		x = ex - canvasOffset.left;
	}
	if (y == undefined) {
		var ey = window.event.clientY;
		y = ey - canvasOffset.top;
	}
	
	var cfg = $.extend(true, {
		pattr: {
			'stroke-width': 2,
			cursor: 'pointer'
		},
		pdata: {
			mtype: mtype
		}
	}, Designer.module[mtype]);

	if (data != undefined) {
		for (var key in cfg.pdata) {
			if (data.hasOwnProperty(key)) {
				cfg.pdata[key] = data[key];
			}
		}
	} else {
		if (cfg.pdata.node) {
			var mname = null;
			while (true) {
				if (Designer.counter[mtype] == undefined) {
					Designer.counter[mtype] = 1;
				} else {
					Designer.counter[mtype] += 1;
				}
				mname = cfg.pdata.name + Designer.counter[mtype];
				if (Designer.name[mname] == undefined) {
					break;
				}
			}
			cfg.pdata['name'] = mname;
			Designer.name[mname] = true;
		}
	}
	
	var ele = null;
	if (cfg.ptype == 'circle') {
		ele = paper.circle(x, y, cfg.pattr.radius).attr(cfg.pattr).data(cfg.pdata);
		ele.arrow = {};
	} else if (cfg.ptype == 'rect') {
		ele = paper.rect(x, y, cfg.pattr.width, cfg.pattr.height).attr(cfg.pattr).data(cfg.pdata);
		ele.arrow = {};
	} else if (cfg.ptype == 'path') {
		ele = paper.path(Arrow.buildPath(x, y)).attr(cfg.pattr).data(cfg.pdata);
	}
	return ele;
}

Designer.eleMove = function(dx, dy, ex, ey, event) {
	var $moveable = Designer.$moveable, paper = Designer.paper, Arrow = Designer.Arrow, canvasOffset = Designer.canvasOffset;
	if (!$moveable.prop('checked')) {
		var ele = paper.getElementByPoint(ex, ey);
		if (ele != null && ele != this && ele.data('node')) {
			Arrow.linkTo(this, ele);
		} else {
			Arrow.moveTo(this.startCX, this.startCY, ex - canvasOffset.left, ey - canvasOffset.top);
		}
		return;
	}
	if (this.type == 'circle') {
		this.attr('cx', this.startCX + dx);
		this.attr('cy', this.startCY + dy);
	} else if (this.type == 'rect') {
		this.attr('x', this.startX + dx);
		this.attr('y', this.startY + dy);
	}
	if ($moveable.prop('checked')) {
		Arrow.refresh($.map(this.arrow, function(v) {
			return v
		}));
	}
}

Designer.eleStart = function(x, y, event) {
	var $moveable = Designer.$moveable, Arrow = Designer.Arrow;
	if (this.type == 'circle') {
		this.startCX = this.attr('cx');
		this.startCY = this.attr('cy');
	} else if (this.type == 'rect') {
		this.startX = this.attr('x');
		this.startY = this.attr('y');
		this.startCX = this.attr('x') + this.attr('width') / 2;
		this.startCY = this.attr('y') + this.attr('height') / 2;
	}
	if (!$moveable.prop('checked')) {
		Arrow.next();
	}
}

Designer.eleStop = function(event) {
	var $moveable = Designer.$moveable, paper = Designer.paper, Arrow = Designer.Arrow;
	var ex = window.event.clientX, ey = window.event.clientY;
	if (!$moveable.prop('checked')) {
		var ele = paper.getElementByPoint(ex, ey);
		if (ele != null && ele != this && ele.data('node')) {
			var arrow = Arrow.current;
			if (arrow != null) {
				var exist = false;
				for (var key in arrow.from.arrow) {
					var ar = arrow.from.arrow[key];
					if ((ar.from == arrow.from && ar.to == arrow.to) || (ar.from == arrow.to && ar.to == arrow.from)) {
						exist = true;
						break;
					}
				}
				if (!exist) {
					Arrow.current = null;
					arrow.from.arrow[arrow.id] = arrow;
					arrow.to.arrow[arrow.id] = arrow;
					return;
				}
			}
		}
		Arrow.remove();
	}
}

Designer.eleClick = function(event) {
	var PropBox = Designer.PropBox;
	if (this != PropBox.current && this.data('node')) {
		PropBox.current = this;
		PropBox.setValues(this.data());
	}
}

Designer.PropBox = {
	current: null,
	setValues: function(values) {
		var $propbox = Designer.$propbox;
		if (values) {
			var props = [];
			for (var key in values) {
				if (key == 'node') continue;
				props.push({
					name: key,
					value: values[key],
					editor: 'text'
				});
			}
			$propbox.propertygrid({
				data: props
			});
			return props;
		}
	},
	updateValue: function(index, row, refresh) {
		var $propbox = Designer.$propbox;
		var current = Designer.PropBox.current;
		if (current) {
			if (row.name == 'name') {
				if (Designer.name[row.value] != undefined) {
					for (var i = 2; true; i++) {
						if (Designer.name[row.value + i] == undefined) {
							row.value = row.value + i;
							break;
						}
					}
				}
			}
			current.data(row.name, row.value);
			if (refresh) {
				$propbox.propertygrid('updateRow', {index: index, row: row})
			}
		}
	}
}

Designer.counter = {}
Designer.name = {}

Designer.getData = function() {
	var data = {modules: []}, $propform = Designer.$propform, paper = Designer.paper;
	
	$propform.find('input[type=hidden]').each(function(index, el) {
		data[el.name] = el.value;
	});
	
	paper.forEach(function(ele) {
		if (ele.data('node')) {
			var module = {xy: [], tos: []};
			
			if (ele.type == 'circle') {
				module.xy.push(ele.attr('cx'));
				module.xy.push(ele.attr('cy'));
			} else if (ele.type == 'rect') {
				module.xy.push(ele.attr('x'));
				module.xy.push(ele.attr('y'));
			}
			
			$.extend(true, module, ele.data());
			if (ele.arrow) {
				for (var key in ele.arrow) {
					var arrow = ele.arrow[key];
					if (arrow.to != ele) {
						module.tos.push(arrow.to.data('name'));
					}
				}
			}
			data.modules.push(module);
		}
	});
	return data;
}

Designer.setData = function(data) {
	var $propform = Designer.$propform, paper = Designer.paper, Arrow = Designer.Arrow;
	var modules = data.modules || [], moduleMap = {}, arrowMap = {};
	delete data.modules;
	Designer.clear();
	$propform.form('load', data);
	for (var i = 0; i < modules.length; i++) {
		var module = modules[i];
		var ele = Designer.eleCreater(module.mtype, module.xy[0], module.xy[1], module);
		ele.drag(Designer.eleMove, Designer.eleStart, Designer.eleStop);
		ele.click(Designer.eleClick);
		moduleMap[module.name] = ele;
		arrowMap[module.name] = module.tos;
	}
	for (var key in arrowMap) {
		var module = moduleMap[key], tos = arrowMap[key];
		if (module != undefined && tos != undefined) {
			for (var i = 0; i < tos.length; i++) {
				var module2 = moduleMap[tos[i]]
				if (module2 != undefined) {
					arrow = Arrow.next();
					Arrow.linkTo(module, module2);
					module.arrow[arrow.id] = arrow;
					module2.arrow[arrow.id] = arrow;
					Arrow.current = null;
				}
			}
		}
	}
}

Designer.clear = function() {
	var paper = Designer.paper;
	paper.clear();
}