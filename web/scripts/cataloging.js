var Cataloging = {};

Cataloging.toggleAreas = function(area, val) {
    var divs = $('div.' + area).hide();

    if (!val) {
        return;
    }

    divs.each(function() {
        var $this = $(this);
        var data = ($this.attr("data") || '').split(',');

        for (var i = 0, len = data.length; i < len; i++) {
            if (data[i] == val) {
                $this.show();
                return true;
            }
        }

        return true;
    });
};

Cataloging.initMarcHelp = function(type) {
    var urls = {
        auth: {
            prefix: 'http://www.loc.gov/marc/authority/ad',
            suffix: '.html'
        },
        biblio: {
            prefix: 'http://www.loc.gov/marc/bibliographic/bd',
            suffix: '.html'
        },
        holding: {
            prefix: 'http://www.loc.gov/marc/holdings/hd',
            suffix: '.html'
        },
        vocabulary: {
            prefix: 'http://www.loc.gov/marc/classification/cd',
            suffix: '.html'
        }
    }

    var url = urls[type];
    if (!url) {
        return false;
    }

    $('fieldset.datafield[data]:not(.dont_show_help)').each(function() {
        var $this = $(this);

        var tag = $this.attr('data');
        var legend = $this.children('legend');

        legend.append('<a href="' + url.prefix + tag + url.suffix + '" target="_blank" class="marc_help">[?]</a>');
    });
};

Cataloging.initRepeatableDataFields = function() {

    $('fieldset.repeatable').each(function() {
        $this = $(this);

        $this.children('legend').append(
            $('<a href="javascript:void(0);" class="marc_repeat">[' + Translations.REPEAT + ']</a>').click(function() {
                Cataloging.repeatDataField($(this).parents('fieldset'));
            })
        );
    });
};

Cataloging.repeatDataField = function(dataField) {
    var clone = dataField.clone(true);

    clone.find(':input').val('');

    if (!dataField.is('.autocreated')) {
        clone.find('legend a.marc_repeat').remove();

        clone.children('legend').append(
            $('<a href="javascript:void(0);">[' + Translations.REMOVE + ']</a>').click(function() {
                $(this).parents('fieldset').remove();
            })
        );

        clone.removeClass('repeatable').addClass('repeated');
    }

    clone.insertAfter(dataField);

    if (clone.hasClass('set_vocabulary_autocomplete')) {
        Bibliographic.setVocabularyAutoCompleteField(clone);
    }

    return clone;
};

Cataloging.initRepeatableSubFields = function() {

    $('div.repeatable').each(function() {
        $this = $(this);

        var fright = $this.find('div.fright');

        if (!fright.size()) {
            fright = $('<div class="fright"></div>').insertAfter($this.find('div.fcenter'));
        }

        fright.append(
            $('<a href="javascript:void(0);" class="marc_repeat">[' + Translations.REPEAT + ']</a>').click(function() {
                Cataloging.repeatSubField($(this).parents('div.subfield'));
            })
        );
    });
};

Cataloging.repeatSubField = function(subField) {
    var clone = subField.clone(true);

    clone.find(':input').val('');

    if (!subField.is('.autocreated')) {
        clone.find('.fright a.marc_repeat').remove();

        clone.find('.fright').append(
            $('<a href="javascript:void(0);">[' + Translations.REMOVE + ']</a>').click(function() {
                $(this).parents('div.subfield').remove();
            })
        );

        clone.removeClass('repeatable').addClass('repeated');
    }
    
    clone.insertAfter(subField);

    return clone;
};

Cataloging.toggleMarcNumbering = function(el) {
    $('input.show_marc').attr('checked', el.checked);

    if (!el.checked) {
        $('span.marc_numbering').remove();
        return;
    }

    $('fieldset.datafield[data]').each(function() {
        var $this = $(this);
        var legend = $this.children('legend');
        var link = legend.children('a.marc_help');

        var tag = $('<span class="marc_numbering">&#160;&#160;&#160;(' + $this.attr('data') + ')</span>');

        if (link.size()) {
            tag.insertBefore(link);
        } else {
            tag.appendTo(legend);
        }
    });

    $('div.subfield, div.indicator').each(function() {
        var $this = $(this);


        var fright = $this.find('div.fright');

        if (!fright.size()) {
            fright = $('<div class="fright"></div>').insertAfter($this.find('div.fcenter'));
        }

        var tag = $this.find(':input').attr('name');

        if (tag == 'ind1') {
            tag = '#1';
        } else if (tag == 'ind2') {
            tag = '#2';
        } else {
            tag = '$' + tag;
        }

        var span = $('<span class="marc_numbering">' + tag + '</span>');

        fright.prepend(span);
    });
};

Cataloging.clearAll = function(root) {
    root = root || $(document);
    root.find('fieldset.repeated, div.repeated, fieldset.autocreated, input.autocreated').remove();
    root.find('fieldset.datafield').find(':input').not('.dont_clear').val('');
};

Cataloging.createJson = function(root) {
    var json = {};
    root = root || $(document);

    root.find('input.autocreated.controlfield[data]').each(function() {
        var controlField = $(this);
        json[controlField.attr('data')] = controlField.val();
    });

    root.find('fieldset.datafield[data]').filter(function() {
        var $this = $(this);
        return ($this.is(':visible') || $this.is('.autocreated'));
    }).each(function() {
        var fieldSet = $(this);
        var dataFieldTag = fieldSet.attr('data');
        var dataField = {};
        var foundSubfield = false;

        fieldSet.find(':input').each(function() {
            var input = $(this);
            var subFieldTag = input.attr('name');
            var value = input.val();

            if (!value) {
                return;
            }

            if (subFieldTag == 'ind1' || subFieldTag == 'ind2') {
                dataField[subFieldTag] = value;
            } else {
                foundSubfield = true;

                if (dataField[subFieldTag]) {
                    dataField[subFieldTag].push(value);
                } else {
                    dataField[subFieldTag] = [value];
                }
            }
        });

        if (!foundSubfield) {
            return;
        }

        if (json[dataFieldTag]) {
            json[dataFieldTag].push(dataField);
        } else {
            json[dataFieldTag] = [dataField];
        }
    });

    return JSON.stringify(json);
};

Cataloging.loadJson = function(root, record) {
    root = root || $(document);

    for (var datafieldtag in record) {
        if (!record.hasOwnProperty(datafieldtag)) {
            continue;
        }

        if (parseInt(datafieldtag, 10) < 10) {
            $('<input type="hidden" class="controlfield autocreated" data="' + datafieldtag + '"/>').val(record[datafieldtag]).appendTo(root);
            continue;
        }

        var originalFieldset = root.find('fieldset.datafield[data="' + datafieldtag + '"]:visible:first');
        if (originalFieldset.size() == 0) {
            originalFieldset = $('<fieldset class="datafield autocreated" data="' + datafieldtag + '"></fieldset>').appendTo(root);
        }

        var datafields = record[datafieldtag];

        for (var i = 0; i < datafields.length; i++) {
            var datafield = datafields[i];
            var fieldset = (i == 0) ? originalFieldset : Cataloging.repeatDataField(originalFieldset);

            Cataloging.populateDataField(fieldset, datafield);
        }
    }

    return record;
};

Cataloging.populateDataField = function(fieldset, datafield) {

    for (var subfieldtag in datafield) {
        if (!datafield.hasOwnProperty(subfieldtag)) {
            continue;
        }

        var originalInput = fieldset.find(':input[name="' + subfieldtag + '"]:first');
        if (originalInput.size() === 0) {
            var autoCreatedSubField = $('<div class="subfield autocreated"></div>').appendTo(fieldset);
            originalInput = $('<input type="hidden" name="' + subfieldtag + '"/>').appendTo(autoCreatedSubField);
        }

        var subfields = datafield[subfieldtag];

        if (!$.isArray(subfields)) {
            // ind1 or ind2
            originalInput.val(subfields);
            continue;
        }

        // other subfields
        for (var i = 0; i < subfields.length; i++) {
            var subfield = subfields[i];
            var input = (i == 0) ? originalInput : Cataloging.repeatSubField(originalInput.parents('div.subfield')).find(':input[name="' + subfieldtag + '"]');

            input.val(subfield);
        }
    }
};


Cataloging.toggleImport = function() {
    if ($('#import_type').val()) {
        $('#marc').val('');
        $('#upload_button_box').show();
    };
};

Cataloging.clearImport = function() {
    $('#import_type, #marc').val('');
    $('#upload_button_box, #marc_box, div.automatic_holdings').hide();
};

Cataloging.initImportUploadField = function() {
    var button = $('#upload_button');

    new AjaxUpload(button, {
        action: 'Controller',
        data: {
            thisPage: 'cataloging_import',
            submitButton: 'UPLOAD_IMPORT'
        },
        autoSubmit: true,
        responseType: 'json',
        onChange: function() {
            button.attr('disabled', true);
        },
        onComplete: function(file, response) {
            if (response.success && response.marc) {
                $('#marc').val(response.marc.replace(/&lt;/gi, '<').replace(/&gt;/gi, '>'));
                $('#marc_box').show();

                var t = $('#import_type').val();

                if (t == 'biblio_MAIN' || t == 'biblio_WORK') {
                    $('fieldset.automatic_holdings').show();
                } else {
                    $('fieldset.automatic_holdings').hide();
                }
            } else if (response && response.message) {
                $.msg({
                    cls: "warning",
                    msg: response.message
                });
            } else {
                $.msg({
                    cls: "warning",
                    msg: Translations.IMPORT_FILE_UPLOAD_ERROR
                });
            }

            button.removeAttr('disabled');
        }
   });
};

Cataloging.automaticHoldingsCheckVolume = function(value, radio) {
    var f = $('fieldset.automatic_holdings');
    var nvolobra = f.find('input[name=nvol_obra]');
    var nvol = f.find('input[name=nvol]');

    if (value == 'nvol') {
        nvolobra.attr('disabled', true).val('');
        nvol.removeAttr('disabled');

        if (radio) {
            nvol.focus();
        } else {
            f.find('input[name=volumes][value=nvol]').attr('checked', true);
        }
    } else {
        nvol.attr('disabled', true).val('');
        nvolobra.removeAttr('disabled');
        if (radio) {
            nvolobra.focus();
        } else {
            f.find('input[name=volumes][value=nvol_obra]').attr('checked', true);
        }
    }
};