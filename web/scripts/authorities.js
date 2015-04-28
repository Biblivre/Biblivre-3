var Authorities = {};
var AuthoritiesSearch = {};

Authorities.isNewRecord = false;
Authorities.isSearchOnly = false;

Authorities.createNew = function() {
    Authorities.setIsNewRecord(true);

    $.msg({cls: 'normal', msg: ""});

    Core.changeTab('form', Authorities.tabHandler, 0);

    $('select[name=author_type]').val('');
    Cataloging.toggleAreas('author_type', '');
    Authorities.clearAll();
};

Authorities.clearAll = function() {
    $('#freemarc').val('');
    Cataloging.clearAll();
};

Authorities.setIsNewRecord = function(bool) {
    Authorities.isNewRecord = bool;
    Authorities.setNavigationDisplay(!bool);
};

Authorities.setNavigationDisplay = function(bool) {
    $('div.navigation_top, div.navigation_bottom').toggle(bool);
};

Authorities.tabHandler = function(tab, serial, force) {
    var fromTab = $('div.tab_selected').attr('rel');

    // dont change if we are changing to the same tab
    if (tab == fromTab && !force) {
        return false;
    }

    serial = serial || $('#search_results').data('lastSerial') || $('#search_results').childrenNotTemplate().find('.box[rel]:first').attr('rel');

    if (fromTab == 'search') {
        // going from search to edit
        if (Authorities.isNewRecord) {
            $('#search_results').data('lastSerial', 0);
            return true;
        }

        if (!serial) {
            $.msg({
                cls: 'warning',
                msg: Translations.ERROR_SEARCH_FIRST
            });
            return false;
        }

        Authorities.loadDescription(tab, serial);
        return true;
    }

    if (tab == 'search') {
        if (Authorities.isNewRecord) {
            if (confirm(Translations.CONFIRM_CANCEL_AUTH)) {
                Authorities.setIsNewRecord(false);
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

    Authorities.loadConvertion(fromTab, tab);

    return false;
};

Authorities.searchSubmit = function(listAll) {
    var inputs = $('#search_box :input:enabled:not(button)');

    var block = false;
    inputs.each(function() {
        var $this = $(this);

        if ($this.attr('name') == 'SEARCH_TERM' && $.trim($this.val()).length === 0 && !listAll) {
            alert(Translations.ERROR_TYPE_ALL_SEARCH_PARAMS);
            $this.focus();
            block = true;
            return false;
        }

        return true;
    });

    if (block) {
        return;
    }

    var params = (listAll) ? {} : inputs.getFormValues();

    params['thisPage'] = 'auth_search';
    params['submitButton'] = 'search';

    Authorities.searchDoSubmit(params);
};

Authorities.searchClearResults = function() {
    $('#search_results')
        .removeData('lastSerial')
        .resetTemplate();

    $('div.search_paging').empty();
    $('div.search_print').hide();
};

Authorities.searchClear = function() {
    $('#search_box input:not(:hidden)').val('');

    Authorities.searchClearResults();
};

Authorities.searchDoSubmit = function(params, whichRowToSelect, tab, dontShowMessage) {
    Authorities.searchClearResults();

    $('#search_results').data('lastParams', params);

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: params,
        loadingHolder: 'div.search_paging:first',
        success: function(data) {
            if (data.success) {
                // On success, reload the search results
                Authorities.searchDumpResults({
                    params: params,
                    data: data,
                    whichRowToSelect: whichRowToSelect,
                    tab: tab,
                    dontShowMessage: dontShowMessage
                });
            } else {
                $.msg({
                    cls: data.errorLevel,
                    msg: data.message
                });
            }
        }
    });
};

Authorities.deleteAuthor = function(serial, button, e) {
    if (!confirm(Translations.CONFIRM_DELETE)) {
        return;
    }

    var params = {
        thisPage: 'auth',
        submitButton: 'delete',
        serial: serial
    };

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: params,
        success: function(data) {
            if (data.success) {
                $(button).parents('.box').fadeOut();
            }

            $.msg({
                cls: data.errorLevel,
                msg: data.message,
                animate: !data.success
            });
        }
    });
};

Authorities.searchDumpResults = function(o) {
    var config = $.extend({}, {
        holder: '#search_results',
        dontShowMessage: false
    }, o);

    if (!config.data || !config.data.results) {
        return;
    }

    // Showing the result count in the Message Div
    if (!config.dontShowMessage) {
        $.msg({msg: config.data.totalRecords + ' ' + Translations.RECORDS_FOUND(config.data.totalRecords), animate: false});
    }

    var holder = $(config.holder);
    if (!holder) {
        return;
    }

    holder.removeData('lastSerial');

    if (config.data.results.length > 0) {
        $('div.search_print').show();
    }

    $.applyTemplates({
        holder: holder,
        array: config.data.results,
        stripe: true,
        fadeOutButtons: true
    });

   //Paginação
    var params = config.params;
    var numberOfPages = parseInt(config.data.totalPages, 10);
    var currentPage = parseInt(config.data.currentPage, 10);
    var recordsPerPage = parseInt(config.data.recordsPerPage, 10);

    Core.pagingGenerator({
        pagingHolder: $('div.search_paging'),
        numberOfPages: numberOfPages,
        currentPage: currentPage,
        recordsPerPage: recordsPerPage,
        selectFunction: function() {
            params.offset = ($(this).val() - 1) * recordsPerPage;
            Authorities.searchDoSubmit(params);
        },
        linkFunction: function() {
            params.offset = ($(this).attr('rel') - 1) * recordsPerPage;
            Authorities.searchDoSubmit(params);
        },
        search_holder: $('#search_results')
    });

    if (config.whichRowToSelect && config.tab) {
        var serial = holder.childrenNotTemplate().find('div.box[rel]').filter(':' + config.whichRowToSelect).attr('rel');

        Core.changeTab(config.tab, Authorities.tabHandler, serial, true);
    }

};

Authorities.searchNavigate = function(tab, direction) {
    direction = direction || 'next';
    tab = tab || 'form';

    var lastSerial = $('#search_results').data('lastSerial');
    if (!lastSerial) {
        return;
    }

    var currentSelectedRow = $('#search_results .box[rel="' + lastSerial + '"]');
    var newSerial;
    if (direction == 'next') {
        newSerial = currentSelectedRow.parent().next(':not(.template):not(.template_empty_message)').children('div[rel]').attr('rel');
    } else {
        newSerial = currentSelectedRow.parent().prev(':not(.template):not(.template_empty_message)').children('div[rel]').attr('rel');
    }

    if (!newSerial) {
        var holder = $('#search_results');
        var lastParams = holder.data('lastParams');
        var currentPage = holder.data('currentPage');
        var numberOfPages = holder.data('numberOfPages');
        var recordsPerPage = holder.data('recordsPerPage');

        if ((!currentPage || currentPage == 1) && (direction == 'prev'))  {
            $.msg({cls: 'warning', msg: Translations.ERROR_FIRST_RECORD});
        } else if ((currentPage == numberOfPages) && (direction == 'next')) {
            $.msg({cls: 'warning', msg: Translations.ERROR_LAST_RECORD});
        } else {
            var newPage = (direction == 'prev') ? currentPage - 1 : currentPage + 1;

            lastParams.offset = (newPage - 1) * recordsPerPage;
            Authorities.searchDoSubmit(lastParams, (direction == 'next') ? 'first' : 'last', tab);
        }
    } else {
        Authorities.loadDescription(tab, newSerial);
    }
};

Authorities._loadCatalogingForm = function(record, tabHolder) {
    Authorities.clearAll();

    if (record['100']) {
       Cataloging.toggleAreas('author_type', '100');
    } else if (record['110']) {
       Cataloging.toggleAreas('author_type', '110');
    } else if (record['111']) {
       Cataloging.toggleAreas('author_type', '111');
    }

    Cataloging.loadJson(tabHolder, record);
};

Authorities._loadCatalogingMarc = function(record) {
    $('#freemarc').val(record);
};

Authorities._loadSearchMarc = function(data) {
    var marcLines = data.data.split(/\r?\n/);
    var fields = [];

    for (var i = 0, len = marcLines.length; i < len; i++) {
        var line = marcLines[i];
        if (line && line.match(/(\d\d\d) (.*)/)) {
            fields.push({
                field: RegExp.$1,
                value: RegExp.$2
            });
        }
    }

    return fields;
};

Authorities._loadSearchFields = function(data) {
    return data.fields;
};

Authorities.loadDescription = function(tab, serial) {
    var holder = $('#search_results');
    var tabHolder = $('div.tab_body[rel="' + tab + '"]');
    var tabHolderBody = tabHolder.find('.tab_inner_body');

    tabHolderBody.resetTemplate();
    holder.data('lastSerial', serial);

    if (!serial) {
        return;
    }

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'auth',
            submitButton: 'open',
            type: tab,
            record_id: serial
        },
        loadingHolder: 'div.tab_body[rel="' + tab + '"] h1:first',
        success: function(data) {
            if (data.success) {
                var expectedSerial = serial;
                if (holder.data('lastSerial') != expectedSerial) {
                    return;
                }

                var expectedTab = tab;
                if ($('div.tab_selected').attr('rel') != expectedTab) {
                    return;
                }

                $.msg();

                if (!Authorities.isSearchOnly) {
                    var record = data.data;

                    if (tab == 'form') {
                        Authorities._loadCatalogingForm(record, tabHolder);
                    } else if (tab == 'marc') {
                        Authorities._loadCatalogingMarc(record);
                    }
                } else {
                    $.msg();

                    var fields;
                    if (tab == 'marc') {
                        fields = Authorities._loadSearchMarc(data);
                    } else {
                        fields = Authorities._loadSearchFields(data);
                    }

                    $.applyTemplates({
                        holder: tabHolderBody,
                        array: fields
                    });
                }
            } else {
                $.msg({
                    cls: data.errorLevel,
                    msg: data.message
                });
            }
        }
    });
};

Authorities.loadConvertion = function(from, to) {
    var serial = $('#search_results').data('serial');
    var tabHolder = $('div.tab_body[rel="' + to + '"]');

    var data = '';

    if (from == 'form') {
        data = Cataloging.createJson($('div.tab_body[rel="form"]'));
    } else if (from == 'marc') {
        data = $('#freemarc').val();
    }

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'auth',
            submitButton: 'switch',
            from: from,
            to: to,
            data: data,
            serial: serial
        },
        loadingHolder: 'div.tab_body[rel="' + from + '"] h1:first',
        success: function(data) {
            if (data.success) {
                var expectedSerial = serial;
                if ($('#search_results').data('serial') != expectedSerial) {
                    return;
                }

                var expectedTab = from;
                if ($('div.tab_selected').attr('rel') != expectedTab) {
                    return;
                }

                Core.changeTab(to);
                $.msg();

                var record = data.data;

                if (to == 'form') {

                    Authorities.clearAll();

                    if (record['100']) {
                       Cataloging.toggleAreas('author_type', '100');
                    } else if (record['110']) {
                       Cataloging.toggleAreas('author_type', '110');
                    } else if (record['111']) {
                       Cataloging.toggleAreas('author_type', '111');
                    }

                    Cataloging.loadJson(tabHolder, record);
                } else if (to == 'marc') {
                    $('#freemarc').val(record);
                }
            } else {
                $.msg({
                    cls: data.errorLevel,
                    msg: data.message
                });
            }
        }
    });
};

Authorities.save = function() {
    var serial = $('#search_results').data('lastSerial');
    var tab = $('div.tab_selected').attr('rel');

    var data = '';


    if (!serial) {
        serial = '0';
    }

    if (tab == 'form') {
        data = Cataloging.createJson($('div.tab_body[rel="form"]'));
    } else if (tab == 'marc') {
        data = $('#freemarc').val();
    }

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'auth',
            submitButton: 'save',
            type: tab,
            record_id: serial,
            data: data
        },
        success: function(response) {
            $.msg({
                cls: response.errorLevel,
                msg: response.message
            });

            if (response.success) {
                if (Authorities.isNewRecord) {
                    Authorities.setIsNewRecord(false);
                } else {
                    var params = $('#search_results').data('lastParams');
                    Authorities.searchDoSubmit(params, null, null, true);
                }

                Core.changeTab('search', Authorities.tabHandler);
            }
        }
    });
};

AuthoritiesSearch.tabHandler = function(tab, serial, force) {
    var fromTab = $('div.tab_selected').attr('rel');

    // dont change if we are changing to the same tab
    if (tab == fromTab && !force) {
        return false;
    }

    serial = serial || $('#search_results').data('lastSerial') || $('#search_results').childrenNotTemplate().find('.box[rel]:first').attr('rel');

    if (fromTab == 'search') {
        if (!serial) {
            $.msg({
                cls: 'warning',
                msg: Translations.ERROR_SEARCH_FIRST
            });
            return false;
        }
    }

    Authorities.loadDescription(tab, serial);
    return true;
};

AuthoritiesSearch.searchSubmit = function() {
    Authorities.isSearchOnly = true;
    Authorities.searchSubmit.apply(Authorities, arguments);
};

AuthoritiesSearch.searchNavigate = Authorities.searchNavigate;