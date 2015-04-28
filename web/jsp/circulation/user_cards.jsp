<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<i18n:setInitialLocale />

<layout:head>
    <script type="text/javascript">
        function printPdf(startOffset) {
            // If we dont have any selected labels we must alert user.
            if (!$('#listlabels :checkbox[name=USER_CARD_SELECT]:checked').size()) {
                alert('<i18n:getText module="biblivre3" textKey="ERROR_MUST_SELECT_USER_CARD"/>');
                return;
            }

            if (startOffset === null) {
                $("#label_example_box").dialog('open');
            } else {
                $('input[name=START_OFFSET]').val(startOffset);
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
            <c:if test="${FILE_DOWNLOAD_URL != null}">
                submitForm('FORM_1', 'DOWNLOAD_USER_CARDS_FILE');
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

        .label_example {
            cursor: pointer;
            border: 1px solid black;
            margin: 5px;
            width: 90px;
            float: left;
            height: 27px;
            line-height: 27px;
            text-align: center;
            font-size: 14px;
            font-weight: bold;
        }

        .label_text {
            text-align: center;
            font-weight: bold;
            padding: 5px;
            font-size: 14px;
        }
    </style>

</layout:head>

<layout:body thisPage="circulation_user_cards">
    <input type="hidden" name="FILE_NAME" value="${FILE_DOWNLOAD_URL}" />
    <input type="hidden" name="START_OFFSET" value="0" />

    <div class="center" style="font-size: 12px;">
        <h1><i18n:getText module="biblivre3" textKey="LABEL_USER_CARDS_SELECT_DATES"/></h1>
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

        <input type="checkbox" name="dellabel" id="dellabel" checked/> <label for="dellabel"><i18n:getText module="biblivre3" textKey="LABEL_REMOVE_USER_CARDS_AFTER_PRINTING"/></label>

        <div class="spacer2"></div>

        <div class="submit_buttons">
            <button type="button" style="width: 200px;" onclick="submitForm('FORM_1', 'GENERATE_USER_CARDS_DATE');"><i18n:getText module="biblivre3" textKey="BUTTON_GENERATE_USER_CARDS"/></button>
            <button type="button" style="width: 200px;" onclick="submitForm('FORM_1', 'LIST_ALL_PENDING_USER_CARDS');"><i18n:getText module="biblivre3" textKey="BUTTON_LIST_ALL_PENDING_USER_CARDS"/></button>
        </div>

        <c:if test="${!empty LIST_ALL_USER_CARDS}">
            <div class="spacer2"></div>
            <table width="100%" cellspacing="0" cellpadding="3" id="listlabels">
                <tr>
                    <th>Nº</th>
                    <th><input type="checkbox" onclick="checkAll(this.checked);"/></th>
                    <th style="width: 60px;"><i18n:getText module="biblivre3" textKey="LABEL_ENROL" /></th>
                    <th><i18n:getText module="biblivre3" textKey="LABEL_NAME" /></th>
                    <th><i18n:getText module="biblivre3" textKey="LABEL_USER_TYPE" /></th>
                </tr>
                <c:forEach items="${LIST_ALL_USER_CARDS}" var="user" varStatus="num">
                    <tr align="center" onclick="toggleLineCheck(event, this);">
                        <td>${num.count}</td>
                        <td><input type="checkbox" name="USER_CARD_SELECT" value="${user.serialCard}"/></td>
                        <td>${user.userId}</td>
                        <td style="text-align:left;">${user.userName}</td>
                        <td>${user.userType}</td>
                    </tr>
                </c:forEach>
            </table>

            <div class="spacer2"></div>

            <div class="submit_buttons">
                <button type="button" style="width: 200px;" onclick="submitForm('FORM_1', 'DELETE_USER_CARD');"><i18n:getText module="biblivre3" textKey="BUTTON_DELETE"/></button>
                <button type="button" style="width: 200px;" onclick="printPdf(null);"><i18n:getText module="biblivre3" textKey="BUTTON_PRINT_PDF"/></button>
            </div>
        </c:if>
    </div>

    <div id="label_example_box">
        <div class="label_text"><i18n:getText module="biblivre3" textKey="LABEL_PRINTING_LABEL_SELECT"/>:</div>

        <div class="label_example" onclick="printPdf(0);">1</div>
        <div class="label_example" onclick="printPdf(1);">2</div>
        <div class="label_example" onclick="printPdf(2);">3</div>
        <div class="clear"></div>
        <div class="label_example" onclick="printPdf(3);">4</div>
        <div class="label_example" onclick="printPdf(4);">5</div>
        <div class="label_example" onclick="printPdf(5);">6</div>
        <div class="clear"></div>
        <div class="label_example" onclick="printPdf(6);">7</div>
        <div class="label_example" onclick="printPdf(7);">8</div>
        <div class="label_example" onclick="printPdf(8);">9</div>
        <div class="clear"></div>
        <div class="label_example" onclick="printPdf(9);">10</div>
        <div class="label_example" onclick="printPdf(10);">11</div>
        <div class="label_example" onclick="printPdf(11);">12</div>
        <div class="clear"></div>
        <div class="label_example" onclick="printPdf(12);">13</div>
        <div class="label_example" onclick="printPdf(13);">14</div>
        <div class="label_example" onclick="printPdf(14);">15</div>
        <div class="clear"></div>
        <div class="label_example" onclick="printPdf(15);">16</div>
        <div class="label_example" onclick="printPdf(16);">17</div>
        <div class="label_example" onclick="printPdf(17);">18</div>
        <div class="clear"></div>

        <div class="spacer2"></div>

        <div class="submit_buttons">
            <button type="button" onclick="cancelPrintPdf();"><i18n:getText module="biblivre3" textKey="BUTTON_CANCEL"/></button>
        </div>
    </div>
</layout:body>

