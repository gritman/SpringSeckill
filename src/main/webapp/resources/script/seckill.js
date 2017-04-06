/**
 * Created by Edwin on 2017/3/14.
 */
// 存放主要交互逻辑JS代码
// JS容易写乱,要模块化
// seckill.detail.init(params);
var seckill = {
    // 封装秒杀相关AJAX的URL, 不要直接写入JS逻辑中, 方便维护修改
    URL: {
        now: function () {
            return '/seckill/time/now';
        },
        exposer: function(seckillId) {
            return '/seckill/' + seckillId + '/exposer';
        },
        execution: function(seckillId, md5) {
            return '/seckill/' + seckillId + '/' + md5 + '/execution';
        }
    },
    // 验证手机号
    validatePhone: function (phone) {
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true;
        } else {
            return false;
        }
    },
    handleSeckillKill: function(seckillId, node) {
        // 获取秒杀地址,控制显示逻辑,执行秒杀
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>>'); // 按钮
        $.post(seckill.URL.exposer(seckillId), {}, function(result) {
            // 在回调函数中执行交互流程
            if(result && result['success']) {
                var exposer = result['data'];
                if(exposer['exposed']) { // 开启秒杀
                    // 开启秒杀,获取秒杀地址
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.execution(seckillId, md5);
                    console.log("killUrl: " + killUrl);
                    // 用one只绑定一次,用click是永远绑定,如果用户重复点击,则会增加服务器压力
                    $('#killBtn').one('click', function() {
                        // 执行秒杀请求的操作
                        // 1 点击后先禁用按钮
                        $(this).addClass('disabled');
                        // 2 发送秒杀请求执行秒杀
                        $.post(killUrl, {}, function(result) {
                            if(result && result['success']) {
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                // 显示秒杀结果
                                node.html('<span class="label label-success">' + stateInfo + '</span>');
                            }
                        });
                    });
                    node.show();
                } else { // 未开启秒杀(用户进入了倒计时页面后,如果客户机的计时快,就会出现客户机先进入秒杀,而服务器还没进入)
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    // 重新计算计时逻辑
                    seckill.countdown(seckillId, now, start, end);
                }
            } else {
                console.log("result error: " + result);
            }
        });
    },
    countdown: function (seckillId, nowTime, startTime, endTime) {
        // 拿到HTML里的节点,作为输出
        var seckillBox = $('#seckill-box');
        // 时间判断
        if (nowTime > endTime) { // 秒杀结束
            seckillbox.html('秒杀结束');
        } else if (nowTime < startTime) { // 秒杀未开始, 计时
            var killTime = new Date(startTime + 1000);
            // 作用: 当时间变化时做相应的日期的输出
            seckillBox.countdown(killTime, function(event) {
                var format = event.strftime('秒杀计时: %D天 %H时 %M分 %S秒');
                seckillBox.html(format);
            }).on('finish.countdown', function() {
                // 时间完成后回调事件,要获取秒杀地址,控制显示逻辑,执行秒杀按钮
                seckill.handleSeckillKill(seckillId, seckillBox);
            });
        } else { // 秒杀开始
            seckill.handleSeckillKill(seckillId, seckillBox);
        }
    },
    // 详情页秒杀逻辑
    detail: {
        // 详情页初始化
        init: function (params) {
            // 用户的手机验证相关的操作,计时交互
            // 规划交互流程
            // 验证用户是否已登录,从cookie中查找手机号
            var killPhone = $.cookie('killPhone');
            // 验证手机号
            if (!seckill.validatePhone(killPhone)) {
                // 如果没有登录,则要绑定手机号码,控制输出
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true, // 显示弹出层(模态对话框)
                    backdrop: false, // 禁止位置关闭
                    keyboard: 'false' // 关闭键盘事件
                });
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    console.log('inputPhone=' + inputPhone);
                    if (seckill.validatePhone(inputPhone)) {
                        // 电话写入cookie,否则刷新页面时,又会执行一遍前面的init逻辑
                        // path说明这个cookie只在用户访问/seckill这个路由时传递到服务端,其他情况不用传
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'});
                        // 刷新页面
                        window.location.reload();
                    } else { // 如果手机号验证没通过
                        // 先隐藏,放好字符串,再300毫秒后显示出来,效果比较好
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误!</label>').show(300);
                    }
                });
            }
            // 已经登录 计时交互
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            $.get(seckill.URL.now(), {}, function (result) {
                if (result && result['success']) {
                    var nowTime = result['data'];
                    // 时间判断
                    seckill.countdown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log("req now error: " + result);
                }
            });
        }
    }
}




