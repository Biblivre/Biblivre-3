var Permissions = {};

Permissions.tabHandler = function(tab, params) {
    var fromTab = $('div.tab_selected').attr('rel');

    // dont change if we are changing to the same tab
    if (tab == fromTab) {
        return false;
    }

    if (tab == 'permissions') {
        
        params = params || $('#user_search_results').childrenNotTemplate().find('.box[rel]:first').attr('rel');

        if (!params) {
            $.msg({cls: 'warning', msg: Translations.ERROR_SEARCH_FIRST});
            return false;
        }

        var tabHolderBody = $('#user_register_box');
        tabHolderBody.resetTemplate();

        $('#user_permissions_box input').removeAttr('checked');

        var buttonsBody = $('#user_submit_buttons');
        buttonsBody.resetTemplate();

        Circulation.selectUser(params, function(data) {
            $.applyTemplates({
                holder: tabHolderBody,
                object: data
            });

            var permissions = data.permissions || [];
            var $permissions = $('#user_permissions_box');
            $permissions.find(':checkbox').removeAttr('checked');

            for (var i = 0; i < permissions.length; i++) {
                $permissions.find(':checkbox[value=' + permissions[i] + ']').attr('checked', true);
            }

            $.applyTemplates({
                holder: buttonsBody,
                object: data
            });
        }, 'permissions', 'open');
    }

    return true;
};

Permissions.loadUserPermissions = function(userId) {
    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'permissions',
            submitButton: 'list_permissions',
            user_id: userId
        },
        //loadingHolder: 'div.user_selected_box h1:first',
        success: function(data) {
            if (data.success) {
                Permissions.permissionsDumpResults({
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

Permissions.permissionsDumpResults = function(o) {
    var config = $.extend({}, {
        holder: '#user_permissions'
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
        stripe: true
    });

    holder.find('button').removeAttr('disabled');
};

Permissions.check = function(o) {
    var cb = $(o).prev('input');

    if (cb.attr('checked')) {
        cb.removeAttr('checked');
    } else {
        cb.attr('checked', true);
    }
};

Permissions.checkAllPermissions = function(cb) {
    $(cb).parents('fieldset').find('input:checkbox[name=permissions]').attr('checked', cb.checked)
};

Permissions.save = function() {
    var params = $('#user_permissions_box input:checkbox[name=permissions]:checked, #user_register_box div:not(.template) input').getFormValues();

    params.thisPage = 'permissions';
    params.submitButton = 'save';

    Permissions.submit(params);
};

Permissions.removeLogin = function() {
    if (!confirm(Translations.CONFIRM_DELETE_LOGIN)) {
        return;
    }
    var params = $('#user_register_box div:not(.template) input').getFormValues();

    params.thisPage = 'permissions';
    params.submitButton = 'remove';

    Permissions.submit(params);
};


Permissions.submit = function(params) {
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
                Core.changeTab('search', Permissions.tabHandler);

                Circulation.searchSubmit($('#user_search_results').data('lastParams'), null, null, true);
            }
        }
    });
};

