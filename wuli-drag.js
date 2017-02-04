$.fn.draggable = function(){
    var div = $(this);
    var left, top, lastClientX, lastClientY;
    var isDown = false;
    div.mousedown(function (event){
        // 获取当前的left、top坐标值
        left = div.position().left;
        top = div.position().top;
        lastClientX = event.clientX;
        lastClientY = event.clientY;
        isDown = true;
    });
    // document注册move事件，避免拖拽过快div跟不上
    $(document).mousemove(function (event){
        if (!isDown) return;
        var offsetX = event.clientX - lastClientX;
        var offsetY = event.clientY - lastClientY;
        div.css({
            "left": left + offsetX + "px",
            "top": top + offsetY + "px"
        });
    })
    // 鼠标抬起时解除move事件的绑定
    $(document).mouseup(function (){
        isDown = false;
    });
}