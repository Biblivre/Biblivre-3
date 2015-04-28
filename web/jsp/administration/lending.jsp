<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<i18n:setInitialLocale />

<layout:head>
    <style type="text/css">
        .bordertable, .bordertable th, .bordertable td {
            border: 1px solid #000;
            font-size: 13px;
            border-collapse: collapse;
        }

        .bordertable th, .bordertable td {
            padding: 4px;
        }

        .label_td {
            font-size: 12px;
            vertical-align: middle;
            padding: 0px 10px 0px 0px;
            text-align: right;
        }

        .button_format {
            width: 150px;
            margin: 10px 5px 10px 5px;
        }

        #container_new, #container_edit {
            display: none;
        }

        #container_new table, #container_edit table {
            width: 350px;
        }
    </style>



    <script type="text/javascript">
        $(document).ready(function() {
            $('#admin_table tr:gt(0):odd').addClass('even');
            $('#admin_table tr:gt(0):even').addClass('odd');

            $('#container_new, #container_edit').dialog({
                autoOpen: false,
                closeOnEscape: false,
                draggable: false,
                resizable: false,
                width: 360,
                modal: true
            });
        });

        function openNew() {
            $('#container_new').find('input:text').val('').end().dialog('open');
        }

        function openEdit() {
            $('#container_edit').dialog('open');
        }

        function saveNew() {
            $('input[name=nameUserType]').val($('input[name=nameUserTypeNew]').val());
            $('input[name=descUserType]').val($('input[name=descUserTypeNew]').val());
            $('input[name=numberItens]').val($('input[name=numberItensNew]').val());
            $('input[name=daysDelivery]').val($('input[name=daysDeliveryNew]').val());
            $('input[name=maxReservationDays]').val($('input[name=maxReservationDaysNew]').val());

            submitForm('FORM_1','USER_TYPE_SAVE');
        }

        function saveEdit() {
            $('input[name=nameUserType]').val($('input[name=nameUserTypeEdit]').val());
            $('input[name=descUserType]').val($('input[name=descUserTypeEdit]').val());
            $('input[name=numberItens]').val($('input[name=numberItensEdit]').val());
            $('input[name=daysDelivery]').val($('input[name=daysDeliveryEdit]').val());
            $('input[name=maxReservationDays]').val($('input[name=maxReservationDaysEdit]').val());

            submitForm('FORM_1','USER_TYPE_UPDATE');
        }

    </script>
</layout:head>

<layout:body thisPage="administration_lending">
    <div class="spacer2"></div>
    <table class="bordertable" width="100%" id="admin_table">
        <tr align="center" >
            <th><i18n:getText module="biblivre3" textKey="LABEL_USER_TYPE"/></th>
            <th><i18n:getText module="biblivre3" textKey="LABEL_USER_TYPE_DESCRIPTION"/></th>
            <th><i18n:getText module="biblivre3" textKey="LABEL_MAX_ITEM_COUNT"/></th>
            <th><i18n:getText module="biblivre3" textKey="LABEL_DELIVERY_TIME_DATE"/></th>
            <th><i18n:getText module="biblivre3" textKey="LABEL_RESERVATION_DAYS"/></th>
        </tr>

        <c:forEach var="tableRow" items="${USER_TYPE_SEARCH_RESULT}" varStatus="rowNumber">
            <tr align="center">
                <td class="left"><input ${SELECTED[tableRow.serial]} type="radio" name="Tipo" onclick="submitForm('FORM_1','USER_TYPE_SELECTED');" value="${tableRow.serial}" /> ${tableRow.name}</td>
                <td>${tableRow.description}</td>
                <td>${tableRow.maxLendingCount}</td>
                <td>${tableRow.maxLendingDays}</td>
                <td>${tableRow.maxReservationDays}</td>
            </tr>
        </c:forEach>
    </table>

    <div class="spacer2"></div>
    <div class="center">
        <button type="button" class="button_format" onclick="openNew();"><i18n:getText module="biblivre3" textKey="BUTTON_NEW"/></button>
        <button type="button" class="button_format" onclick="openEdit();"><i18n:getText module="biblivre3" textKey="BUTTON_ALTER"/></button>
        <button type="button" class="button_format" onclick="submitForm('FORM_1','USER_TYPE_DELETE');"><i18n:getText module="biblivre3" textKey="BUTTON_DELETE"/></button>
    </div>

    <input type="hidden" name="nameUserType"/>
    <input type="hidden" name="descUserType"/>
    <input type="hidden" name="numberItens"/>
    <input type="hidden" name="daysDelivery"/>
    <input type="hidden" name="maxReservationDays"/>

    <div id="container_new" class="center">
        <table align="center">
            <tr>
                <td class="label_td"><i18n:getText module="biblivre3" textKey="LABEL_NEW_TYPE"/>:</td>
                <td><input type="text" maxlength="30" name="nameUserTypeNew"/></td>
            </tr>
            <tr>
                <td class="label_td"><i18n:getText module="biblivre3" textKey="LABEL_NEW_DESCRIPTION"/>:</td>
                <td><input type="text" maxlength="30" name="descUserTypeNew" /></td>
            </tr>
            <tr>
                <td class="label_td"><i18n:getText module="biblivre3" textKey="LABEL_MAX_ITEM_COUNT"/>:</td>
                <td><input type="text" maxlength="30" name="numberItensNew" /></td>
            </tr>
            <tr>
                <td class="label_td"><i18n:getText module="biblivre3" textKey="LABEL_DELIVERY_TIME"/>:</td>
                <td><input type="text" maxlength="30" name="daysDeliveryNew"/></td>
            </tr>
            <tr>
                <td class="label_td"><i18n:getText module="biblivre3" textKey="LABEL_RESERVATION_DAYS"/>:</td>
                <td><input type="text" maxlength="30" name="maxReservationDaysNew"/></td>
            </tr>
        </table>

        <div class="spacer2"></div>
        <div class="center">
            <button type="button" class="button_format" onclick="$('#container_new').dialog('close');"><i18n:getText module="biblivre3" textKey="BUTTON_CANCEL"/></button>
            <button type="button" class="button_format" onclick="saveNew();"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE"/></button>
        </div>
    </div>

    <div id="container_edit" class="center">
        <table align="center">
            <tr>
                <td class="label_td"><i18n:getText module="biblivre3" textKey="LABEL_USER_TYPE"/>:</td>
                <td><input type="text" maxlength="30" name="nameUserTypeEdit" value="${USER_TYPE_SELECTED.name}"/></td>
            </tr>
            <tr>
                <td class="label_td"><i18n:getText module="biblivre3" textKey="LABEL_USER_TYPE_DESCRIPTION"/>:</td>
                <td><input type="text" maxlength="30" name="descUserTypeEdit" value="${USER_TYPE_SELECTED.description}"/></td>
            </tr>
            <tr>
                <td class="label_td"><i18n:getText module="biblivre3" textKey="LABEL_MAX_ITEM_COUNT"/>:</td>
                <td><input type="text" maxlength="30" name="numberItensEdit" value="${USER_TYPE_SELECTED.maxLendingCount}"/></td>
            </tr>
            <tr>
                <td class="label_td"><i18n:getText module="biblivre3" textKey="LABEL_DELIVERY_TIME"/>:</td>
                <td><input type="text" maxlength="30" name="daysDeliveryEdit" value="${USER_TYPE_SELECTED.maxLendingDays}"/></td>
            </tr>
            <tr>
                <td class="label_td"><i18n:getText module="biblivre3" textKey="LABEL_RESERVATION_DAYS"/>:</td>
                <td><input type="text" maxlength="30" name="maxReservationDaysEdit" value="${USER_TYPE_SELECTED.maxReservationDays}"/></td>
            </tr>
        </table>

        <div class="spacer2"></div>
        <div class="center">
            <button type="button" class="button_format" onclick="$('#container_edit').dialog('close');"><i18n:getText module="biblivre3" textKey="BUTTON_CANCEL"/></button>
            <button type="button" class="button_format" onclick="saveEdit()"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE"/></button>
        </div>
    </div>
</layout:body>

