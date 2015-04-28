var Bibliographic = {};
var BibliographicSearch = {};
var BibliographicHolding = {};

Bibliographic.isNewRecord = false;
Bibliographic.isNewHolding = false;
Bibliographic.isEditingHolding = false;
Bibliographic.isSearchOnly = false;

Bibliographic.getDatabase = function() {
    if (Bibliographic.isSearchOnly) {
        return 'MAIN';
    }

    return $('#biblio_database').val();
};

Bibliographic.enableDatabaseChange = function(bool) {
    $('#biblio_database').attr('disabled', !bool);
};

Bibliographic.onDatabaseChange = function() {
    Bibliographic.searchClearResults();
    Bibliographic.updateDatabaseCount();
    Bibliographic.clearExportList();
};

Bibliographic.updateDatabaseCount = function() {
    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'biblio',
            submitButton: 'item_count',
            base: Bibliographic.getDatabase()
        },
        success: function(data) {
            $('#biblio_database_count').html(data.data);
        }
    });
};

Bibliographic.createNew = function() {
    Bibliographic.setIsNewRecord(true);

    $.msg({cls: 'normal', msg: ''});

    Core.changeTab('form', Bibliographic.tabHandler, 0);

    $('select[name=material_type]').val('');
    Cataloging.toggleAreas('material_type', '');
    Bibliographic.clearAll();
};

Bibliographic.deleteRecord = function(serial, button) {
    if (confirm(Translations.CONFIRM_DELETE)) {
        var params = {
            thisPage: 'biblio',
            submitButton: 'delete',
            record_id: serial,
            base: Bibliographic.getDatabase()
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
                    Bibliographic.removeFromExportList(serial);
                    Bibliographic.updateDatabaseCount();
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

Bibliographic.clearAll = function() {
    $('#freemarc').val('');
    $('#holding_freemarc').val('');
    Cataloging.clearAll();
};

Bibliographic.clearRecord = function() {
    $('#holding_results')
        .removeData('lastHoldingSerial')
        .resetTemplate();

    $('#record_results').resetTemplate();
    $('#links_results').resetTemplate();
};

Bibliographic.setIsNewRecord = function(bool) {
    Bibliographic.isNewRecord = bool;
    Bibliographic.setNavigationDisplay(!bool);
    $('button.biblio_save_as').toggle(!bool);
};

Bibliographic.setIsNewHolding = function(bool) {
    Bibliographic.isNewHolding = bool;
    $('button.holding_save_as').toggle(!bool);
};

Bibliographic.setIsEditingHolding = function(bool) {
    $('div.tab[rel=holding_form], div.tab[rel=holding_marc]').toggle(bool);
    $('div.tab[rel=form], div.tab[rel=marc]').toggle(!bool);

    Bibliographic.isEditingHolding = bool;
    Bibliographic.setNavigationDisplay(!bool);
    if (!bool) {
        Bibliographic.setIsNewHolding(false);
    }
};

Bibliographic.setNavigationDisplay = function(bool) {
    $('div.navigation_top, div.navigation_bottom').toggle(bool);
};

Bibliographic.tabHandler = function(tab, serial, force) {
    var fromTab = $('div.tab_selected').attr('rel');

    // dont change if we are changing to the same tab
    if (tab == fromTab && !force) {
        return false;
    }

    serial = serial || $('#search_results').data('lastSerial') || $('#search_results').childrenNotTemplate().find('.box[rel]:first').attr('rel');

    if (fromTab == 'search') {
        if (Bibliographic.isNewRecord) {
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

        Bibliographic.loadDescription(tab, serial);
        return false;
    }

    if (tab == 'search') {
        if (Bibliographic.isNewRecord) {
            if (confirm(Translations.CONFIRM_CANCEL_BIBLIO)) {
                Bibliographic.setIsNewRecord(false);
                Bibliographic.enableDatabaseChange(true);
                return true;
            } else {
                return false;
            }
        }

        if (Bibliographic.isEditingHolding) {
            if (confirm(Translations.CONFIRM_CANCEL_HOLDING)) {
                Bibliographic.setIsEditingHolding(false);
                Bibliographic.enableDatabaseChange(true);
                return true;
            } else {
                return false;
            }
        }

        Bibliographic.enableDatabaseChange(true);
        return true;
    } else if (tab == 'record') {
        if (Bibliographic.isNewRecord) {
            return false;
        }

        if (Bibliographic.isEditingHolding) {
            if (confirm(Translations.CONFIRM_CANCEL_HOLDING)) {
                Bibliographic.setIsEditingHolding(false);
                return true;
            } else {
                return false;
            }
        }

        Bibliographic.loadDescription(tab, serial, true);
        return false;
    } else {

        if (tab == 'marc' || tab == 'form') {
            if (!force && (fromTab == 'marc' || fromTab == 'form')) {
                Bibliographic.loadConvertion(fromTab, tab);
            } else {
                Bibliographic.loadDescription(tab, serial);
            }
        } else if (tab == 'holding_marc' || tab == 'holding_form') {
            if (!force && (fromTab == 'holding_marc' || fromTab == 'holding_form')) {
                BibliographicHolding.loadConvertion(fromTab, tab);
            } else {
                BibliographicHolding.loadDescription(tab, serial);
            }
        }

        return false;
    }

    return false;
};


Bibliographic.searchAddTerm = function() {
    var termo = $('#biblio_search_term_template');

    // Duplica a tr busca_bibliografica_novo_termo e adiciona um "name" incremental
    termo.clone(true)
    .removeAttr('id')
    .removeClass('template')
    .removeClass('dont_clear')
    .find('input, select')
        .attr('disabled', false)
    .end()
    .insertBefore(termo)
    .find('input').focus();
};

Bibliographic.searchClearResults = function() {
    $('#search_results')
        .removeData('lastSerial')
        .resetTemplate();

    $('div.search_paging').empty();
    $('div.search_print').hide();
};

Bibliographic.searchClear = function() {
    $('#biblio_search_box')
        .find('.search_term:not(.dont_clear)')
            .remove()
        .end()
        .find('input:not(:hidden)').val('');

    $('#search_results').removeData('lastParams');

    Bibliographic.searchClearResults();
};

Bibliographic.searchSubmit = function(listAll) {
    var inputs;
    if (listAll) {
        inputs = $('#biblio_search_box .input_material:input');
    } else {
        inputs = $('#biblio_search_box :input:visible:enabled:not(button)');
    }

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

    var params = inputs.getFormValues();

    params['thisPage'] = 'biblio_search';
    params['submitButton'] = 'search';
    params['base'] = Bibliographic.getDatabase();

    Bibliographic.searchDoSubmit(params);
};

Bibliographic.searchDoSubmit = function(params, whichRowToSelect, tab, dontShowMessage) {
    Bibliographic.searchClearResults();

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
                Bibliographic.searchDumpResults({
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

Bibliographic.searchDumpResults = function(o) {
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
            Bibliographic.searchDoSubmit(params);
        },
        linkFunction: function() {
            params.offset = ($(this).attr('rel') - 1) * recordsPerPage;
            Bibliographic.searchDoSubmit(params);
        },
        search_holder: $('#search_results')
    });

    if (config.whichRowToSelect && config.tab) {
        var serial = holder.childrenNotTemplate().find('div.box[rel]').filter(':' + config.whichRowToSelect).attr('rel');

        Core.changeTab(config.tab, Bibliographic.tabHandler, serial, true);
    }
};

Bibliographic.searchNavigate = function(tab, direction) {
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
            Bibliographic.searchDoSubmit(lastParams, (direction == 'next') ? 'first' : 'last', tab, true);
        }
    } else {
        Bibliographic.loadDescription(tab, newSerial);
    }
};

Bibliographic._loadCatalogingForm = function(data, record, tabHolder) {
    Bibliographic.clearAll();

    var authorType = '';

    if (record['100']) {
        authorType = '100';
    } else if (record['110']) {
        authorType = '110';
    } else if (record['111']) {
        authorType = '111';
    } else if (record['130']) {
        authorType = '130';
    }

    $('select[name=author_type]').val(authorType);
    Cataloging.toggleAreas('author_type', authorType);

    var materialType = (data.material_type || '').toLowerCase();

    $('select[name=material_type]').val(materialType);
    Cataloging.toggleAreas('material_type', materialType);

    Cataloging.loadJson(tabHolder, record);
};

Bibliographic._loadCatalogingMarc = function(data, record) {
    var marcMaterialType = (data.material_type || '').toLowerCase();

    $('select[name=material_type]').val(marcMaterialType);
    $('#freemarc').val(record);
};

Bibliographic._loadCatalogingRecord = function(data, tabHolder) {
    Bibliographic.clearRecord();

    tabHolder.find('.tab_inner_title').text(data.title);

    var recordHolder = $('#record_results');
    $.applyTemplates({
        holder: recordHolder,
        array: data.fields
    });

    var holdingHolder = $('#holding_results');
    if (holdingHolder.size()) {
        $.applyTemplates({
            holder: holdingHolder,
            array: data.holdings,
            stripe: true
        });
    }

    var tabHolderLinks = tabHolder.find('.tab_inner_links');

    if (tabHolderLinks.size()) {
        $.applyTemplates({
            holder: tabHolderLinks,
            array: data.links
        });
    }
};

Bibliographic._loadSearchMarc = function(data) {
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

Bibliographic._loadSearchFields = function(data) {
    return data.fields;
};

Bibliographic.processResult = function(data, serial, from, tab, tabHolder, dontShowMessage) {
    $('div.tab_body[rel="' + tab + '"] h1:first').html('');

    if (data.success) {
        if (!Bibliographic.isNewRecord) {
            Bibliographic.enableDatabaseChange(false);
        }

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

        if (!Bibliographic.isSearchOnly) {
            var record = data.data;

            if (tab == 'form') {
                Bibliographic._loadCatalogingForm(data, record, tabHolder);
            } else if (tab == 'marc') {
                Bibliographic._loadCatalogingMarc(data, record);
            } else if (tab == 'record') {
                Bibliographic._loadCatalogingRecord(data, tabHolder);
            }
        } else {
            tabHolder.find('.tab_inner_title').text(data.title);

            var fields;
            if (tab == 'marc') {
                fields = Bibliographic._loadSearchMarc(data);
            } else {
                fields = Bibliographic._loadSearchFields(data);
            }

            var tabHolderBody = tabHolder.find('.tab_inner_body');

            $.applyTemplates({
                holder: tabHolderBody,
                array: fields
            });

            if (tab == 'record') {
                var holdingHolder = $('#holding_results');
                if (holdingHolder.size()) {
                    $.applyTemplates({
                        holder: holdingHolder,
                        array: data.holdings,
                        stripe: true
                    });
                }
            }


            var tabHolderLinks = tabHolder.find('.tab_inner_links');
            var tabHolderAvailability = tabHolder.find('.tab_inner_availability');

            $.applyTemplates({
                holder: tabHolderLinks,
                array: data.links
            });

            if (tabHolderAvailability.size() > 0) {
                $.applyTemplates({
                    holder: tabHolderAvailability,
                    array: [data]
                });
            }
        }

        $('#content_outer').scrollTo(0, 0);
    } else {
        $.msg({cls: data.errorLevel, msg: data.message});
    }
};

Bibliographic.loadDescription = function(tab, serial, dontShowMessage) {
    var holder = $('#search_results');
    var tabHolder = $('div.tab_body[rel="' + tab + '"]');
    var tabHolderBody = tabHolder.find('.tab_inner_body');
    var tabHolderLinks = tabHolder.find('.tab_inner_links');
    var tabHolderAvailability = tabHolder.find('.tab_inner_availability');
    var from = $('div.tab_selected').attr('rel');

    tabHolderBody.resetTemplate();
    tabHolderLinks.resetTemplate();
    tabHolderAvailability.resetTemplate();

    $('#holding_results').removeData('lastHoldingSerial').resetTemplate();
    holder.data('lastSerial', serial);

    if (!serial) {
        return;
    }

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'biblio',
            submitButton: 'open',
            base: Bibliographic.getDatabase(),
            type: tab,
            record_id: serial
        },
        loadingHolder: 'div.tab_body[rel="' + tab + '"] h1:first',
        success: function(data) {
            Bibliographic.processResult(data, serial, from, tab, tabHolder, dontShowMessage);
        }
    });
};

Bibliographic.loadConvertion = function(from, to) {
    var serial = $('#search_results').data('lastSerial');
    var tabHolder = $('div.tab_body[rel="' + to + '"]');

    var data = '';
    var material_type = $('div.tab_body[rel="' + from + '"]').find('select[name=material_type]').val();

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
            thisPage: 'biblio',
            submitButton: 'switch',
            from: from,
            to: to,
            material_type: material_type,
            data: data,
            serial: serial
        },
        loadingHolder: 'div.tab_body[rel="' + from + '"] h1:first',
        success: function(data) {
            Bibliographic.processResult(data, serial, from, to, tabHolder);
        }
    });
};

Bibliographic.saveAsNew = function() {
    Bibliographic.setIsNewRecord(true);
    $('#search_results').removeData('lastSerial');
    Bibliographic.save();
};

Bibliographic.save = function() {
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

    var material_type = $('div.tab_body[rel="' + tab + '"]').find('select[name=material_type]').val();

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'biblio',
            submitButton: 'save',
            base: Bibliographic.getDatabase(),
            material_type: material_type,
            type: tab,
            record_id: serial,
            data: data
        },
        success: function(response) {
            Bibliographic.updateDatabaseCount();

            $.msg({
                cls: response.errorLevel,
                msg: response.message
            });

            if (response.success) {
                if (Bibliographic.isNewRecord) {
                    Bibliographic.setIsNewRecord(false);
                    Core.changeTab('search', Bibliographic.tabHandler);
                    BibliographicHolding.openAutomaticHoldingDialog(response.data);
                } else {
                    Core.changeTab('record', Bibliographic.tabHandler);
                }
            }
        }
    });
};

BibliographicSearch.tabHandler = function(tab, serial, force) {
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
        Bibliographic.loadDescription(tab, serial);
        return false;
    }
    return true;
};

BibliographicSearch.searchSubmit = function() {
    Bibliographic.isSearchOnly = true;
    Bibliographic.searchSubmit.apply(Bibliographic, arguments);
};

BibliographicSearch.searchNavigate = Bibliographic.searchNavigate;

BibliographicSearch.searchAddTerm = Bibliographic.searchAddTerm;
BibliographicSearch.searchClear = Bibliographic.searchClear;


BibliographicHolding.deleteHolding = function(serial, button, e) {
    if (confirm(Translations.CONFIRM_DELETE)) {
        var params = {
            thisPage: 'holding',
            submitButton: 'delete',
            holding_id: serial
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

                    $(button).parents('.holding_result').fadeOut();
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

BibliographicHolding.loadDescription = function(tab, serial) {
    var holder = $('#holding_results');
    var tabHolder = $('div.tab_body[rel="' + tab + '"]');
    var tabHolderBody = tabHolder.find('.tab_inner_body');
    var from = $('div.tab_selected').attr('rel');

    tabHolderBody.resetTemplate();
    holder.data('lastHoldingSerial', serial);

    if (!serial) {
        return;
    }

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'holding',
            submitButton: 'open',
            base: Bibliographic.getDatabase(),
            type: tab,
            holding_id: serial
        },
        loadingHolder: 'div.tab_body[rel="' + tab + '"] h1:first',
        success: function(data) {
            BibliographicHolding.processResult(data, serial, from, tab, tabHolder);
        }
    });
};

BibliographicHolding.loadConvertion = function(from, to) {
    var serial = $('#holding_results').data('lastHoldingSerial');
    var tabHolder = $('div.tab_body[rel="' + to + '"]');

    var data = '';

    if (from == 'holding_form') {
        data = Cataloging.createJson($('div.tab_body[rel="holding_form"]'));
    } else if (from == 'holding_marc') {
        data = $('#holding_freemarc').val();
    }

    var available = $('div.tab_body[rel="' + from + '"]').find('select[name=available]').val();

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'holding',
            submitButton: 'switch',
            available: available,
            from: from,
            to: to,
            data: data,
            serial: serial
        },
        loadingHolder: 'div.tab_body[rel="' + from + '"] h1:first',
        success: function(data) {
            BibliographicHolding.processResult(data, serial, from, to, tabHolder);
        }
    });
};

BibliographicHolding.processResult = function(data, serial, from, tab, tabHolder) {
    if (data.success) {
        var expectedSerial = serial;
        if ($('#holding_results').data('lastHoldingSerial') != expectedSerial) {
            return;
        }

        var expectedTab = from;
        if ($('div.tab_selected').attr('rel') != expectedTab) {
            return;
        }

        Core.changeTab(tab);
        $.msg();

        var record = data.data;

        if (tab == 'holding_marc') {
            $('#holding_freemarc').val(record);

            var marcAvailable = data.available;
            $('select[name=available]').val(marcAvailable);

        } else if (tab == 'holding_form') {
            Bibliographic.clearAll();

            var available = data.available;
            $('select[name=available]').val(available);

            if (!record["949"]) {
                record["949"] = [{"a":[data.assetHolding]}];
            }

            Cataloging.loadJson(tabHolder, record);

        } else if (tab == 'record') {
            var holdingHolder = $('#holding_results');

            $.applyTemplates({
                holder: holdingHolder,
                array: data.holdings,
                stripe: true
            });
        }

        $('#content_outer').scrollTo(0, 0);
    } else {
        $.msg({
            cls: data.errorLevel,
            msg: data.message
        });
    }
};


BibliographicHolding.edit = function(serial) {
    Bibliographic.setIsEditingHolding(true);

    if (serial) {
        BibliographicHolding.loadDescription('holding_form', serial, 'holding');
    }
};

BibliographicHolding.saveAsNew = function() {
    Bibliographic.setIsNewHolding(true);
    $('#holding_results').data('lastHoldingSerial', 0);
    BibliographicHolding.save();
};

BibliographicHolding.createNew = function() {
    Bibliographic.setIsEditingHolding(true);
    Bibliographic.setIsNewHolding(true);

    $('#holding_results').data('lastHoldingSerial', 0);

    $.msg({cls: 'normal', msg: ''});
    $('#content_outer').scrollTo(0, 0);

    Bibliographic.clearAll();
    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'holding',
            submitButton: 'get_next_location',
            record_id: $('#search_results').data('lastSerial')
        },
        success: function(response) {
            if (response.success) {
                var fieldset = $('div.tab_body[rel=holding_form] fieldset[data=090]');

                if (response.locationA) {
                    fieldset.find('input[name=a]').val(response.locationA);
                }

                if (response.locationB) {
                    fieldset.find('input[name=b]').val(response.locationB);
                }

                if (response.locationC) {
                    fieldset.find('input[name=c]').val(response.locationC);
                }

                if (response.locationD) {
                    fieldset.find('input[name=d]').val(response.locationD);
                }
            }
        },
        complete: function() {
            Core.changeTab('holding_form');
        }

    });
};

BibliographicHolding.generateLabel = function(holding_serial, button) {
    var biblio_serial = $('#search_results').data('lastSerial');

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'holding',
            submitButton: 'generate_label',
            base: Bibliographic.getDatabase(),
            record_id: biblio_serial,
            holding_id: holding_serial
        },
        success: function(response) {
            if (response.success) {
                $(button).attr("disabled", true).html(Translations.LABEL_ADDED_TO_QUEUE);
            }
        }
    });
};

BibliographicHolding.save = function() {
    var biblio_serial = $('#search_results').data('lastSerial');
    var holding_serial = $('#holding_results').data('lastHoldingSerial');
    var tab = $('div.tab_selected').attr('rel');

    var data = '';


    if (!holding_serial) {
        holding_serial = '0';
    }

    if (tab == 'holding_form') {
        data = Cataloging.createJson($('div.tab_body[rel="holding_form"]'));
    } else if (tab == 'holding_marc') {
        data = $('#holding_freemarc').val();
    }

    var available = $('div.tab_body[rel="' + tab + '"]').find('select[name=available]').val();

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'holding',
            submitButton: 'save',
            base: Bibliographic.getDatabase(),
            type: tab,
            available: available,
            record_id: biblio_serial,
            holding_id: holding_serial,
            data: data
        },
        success: function(response) {
            $.msg({
                cls: response.errorLevel,
                msg: response.message
            });

            if (response.success) {
                Bibliographic.setIsEditingHolding(false);
                Core.changeTab('record', Bibliographic.tabHandler);
            }
        }
    });
};

BibliographicHolding.openAutomaticHoldingDialog = function(serial) {
    if (!serial) {
        return;
    }

    $("#automatic_holdings").clone(true).removeAttr('id').data('serial', serial).dialog({
        closeOnEscape: false,
        draggable: false,
        resizable: false,
        width: 410,
        modal: true
    });
};

BibliographicHolding.closeAutomaticHoldingDialog = function(button) {
    $(button).parents('.automatic_holdings').dialog('destroy');
};

BibliographicHolding.createAutomaticHolding = function(button) {
    var parent = $(button).parents('.automatic_holdings')
    var inputs = parent.find(':input');
    var serial = parent.data('serial');

    var block = false;
    inputs.each(function() {
        var $this = $(this);

        if ($this.attr('name') == 'quant' && (!Core.isNumeric($this.val()) || (parseInt($this.val(), 10) <= 0))) {
            alert(Translations.ERROR_CREATE_AT_LEAST_ONE_HOLDING);
            $this.focus();
            block = true;
            return false;
        }

        return true;
    });

    if (block) {
        return;
    }

    var params = inputs.getFormValues();

    params['thisPage'] = 'holding';
    params['submitButton'] = 'create_automatic_holding';
    params['base'] = Bibliographic.getDatabase();
    params['record_id'] = serial

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
                Core.changeTab('record', Bibliographic.tabHandler, serial);
            }
        }
    });

    BibliographicHolding.closeAutomaticHoldingDialog(button);
};

Bibliographic.initUploadField = function() {
    var button = $('button.file_upload_button');
    new AjaxUpload(button, {
        action: 'UploadController',
        autoSubmit: true,
        responseType: 'json',
        onComplete: function(file, response) {
            if (response.success && response.id) {
                var record_id = $('#search_results').data('lastSerial');
                var description = $('#file_description').val();

                if (!description) {
                    description = prompt(Translations.FILE_DESCRIPTION) || '';
                }

                $.ajax({
                    url: 'JsonController',
                    type: 'POST',
                    dataType: 'json',
                    data: {
                        thisPage: 'biblio',
                        submitButton: 'file_upload',
                        base: Bibliographic.getDatabase(),
                        record_id: record_id,
                        file_id: response.id,
                        description: description
                    },
                    success: function(response) {
                        if (response && response.success && response.links) {
                            var tabHolderLinks = $('div.tab_body[rel="record"] .tab_inner_links');
                            tabHolderLinks.resetTemplate();

                            $.applyTemplates({
                                holder: tabHolderLinks,
                                array: response.links
                            });

                            $('#file_description').val('');
                        }
                    }
                });
            } else {
                $.msg({
                    cls: "warning",
                    msg: Translations.FILE_UPLOAD_ERROR
                });
            }
        }
   });
};

Bibliographic.exportList = [];

Bibliographic.exportRecords = function() {
    var serialsList = [];

    for (var i = 0, ilen = Bibliographic.exportList.length; i < ilen; i++) {
        serialsList.push(Bibliographic.exportList[i].serial);
    }

    $('#export_serial_list').val(serialsList.join(','));
    $('#export_base').val(Bibliographic.getDatabase());

    Bibliographic.clearExportList();

    submitForm('FORM_1', 'EXPORT_RECORD');
};

Bibliographic.clearExportList = function() {
    Bibliographic.exportList = [];
    Bibliographic.updateExportList();
};

Bibliographic.removeFromExportList = function(serial) {
    Bibliographic.exportList = $.grep(Bibliographic.exportList, function(value) {return value.serial != serial;});
    Bibliographic.updateExportList();
};

Bibliographic.addAllToExportList = function() {
    $('#search_results').childrenNotTemplate().each(function() {
        Bibliographic.addToExportList($(this), false);
    });

    Bibliographic.updateExportList();
};

Bibliographic.addToExportList = function(search_result, dontUpdate) {
    var result = search_result.data('result');

    if (result && $.inArray(result, Bibliographic.exportList) == -1) {
        Bibliographic.exportList.push(result);
        if (!dontUpdate) {
            Bibliographic.updateExportList();
        }
    }

    var button = search_result.find('button.export_button');
    if (!button.attr("disabled")) {
        var oldHtml = button.html();
        button.attr("disabled", true).html(Translations.EXPORT_ADDED_TO_QUEUE);

        setTimeout(function() {
            button.removeAttr("disabled").html(oldHtml);
        }, 2000);
   }
};

Bibliographic.updateExportList = function() {
    $('#export_list').resetTemplate();

    $('#export_box')
        .removeClass('database_MAIN')
        .removeClass('database_WORK')
        .addClass('database_' + Bibliographic.getDatabase())

    if (Bibliographic.exportList.length === 0) {
        $('#export_box').hide();
        return;
    } else {
        $('#export_box').show()
    }

    $.applyTemplates({
        holder: $('#export_list'),
        array: Bibliographic.exportList
    });
};

Bibliographic.moveAllRecords = function() {
    if (!confirm(Translations['CONFIRM_MOVE_FROM_' + Bibliographic.getDatabase() + '_DATABASE'])) {
        return;
    }

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'biblio',
            submitButton: 'move_all_records',
            base: Bibliographic.getDatabase(),
            material_type: $('select[name=ITEM_TYPE]').val()
        },
        success: function(data) {
            $.msg({
                cls: data.errorLevel,
                msg: data.message
            });

            if (data.success) {
                Bibliographic.onDatabaseChange();
            }
        }
    });
};

Bibliographic.moveRecords = function() {
    var serialsList = [];

    for (var i = 0, ilen = Bibliographic.exportList.length; i < ilen; i++) {
        serialsList.push(Bibliographic.exportList[i].serial);
    }

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'biblio',
            submitButton: 'move_records',
            base: Bibliographic.getDatabase(),
            serial_list: serialsList.join(',')
        },
        success: function(data) {
            $.msg({
                cls: data.errorLevel,
                msg: data.message
            });

            if (data.success) {
                Bibliographic.onDatabaseChange();
          }
        }
    });
};


Bibliographic.initAutoCompleteField = function() {
    Bibliographic.setAuthoritiesAutoCompleteField('100');
    Bibliographic.setAuthoritiesAutoCompleteField('110');
    Bibliographic.setAuthoritiesAutoCompleteField('111');
    Bibliographic.setVocabularyAutoCompleteField($('fieldset.datafield[data=600]'));
    Bibliographic.setVocabularyAutoCompleteField($('fieldset.datafield[data=610]'));
    Bibliographic.setVocabularyAutoCompleteField($('fieldset.datafield[data=611]'));
    Bibliographic.setVocabularyAutoCompleteField($('fieldset.datafield[data=630]'));
    Bibliographic.setVocabularyAutoCompleteField($('fieldset.datafield[data=650]'));
    Bibliographic.setVocabularyAutoCompleteField($('fieldset.datafield[data=651]'));
};

Bibliographic.setAuthoritiesAutoCompleteField = function(df) {
    $('#b_' + df + '_a').autocomplete('JsonController', {
        formatItem: function(item) {
            return item.name;
        },
        extraParams: {
            thisPage: 'auth_search',
            submitButton: 'auto_complete'
        },
        scroll: true,
        parse: function(data) {
            var json = JSON.parse(data);
            var parsed = [];

            if (json.success && json.data) {
                for (var i=0; i < json.data.length; i++) {
                    var row = json.data[i];

                    if (row.data[df]) {
                        parsed.push({
                            data: row,
                            value: row.name,
                            result: row.name
                        });
                    }
                }

            }

            return parsed;
        }
    }).result(function(event, item) {
        var fieldset = $(document).find('fieldset.datafield[data="' + df + '"]:visible:first');
        var datafield = item.data[df];

        if (datafield) {
            fieldset.find('div.repeated, input.autocreated').remove();
            fieldset.find(':input').not('.dont_clear').val('');
            Cataloging.populateDataField(fieldset, datafield[0]);
        }
    });
};

Bibliographic.setVocabularyAutoCompleteField = function(el) {
    el.find(':input[name=a]').autocomplete('JsonController', {
        formatItem: function(item) {
            return item.term;
        },
        extraParams: {
            thisPage: 'vocabulary_search',
            submitButton: 'auto_complete'
        },
        scroll: true,
        parse: function(data) {
            var json = JSON.parse(data);
            var parsed = [];

            if (json.success && json.data) {
                for (var i=0; i < json.data.length; i++) {
                    var row = json.data[i];

                    parsed.push({
                        data: row,
                        value: row.term,
                        result: row.term
                    });
                }

            }

            return parsed;
        }
    });
};