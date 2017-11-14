var SharedLibrary = SharedLibrary || (function() {
    var contextPath = null;

    return {
        setLang : function(locale) {
            if (locale == 'br') {
                lang = 'pt-br';
            } else {
                lang = locale;
            }
        },
        getLang : function() {
            return lang;
        }
    };
}());