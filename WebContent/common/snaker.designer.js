var Snaker = {};

Snaker.initDesigner = function(canvasId, moveableId, canvasItemClass, propboxId) {
	var $canvas = $('#' + canvasId), $moveable = $('#' + moveableId), $propbox = $('#' + propboxId), paper = Raphael(canvasId, $canvas.width() - 5, $canvas.height() - 5), canvasOffset = $canvas.offset();
	var Arrow = {
		rhombus: 15,
		list: [],
		current: null,
		next: function() {
			if (!this.current) {
				this.current = eleCreater('arrow', 0, 0);
				this.current.drag(eleMove, eleStart, eleStop);
				this.current.toBack();
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
	
	var Prop = {
		current: null,
		values: function(values) {
			console.log(1);
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
		}
	}
	
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
			var module = $(source).data('module');
			var ele = eleCreater(module);
			ele.drag(eleMove, eleStart, eleStop);
			ele.click(eleClick);
		}
	});

	function eleCreater(module, x, y) {
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
				module: module
			}
		}, Snaker.module[module]);

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

	function eleMove(dx, dy, ex, ey, event) {
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

	function eleStart(x, y, event) {
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

	function eleStop(event) {
		var ex = window.event.clientX, ey = window.event.clientY;
		if (!$moveable.prop('checked')) {
			var ele = paper.getElementByPoint(ex, ey);
			if (ele != null && ele != this && ele.data('node')) {
				var arrow = Arrow.current;
				if (arrow != null) {
					Arrow.current = null;
					arrow.from.arrow[arrow.id] = arrow;
					arrow.to.arrow[arrow.id] = arrow;
				}
			} else {
				Arrow.remove();
			}
		}
	}

	function eleClick(event) {
		if (this != Prop.current && this.data('node')) {
			Prop.current = this;
			
			Prop.values(this.data());
		}
	}
};

Snaker.module = {
	arrow: {
		ptype: 'path',
		pattr: {
			fill: '#000000'
		}
	},
	start: {
		ptype: 'circle',
		pattr: {
			radius: 20,
			fill: '#00FA9A'
		},
		pdata: {
			node: true
		}
	},
	stop: {
		ptype: 'circle',
		pattr: {
			radius: 20,
			fill: '#F10909'
		},
		pdata: {
			node: true
		}
	},
	task: {
		ptype: 'rect',
		pattr: {
			width: 100,
			height: 100,
			fill: '#999999'
		},
		pdata: {
			node: true
		}
	}
}