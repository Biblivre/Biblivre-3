var Core = {};

function submitForm(formId, messg, menuOption, target) {
    document.getElementById(formId).submitButton.value = messg;

    if (menuOption !== undefined) {
        $('#menuOption').val(menuOption);
    }

    var form = document.getElementById(formId);
    if (target) {
        form.target = target;
    }

    form.submit();
}

function resetForm(formID) {
    document.getElementById(formID).reset();
}

(function($) {
    // Get parameter from query string
    $.qs = function(key) {
        var qs = window.location.href.split('?')[1];
        key = (key) ? key.toLowerCase() : '';

        if (key && qs) {
            var pairs = qs.split('&');
            for (var i = 0; i < pairs.length; i++) {
                var pair = pairs[i].split('=');
                if (pair[0] && pair[0].toLowerCase() == key) {
                    return pair[1];
                }
            }
        }

        return '';
    };

    $.keepSession = function() {
        $.ajax({
            type: 'POST',
            url: 'JsonController',
            dataType: 'json',
            data: {
                thisPage: 'ping',
                submitButton: 'ping'
            },
            complete: function() {
                setTimeout($.keepSession, 300000);
            }
        });
    };
    setTimeout($.keepSession, 30000);

    // Adjusts layout after window resize
    $.fixResize = function() {
        var $html = $('html');
        $html.removeClass('double_line_header').removeClass('triple_line_header');

        var $header = $('#header_title');
        var lineHeight = parseInt($header.css('line-height'), 10);

        if ($header.height() > lineHeight * 2.5) {
            $html.addClass('triple_line_header');
        } else if ($header.height() > lineHeight * 1.5) {
            $html.addClass('double_line_header');
        }

        // Fix to small  resolutions
        if ($(window).width() < 915) {
            $html.addClass('small_window').removeClass('medium_window');
        } else if ($(window).width() < 1030) {
            $html.addClass('medium_window').removeClass('small_window');
        } else {
            $html.removeClass('small_window').removeClass('medium_window');
        }

        $("#content_outer").height($(window).height() - $("#header").height());

        var newHeight = $("#content_outer").height() - 22;

        if ($.browser.msie && parseInt($.browser.version, 10) <= 6) {
            $("#content").height('auto');
            var height = $("#content").height();

            if (newHeight > height) {
                $(".ie6 #content").css('height', newHeight);
            }
        } else {
            $("#content").css('min-height', newHeight);
        }
    };

    $.animateLogos = function() {
        $.setTime();
        $('#logos div:last, #sponsor_logo div:last').fadeOut(2000, function() {
            var $this = $(this);
            $this.parent().prepend(this);
            $this.show();
        });
    };

    $.setTime = function() {
        $('#logo_time').html('<br/>' + formatDate(new Date(), 'HH:mm'));
    };

    

    // Add a class when objects are hovered
    $.fn.hoverClass = function(cls) {
        if (!cls) {
            cls = 'hover';
        }

        return this.hover(function() {
            $(this).addClass(cls);
        }, function() {
            $(this).removeClass(cls);
        });
    };

    // Add a class when objects are clicked
    $.fn.clickClass = function(cls) {
        if (!cls) {
            cls = 'clicked';
        }

        return this.bind('mousedown', function() {
            $(this).addClass(cls);
        }).bind('mouseup', function() {
            $(this).removeClass(cls);
        });
    };

    $.fn.toggleValue = function() {
        var $this = $(this);
        $this.attr('checked', !$this.attr('checked'));
    };

    $.fn.getFormValues = function() {
        var params = {};

        this.each(function() {
            var $this = $(this);
            var name = $this.attr('name');
            var value = $this.val();

            if (!params[name]) {
                params[name] = value;
            } else if (typeof params[name] == "string") {
                params[name] = [params[name], value];
            } else {
                params[name].push(value);
            }
        });

        return params;
    };

    $.msg = function(o) {
        var config = {
            cls: 'normal',
            animate: true
        };

        if (typeof o == 'object') {
            $.extend(config, o);
        } else {
            config.msg = o;
        }

        var empty = !config.msg;
        if (empty) {
            config.msg = '&#160;';
            config.cls = 'normal';
        }

        $('#message p').html(config.msg);

        $('#message')
        .removeClass()
        .removeAttr('style')
        .stop()
        .addClass(config.cls)
        .slideDown('normal');

        if (!empty && config.animate) {
            $('#message').effect('highlight', {}, 2000);
            $('#content_outer').scrollTo(0, 0);
        }
    };
    
    $.ajaxSetup({
        beforeSend: function() {
            if (this.loadingHolder) {
                $(this.loadingHolder).html('<div class="loading"><img src="images/ajax-loader.gif" style="vertical-align: middle;" width="16" height="16" alt="' + (this.loadingText || Translations.LOADING) + '"/> ' + (this.loadingText || Translations.LOADING) + '</div>').show();
            }
        },
        complete: function() {
            if (this.loadingHolder) {
                $(this.loadingHolder).find('.loading').remove();
            }            
        }
    });

    $.fn.childrenNotTemplate = function() {
        return this.children(':not(.template):not(.template_empty_message)');
    };

    $.fn.resetTemplate = function() {
        return this.each(function() {
            $(this).childrenNotTemplate().remove();
        });
    };

    $.fn.addStripes = function() {
        return this.each(function() {
            var $this = $(this);
            $this.children(':not(.template):not(.template_empty_message):odd').addClass('odd');
            $this.children(':not(.template):not(.template_empty_message):even').addClass('even');
        });
    };

    $.fn.checkEmptyTemplate = function() {
        return this.each(function() {
            var $this = $(this);
            if ($this.children(':not(.template):not(.template_empty_message)').size() === 0) {
                $this.addClass('empty');
            } else {
                $this.removeClass('empty');
            }
        });
    };

    $.applyTemplates = function(o) {
        var config = {
            stripe: false,
            fadeOutButtons: false
        };

        $.extend(config, o);
        // This regular expression search for {foo} strings inside the template
        // so we can replace those tokens with result data
        var regexp = RegExp("(.*?){({(.*?)})?(.*?)}(.*)", "i");

        if (!config.array) {
            config.array = [];

            if (config.object) {
                config.array.push(config.object);
            }
        }

        if (!config.template) {
            config.template = config.holder.children('.template');
        }

        if (!config.array.length) {
            config.holder.addClass('empty');
        } else {
            config.holder.removeClass('empty');
        }

        var limit = Math.min(config.limit || config.array.length, config.array.length);
        var originalHTML = '';
        
        if (limit) {
            var originalRecord = config.template.clone(true).removeAttr('id').removeClass('template');
            originalHTML = originalRecord.html().replace(/%7D/gm, '}').replace(/%7B/gm, '{').replace(/\r?\n\s*/gm, ''); // Regexp works better without line breaks;
                
            if ($.browser.msie) {
                originalHTML = ieInnerHTML(originalHTML, false);
            }

        }

        for (var i = 0; i < limit; i++) {
            // Cloning template, removing id to prevent conflicts and removing class template
            var record = config.template.clone(true).removeAttr('id').removeClass('template');
            var result = config.array[i];

            record.data('result', result);
            var html = originalHTML;

            while (regexp.test(html)) {
                var before = RegExp.$1;
                var conditional = RegExp.$3;
                var expression = RegExp.$4;
                var after = RegExp.$5;
                var backreference;
                var onlyIfEmpty = false;

                if (conditional) {
                    if (conditional[0] == '!') {
                        onlyIfEmpty = true;
                        backreference = conditional.substr(1).replace('%7C', '|').split('|');
                    } else {
                        backreference = conditional.replace('%7C', '|').split('|');
                    }
                } else {
                    backreference = expression.replace('%7C', '|').split('|');
                    expression = '%%';
                }

                var value = '';
                var key = backreference[0];
                var type = backreference[1];
                var complement = backreference[2];

                var keyArr = key.split(/\./);
                var keyObj = result;
                var k = 0;

                for (; k < keyArr.length - 1; k++) {
                    keyObj = keyObj[keyArr[k]] || {};
                }

                var tempValue = keyObj[keyArr[k]];
                var pattern, year, month, day, hour, minute, date;

                if (type) {
                    switch (type) {
                        case 'Date':
                            if (!Core.isUndef(tempValue)) {
                                pattern = complement || Translations.SHORT_DATE;

                                if (tempValue.match(/(\d*)-(\d*)-(\d*)/) && RegExp.$1 != "0001") {
                                    year = RegExp.$1;
                                    month = RegExp.$2;
                                    day = RegExp.$3;

                                    date = new Date(RegExp.$1, RegExp.$2 - 1, RegExp.$3);

                                    value = formatDate(date, pattern);
                                }
                            }

                            break;

                        case 'Datetime':
                            if (!Core.isUndef(tempValue)) {
                                pattern = complement || Translations.SHORT_DATETIME;

                                if (tempValue.match(/(\d*)-(\d*)-(\d*)[ ]?(\d*)?:?(\d*)?/) && RegExp.$1 != "0001") {
                                    year = RegExp.$1;
                                    month = RegExp.$2;
                                    day = RegExp.$3;
                                    hour = RegExp.$4 || 0;
                                    minute = RegExp.$5 || 0;


                                    date = new Date(year, month - 1, day, hour, minute);
                                    value = formatDate(date, pattern);
                                }
                            }

                            break;

                        case 'Money':
                            if (!Core.isUndef(tempValue)) {
                                if (Core.isFloat(tempValue)) {
                                    value = parseFloat(tempValue, 10).toFixed(2);
                                }
                            }

                            break;
                          
                        case 'select':
                            if (!Core.isUndef(tempValue)) {
                                var option = complement;
                                if (tempValue == option) {
                                    value = 'selected="true"';
                                }
                            }

                            break;

                        case 'translation':
                            if (!Core.isUndef(tempValue)) {
                                value = Translations[complement][tempValue];
                            }

                            break;

                        case 'StringList':
                            if (!Core.isUndef(tempValue)) {
                                var arr = tempValue;
                                var separator = complement || '<br/>';

                                if ($.isArray(arr)) {
                                    value = arr.join(separator);
                                } else {
                                    value = arr;
                                }
                            }

                            break;

                        case 'escape':
                            if (!Core.isUndef(tempValue)) {
                                value = tempValue.replace(/'/g, "\\'");
                            }
                            
                            break;

                        case 'href':
                            value = 'href="' + tempValue + '"';

                            break;

                        case 'random':
                            value = (new Date()).getTime();
                            break;
                    }
                } else {
                    value = tempValue;
                }

                if (conditional) {
                    if (value === null || value === undefined || value === '' || value === false) {
                        expression = onlyIfEmpty ? expression : '';
                    } else {
                        expression = onlyIfEmpty ? '' : expression;
                    }
                }

                if (value === null || value === undefined) {
                    value = '';
                }

                html = before + expression.replace(/%%/g, value) + after;
            }

            if (config.prepend) {
                record.html(html).prependTo(config.holder);
            } else {
                record.html(html).appendTo(config.holder);
            }
        }

       if (limit < config.array.length) {
            $('<div class="box show_more">' + Translations.SHOW_MORE(config.array.length - limit) + '</div>').click(function() {
                $(this).remove();
                config.array = config.array.slice(limit);
                config.limit = null;

                $.applyTemplates(config);
            }).appendTo(config.holder);
       }

       if (config.stripe) {
           config.holder.addStripes();
       }

       if (config.fadeOutButtons) {
            var lines = config.holder.childrenNotTemplate();
            lines.hover(function() {
                $(this).addClass('mouseover').find('button:visible').stop().show().fadeTo('fast', 1);
            }, function() {
                $(this).removeClass('mouseover').find('button:visible').stop().fadeTo('slow', 0);
            });

            setTimeout(function() {
                lines.not('.mouseover').find('button').fadeTo('slow', 0);
            }, 1000);
       }
    };

    $.ajaxSetup({
        timeout: 300000,
        error: function(xhr) {

            if (xhr.status == 403) {
                alert(Translations.ERROR_EXPIRED_SESSION);
            }
        }
    });

})(jQuery);

$(document).ready(function() {
    $(window).resize(function() {
        $.fixResize();
    });
    
    // It's good to have all images with their titles the same as their alts
    $('img[alt]').attr('title', function() {
        return $(this).attr('alt');
    });

    // Every button and tab has it's hover and click classes
    $('button').live('mouseover', function() {
        $(this).addClass('button_hover');
    }).live('mouseout', function() {
        $(this).removeClass('button_hover');
    }).live('mousedown', function() {
        $(this).addClass('button_clicked');
    }).live('mouseup', function() {
        $(this).removeClass('button_clicked');
    });

    $('div.tab[onclick]').hoverClass('tab_hover').clickClass('tab_clicked');

    // Let's populate our dynamic breadcrumb
    var menuOption = $('#menuOption').val();
    var breadcrumb = '';

    if (!menuOption) {
        breadcrumb = 'Biblivre';
    } else {
        var menuEntry = $('#menu li[rel="' + menuOption + '"]:first');
        if (menuEntry.size()) {
            breadcrumb = menuEntry.parents('li').html().replace(/\r?\n|<.*$|^\s*/gm, '');
            breadcrumb += ' &gt; ' + menuEntry.text();
        }
    }
    $('#breadcrumb').html(breadcrumb);
    //

    // Prepara o hover do menu, para aparecer os filhos
    $('#menu_root li').hoverClass();
    $('#menu_root>li>ul').bgiframe();
    $('#menu_root>li').bind('mouseenter', function() {
        var $this = $(this);

        $this.children('ul').css('top', $this.outerHeight());
    });

    if ($.browser.msie) {
        $('#menu_root .submenu').each(function() {
            var $this = $(this);
            $this.width($this.outerWidth());
        });
    }
    //

    // Clique em uma opção do menu
    $('#menu_root .submenu>li:not(.disabled)').click(function() {
        var $this = $(this);
        //var rel = $this.parents('li').attr('rel') + '/' + $this.attr('rel');
        var rel = $this.attr('rel');
        if (rel.charAt(0) == '.') {
            window.open(rel, '', 'width=800,height=600,resizable=no,scrollbars=no,menubar=no,toolbar=no,location=no,directories=no,status=no,copyhistory=no,top=10,left=50');
        } else if (rel.substring(0, 7) == 'http://') {
            window.open(rel);
        } else {
            submitForm('FORM_1', rel, rel);
        }
    });
    //

    $.fixResize();
    $.setTime();
    setInterval($.animateLogos, 6000);
});

Core.changeTab = function(tab, handler, params, forceChange) {
    if ($.isFunction(handler)) {
        if (handler(tab, params, forceChange) === false) {
            return false;
        }
    }

    $('div.tab').removeClass('tab_selected').filter('[rel="' + tab + '"]').addClass('tab_selected');
    $('div.tab_body, div.tab_footer').addClass('hidden').filter('[rel="' + tab + '"]').removeClass('hidden');
    return true;
};

Core.submitKeyPress = function(e, handler, args) {
    if (!$.isFunction(handler)) {
        return;
    }

    if (e.keyCode == 10 || e.keyCode == 13) {
        Core.runStopingEvent(e, handler, args);
    }

};

Core.pagingGenerator = function(o) {
    var config = $.extend({}, {
        numberOfPages: 0,
        currentPage: 1,
        recordsPerPage: 1,
        selectFunction: function() {},
        linkFunction: function() {},
        search_holder: null
    }, o);

    if (config.numberOfPages == 1) {
        return;
    }

    if (config.search_holder) {
        config.search_holder
            .data('currentPage', config.currentPage)
            .data('numberOfPages', config.numberOfPages)
            .data('recordsPerPage', config.recordsPerPage);
    }

    config.pagingHolder.empty();
    
    if (config.currentPage > 1) {
        config.pagingHolder.append('<a href="javascript:void(0);" class="paging_arrow" rel="1">&lt;&lt;</a>');
        config.pagingHolder.append('<a href="javascript:void(0);" class="paging_arrow" rel="' + (config.currentPage - 1) + '">&lt;</a>');
    }

    var ellipsis = false;
    var i, maxi;
    for (i = 1; i <= config.numberOfPages; i++) {
        if ((i <= 3) || (i > config.numberOfPages - 3) || ((i > config.currentPage - 3) && (i < config.currentPage + 3))) {
            ellipsis = false;

            if (i == config.currentPage) {
                config.pagingHolder.append('<span class="paging_actual_page">' + i + '</a>');
            } else {
                config.pagingHolder.append('<a href="javascript:void(0);" rel="' + i + '">' + i + '</a>');
            }
        } else if (!ellipsis) {
            ellipsis = true;
            config.pagingHolder.append('<span>...</span>');
        }
    }

    if (config.currentPage < config.numberOfPages) {
        config.pagingHolder.append('<a href="javascript:void(0);" class="paging_arrow" rel="' + (config.currentPage + 1) + '">&gt;</a>');
        config.pagingHolder.append('<a href="javascript:void(0);" class="paging_arrow" rel="' + config.numberOfPages + '">&gt;&gt;</a>');
    }

    config.pagingHolder.find('a[rel]').click(config.linkFunction);

    if (config.numberOfPages <= 50 && typeof config.selectFunction == 'function') {
        var html = ['<select style="margin-left: 10px;">'];

        for (i = 1, maxi = config.numberOfPages; i <= maxi; i++) {
            if (i == config.currentPage) {
                html.push('<option value="', i, '" selected>', i, '</option>');
            } else {
                html.push('<option value="', i, '">', i, '</option>');
            }
        }

        var select = $(html.join(''));
        select.bind('change', config.selectFunction);
        select.appendTo(config.pagingHolder).val(config.currentPage);
   }
};

Core.dateMask = function(inputData, e) {
    var key;
    if (document.all) { // Internet Explorer
        key = event.keyCode;
    } else { //Outros Browsers
        key = e.which;
    }

    if (key >= 47 && key < 58) { // numeros de 0 a 9
        var data = inputData.value;

        if (data.length == 10) {
            return false;
        }

        if (key != 47 && (data.length == 5 || data.length == 2)) {
            data += '/';
            inputData.value = data;
        }

        return true;
    } else if(key == 8 || key === 0) { // Backspace, Delete e setas direcionais(para mover o cursor, apenas para FF)
        return true;
    } else {
        return false;
    }
};

Core.tempDisable = function(el) {
    var $el = $(el);
    $el.attr('disabled', true);

    setTimeout(function() {
        $el.removeAttr('disabled');
    }, 2000);
};

Core.runStopingEvent = function(e, f, args) {
    if ($.isFunction(f)) {
        f.apply(this, args || []);
    }

    if (!e) {
        e = window.event;
    }

    e.cancelBubble = true;
    if (e.stopPropagation) {
        e.stopPropagation();
    }

    if (e.preventDefault) {
        e.preventDefault();
    }

    return false;
};

Core.populateForm = function(o) {
    var defaults = {
        url: 'JsonController',
        error: function() {
            $.msg({
                cls: 'warning',
                msg: Translations.ERROR_WHILE_POPULATING_FORM
            });
        },
        object: null
    };
    var config = $.extend({}, defaults, o);

    function populate(data) {
        var $inputs = $(config.root || document).find(':input');

        for (var key in data) {
            if (data.hasOwnProperty(key)) {
                var input = $inputs.filter('[name="' + key + '"]');
                var inputData = input.attr('data');
                var inputType = input.attr('type');
                var value = data[key];
                if (inputData == 'Date') {
                    if (value && value.match(/(\d*)-(\d*)-(\d*)/) && RegExp.$1 != "0001") {
                        var date = new Date(RegExp.$1, RegExp.$2 - 1, RegExp.$3);
                        value = formatDate(date, Translations.SHORT_DATE);
                    }
                }

                if (inputType == 'checkbox' || inputType == 'radio') {
                    input.attr('checked', value == input.val());
                } else {
                    input.val(value);
                }
            }
        }

        if ($.isFunction(config.callback)) {
            config.callback(data);
        }
    }

    if (config.object) {
        
        populate(config.object);
    } else {

        $.ajax({
            type: 'POST',
            url: config.url,
            dataType: 'json',
            data: config.params,
            success: function(data) {

                if (data.success) {
                    populate(data);
                }

                $.msg({
                    cls: data.errorLevel,
                    msg: data.message
                });
            },
            error: config.error
        });
    }
};
/*
Core.populateForm({
    params: {
        thisPage: 'thisPage',
        submitButton: '',
        id: ''
    },
    root: $('#id_do_form')
});
*/

Core.isNumeric = function(input) {
    return (input - 0) == input && input.length > 0;
};

Core.isFloat = function(s) {
    var n = $.trim("" + s);
    return n.length > 0 && !(/[^0-9.]/).test(n);
};

Core.isUndef = function(o) {
    return (o === undefined || o === null);
};

Core.formatDateDime = function(str) {
    var pattern = Translations.SHORT_DATETIME;

    if (str.match(/(\d*)-(\d*)-(\d*)[ ]?(\d*)?:?(\d*)?/) && RegExp.$1 != "0001") {
        var year = RegExp.$1;
        var month = RegExp.$2;
        var day = RegExp.$3;
        var hour = RegExp.$4 || 0;
        var minute = RegExp.$5 || 0;


        var date = new Date(year, month - 1, day, hour, minute);
        return formatDate(date, pattern);
    }

    return "";
};