(function() {
    function AjaxPostRequest(opts) {
        opts.type  = "post";
        this.init(opts);
    }

    AjaxPostRequest.prototype = {
        // 初始化
        init: function (opts) {
            new AjaxRequest(opts);
        }
    };
    window.AjaxPostRequest = AjaxPostRequest;


    function AjaxRequest(opts) {
        this.type          = opts.type || "get";
        this.url           = opts.url;
        this.contentType   = opts.contentType || "application/json; charset=UTF-8"; // "application/x-www-form-urlencoded;charset=UTF-8"
        this.params        = opts.params || {};
        this.isShowLoader  = opts.isShowLoader || false;
        this.dataType      = opts.dataType || "json";
        this.beforeSend    = opts.beforeSend; // 有多个参数的时候,不知道怎么传,参数传进来了,怎么用,放到请求头,请求体,URL参数,怎么放,定不下来
        this.success       = opts.success;    // 与jQuery的ajax的success函数名保持一致
        this.error         = opts.error;      // 与jQuery的ajax的error函数名保持一致
        this.complete      = opts.complete;   // 与jQuery的ajax的complete函数名保持一致
        this.init();
    }

    AjaxRequest.prototype = {
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
                data: this.params,
                dataType: this.dataType,
                contentType: this.contentType,
                // beforeSend: this.showLoader(),
                success: function(data, status, XMLHttpRequest) {
                    arguments.length;
                    self.hideLoader();
                    // if (res != null && res != "") {
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
                    // }
                }
            });
        }
    };

    window.AjaxRequest = AjaxRequest;
})();