(function($){
    //插件书写部分
    $.fn.page = function(options){
    	var $this = $(this);
        var defaults = {
            'url': '',
            'maxShowItem': 7,
            'data': {
                'start': 0,
                'limit': 10
            },
            // 当前页码，总页码数，当前页码内容（当页结果result，数据总数count）
            'callback': function(currentPage, pageCount, obj){
            	console.log(currentPage, pageCount, obj);
            }
        };
        // 将合并的值赋在一个新的{}中，避免改变defaults的值；true采用深度拷贝，防止data覆盖
        var settings = $.extend(true, {}, defaults, options);
        // 初始化page
        page.init(settings, $this);
    };
    
    var page = {
    	'url': '',
    	'div': null,
    	'maxShowItem': 7,
    	'data': {},
		'callback': function(){},
		'init': function(settings, $div){
			this.url = settings.url;
			this.data = settings.data;
			this.callback = settings.callback;
			this.div = $div;
			this.maxShowItem = settings.maxShowItem;
			// 首次加载
			this.ajax(1);
		},
		'ajax': function(currentPage){
			this.data.start = (currentPage - 1) * this.data.limit;
			$.ajax({
				'url': this.url,
	            'type': 'get',
	            'dataType': 'json',
	            'data': this.data,
	            'success': function(obj){
	            	var limit = page.data.limit;
	            	var pageCount = Math.ceil(obj.count / limit);
	            	page.resetPageItem(currentPage, pageCount);
	            	page.callback(currentPage, pageCount, obj);
	            },
	            'error': function(XMLHttpRequest, textStatus, errorThrown){
	                console.log(XMLHttpRequest);
	                console.log(textStatus);
	                console.log(errorThrown);
	            }
			});
		},
		// 重置分页item（第一次，和每次item点击都会触发）
		'resetPageItem': function(currentPage, pageCount) {
			var pageItem = this.getPageItem(currentPage, pageCount);
			page.div.html(pageItem);
			// 为每个item绑定点击事件
			page.div.children('a[class=\'pageItem\']').on('click',function(){
				var $this = $(this);
				var currentPage = parseInt($this.attr('page-data'));
				page.ajax(currentPage);
	        });
		},
		// 根据总页数和当前页数，得到分页item的html内容
		'getPageItem': function(currentPage, pageCount){
			var prePage = currentPage - 1;
			var nextPage = currentPage + 1;
			var prePageClass = 'pageItem';
			var nextPageClass = 'pageItem';
			if (prePage <= 0) {
				prePageClass = 'pageItemDisable';
			}
			if (nextPage > pageCount) {
				nextPageClass = 'pageItemDisable';
			}
			var appendStr = '';
	        // appendStr += '<a href=\'#\' class=\'' + prePageClass + '\' page-data=\'1\'' + '>首页</a>';
	        appendStr += '<a href=\'#\' class=\'' + prePageClass + '\' page-data=\''+ prePage +'\'>&lt;上一页</a>';
	        
	        var unit = (page.maxShowItem - 3) / 2;
	        // 左边临界
	        var left = currentPage - unit;
	        // 右边临界
	        var right = currentPage + unit;
	        
	        if(left <= 1){
	        	right = page.maxShowItem - 1;
	        }
	        if(right >= pageCount){
	        	left = pageCount - (page.maxShowItem - 2);
	        }
	        
	        var leftEllipsis = false;
	        var rightEllipsis = false;
	        
	        for (var i = 1; i <= pageCount; i++) {
	            var itemPageClass = 'pageItem';
	            // 左侧省略号
	        	if(i > 1 && i < left){
	        		if(!leftEllipsis){
	        			itemPageClass = 'ellipsis';
	        			appendStr+='<span class=\'' + itemPageClass + '\'>...</span>';
	        			leftEllipsis = true;
	        		}
	        		continue;
	        	}
	        	// 右侧省略号
	        	if(i < pageCount && i > right){
	        		if(!rightEllipsis){
	        			itemPageClass = 'ellipsis';
	        			appendStr+='<span class=\'' + itemPageClass + '\'>...</span>';
	        			rightEllipsis = true;
	        		}
	        		continue;
	        	}
	        	
	            if(i == currentPage){
	                itemPageClass = 'pageItemActive';
	            }
	            appendStr+='<a href=\'#\' class=\'' + itemPageClass + '\' page-data=\'' + i + '\'>' + i + '</a>';
	        }
	        appendStr += '<a href=\'#\' class=\'' + nextPageClass + '\' page-data=\'' + nextPage + '\'>下一页&gt;</a>';
	        // appendStr += '<a href=\'#\' class=\'' + nextPageClass + '\' page-data=\'' + pageCount + '\'>尾页</a>';
        	return appendStr;
		}
    };
})(jQuery);