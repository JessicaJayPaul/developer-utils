$.fn.extend({
    // 内容重置
    reset: function () {
        var $this = $(this);
        // 输入框
        $this.find('[type="text"]').val('');
        // 文本域
        $this.find('textarea').val('');
        // 下拉框
        $this.find('select').find('option:first-child').prop('selected', true);
        // 单选按钮
        var radioNames = [];
        $this.find('[type="radio"]').each(function (i, radio) {
            if (!isExist(radioNames, radio.name)) {
                radioNames.push(radio.name);
            }
        });
        radioNames.forEach(function (name) {
            $this.find('[name="' + name + '"]:eq(0)').prop('checked', true);
        });
        // 复选框
        $this.find('[type="checkbox"]').prop('checked', false);
    },
    // 内容填充
    fill: function (obj) {
        console.log(obj)
        var $this = $(this);
        for (var name in obj) {
            var value = obj[name];
            var $element = $this.find('[name="' + name + '"]');
            // 如果是下拉框
            if ($element.is('select')) {
                $element.find('[value="' + value + '"]').prop('selected', true);
                continue;
            }
            // 如果是单选按钮
            if ($element.prop('type') === 'radio') {
                $element.filter('[value="' + value + '"]').prop('checked', true);
                continue;
            }
            $element.val(value);
        }
    }
});