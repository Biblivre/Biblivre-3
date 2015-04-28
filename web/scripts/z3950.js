var Z3950 = {};

Z3950.createNew = function() {
    $.msg({cls: 'normal', msg: ''});

    Core.changeTab('form', Z3950.tabHandler, 0);
    Z3950.clearAll();
};

Z3950.clearAll = function() {
    $('#form_edit_box :input:not(button)').val('');
    $('#form_edit_box :input.use_default_value').each(function () {
        $(this).val($(this).attr('defaultvalue'));
    });
};

Z3950.changeServerStatus = function(status) {
    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'distributed',
            submitButton: 'change_server_status',
            status: status
        },
        success: function(response) {
            if (response.success) {
                $('#server_status').removeClass().addClass(status == 'activate' ? 'serveron' : 'serveroff');
            }

            $.msg({
                cls: response.errorLevel,
                msg: response.message
            });
        }
    });
};

Z3950.tabHandler = function(tab, serial, force) {
    var fromTab = $('div.tab_selected').attr('rel');

    // dont change if we are changing to the same tab
    if (tab == fromTab && !force) {
        return false;
    }

    if (tab == 'form') {
        if (!serial) {
            serial = 0;
        }

        Z3950.clearAll();

        var server = $('#search_results div.box[rel=' + serial + ']').parent().data('result');
        if (!server) {
            server = {
                serverId: 0
            }
        }

        Core.populateForm({
            root: $('#form_edit_box'),
            object: server
        });

        $('#search_results').data('lastSerial', serial);

        return true;
    } else if (tab == 'remote_servers') {
        Z3950.listServers();
    }

    return true;
};

Z3950.listServers = function() {
    $('#search_results').resetTemplate();

    
    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'distributed',
            submitButton: 'list_servers'
        },
        loadingHolder: 'div.tab_body[rel=remote_servers] h1:first',
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

Z3950.deleteServer = function(serial, button, e) {
    if (!confirm(Translations.CONFIRM_DELETE)) {
        return;
    }

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'distributed',
            submitButton: 'delete_server',
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

Z3950.createJson = function(root) {
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

Z3950.saveServer = function() {
    var serial = $('#search_results').data('lastSerial');
    var data = Z3950.createJson($('div.tab_body[rel="form"]'));

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'distributed',
            submitButton: 'save_server',
            serial: serial,
            data: data
        },
        success: function(response) {
            $.msg({
                cls: response.errorLevel,
                msg: response.message
            });

            if (response.success) {
                Core.changeTab('remote_servers', Z3950.tabHandler);
            }
        }
    });
};