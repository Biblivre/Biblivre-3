var UserTypes = {};

UserTypes.createNew = function() {
    $.msg({cls: 'normal', msg: ''});

    Core.changeTab('form', UserTypes.tabHandler, 0);
    UserTypes.clearAll();
};

UserTypes.clearAll = function() {
    $('#form_edit_box :input:not(button)').val('');
    $('#form_edit_box :input.use_default_value').each(function () {
        $(this).val($(this).attr('defaultvalue'));
    });
};

UserTypes.tabHandler = function(tab, serial, force) {
    var fromTab = $('div.tab_selected').attr('rel');

    // dont change if we are changing to the same tab
    if (tab == fromTab && !force) {
        return false;
    }

    if (tab == 'form') {
        if (!serial) {
            serial = 0;
        }

        UserTypes.clearAll();

        var type = $('#search_results div.box[rel=' + serial + ']').parent().data('result');
        if (!type) {
            type = {
                serial: 0
            }
        }

        Core.populateForm({
            root: $('#form_edit_box'),
            object: type
        });

        $('#search_results').data('lastSerial', serial);

        return true;
    } else if (tab == 'list') {
        UserTypes.list();
    }

    return true;
};

UserTypes.list = function() {
    $('#search_results').resetTemplate();

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'usertypes',
            submitButton: 'list'
        },
        loadingHolder: 'div.tab_body[rel=list] h1:first',
        success: function(data) {
            if (data.success) {
                // On success, reload the search results
                $.applyTemplates({
                    holder: $('#search_results'),
                    array: data.data,
                    stripe: true,
                    fadeOutButtons: true
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

UserTypes.deleteType = function(serial, button, e) {
    if (!confirm(Translations.CONFIRM_DELETE)) {
        return;
    }

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'usertypes',
            submitButton: 'delete',
            serial: serial
        },
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

UserTypes.createJson = function(root) {
    var json = {};
    root = root || $(document);

    root.find(':input:enabled').each(function() {
        var input = $(this);
        var name = input.attr('name');
        var value = input.val();
        json[name] = value;
    });

    return JSON.stringify(json);
};

UserTypes.saveType = function() {
    var serial = $('#search_results').data('lastSerial');
    var data = UserTypes.createJson($('div.tab_body[rel="form"]'));

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'usertypes',
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
                Core.changeTab('list', UserTypes.tabHandler);
            }
        }
    });
};