var Acquisition = {};
var AcquisitionSearch = {};
var AcquisitionQuotation = {};

Acquisition.isNewRecord = false;

Acquisition.createNew = function(handler) {
    Acquisition.setIsNewRecord(true);
    $.msg({cls: 'normal', msg: ''});

    Core.changeTab('form', Acquisition.tabHandler, [handler, 0]);
    Acquisition.clearAll();
};

Acquisition.clearAll = function() {
    $('#form_edit_box :input:not(button)').val('');
    $('#form_edit_box :input.use_default_value').each(function () {
        $(this).val($(this).attr('defaultvalue'));
    });

    $('#serialQuotation option:not(.dont_clear)').remove();
    AcquisitionQuotation.clearRequests();
};

Acquisition.setIsNewRecord = function(bool) {
    Acquisition.isNewRecord = bool;
    Acquisition.setNavigationDisplay(!bool);
};

Acquisition.setNavigationDisplay = function(bool) {
    $('div.navigation_top, div.navigation_bottom').toggle(bool);
};

Acquisition.tabHandler = function(tab, params, force) {
    var fromTab = $('div.tab_selected').attr('rel');
    var handler;
    var serial;

    if (params) {
        handler = params[0];
        serial = params[1];
    }

    // dont change if we are changing to the same tab
    if (tab == fromTab && !force) {
        return false;
    }

    serial = serial || $('#search_results').data('lastSerial') || $('#search_results').childrenNotTemplate().find('.box[rel]:first').attr('rel');

    if (tab == 'form') {
        // going from search to edit
        if (Acquisition.isNewRecord) {
            $('#search_results').data('lastSerial', 0);

            if (handler == 'order') {
                $('#received_order input:not(:checkbox)').attr('disabled', true);
            }
            return true;
        }

        if (!serial) {
            $.msg({
                cls: 'warning',
                msg: Translations.ERROR_SEARCH_FIRST
            });
            return false;
        }

        Acquisition.clearAll();

        Core.populateForm({
            params: {
                thisPage: handler,
                submitButton: 'open',
                serial: serial
            },
            root: $('#form_edit_box'),
            callback: function(data) {
                if (handler == 'quotation') {
                    AcquisitionQuotation.arrayToQueue(data.itemQuotationList);
                } else if (handler == 'order') {
                    var serialSupplier = (data.quotation || {}).serialSupplier;

                    $('#form_edit_box #serialSupplier').val(serialSupplier);
                    Acquisition.doListQuotations(serialSupplier, data.serialQuotation);

                    $('#received_order input:not(:checkbox)').attr('disabled', data.status != '1');
                }
            }
        });

        $('#search_results').data('lastSerial', serial);

        return true;
    }

    if (tab == 'search') {
        if (Acquisition.isNewRecord) {
            if (confirm(Translations.CONFIRM_CANCEL)) {
                Acquisition.setIsNewRecord(false);
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

    return false;
};

Acquisition.searchSubmit = function(handler, listAll) {
    var inputs = $('#search_box :input:enabled:not(button)');

    var block = false;
    inputs.each(function() {
        var $this = $(this);

        if ($this.attr('name') == 'search_term' && $.trim($this.val()).length === 0 && !listAll) {
            alert(Translations.ERROR_TYPE_ALL_SEARCH_PARAMS);
            $this.focus();
            block = true;
            return;
        }
    });

    if (block) {
        return;
    }

    var params = (listAll) ? {} : inputs.getFormValues();

    var statusInput = $("input[name='statusCheckBox']:checked").val();
    if (statusInput) {
        params['search_status'] = statusInput;
    }
    params['thisPage'] = handler;
    params['submitButton'] = 'search';

    Acquisition.searchDoSubmit(handler, params);
};

Acquisition.searchClearResults = function() {
    $('#search_results')
        .removeData('lastSerial')
        .resetTemplate();

    $('div.search_paging').empty();
    $('div.search_print').hide();
};

Acquisition.searchClear = function() {
    $('#search_box')
        .find('.search_term:not(.dont_clear)')
            .remove()
        .end()
        .find('input:not(:hidden)').val('');

    Acquisition.searchClearResults();
};

Acquisition.searchDoSubmit = function(handler, params, whichRowToSelect, tab, dontShowMessage) {
    Acquisition.searchClearResults();

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
                Acquisition.searchDumpResults(handler, {
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

Acquisition.deleteRecord = function(serial, button, handler) {
    if (confirm(Translations.CONFIRM_DELETE)) {
        var params = {
            thisPage: handler,
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

Acquisition.searchDumpResults = function(handler, o) {
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
            Acquisition.searchDoSubmit(handler, params);
        },
        linkFunction: function() {
            params.offset = ($(this).attr('rel') - 1) * recordsPerPage;
            Acquisition.searchDoSubmit(handler, params);
        },
        search_holder: $('#search_results')
    });

    if (config.whichRowToSelect && config.tab) {
        var serial = holder.childrenNotTemplate().find('div.box[rel]').filter(':' + config.whichRowToSelect).attr('rel');

        Core.changeTab(config.tab, Acquisition.tabHandler, [handler, serial], true);
    }

};

Acquisition.searchNavigate = function(handler, tab, direction) {
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
            Acquisition.searchDoSubmit(handler, lastParams, (direction == 'next') ? 'first' : 'last', tab);
        }
    } else {
        Acquisition.loadDescription(tab, newSerial);
    }
};

Acquisition.save = function(handler) {
    var serial = $('#search_results').data('lastSerial');
    var data = '';

    if (!serial) {
        serial = '0';
    }

    data = Acquisition.createJson(handler, $('div.tab_body[rel="form"]'));

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: handler,
            submitButton: 'save',
            serial: serial,
            data: data
        },
        success: function(response) {
            $.msg({
                cls: response.errorLevel,
                msg: response.message
            });

            if (response.success) {
                if (Acquisition.isNewRecord) {
                    Acquisition.setIsNewRecord(false);
                } else {
                    var params = $('#search_results').data('lastParams');
                    Acquisition.searchDoSubmit(handler, params, null, null, true);
                }
                Core.changeTab('search', Acquisition.tabHandler, handler);
            }
        }
    });
};

Acquisition.createJson = function(handler, root) {
    var json = {};
    root = root || $(document);

    root.find('fieldset').filter(function() {
        return $(this).is(':visible');
    }).find(':input:enabled').each(function() {
        var input = $(this);
        var name = input.attr('name');
        var type = input.attr('type');
        var value = input.val();

        if (type == 'checkbox' || type == 'radio') {
            if (this.checked) {
                json[name] = value;
            }
        } else {
            json[name] = value;
        }
    });

    if (handler == 'quotation') {
        json.itemQuotationList = AcquisitionQuotation.queueToArray();
    }

    return JSON.stringify(json);
};

AcquisitionQuotation.selectRequest = function(obj) {
    var $obj = $(obj);
    var values = $obj.val().split('|');

    $obj.parents('fieldset').find('input[name=quotationQuantity]').val(values[1] || '');
    $obj.parents('fieldset').find('input[name=unitValue]').val('').focus();
};

AcquisitionQuotation.addRequest = function(obj) {
    var $obj = $(obj).parents('fieldset');

    var requestSelect = $obj.find('select[name=serialRequisition]');
    
    var requestvalue = requestSelect.val();
    if (!requestvalue) {
        return false;
    }

    var requestArr = requestvalue.split('|');

    var title = requestSelect.find('option[value="' + requestvalue + '"]').text();
    var qtd = $obj.find('input[name=quotationQuantity]').val();
    var value = $obj.find('input[name=unitValue]').val().replace(',', '.');

    if (!Core.isNumeric(qtd) || !Core.isFloat(value) || parseInt(qtd, 10) <= 0 || parseFloat(value, 10) <= 0.0) {
        alert(Translations.ERROR_REQUEST_VALUES_MUST_BE_POSITIVE_NUMBERS);
        return false;
    }

    AcquisitionQuotation.addRequestToQueue({
        request_id: requestArr[0],
        title: $.trim(title),
        qtd: qtd,
        value: value
    }, true);

    return true;
};

AcquisitionQuotation.addRequestToQueue = function(req, showMessages) {
    var $tbody = $('table.requisition_table tbody');

    var block = false;
    $tbody.children().each(function() {
        var data = $(this).data('request');

        if (data && data.request_id == req.request_id) {
            block = true;
            return false;
        }

        return true;
    });

    if (block) {
        if (showMessages) {
            alert(Translations.ERROR_REQUEST_ALREADY_IN_QUEUE);
        }
        return false;
    }

    $tbody.append(
        $('<tr></tr>')
            .append($('<td style="text-align: left;"></td>').text(req.title))
            .append($('<td></td>').text(req.qtd))
            .append($('<td></td>').text(parseFloat(req.value, 10).toFixed(2)))
            .append($('<td class="pointer"></td>').text('X').click(function() {
                AcquisitionQuotation.removeRequest(req.request_id);
            }))
            .data('request', req)
        );

    return true;
};

AcquisitionQuotation.queueToArray = function() {
    var $tbody = $('table.requisition_table tbody');
    var arr = [];

    $tbody.children().each(function() {
        var data = $(this).data('request');

        if (data) {
            arr.push(data);
        }
    });

    return arr;
};

AcquisitionQuotation.arrayToQueue = function(arr) {
    if (!arr) {
        return;
    }
    
    for (var i = 0, imax = arr.length; i < imax; i++) {
        AcquisitionQuotation.addRequestToQueue(arr[i]);
    }
};

AcquisitionQuotation.removeRequest = function(request_id) {
    var $tbody = $('table.requisition_table tbody');

    $tbody.children().each(function() {
        var data = $(this).data('request');

        if (data && data.request_id == request_id) {
            $(this).remove();
            return false;
        }

        return true;
    });
};

AcquisitionQuotation.clearRequests = function() {
    $('table.requisition_table tbody').empty();
};

Acquisition.listQuotations = function(obj) {
    $('#serialQuotation option:not(.dont_clear)').remove();
    $('#quotationInfo input').val('');
    AcquisitionQuotation.clearRequests();

    var serial = $(obj).val();
    if (!serial) {
        return;
    }

    Acquisition.doListQuotations(serial);
};

Acquisition.doListQuotations = function(serialSupplier, forceSerialQuotation) {

    if (!serialSupplier) {
        return;
    }

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'quotation',
            submitButton: 'search',
            search_term: serialSupplier,
            search_type: 'serialSupplier'
        },
        success: function(response) {
            if (response.success && response.results) {
                var quotationsSelect = $('#serialQuotation');

                for (var i = 0; i < response.results.length; i++) {
                    var quotation = response.results[i];
                    var text = '';

                    if (quotation.items.length > 2) {
                        text = quotation.items.slice(0, 2).join(', ') + ', ...';
                    } else {
                        text = quotation.items.join(', ');
                    }

                    if (text.length > 50) {
                        text = text.substring(0, 50) + '...';
                    }

                    $('<option></option>')
                        .attr('value', quotation.serial)
                        .html(text)
                        .data('quotation', quotation)
                        .appendTo(quotationsSelect);
                }

                if (forceSerialQuotation) {
                    quotationsSelect.val(forceSerialQuotation);
                    Acquisition.showQuotation(quotationsSelect);
                }
            }
        }
    });
};

Acquisition.showQuotation = function(obj) {
    $('#quotationInfo input').val('');
    AcquisitionQuotation.clearRequests();

    var option = $(obj).children('option[value=' + $(obj).val() + ']');
    var quotation = option.data('quotation');

    if (!quotation) {
        return;
    }

    $('#deliveryTime').val(quotation.deliveryTime);

    AcquisitionQuotation.arrayToQueue(quotation.itemQuotationList);
};
