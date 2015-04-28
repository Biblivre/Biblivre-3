var Access = {};

Access.tabHandler = function(tab, params) {
    var fromTab = $('div.tab_selected').attr('rel');

    // dont change if we are changing to the same tab
    if (tab == fromTab) {
        return false;
    }

    if (tab == 'form') {
        Access.clearForm();
        return true;
    }

    if (tab == 'cards') {
        $('#user_selected').checkEmptyTemplate();

        if (params) {
            Circulation.selectUser(params);
        }
    }


    return true;
};

Access.previewForm = function() {
    var first = $('#prefix').val() + $('#start').val() + $('#suffix').val();

    $('#preview').val(first);
    return true;
};

Access.createNew = function() {
    Core.changeTab('form', Access.tabHandler);
};

Access.clearForm = function() {
    $('#card_list, #single_card').find(':input:enabled:not(button)').val('');
};

Access.searchSubmit = function(listAll) {
    var inputs = $('#card_search_box :input:enabled:not(button)');

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

    params['thisPage'] = 'admin_access';
    params['submitButton'] = 'search_cards';

    Access.searchDoSubmit(params);
};

Access.searchClearResults = function() {
    $('#search_results').resetTemplate();

    $('div.search_paging').empty();
};

Access.searchClear = function() {
    $('#card_search_box input:not(:hidden)').val('');

    Access.searchClearResults();
};

Access.searchDoSubmit = function(params) {
    Access.searchClearResults();

    $('#search_results').data('lastParams', params);

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: params,
        loadingHolder: 'div.search_paging:first',
        success: function(data) {
            if (data.success) {
                Access.searchDumpResults({
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

Access.deleteCard = function(cardName, card_id, button) {
    if (confirm(Translations.CONFIRM_DELETE_CARD($.trim(cardName)))) {
        var params = {
            thisPage: 'admin_access',
            submitButton: 'delete_card',
            card_id: card_id
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
    }
};

Access.searchDumpResults = function(o) {
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
            Access.searchDoSubmit(params);
        },
        linkFunction: function() {
            params.offset = ($(this).attr('rel') - 1) * recordsPerPage;
            Access.searchDoSubmit(params);
        }
    });
};

Access.blockCard = function(card_id, button) {
    var params = {
        thisPage: 'admin_access',
        submitButton: 'block_card',
        card_id: card_id
    };

    Access.doBlockCard(params, button);
};

Access.unBlockCard = function(card_id, button) {
    var params = {
        thisPage: 'admin_access',
        submitButton: 'unblock_card',
        card_id: card_id
    };

    Access.doBlockCard(params, button);
};

Access.doBlockCard = function(params, button) {
    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: params,
        success: function(data) {
            $.msg({
                cls: data.errorLevel,
                msg: data.message
            });

            if (data.success) {
                if (params.submitButton == 'block_card') {
                    $(button).parents('.box')
                        .removeClass('card_status_AVAILABLE')
                        .removeClass('card_status_IN_USE')
                        .removeClass('card_status_IN_USE_AND_BLOCKED')
                        .removeClass('card_status_CANCELLED')
                        .addClass('card_status_BLOCKED');
                } else {
                    $(button).parents('.box')
                        .removeClass('card_status_IN_USE')
                        .removeClass('card_status_BLOCKED')
                        .removeClass('card_status_IN_USE_AND_BLOCKED')
                        .removeClass('card_status_CANCELLED')
                        .addClass('card_status_AVAILABLE');
                }
            }
        }
    });
};

Access.save = function(mode) {
    var params = $('#' + mode + ' :input:enabled:not(button)').getFormValues();

    params.thisPage = 'admin_access';
    params.submitButton = (mode == 'single_card') ? 'add_card' : 'add_card_list';

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: params,
        success: function(response) {
            $.msg({
                cls: response.errorLevel,
                msg: response.message
            });

            if (response.success) {
                Core.changeTab('search', Access.tabHandler);
            }
        }
    });
};

Access.cardSearchSubmit = function(skipMessage) {
    var params = $('#card_search_box :input:enabled:not(button)').getFormValues();

    params['thisPage'] = 'access';
    params['submitButton'] = 'get_card';

    Access.cardSearchDoSubmit(params, skipMessage);
};

Access.cardSearchClearResults = function() {
    $('#card_search_results').resetTemplate();
};

Access.cardSearchDoSubmit = function(params, skipMessage) {
    Access.cardSearchClearResults();

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: params,
        success: function(data) {
            if (data.success) {
                // On success, dump the results to the page
                Access.cardSearchDumpResults({
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

Access.cardSearchDumpResults = function(o) {
     var config = $.extend({}, {
        holder: '#card_search_results'
    }, o);

    if (!config.data) {
        return;
    }

    // Showing the result count in the Message Div
    if (!config.skipMessage) {
        $.msg({msg: Translations.CARD_FOUND, animate: false});
    }

    var holder = $(config.holder);
    if (!holder) {
        return;
    }

    var card = config.data;

    $.applyTemplates({
        holder: holder,
        object: card
    });

    holder
        .removeClass('card_status_AVAILABLE')
        .removeClass('card_status_IN_USE')
        .removeClass('card_status_BLOCKED')
        .removeClass('card_status_IN_USE_AND_BLOCKED')
        .removeClass('card_status_CANCELLED');

    holder.addClass('card_status_' + card.status);
    holder.fadeIn();
};

Access.lend = function(button, card_id) {
    var params = {
        thisPage: 'access',
        submitButton: 'lend',
        card_id: card_id,
        user_id: $('#user_selected').data('lastUserId')
    };

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: params,
        success: function(data) {
            if (data.success) {
                $(button).parents('.box').fadeOut('normal', function() {
                    $(this).remove();
                });
            }

            $.msg({
                cls: data.errorLevel,
                msg: data.message
            });
        }
    });
};

Access.lendReturn = function(button, card_id, user_id) {
    var params = {
        thisPage: 'access',
        submitButton: 'return',
        card_id: card_id,
        user_id: user_id
    };

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: params,
        success: function(data) {
            if (data.success && card_id) {
                $(button).parents('.box').fadeOut('normal', function() {
                    $(this).remove();
                });
            }

            if (data.success && user_id) {
               $(button).parents('.box').addClass('user_has_card_false').removeClass('user_has_card_true');
            }

            $.msg({
                cls: data.errorLevel,
                msg: data.message
            });
        }
    });
};
