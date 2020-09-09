/*
* type              请求的方式  默认为get
* url               发送请求的地址
* param             发送请求的参数
* isShowLoader      是否显示loader动画  默认为false
* dataType          返回JSON数据  默认为JSON格式数据
* callBack          请求的回调函数
*/
(function() {
    window.AjaxGet = function AjaxGet(opts) {
        opts.type  = "get";
        // opts.contentType = "application/x-www-form-urlencoded;charset=UTF-8"
        new Ajax(opts);
    }

    window.AjaxPost = function AjaxPost(opts) {
        opts.type  = "post";
        new Ajax(opts);
    }

    window.AjaxPut = function AjaxPut(opts) {
        opts.type  = "put";
        new Ajax(opts);
    }

    window.AjaxDelete = function AjaxDelete(opts) {
        opts.type  = "delete";
        new Ajax(opts);
    }

    window.AjaxPatch = function AjaxPatch(opts) {
        opts.type  = "patch";
        new Ajax(opts);
    }

    /** 分开写
    function AjaxPost(opts) {
        opts.type  = "post";
        new Ajax(opts);
    }
    window.AjaxPost = AjaxPost;
     */

    function Ajax(opts) {
        this.type          = opts.type || "get";
        this.url           = opts.url;
        this.cache         = opts.cache || false;
        this.async         = opts.async || false;
        this.urlParams     = opts.urlParams || {};
        this.bodyParams    = opts.bodyParams || "{}";
        this.isShowLoader  = opts.isShowLoader || false;
        this.dataType      = opts.dataType || "json";
        this.beforeSend    = opts.beforeSend; // 有多个参数的时候,不知道怎么传,参数传进来了,怎么用,放到请求头,请求体,URL参数,怎么放,定不下来
        this.success       = opts.success;    // 与jQuery的ajax的success函数名保持一致
        this.error         = opts.error;      // 与jQuery的ajax的error函数名保持一致
        this.complete      = opts.complete;   // 与jQuery的ajax的complete函数名保持一致
        if (this.type.toLocaleLowerCase()!=="get") {
            this.bodyParams  = "";
            this.contentType = opts.contentType || "application/json; charset=UTF-8"; // "application/x-www-form-urlencoded;charset=UTF-8"
        }
        console.log("\n\n\n\n\n\n   "+typeof(this.params)+"  \n\n\n\n\n\n\n");
        this.init();
    }

    Ajax.prototype = {
        //初始化
        init: function() {
            this.sendRequest();
        },
        //渲染loader
        showLoader: function() {
            if(this.isShowLoader) {
                console.log("\n\n\n\n\n 加载中... \n\n\n\n\n\n")
                var loader = '<div class="ajaxLoader"><div class="loader">加载中...</div></div>';
                $("body").append(loader);
            }
        },
        //隐藏loader
        hideLoader: function() {
            if(this.isShowLoader) {
                $(".ajaxLoader").remove();
            }
        },
        //发送请求
        sendRequest: function(){
            var self = this;
            $.ajax({
                type: this.type,
                url: this.url,
                // data: this.bodyParams,
                data: this.urlParams,
                dataType: this.dataType,
                // contentType: this.contentType,
                // beforeSend: this.showLoader(),
                success: function(data, status, XMLHttpRequest) {
                    arguments.length;
                    self.hideLoader();
                    if(self.success) {
                        if (Object.prototype.toString.call(self.success) === "[object Function]") {   //Object.prototype.toString.call方法--精确判断对象的类型
                            // javascript获取回调函数的参数个数 self.successCallBack.length
                            // console.log("参数的个数"+self.successCallBack.length);
                            // javascript获取当前函数的参数个数 arguments.length
                            if (1==self.success.length) {
                                self.success(data);
                            } else if (2==self.success.length) {
                                self.success(data, status)
                            } else if (3==self.success.length) {
                                self.success(data, status, XMLHttpRequest);
                            } else if (3<self.success.length) {
                                self.success(data, status, XMLHttpRequest);
                            }
                        } else {
                            console.log("callBack is not a function");
                        }
                    }
                }
            });
        }
    };

    window.Ajax = Ajax;
})();


