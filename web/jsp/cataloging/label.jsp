<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<i18n:setInitialLocale />

<layout:head>
    <script type="text/javascript">
        var labels = [{
            model: 'A4365',
            width: 99,
            height: 67.7,
            columns: 2,
            rows: 4,
            paper_size: 'A4'
        },{
            model: 'A4350',
            width: 99,
            height: 55.8,
            columns: 2,
            rows: 5,
            paper_size: 'A4'
        },{
            model: 'A4363',
            width: 99,
            height: 38.1,
            columns: 2,
            rows: 7,
            paper_size: 'A4'
        },{
            model: 'A4362',
            width: 99,
            height: 33.9,
            columns: 2,
            rows: 8,
            paper_size: 'A4'
        },{
            model: 'A4361',
            width: 63.5,
            height: 46.5,
            columns: 3,
            rows: 6,
            paper_size: 'A4'
        },{
            model: 'A4360',
            width: 63.5,
            height: 38.1,
            columns: 3,
            rows: 7,
            paper_size: 'A4'
        },{
            model: 'A4354',
            width: 99,
            height: 25.4,
            columns: 2,
            rows: 11,
            paper_size: 'A4'
        },{
            model: 'A4355',
            width: 63.5,
            height: 31,
            columns: 3,
            rows: 9,
            paper_size: 'A4',
            selected: true
        },{
            model: 'A4356',
            width: 63.5,
            height: 25.4,
            columns: 3,
            rows: 11,
            paper_size: 'A4'
        }];
        
        function showLabelFormatChoser() {
            $("#label_example_box").dialog('open');
        }

        function configureLabelFormat() {
            var select = $('#label_format_select');
            select.empty();
            
            for (var i = 0; i < labels.length; i++) {
                var option = $('<option></option>');
                var label = labels[i];
                
                option
                    .val(i)
                    .text(Translations.LABEL_FORMAT(label))
                    .appendTo(select);
                    
                if (label.selected) {
                    option.attr('selected', 'selected');
                }
            }

            select.trigger('change');
        }
        

        function configureLabelExample(label) {
            var table = $('#label_example_table tbody');

            table.empty();
            
            var cell = 1;
            
            for (var i = 0; i < label.rows; i++) {
                var tr = $('<tr></tr>');
                
                for (var j = 0; j < label.columns; j++) {
                    var td = $('<td></td>');
                    
                    
                    td.text(cell).data('cell', cell).click(function() {
                        printPdf($(this).data('cell') - 1, label);
                    }).appendTo(tr);
                
                    cell++;
                }
                
                table.append(tr);
            }
            
            table.find('td').width((100 / label.columns) + '%');
        }
        
        function printPdf(startOffset, label) {
            // If we dont have any selected labels we must alert user.
            if (!$('#listlabels :checkbox[name=HOLDING_SELECT]:checked').size()) {
                alert('<i18n:getText module="biblivre3" textKey="ERROR_MUST_SELECT_LABEL"/>');
                return;
            }

            if (startOffset === null) {
                configureLabelFormat();
                $("#label_example_box").dialog('open');
            } else {
                $('input[name=START_OFFSET]').val(startOffset);
                
                $('input[name=WIDTH]').val(label.width);
                $('input[name=HEIGHT]').val(label.height);
                $('input[name=COLUMNS]').val(label.columns);
                $('input[name=ROWS]').val(label.rows);
                
                cancelPrintPdf();
                submitForm('FORM_1', 'RECORD_FILE_PDF');
            }
        }

        function cancelPrintPdf() {
            $('#label_example_box').dialog('close');
        }

        function checkAll(check) {
            $('#listlabels :checkbox').attr('checked', check);
        }

        function toggleLineCheck(e, line) {
            var target = e.target ? e.target : e.srcElement;

            if (target.tagName.toUpperCase() == 'INPUT') {
                return;
            }
            
            var checkbox = $(line).find(':checkbox');
            
            checkbox.attr('checked', !checkbox.attr('checked'));
        }

        $(document).ready(function() {      
            $('#label_format_select').change(function() {
                var label = labels[$(this).val()];

                configureLabelExample(label);
            });

            
            <c:if test="${FILE_DOWNLOAD_URL != null}">
                submitForm('FORM_1', 'DOWNLOAD_LABEL_FILE');
            </c:if>

            $("#label_example_box").dialog({
                autoOpen: false,
                closeOnEscape: false,
                draggable: false,
                resizable: false,
                width: 310,
                modal: true
            });
        });
    </script>


    <style type="text/css">
        #listlabels, #listlabels th, #listlabels td {
            border: 1px solid #000;
            padding: 5px;
            vertical-align: middle;
        }

        #listlabels td {
            cursor: default;
        }

        #label_example_box {
            width: 310px;
            display: none;
        }
        
        #label_example_box select {
            width: 100%;
        }

        #label_example_table {
            border-collapse: separate;
            border-spacing: 3px;
            
            width: 100%;
            height: 320px;
            border: 1px solid black;
            
        }
        
        #label_example_table td {
            border: 1px solid black;
            
            border-radius: 5px; 
            -moz-border-radius: 5px; 
            -webkit-border-radius: 5px; 
            
            text-align: center;
            vertical-align: middle;
            
            background: #FFFFFF;
            cursor: pointer;
            
            font-size: 12px;
            font-weight: bold;
            padding: 4px;
        }

        .label_text {
            padding: 5px;
            font-size: 12px;
        }
    </style>

</layout:head>

<layout:body thisPage="cataloging_label">
    <input type="hidden" name="FILE_NAME" value="${FILE_DOWNLOAD_URL}" />
    <input type="hidden" name="START_OFFSET" value="0" />
    <input type="hidden" name="WIDTH" value="" />
    <input type="hidden" name="HEIGHT" value="" />
    <input type="hidden" name="COLUMNS" value="" />
    <input type="hidden" name="ROWS" value="" />

    <div class="center" style="font-size: 12px;">
        <h1><i18n:getText module="biblivre3" textKey="LABEL_LABEL_CATALOGING"/></h1>

        <div class="spacer2"></div>

        <i18n:getText module="biblivre3" textKey="LABEL_LABEL_CATALOGING_BASE_RECORD"/>:
        <select name='BASE'>
            <option value='MAIN'><i18n:getText module="biblivre3" textKey="VALUE_MAIN"/></option>
            <option value='WORK'><i18n:getText module="biblivre3" textKey="VALUE_WORK"/></option>
        </select>

        <div class="spacer2"></div>

        <h1><i18n:getText module="biblivre3" textKey="LABEL_LABEL_CATALOGING2"/></h1>
        <div class="spacer2"></div>

        <div class="format_form">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_DATE_START"/></div>
            <div class="fcenter"><input type="text" name="startDate" maxlength="10" class="finput" style="width:90px;" onkeypress="return Core.dateMask(this, event);"/>&nbsp;<i18n:getText module="biblivre3" textKey="DEFAULT_DATE_SCREEN"/></div>
            <div class="clear"></div>
        </div>

        <div class="format_form">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_DATE_END"/></div>
            <div class="fcenter"><input type="text" name="endDate" maxlength="10" class="finput" style="width:90px;" onkeypress="return Core.dateMask(this, event);"/>&nbsp;<i18n:getText module="biblivre3" textKey="DEFAULT_DATE_SCREEN"/></div>
            <div class="clear"></div>
        </div>

        <div class="spacer2"></div>
        
        <div class="submit_buttons">
            <button type="button" style="width: 200px;" onclick="submitForm('FORM_1', 'GENERATE_LABELS_DATE');"><i18n:getText module="biblivre3" textKey="BUTTON_GENERATE_LABELS"/></button>
            <button type="button" style="width: 200px;" onclick="submitForm('FORM_1', 'LIST_ALL_PENDING_LABELS');"><i18n:getText module="biblivre3" textKey="BUTTON_LIST_ALL_PENDING_LABELS"/></button>
        </div>

        <c:if test="${!empty LIST_ALL_LABEL}">
            <div class="spacer2"></div>
            <table width="100%" cellspacing="0" cellpadding="3" id="listlabels">
                <tr>
                    <th>Nº</th>
                    <th><input type="checkbox" onclick="checkAll(this.checked);"/></th>
                    <th style="width: 40px;"><i18n:getText module="biblivre3" textKey="LABEL_HOLDING_SERIAL" /></th>
                    <th style="width: 50px;"><i18n:getText module="biblivre3" textKey="LABEL_HOLDING_NUMBER" /></th>
                    <th style="width: 200px;"><i18n:getText module="biblivre3" textKey="LABEL_AUTHOR" /></th>
                    <th><i18n:getText module="biblivre3" textKey="LABEL_TITLE" /></th>
                    <th style="width: 150px;">
                        <i18n:getText module="biblivre3" textKey="LABEL_LOC_A" /><br/>
                        <i18n:getText module="biblivre3" textKey="LABEL_LOC_BB" /><br/>
                        <i18n:getText module="biblivre3" textKey="LABEL_LOC_CB" />
                    </th>
                </tr>
                <c:forEach items="${LIST_ALL_LABEL}" var="holding" varStatus="num">
                    <tr align="center" onclick="toggleLineCheck(event, this);">
                        <td>${num.count}</td>
                        <td><input type="checkbox" name="HOLDING_SELECT" value="${holding.holdingSerial}"/></td>
                        <td>
                            ${holding.holdingSerial}<br/>
                            ${holding.assetHolding}
                        </td>
                        <td>${holding.locationD}</td>
                        <td>${holding.author}</td>
                        <td>${holding.title}</td>
                        <td>
                            ${holding.locationA}<br/>
                            ${holding.locationB}<br/>
                            ${holding.locationC}
                        </td>
                    </tr>
                </c:forEach>
            </table>

            <div class="spacer2"></div>
            
            <input type="checkbox" name="dellabel" id="dellabel" checked/> <label for="dellabel"><i18n:getText module="biblivre3" textKey="LABEL_REMOVE_LABELS_AFTER_PRINTING"/></label>

            <div class="spacer2"></div>

            <div class="submit_buttons">
                <button type="button" style="width: 200px;" onclick="submitForm('FORM_1', 'DELETE_LABEL');"><i18n:getText module="biblivre3" textKey="BUTTON_DELETE"/></button>
                <button type="button" style="width: 200px;" onclick="submitForm('FORM_1', 'RECORD_FILE_TXT');"><i18n:getText module="biblivre3" textKey="BUTTON_RECORD_FILE"/></button>
                <button type="button" style="width: 200px;" onclick="printPdf(null);"><i18n:getText module="biblivre3" textKey="BUTTON_PRINT_PDF"/></button>
            </div>
        </c:if>
    </div>

    <div id="label_example_box">
        <div class="label_text"><i18n:getText module="biblivre3" textKey="LABEL_PRINTING_LABEL_FORMAT"/>:</div>
        <select id="label_format_select"></select>

        <div class="label_text"><i18n:getText module="biblivre3" textKey="LABEL_PRINTING_LABEL_SELECT"/>:</div>

        <table id="label_example_table">
            <tbody></tbody>
        </table>

        <div class="spacer2"></div>

        <div class="submit_buttons">
            <button type="button" onclick="cancelPrintPdf();"><i18n:getText module="biblivre3" textKey="BUTTON_CANCEL"/></button>
        </div>
    </div>
</layout:body>

