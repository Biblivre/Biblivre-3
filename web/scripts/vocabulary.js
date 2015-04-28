var Vocabulary = {};
var VocabularySearch = {};

Vocabulary.isNewRecord = false;
Vocabulary.isSearchOnly = false;

Vocabulary.createNew = function() {
    Vocabulary.setIsNewRecord(true);
    $.msg({
        cls: 'normal',
        msg: ''
    });
    Core.changeTab('form', Vocabulary.tabHandler, 0);
    Vocabulary.clearAll();
};

Vocabulary.deleteRecord = function(serial, button) {
    if (confirm(Translations.CONFIRM_DELETE)) {
        var params = {
            thisPage: 'vocabulary',
            submitButton: 'delete',
            record_id: serial
        };

        $.ajax({
            url: 'JsonController',
            type: 'POST',
            dataType: 'json',
            data: params,
            success: function(data) {
                if (data.success) {
                    $.msg({
                        cls: data.errorLevel,
                        msg: data.message,
                        animate: false
                    });

                    $(button).parents('.box').fadeOut();
                } else {
                    $.msg({
                        cls: data.errorLevel,
                        msg: data.message
                    });
                }
            }
        });
    }
};

Vocabulary.clearAll = function() {
    $('#freemarc').val('');
    Cataloging.clearAll();
};

Vocabulary.clearRecord = function() {
    $('#record_results').resetTemplate();
};

Vocabulary.setIsNewRecord = function(bool) {
    Vocabulary.isNewRecord = bool;
};

Vocabulary.tabHandler = function(tab, serial, force) {
    var fromTab = $('div.tab_selected').attr('rel');

    // dont change if we are changing to the same tab
    if (tab == fromTab && !force) {
        return false;
    }

    serial = serial || $('#search_results').data('lastSerial') || $('#search_results').childrenNotTemplate().find('.box[rel]:first').attr('rel');

    if (fromTab == 'search') {
        if (Vocabulary.isNewRecord) {
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

        Vocabulary.loadDescription(tab, serial);
        return false;
    }

    if (tab == 'search') {
        if (Vocabulary.isNewRecord) {
            if (confirm(Translations.CONFIRM_CANCEL)) {
                Vocabulary.setIsNewRecord(false);
                return true;
            } else {
                return false;
            }
        }

        return true;
    } else if (tab == 'record') {
        if (Vocabulary.isNewRecord) {
            return false;
        }

        Vocabulary.loadDescription(tab, serial, true);
        return false;
    } else {

        if (tab == 'marc' || tab == 'form') {
            if (fromTab == 'marc' || fromTab == 'form') {
                Vocabulary.loadConvertion(fromTab, tab);
            } else {
                Vocabulary.loadDescription(tab, serial);
            }
        }

        return false;
    }

    return false;
};

Vocabulary.searchClearResults = function() {
    $('#search_results')
        .removeData('lastSerial')
        .resetTemplate();

    $('div.search_paging').empty();
    $('div.search_print').hide();
};

Vocabulary.searchClear = function() {
    $('#search_box input:not(:hidden)').val('');

    $('#search_results').removeData('lastParams');

    Vocabulary.searchClearResults();
};

Vocabulary.searchSubmit = function(listAll) {
    var inputs = $('#search_box :input:enabled:not(button)');

    var block = false;
    inputs.each(function() {
        var $this = $(this);
        if ($this.attr('name') == 'search_term' && $.trim($this.val()).length === 0 && !listAll) {
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

    params['thisPage'] = 'vocabulary';
    params['submitButton'] = 'search';

    Vocabulary.searchDoSubmit(params);
};

Vocabulary.searchDoSubmit = function(params, whichRowToSelect, tab, dontShowMessage) {
    Vocabulary.searchClearResults();

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
                Vocabulary.searchDumpResults({
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

Vocabulary.searchDumpResults = function(o) {
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
            Vocabulary.searchDoSubmit(params);
        },
        linkFunction: function() {
            params.offset = ($(this).attr('rel') - 1) * recordsPerPage;
            Vocabulary.searchDoSubmit(params);
        },
        search_holder: $('#search_results')
    });

    if (config.whichRowToSelect && config.tab) {
        var serial = holder.childrenNotTemplate().find('div.box[rel]').filter(':' + config.whichRowToSelect).attr('rel');

        Core.changeTab(config.tab, Vocabulary.tabHandler, serial, true);
    }
};

Vocabulary.searchNavigate = function(tab, direction) {
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
            Vocabulary.searchDoSubmit(lastParams, (direction == 'next') ? 'first' : 'last', tab);
        }
    } else {
        Vocabulary.loadDescription(tab, newSerial);
    }
};

Vocabulary._loadCatalogingForm = function(data, record, tabHolder) {
    Vocabulary.clearAll();
    Cataloging.loadJson(tabHolder, record);
};

Vocabulary._loadCatalogingMarc = function(data, record) {
    $('#freemarc').val(record);
};

Vocabulary._loadCatalogingRecord = function(data, tabHolder) {
    Vocabulary.clearRecord();
    tabHolder.find('.tab_inner_title').text(data.title);
    var recordHolder = $('#record_results');

    $.applyTemplates({
        holder: recordHolder,
        array: data.fields
    });
};

Vocabulary._loadSearchMarc = function(data) {
    var marcLines = data.data.split(/\r?\n/);
    var fields = [];

    for (var i = 0, len = marcLines.length; i < len; i++) {
        var line = marcLines[i];

        if (line && line.match(/(\d\d\d) (.*)/)) {
            var field = RegExp.$1;
            var value = RegExp.$2;

            fields.push({
                field: field,
                value: value
            });
        }
    }

    return fields;
};

Vocabulary._loadSearchFields = function(data) {
    return data.fields;
}

Vocabulary.processResult = function(data, serial, from, tab, tabHolder, dontShowMessage) {
    $('div.tab_body[rel="' + tab + '"] h1:first').html('');

    if (data.success) {
        var expectedSerial = serial;
        if ($('#search_results').data('lastSerial') != expectedSerial) {
            return;
        }

        var expectedTab = from;
        if ($('div.tab_selected').attr('rel') != expectedTab) {
            return;
        }

        Core.changeTab(tab);

        if (!dontShowMessage) {
            $.msg();
        }

        if (!Vocabulary.isSearchOnly) {
            var record = data.data;

            if (tab == 'form') {
                Vocabulary._loadCatalogingForm(data, record, tabHolder);
            } else if (tab == 'marc') {
                Vocabulary._loadCatalogingMarc(data, record);
            } 
        } else {
            var fields;
            if (tab == 'marc') {
                fields = Vocabulary._loadSearchMarc(data);
            } else {
                fields = Vocabulary._loadSearchFields(data);
            }

            var tabHolderBody = tabHolder.find('.tab_inner_body');

            $.applyTemplates({
                holder: tabHolderBody,
                array: fields
            });
        }

        $('#content_outer').scrollTo(0, 0);
    } else {
        $.msg({cls: data.errorLevel, msg: data.message});
    }
};

Vocabulary.loadDescription = function(tab, serial, dontShowMessage) {
    var holder = $('#search_results');
    var tabHolder = $('div.tab_body[rel="' + tab + '"]');
    var tabHolderBody = tabHolder.find('.tab_inner_body');
    var from = $('div.tab_selected').attr('rel');

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
            thisPage: 'vocabulary',
            submitButton: 'open',
            type: tab,
            record_id: serial
        },
        loadingHolder: 'div.tab_body[rel="' + tab + '"] h1:first',
        success: function(data) {
            Vocabulary.processResult(data, serial, from, tab, tabHolder, dontShowMessage);
        }
    });
};

Vocabulary.loadConvertion = function(from, to) {
    var serial = $('#search_results').data('lastSerial');
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
            thisPage: 'vocabulary',
            submitButton: 'switch',
            from: from,
            to: to,
            data: data,
            serial: serial
        },
        loadingHolder: 'div.tab_body[rel="' + from + '"] h1:first',
        success: function(data) {
            Vocabulary.processResult(data, serial, from, to, tabHolder);
        }
    });
};

Vocabulary.save = function() {
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
            thisPage: 'vocabulary',
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
                if (Vocabulary.isNewRecord) {
                    Vocabulary.setIsNewRecord(false);
                    Core.changeTab('search', Vocabulary.tabHandler);
                } else {
                    Core.changeTab('search', Vocabulary.tabHandler);
                }
            }
        }
    });
};

VocabularySearch.tabHandler = function(tab, serial, force) {
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

    Vocabulary.loadDescription(tab, serial);
    return false;
};

VocabularySearch.searchSubmit = function() {
    Vocabulary.isSearchOnly = true;
    Vocabulary.searchSubmit.apply(Vocabulary, arguments);
};

VocabularySearch.searchNavigate = Vocabulary.searchNavigate;

VocabularySearch.searchAddTerm = Vocabulary.searchAddTerm;
VocabularySearch.searchClear = Vocabulary.searchClear;


