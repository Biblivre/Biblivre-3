var Circulation = {};

Circulation.tabHandler = function(tab, params) {
    var fromTab = $('div.tab_selected').attr('rel');
    var myParams = params || {};

    var userId = myParams[0];
    var user = myParams[1]

    // dont change if we are changing to the same tab
    if (tab == fromTab) {
        return false;
    }

    if (tab != 'search') {
        userId = userId || $('#user_search_results').data('lastUserId') || $('#user_search_results').childrenNotTemplate().find('.box[rel]:first').attr('rel');

        if (!userId && (user === undefined)) {
            $.msg({cls: 'warning', msg: Translations.ERROR_SEARCH_FIRST});
            return false;
        }

        Circulation.loadUserDescription(tab, userId, user);
        return true;
    }

    return true;
};

Circulation.search = function(listAll) {
    var params = {};

    if (!listAll) {
        params = $('#user_search_box :input:enabled:not(button)').getFormValues();
    }

    params['thisPage'] = 'user_search';
    params['submitButton'] = 'search';

    Circulation.searchSubmit(params);
};

Circulation.searchSubmit = function(params, whichRowToSelect, tab, dontShowMessage) {

    Circulation.clearSearchResults();
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
                Circulation.dumpSearchResults({
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

// This function dumps the results to the page. It receives a config object
Circulation.dumpSearchResults = function(o) {
    var config = $.extend({}, {
        holder: '#user_search_results',
        pagingHolder: 'div.user_search_paging',
        dontShowMessage: false,
        msgFunction: Translations.USERS_FOUND
    }, o);

    if (!config.data || !config.data.results) {
        return;
    }

    // Showing the result count in the Message Div
    if (!config.dontShowMessage) {
        $.msg({msg: config.data.totalRecords + ' ' + config.msgFunction(config.data.totalRecords), animate: false});
    }

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

    var params = config.params;
    var numberOfPages = parseInt(config.data.totalPages, 10);
    var currentPage = parseInt(config.data.currentPage, 10);
    var recordsPerPage = parseInt(config.data.recordsPerPage, 10);

    Core.pagingGenerator({
        pagingHolder: $(config.pagingHolder),
        numberOfPages: numberOfPages,
        currentPage: currentPage,
        recordsPerPage: recordsPerPage,
        selectFunction: function() {
            params.offset = ($(this).val() - 1) * recordsPerPage;
            Circulation.searchSubmit(params);
        },
        linkFunction: function() {
            params.offset = ($(this).attr('rel') - 1) * recordsPerPage;
            Circulation.searchSubmit(params);
        },
        search_holder: $('#user_search_results')
    });

    if (config.whichRowToSelect && config.tab) {
        var userId = holder.childrenNotTemplate().find('div.box[rel]').filter(':' + config.whichRowToSelect).attr('rel');

        Circulation.loadUserDescription(config.tab, userId);
    }
};

Circulation.searchNavigate = function(tab, direction) {
    direction = direction || 'next';
    tab = tab || 'record';

    var lastUserId = $('#user_search_results').data('lastUserId');
    if (!lastUserId) {
        return;
    }

    var currentSelectedRow = $('#user_search_results .box[rel="' + lastUserId + '"]');
    var newUserId;
    if (direction == 'next') {
        newUserId = currentSelectedRow.parent().next(':not(.template):not(.template_empty_message)').children('.box[rel]').attr('rel');
    } else {
        newUserId = currentSelectedRow.parent().prev(':not(.template):not(.template_empty_message)').children('.box[rel]').attr('rel');
    }

    if (!newUserId) {
        var holder = $('#user_search_results');
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
            // This userSubmit must auto select the new record
            // Load search with params, selecting (first|last) row into the <tab>
            Circulation.searchSubmit(lastParams, (direction == 'next') ? 'first' : 'last', tab);
        }
    } else {
        Circulation.loadUserDescription(tab, newUserId);
    }
};

Circulation.loadUserDescription = function(tab, userId, user) {
    var holder = $('#user_search_results');
    var tabHolder = $('div.tab_body[rel="' + tab + '"]');
    var tabHolderTitle = tabHolder.find('.tab_inner_title');
    var tabHolderBody = tabHolder.find('.tab_inner_body');

    if (tab == "user_history") {
        $('#current_fines, #past_fines, #current_lendings, #past_lendings').resetTemplate();
    } else {
        tabHolderBody.resetTemplate();
    }

    holder.data('lastUserId', userId);

    if (user === undefined) {
        $.ajax({
            url: 'JsonController',
            type: 'POST',
            dataType: 'json',
            data: {
                thisPage: 'user_search',
                submitButton: tab,
                userid: userId
            },
            loadingHolder: 'div.tab_body[rel="' + tab + '"] h1:first',
            success: function(data) {
                if (data.success) {
                    var expectedUserId = userId;

                    if (holder.data('lastUserId') != expectedUserId) {
                        return;
                    }

                    if (tab == "record") {
                        $.msg();
                        tabHolderTitle.hide();

                        $.applyTemplates({
                            holder: tabHolderBody,
                            object: data
                        });

                        Circulation.photoUploadConfig(tabHolderBody);
                    }

                    if (tab == "logins") {
                        $.applyTemplates({
                            holder: tabHolderBody,
                            object: data
                        });
                    }

                    if (tab == "user_history") {
                        $.applyTemplates({
                            holder: $('#current_fines'),
                            array: data.currentFines,
                            stripe: true
                        });

                        $.applyTemplates({
                            holder: $('#past_fines'),
                            array: data.pastFines,
                            limit: 5,
                            stripe: true
                        });

                        $.applyTemplates({
                            holder: $('#current_lendings'),
                            array: data.currentLendings,
                            stripe: true
                        });

                        $.applyTemplates({
                            holder: $('#past_lendings'),
                            array: data.pastLendings,
                            limit: 5,
                            stripe: true
                        });
                    }
                } else {
                    $.msg({
                        cls: data.errorLevel,
                        msg: data.message
                    })
                }
            }
        });
    } else {
        $.msg();
        tabHolderTitle.hide();

        $.applyTemplates({
            holder: tabHolderBody,
            object: user
        });

        Circulation.photoUploadConfig(tabHolderBody);
    }
}

Circulation.clearSearchResults = function() {
    $('#user_search_results').removeData('lastUserId').resetTemplate();
    $('div.user_search_paging').empty();
};

Circulation.userSave = function() {
    var params = $('#user_register_box .registerTable:not(.template) :input:not(button)').getFormValues();

    params['thisPage'] = 'user';
    params['submitButton'] = 'save_user';

    Circulation.userSaveSubmit(params);
};

Circulation.userSaveSubmit = function(params, callback) {
    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: params,
        loadingHolder: 'div.tab_body[rel=record] h1:first',
        success: function(data) {
            $.msg({
                cls: data.errorLevel,
                msg: data.message
            });

            if ($.isFunction(callback)) {
                callback(data);
            }
        }
    });
};

Circulation.deleteUser = function() {
    if (!confirm(Translations.CONFIRM_DELETE)) {
        return;
    }

    var params = $('#user_register_box .registerTable:not(.template) :input:not(button)').getFormValues();

    params['thisPage'] = 'user';
    params['submitButton'] = 'delete_user';

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: params,
        loadingHolder: 'div.tab_body[rel=record] h1:first',
        success: function(data) {
            $.msg({
                cls: data.errorLevel,
                msg: data.message
            });

            if (data.success) {
                $('#user_search_results .box[rel="' + params['user_id'] + '"]').parent().remove();
                $('#user_search_results').removeData('lastUserId');
                Core.changeTab('search', Circulation.tabHandler);
            }
        }
    });
};

Circulation.blockUser = function(user_id, button) {
    var params = {
        thisPage: 'user',
        submitButton: 'block_user',
        user_id: user_id
    };

    Circulation.doBlockUser(params, button);
};

Circulation.unBlockUser = function(user_id, button) {
    var params = {
        thisPage: 'user',
        submitButton: 'unblock_user',
        user_id: user_id
    };

    Circulation.doBlockUser(params, button);
};

Circulation.doBlockUser = function(params, button) {
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
                if (params.submitButton == 'block_user') {
                    $(button).parents('.box').addClass('user_status_BLOCKED');
                } else {
                    $(button).parents('.box').removeClass('user_status_BLOCKED');
                }
            }
        }
    });
};

Circulation.savePassword = function() {
    var params = $('#user_register_box .registerTable:not(.template) :input:not(button)').getFormValues();

    params['thisPage'] = 'user';
    params['submitButton'] = 'save_login';

    if (!params['OLDPASSWORD']) {
        alert(Translations.ERROR_EMPTY_PASSWORD);
        return false;
    }

    if (params['NEWPASSWORD'] != params['NPRPASSWORD']) {
        alert(Translations.ERROR_PASSWORDS_DONT_MATCH);
        return false;
    }

    return Circulation.userSaveSubmit(params);
};

Circulation.saveLogin = function() {
    var params = $('#user_register_box .registerTable:not(.template) :input:not(button)').getFormValues();

    params['thisPage'] = 'user';
    params['submitButton'] = 'save_login';

    if (!params['PASSWORD']) {
        alert(Translations.ERROR_EMPTY_PASSWORD);
        return false;
    }

    if (params['PASSWORD'] != params['PASSWORD2']) {
        alert(Translations.ERROR_PASSWORDS_DONT_MATCH);
        return false;
    }

    if (params['LOGIN_NAME'] != undefined && !params['LOGIN_NAME']) {
        alert(Translations.ERROR_EMPTY_LOGINNAME);
        return false;
    }

    return Circulation.userSaveSubmit(params, function() {
        Circulation.loadUserDescription('logins', params['user_id']);
    });
};

Circulation.removeLogin = function() {
    if (!confirm(Translations.CONFIRM_DELETE)) {
        return false;
    }

    var params = $('#user_register_box .registerTable:not(.template) :input:not(button)').getFormValues();

    params['thisPage'] = 'user';
    params['submitButton'] = 'remove_login';

    return Circulation.userSaveSubmit(params, function() {
        Circulation.loadUserDescription('logins', params['user_id']);
    });
};

Circulation.photoUploadConfig = function(tabHolderBody) {
    var button = tabHolderBody.children(':not(.template)').find('.photo_upload_button');

    new AjaxUpload(button, {
        action: 'UploadController',
        autoSubmit: true,
        responseType: 'json',
        onComplete: function(file, response) {
            if (response && response.success && response.id) {
                var photo = $('#user_register_box .registerTable:not(.template) .userBigPhoto');
                
                photo.attr("src", "DigitalMediaController?default=images/photo.jpg&id=" + response.id);
                $('#user_register_box .registerTable:not(.template) :input[name=NEW_USER_PHOTO]').val(response.id);
            }
        }
   });
};

Circulation.createUserCard = function(userid, button) {
    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'user',
            submitButton: 'create_user_card',
            userid: userid
        },
        success: function(response) {
            $.msg({
                cls: response.errorLevel,
                msg: response.message
            });
            
            if (response.success) {
                $(button).attr("disabled", true).html(Translations.USER_CARD_ADDED_TO_QUEUE);
            }
        }
    });
};

Circulation.selectUser = function(userId, callback, thisPage, submitButton) {
    var holder = $('#user_selected');

    holder.resetTemplate();
    holder.data('lastUserId', userId);

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: thisPage || 'user',
            submitButton: submitButton || 'record',
            userid: userId
        },
        loadingHolder: 'div.user_selected_box h1:first',
        success: function(data) {
            if (data.success) {
                var expectedUserId = userId;

                if (holder.data('lastUserId') != expectedUserId) {
                    return;
                }

                $.msg();

                $.applyTemplates({
                    holder: holder,
                    object: data
                });

                if ($.isFunction(callback)) {
                    callback(data);
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