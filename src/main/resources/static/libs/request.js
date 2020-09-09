/*
* 该工具依赖了layui
* type              请求的方式  默认为get
* url               发送请求的地址
* data              发送请求的参数
* isShowLoader      是否显示loader动画  默认为false
* dataType          返回JSON数据  默认为JSON格式数据
* callBack          请求的回调函数
*/
(function(){
    function AjaxReq(opts) {
        this.type         = opts.type || "get";
        this.url          = opts.url;
        this.data         = opts.data || {};
        // this.isShowLoader = opts.isShowLoader || false;
        this.async        = (opts.async==null || opts.async==undefined)? true:opts.async; //异步加载
        this.crossDomain  = (opts.crossDomain==null || opts.crossDomain==undefined)? true:opts.crossDomain;
        this.dataType     = opts.dataType || "json";
        this.success      = opts.success || complete;
        this.error        = opts.success || error;
        this.complete     = opts.complete || complete;
        this.beforeSend   = opts.beforeSend || beforeSend;
        this.carryToken   = (opts.carryToken==null || opts.carryToken==undefined)? true:opts.carryToken;
        this.contentType  = opts.contentType || 'application/json; charset=UTF-8';
        this.headers      = opts.headers || {'accept': 'application/json, text/javascript, text/html, application/xml'};
        this.init();
    }

    function complete(xhr) {
        dealErr(xhr);
    }

    function dealErr(xhr) {
        // 如果httpStatus封装都返回200的话，接口返回的异常统一在complete捕获
        // 如果当前页面是登录页面的URL，不需要跳转到登录页面 // && location.pathname != "/views/login.html") {
        if (401==xhr.status) {
            layer.msg('请先登录系统', {time: 2000}, function () {
                window.parent.location.href = "./views/login.html";
            });
            return;
        }

        // httpStatus大于400的抛错, 302, 301这些状态码不抛错
        if (xhr.status>=400) {
            var responseJSON = xhr.responseJSON;
            if (responseJSON==null || responseJSON==undefined) {
                console.log(xhr.responseText);
                layui.layer.msg("服务内部错误， "+xhr.responseText);
            } else {
                var msg = responseJSON["msg"];
                layui.layer.msg((responseJSON==null || responseJSON==undefined)? "服务内部错误":msg);
            }
        }
    }

    function error(xhr, textStatus, errorThrown) {
        // layui.layer.msg("请求对象XMLHttpRequest: "+XMLHttpRequest+"错误类型textStatus: "+textStatus+"异常对象errorThrown: "+errorThrown);
        // console.log("请求对象XMLHttpRequest: "+XMLHttpRequest+"错误类型textStatus: "+textStatus+"异常对象errorThrown: "+errorThrown);
        // console.log(xhr);
        dealErr(xhr);
    }

    function beforeSend(xhr) {
        // console.log(this); this对象是$.ajax对象，不是Request对象
        if (this.carryToken) {
            xhr.setRequestHeader('token', layui.data("userInfo")["token"]);
        }
    }

    Request.prototype = {
        //初始化
        init: function() {
            if (this.carryToken) {
                var token = layui.data("userInfo")["token"];
                if (token==null || token==undefined) {
                    layer.msg('请先登录系统', {time: 2000}, function () {
                        window.parent.location.href = "./views/login.html";
                    });
                    return;
                }
            }
            this.sendRequest();
        },
        // //渲染loader
        // showLoader: function(){
        //     if(this.isShowLoader){
        //         var loader = '<div class="ajaxLoader"><div class="loader">加载中...</div></div>';
        //         $("body").append(loader);
        //     }
        // },
        // //隐藏loader
        // hideLoader: function(){
        //     if(this.isShowLoader){
        //         $(".ajaxLoader").remove();
        //     }
        // },
        //发送请求
        sendRequest: function() {
            $.ajax({
                url: this.url,
                type: this.type,
                data: this.data,
                async: this.async,
                dataType: this.dataType,
                contentType: this.contentType,
                crossDomain: this.crossDomain,
                headers: this.headers,
                beforeSend: this.beforeSend,
                success: this.success,
                error: this.error,
                complete: this.complete,
                carryToken: this.carryToken
            });
        }
    };

    window.AjaxReq = AjaxReq;
})();