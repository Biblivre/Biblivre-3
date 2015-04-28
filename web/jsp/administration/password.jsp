<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<i18n:setInitialLocale />

<layout:head>
    <style type="text/css">
        .label_td {
            font-size: 12px;
            vertical-align: middle;
            padding: 0px 10px 0px 0px;
            text-align: right;
            width: 45%;
        }

        .button_format {
            width: 150px;
            margin: 10px 5px 10px 5px;
        }
    </style>
</layout:head>

<layout:body thisPage="administration_password">
    <fieldset>
        <legend><i18n:getText module="biblivre3" textKey="LABEL_PASSWORD"/></legend>
        <div class="spacer"></div>
        <table id="user_register_box" border="0" align="center" width="700">
            <tr>
                <td class="label_td"><i18n:getText module="biblivre3" textKey="LABEL_PASSWORD"/>:</td>
                <td><input type="password" name="OLDPASSWORD" /></td>
            </tr>
            <tr>
                <td class="label_td"><i18n:getText module="biblivre3" textKey="LABEL_NEW_PASSWORD"/>:</td>
                <td><input type="password" name="NEWPASSWORD" /></td></tr>
            <tr>
                <td class="label_td"><i18n:getText module="biblivre3" textKey="LABEL_REPEAT_PASSWORD"/>:</td>
                <td><input type="password" name="NPRPASSWORD" /></td>
            </tr>
            <tr>
                <td colspan="2" style="text-align: center;">
                    <button type="button" class="button_format" onclick="submitForm('FORM_1','CHANGE_PASSWORD');"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE"/></button>
                    <button type="button" class="button_format" onclick="$('#user_register_box :input').val('');"><i18n:getText module="biblivre3" textKey="BUTTON_CLEAR"/></button>
                </td>
            </tr>
        </table>
    </fieldset>
</layout:body>
