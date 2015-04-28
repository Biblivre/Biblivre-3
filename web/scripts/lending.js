var Lending = {};

Lending.tabHandler = function(tab, params) {
    var fromTab = $('div.tab_selected').attr('rel');

    // dont change if we are changing to the same tab
    if (tab == fromTab) {
        return false;
    }

    if (tab == 'lendings') {
        $('#user_selected').checkEmptyTemplate();

        if (params) {
            $('#user_lent_books').resetTemplate();

            Circulation.selectUser(params, function() {
                $('#user_lents').show();
                Lending.clearReceiptItens();
                Lending.loadUserLents(params);
            });
        }
    } else if (tab == 'list') {
        Lending.listAll();
    }

    return true;
};

Lending.listAll = function() {
    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'lending',
            submitButton: 'list_all_lendings'
        },
        //loadingHolder: 'div.user_selected_box h1:first',
        success: function(data) {
            if (data.success) {
                $('#lending_results').resetTemplate();

                Lending.holdingLentsDumpResults({
                    data: data,
                    holder: '#lending_results',
                    limit: 10
                });
            } else {
                $.msg({
                    cls: data.errorLevel,
                    msg: data.message
                })
            }
        }
    });

};

Lending.receiptItems = [];

Lending.clearReceiptItens = function() {
    Lending.receiptItems = [];
    $('button.receipt_button').attr('disabled', true);
};

Lending.addReceiptItem = function(action, userSerial, holdingSerial) {
    Lending.receiptItems.push(action + "_" + userSerial + "_" + holdingSerial);
    $('button.receipt_button').removeAttr('disabled');
};

Lending.generateReceipt = function() {
    if (Lending.receiptItems) {
        window.open('Controller?thisPage=circulation_lending_receipts&submitButton=LENDING_RECEIPT&items=' + Lending.receiptItems.join(',') + "&user_serial=" + $('#user_selected').data('lastUserId'));
    } else {
        alert(Translations.ERROR_EMPTY_RECEIPT_LIST);
    }
};

Lending.loadUserLents = function(userId) {
    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'lending',
            submitButton: 'list_lent',
            user_id: userId
        },
        //loadingHolder: 'div.user_selected_box h1:first',
        success: function(data) {
            if (data.success) {
                Lending.holdingLentsDumpResults({
                    data: data
                });
            } else {
                $.msg({
                    cls: data.errorLevel,
                    msg: data.message
                })
            }
        }
    });
};

Lending.holdingLentsDumpResults = function(o) {
    var config = $.extend({}, {
        holder: '#user_lent_books'
    }, o);

    if (!config.data) {
        return;
    }

    var holder = $(config.holder);
    if (!holder) {
        return;
    }

    $.applyTemplates({
        holder: holder,
        array: config.data.data,
        stripe: true,
        limit: config.limit
    });

    holder.find('button').removeAttr('disabled');
};

Lending.holdingSearchSubmit = function(skipMessage) {
    var params = $('#holding_search_box :input:enabled:not(button)').getFormValues();

    params['thisPage'] = 'lending';
    params['submitButton'] = 'search';

    Lending.holdingSearchDoSubmit(params, skipMessage);
};

Lending.holdingSearchClearResults = function() {
    $('#holding_search_results').resetTemplate();
};

Lending.holdingSearchDoSubmit = function(params, skipMessage) {
    Lending.holdingSearchClearResults();

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: params,
        success: function(data) {
            if (data.success) {
                // On success, dump the results to the page
                Lending.holdingSearchDumpResults({
                    params: params,
                    data: data,
                    skipMessage: skipMessage
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

Lending.holdingSearchDumpResults = function(o) {
     var config = $.extend({}, {
        holder: '#holding_search_results'
    }, o);

    if (!config.data) {
        return;
    }

    // Showing the result count in the Message Div
    if (!config.skipMessage) {
        $.msg({msg: Translations.HOLDING_FOUND, animate: false});
    }

    var holder = $(config.holder);
    if (!holder) {
        return;
    }

    $.applyTemplates({
        holder: holder,
        object: config.data
    });

    if (config.data.lent) {
        holder.addClass('lent');
    } else {
        holder.removeClass('lent');
    }

    if (config.data.reserved) {
        holder.addClass('reserved');
    } else {
        holder.removeClass('reserved');
    }

    holder.fadeIn();
};

Lending.lend = function(button, holdingSerial) {
    var params = {
        thisPage: 'lending',
        submitButton: 'lend',
        holding_serial: holdingSerial,
        user_id: $('#user_selected').data('lastUserId')
    };

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: params,
        success: function(data) {
            if (data.success) {
                var holder = $('#user_lent_books');

                $.applyTemplates({
                    holder: holder,
                    object: data,
                    prepend: true
                });

                $(button).parents('.box').fadeOut('normal', function() {
                    $(this).remove();
                });

                $.msg({
                    msg: data.message
                });

                Lending.addReceiptItem('lending', data.userSerial, data.holdingSerial);
            } else {
                $.msg({
                    cls: data.errorLevel,
                    msg: data.message
                });
            }
        }
    });
};


Lending.lendRenew = function(button, holdingSerial, userSerial, fromSearch, daysLate) {
    daysLate = parseInt(daysLate, 10);

    if (daysLate > 0) {
        alert(Translations.ERROR_RETURN_IS_LATE_SO_USER_CANT_RENEW);
        return;
    }

    var params = {
        thisPage: 'lending',
        submitButton: 'renew',
        holding_serial: holdingSerial,
        user_id: userSerial
    };

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: params,
        success: function(data) {
            if (data.success) {
                if (fromSearch) {
                    $('#holding_search_box input[name=SEARCH_SERIAL]').val(holdingSerial);

                    Lending.holdingSearchSubmit(true);
                } else {
                    $(button).parents('.box').remove();
                    var holder = $('#user_lent_books');

                    $.applyTemplates({
                        holder: holder,
                        object: data,
                        prepend: true
                    });
                }

                $.msg({
                    msg: data.message
                });

                Lending.addReceiptItem('renew', data.userSerial, data.holdingSerial);
            } else {
                $.msg({
                    cls: data.errorLevel,
                    msg: data.message
                });
            }
        }
    });
};


Lending.lendReturn = function(button, holdingSerial, userSerial, fromSearch, daysLate, fineValue) {
    daysLate = parseInt(daysLate, 10);

    if (!Lending.openFinesDialog(button, fromSearch, userSerial, holdingSerial, daysLate, fineValue)) {
        Lending.completeLendReturn(button, holdingSerial, userSerial, fromSearch);
    }
};

Lending.completeLendReturn = function(button, holdingSerial, userSerial, fromSearch, fine_value, fine_action) {
    var params = {
        thisPage: 'lending',
        submitButton: 'return',
        holding_serial: holdingSerial,
        user_id: userSerial,
        fine_value: fine_value,
        fine_action: fine_action
    };

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: params,
        success: function(data) {
            if (data.success) {
                if (fromSearch) {
                    $('#holding_search_box .input_holding_serial').val(holdingSerial);
                    Lending.holdingSearchSubmit(true);
                } else {
                    $(button).parents('.box').fadeOut('normal', function() {
                        $(this).remove();

                        $('#user_lent_books').checkEmptyTemplate();
                    });
                }

                $.msg({
                    msg: data.message
                });

                if (userSerial == $('#user_selected').data('lastUserId')) {
                    Lending.addReceiptItem('return', data.userSerial, data.holdingSerial);
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

Lending.userSearchSubmit = function(listAll) {
    var params = {};

    if (!listAll) {
        params = $('#user_search_box :input:enabled:not(button)').getFormValues();
    }

    params['thisPage'] = 'user_search';
    params['submitButton'] = 'search';
    
    Lending.userSearchDoSubmit(params);
};
    
Lending.userSearchClearResults = function() {
    $('#user_search_results')
        .removeData('lastUserId')
        .resetTemplate();

    $('div.user_search_paging').empty();
};
    
Lending.userSearchDoSubmit = function(params) {
    Lending.userSearchClearResults();

    $('#user_search_results').data('lastParams', params);

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: params,
        loadingHolder: 'div.user_search_paging:first',
        success: function(data) {
            if (data.success) {
                // On success, dump the results to the page
                Lending.userSearchDumpResults({
                    params: params,
                    data: data
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

Lending.userSearchDumpResults = function(o) {
    var config = $.extend({}, {
        holder: '#user_search_results',
        linhas: '.user_search_result'
    }, o);

    if (!config.data || !config.data.results) {
        return;
    }

    // Showing the result count in the Message Div
    $.msg({msg: config.data.totalRecords + ' ' + Translations.USERS_FOUND(config.data.totalRecords), animate: false});

    var holder = $(config.holder);

    if (!holder) {
        return;
    }

    holder.removeData('lastUserId');

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
        pagingHolder: $('div.user_search_paging'),
        numberOfPages: numberOfPages,
        currentPage: currentPage,
        recordsPerPage: recordsPerPage,
        selectFunction: function() {
            params.offset = ($(this).val() - 1) * recordsPerPage;
            Lending.userSearchDoSubmit(params);
        },
        linkFunction: function() {
            params.offset = ($(this).attr('rel') - 1) * recordsPerPage;
            Lending.userSearchDoSubmit(params);
        }
    });
};

Lending.transferLending = function(obj) {
    var holder = $('#user_lent_books');
    var tempDiv = $('<div></div>').prependTo(holder);
    
    obj.effect('transfer', {to: tempDiv}, 600, function() {
        $(this).clone().removeAttr('id').hide().prependTo(holder).slideDown('fast');
    });
};

Lending.openFinesDialog = function(button, fromSearch, user_serial, holding_serial, days_late, fine_value) {
    if (!user_serial || !holding_serial || !days_late || !fine_value) {
        return false;
    }

    var form = $("#fines_form")
        .clone(true).removeAttr('id')
        .data('user_serial', user_serial)
        .data('holding_serial', holding_serial)
        .data('transfer_button', button)
        .data('from_search', fromSearch);

    form.find('.fine_days_late').text(days_late);
    form.find('.fine_price_per_day').text((fine_value / days_late).toFixed(2));
    form.find('input[name=fine_value]').val(parseFloat(fine_value).toFixed(2));

    form.dialog({
        closeOnEscape: false,
        draggable: false,
        resizable: false,
        width: 360,
        modal: true
    });

    return true;
};

Lending.closeFinesDialog = function(button) {
    $(button).parents('.fines_form').dialog('destroy');
};

Lending.saveFines = function(button, action) {
    var parent = $(button).parents('.fines_form')
    var input = parent.find(':input[name=fine_value]');

    var user_serial = parent.data('user_serial');
    var holding_serial = parent.data('holding_serial');
    var transfer_button = parent.data('transfer_button');
    var from_search = parent.data('from_search');

    var fine_value = input.val();
    var fine_action = null;

    if (action == 'accredit') {
        fine_value = 0;
        fine_action = 'pay';
    } else if (action == 'pay') {
        fine_action = 'pay';
    } else {
        fine_action = 'create';
    }

    if (!Core.isFloat(fine_value)) {
        alert(Translations.ERROR_FINE_MUST_HAVE_NUMERIC_VALUE);
        return;
    }

    Lending.completeLendReturn(transfer_button, holding_serial, user_serial, from_search, fine_value, fine_action);
    Lending.closeFinesDialog(button);
};

Lending.changeFineStatus = function(fine_id, status, button) {
    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'lending',
            submitButton: 'pay_fine',
            fine_id: fine_id,
            action: status
        },
        success: function(data) {
            if (data.success) {
                Lending.transferFine($(button).parents('.box:first'));
            } else {
                $.msg({
                    cls: data.errorLevel,
                    msg: data.message
                });
            }
        }
    });
};

Lending.transferFine = function(obj) {
    var holder = $('#past_fines');
    var tempDiv = $('<div></div>').prependTo(holder);
    obj.effect('transfer', {to: tempDiv}, 600, function() {
        $(this)
            .clone().hide()
            .find('button')
                .remove()
            .end()
            .prependTo(holder).slideDown('fast');

        $(this).remove();
        holder.removeClass('empty');
        $('#current_fines').checkEmptyTemplate();
    });
};