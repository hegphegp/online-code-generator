var version = "4.0.0";
// 在当前的js文件引入当前路径下的另外一个js文件
var currentPath = document.currentScript ? document.currentScript.src : function(){
    var js = document.scripts, last = js.length - 1, src;
    for(var i = last; i > 0; i--){
        if(js[i].readyState === 'interactive'){
            src = js[i].src;
            break;
        }
    }
    return src || js[last].src;
}();
// alert(currentPath.substring(0, currentPath.lastIndexOf('/') + 1));
var cryptoJsPath = currentPath.substring(0, currentPath.lastIndexOf('/') + 1)+version+"/crypto-js.min.js";
document.write("<script type='text/javascript' src='"+cryptoJsPath+"'></script>");

function encryptAesCBC(needEncryptStr, key, iv) {
    var key = CryptoJS.enc.Utf8.parse(key);
    var iv = CryptoJS.enc.Utf8.parse(iv);
    var content = CryptoJS.enc.Utf8.parse(needEncryptStr);
    var encrypted = CryptoJS.AES.encrypt(content, key, {iv:iv, mode:CryptoJS.mode.CBC, padding:CryptoJS.pad.Pkcs7});
    return encrypted.toString();
}

function decryptAESCBC(needDecryptStr, key, iv) {
    var key = CryptoJS.enc.Utf8.parse(key);
    var iv = CryptoJS.enc.Utf8.parse(iv);
    var decrypt = CryptoJS.AES.decrypt(needDecryptStr, key, {iv:iv, mode:CryptoJS.mode.CBC, padding:CryptoJS.pad.Pkcs7});
    return CryptoJS.enc.Utf8.stringify(decrypt).toString();
}

/plugins/crypto-js/crypto-utils.js
function encryptAesECB(needEncryptStr, key) {
    var key = CryptoJS.enc.Utf8.parse(key);
    var content = CryptoJS.enc.Utf8.parse(needEncryptStr);
    var encrypted = CryptoJS.AES.encrypt(content, key, {mode:CryptoJS.mode.ECB, padding:CryptoJS.pad.Pkcs7});
    return encrypted.toString();
}

function decryptAesECB(needDecryptStr, key) {
    var key = CryptoJS.enc.Utf8.parse(key);
    var decrypt = CryptoJS.AES.decrypt(needDecryptStr, key, {mode:CryptoJS.mode.ECB, padding:CryptoJS.pad.Pkcs7});
    return CryptoJS.enc.Utf8.stringify(decrypt).toString();
}