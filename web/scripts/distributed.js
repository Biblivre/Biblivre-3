var DistributedSearch = {};

DistributedSearch.tabHandler = function(tab, serial, force) {
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

    if (tab != 'search') {
        DistributedSearch.loadDescription(tab, serial);
        return false;
    }
    return true;
};

DistributedSearch.processResult = function(data, serial, from, tab, tabHolder, dontShowMessage) {
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

        tabHolder.find('.tab_inner_title').text(data.title);

        if (tab == 'marc') {
            $('#freemarc').val(data.data);
        } else {
            var tabHolderBody = tabHolder.find('.tab_inner_body');

            $.applyTemplates({
                holder: tabHolderBody,
                array: data.fields
            });
        }

        $('#content_outer').scrollTo(0, 0);
    } else {
        $.msg({cls: data.errorLevel, msg: data.message});
    }
};

DistributedSearch.loadDescription = function(tab, serial, dontShowMessage) {
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
            thisPage: 'distributed_search',
            submitButton: 'open',
            type: tab,
            index: serial
        },
        loadingHolder: 'div.tab_body[rel="' + tab + '"] h1:first',
        success: function(data) {
            DistributedSearch.processResult(data, serial, from, tab, tabHolder, dontShowMessage);
        }
    });
};

DistributedSearch.searchClearResults = function() {
    $('#search_results').removeData('lastSerial').resetTemplate();

    $('div.search_paging').empty();
};

DistributedSearch.searchClear = function() {
    $('#biblio_search_box').find('input:not(:hidden):not(:checkbox)').val('');
    $('#biblio_search_box').find('input:not(:hidden):checkbox').attr('checked', false);
    $('#search_results').removeData('lastParams');

    DistributedSearch.searchClearResults();
};

DistributedSearch.searchSubmit = function() {
    var inputs = $('#biblio_search_box :checkbox:checked');
    var params = inputs.getFormValues();

    params['SEARCH_TERM'] = $('#biblio_search_box :input[name=SEARCH_TERM]').val();
    params['SEARCH_ATTR'] = $('#biblio_search_box :input[name=SEARCH_ATTR]').val();

    if ($.trim(params['SEARCH_TERM']).length === 0) {
        alert(Translations.ERROR_TYPE_ALL_SEARCH_PARAMS);
        $('#biblio_search_box :input[name=SEARCH_TERM]').focus();
        return;
    }

    params['thisPage'] = 'distributed_search';
    params['submitButton'] = 'search';

    DistributedSearch.searchDoSubmit(params);
};

DistributedSearch.searchDoSubmit = function(params, whichRowToSelect, tab, dontShowMessage) {
    DistributedSearch.searchClearResults();

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
                DistributedSearch.searchDumpResults({
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

DistributedSearch.searchDumpResults = function(o) {
    var config = $.extend({}, {
        holder: '#search_results',
        dontShowMessage: false
    }, o);

    if (!config.data || !config.data.results) {
        return;
    }

    // Showing the result count in the Message Div
    if (!config.dontShowMessage) {
        $.msg({msg: config.data.totalRecords + ' ' + Translations.RECORDS_FOUND(config.data.totalRecords), cls: 'normal', animate: false});
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
            params.submitButton = 'paginate';
            DistributedSearch.searchDoSubmit(params);
        },
        linkFunction: function() {
            params.offset = ($(this).attr('rel') - 1) * recordsPerPage;
            params.submitButton = 'paginate';
            DistributedSearch.searchDoSubmit(params);
        },
        search_holder: $('#search_results')
    });

    if (config.whichRowToSelect && config.tab) {
        var serial = holder.childrenNotTemplate().find('div.box[rel]').filter(':' + config.whichRowToSelect).attr('rel');

        Core.changeTab(config.tab, DistributedSearch.tabHandler, serial, true);
    }
};

DistributedSearch.searchNavigate = function(tab, direction) {
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
            lastParams.submitButton = 'paginate';
            DistributedSearch.searchDoSubmit(lastParams, (direction == 'next') ? 'first' : 'last', tab);
        }
    } else {
        DistributedSearch.loadDescription(tab, newSerial);
    }
};

DistributedSearch.save = function() {
    var freemarc = $('#freemarc').val();

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'distributed',
            submitButton: 'save',
            freemarc: freemarc
        },
        success: function(response) {
            $.msg({
                cls: response.errorLevel,
                msg: response.message
            });

            if (response.success) {
                Core.changeTab('search', DistributedSearch.tabHandler);
            }
        }
    });
};