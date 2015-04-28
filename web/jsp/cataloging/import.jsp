<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<i18n:setInitialLocale />

<layout:head>
    <link rel="stylesheet" type="text/css" href="css/cataloging.css" />

    <script type="text/javascript" src="scripts/ajaxfileupload.js"></script>
    <script type="text/javascript" src="scripts/cataloging.js"></script>

    <script type="text/javascript">
        $(document).ready(function() {
            Cataloging.initImportUploadField();
            $('#upload_button').removeAttr('disabled');
            $('#import_type').val('');
        });
    </script>
</layout:head>

<layout:body thisPage="cataloging_import">
    <div class="spacer"></div>

    <div class="import_combo">
        <i18n:getText module="biblivre3" textKey="LABEL_SELECT_DATABASE"/>:
        <select name="import_type" id="import_type" onchange="Cataloging.toggleImport();">
            <option></option>
            <option value="biblio_MAIN"><i18n:getText module="biblivre3" textKey="VALUE_IMPORT_BIBLIO_MAIN" /></option>
            <option value="biblio_WORK"><i18n:getText module="biblivre3" textKey="VALUE_IMPORT_BIBLIO_WORK" /></option>
            <option value="authorities"><i18n:getText module="biblivre3" textKey="VALUE_IMPORT_AUTH" /></option>
            <option value="vocabulary"><i18n:getText module="biblivre3" textKey="VALUE_IMPORT_THESAURUS" /></option>
        </select>
    </div>

    <div style="text-align: center;">
        <div id="upload_button_box" style="display: none;">
            <div class="spacer2"></div>
            <button id="upload_button" type="button" style="width: 150px;"><i18n:getText module="biblivre3" textKey="BUTTON_SEND_FILE" /></button>
        </div>

        <div id="marc_box" style="display: none;">
            <div class="spacer2"></div>
            <textarea cols="80" rows="14" name="marc" id="marc"></textarea>
            <div class="spacer2"></div>
            <div id="automatic_holdings">
                <fieldset class="automatic_holdings">
                    <legend><i18n:getText module="biblivre3" textKey="LABEL_AUTOMATIC_HOLDING" /></legend>

                    <table border="0">
                        <tr>
                            <td colspan="2" style="text-align: center;">
                                <input type="checkbox" name="holding" value="1" checked/>
                                <i18n:getText module="biblivre3" textKey="LABEL_AVAILABLE_HOLDING"/>
                            </td>
                        </tr>
                        <tr>
                            <td><i18n:getText module="biblivre3" textKey="LABEL_NUMBER_HOLDINGS"/>:</td>
                            <td><input type="text" value="1" name="quant" /></td>
                        </tr>
                        <tr>
                            <td><i18n:getText module="biblivre3" textKey="LABEL_NUMBER_VOLUME"/>:</td>
                            <td>
                                <input type="radio" name="volumes" value="nvol" onclick="Cataloging.automaticHoldingsCheckVolume('nvol', true);" />
                                <input type="text" name="nvol" onfocus="Cataloging.automaticHoldingsCheckVolume('nvol', false);"/>
                            </td>
                        </tr>
                        <tr>
                            <td><i18n:getText module="biblivre3" textKey="LABEL_NUMBER_VOLUME_WORK"/>:</td>
                            <td>
                                <input type="radio" name="volumes" value="nvol_obra" onclick="Cataloging.automaticHoldingsCheckVolume('nvol_obra', true);" />
                                <input type="text" name="nvol_obra" onfocus="Cataloging.automaticHoldingsCheckVolume('nvol_obra', false);"/>
                            </td>
                        </tr>
                        <tr>
                            <td><i18n:getText module="biblivre3" textKey="LABEL_DATE_TUMBLING"/>:</td>
                            <td><input type="text" name="dt_tomb" /></td>
                        </tr>
                        <tr>
                            <td><i18n:getText module="biblivre3" textKey="LABEL_DEPOSITORY_LIBRARY"/>:</td>
                            <td><input type="text" name="biblio_dep" /></td>
                        </tr>
                        <tr>
                            <td><i18n:getText module="biblivre3" textKey="LABEL_TYPE_ACQUISITION"/>:</td>
                            <td><input type="text" name="aquis" value="<i18n:getText module="biblivre3" textKey="LABEL_PURCHASE"/>" /></td>
                        </tr>
                    </table>
                </fieldset>
            </div>
            <div class="spacer2"></div>
            <div class="submit_buttons">
                <button type="button" onclick="Cataloging.clearImport();"><i18n:getText module="biblivre3" textKey="BUTTON_CANCEL" /></button>
                <button type="button" onclick="submitForm('FORM_1', 'SAVE_IMPORT');"><i18n:getText module="biblivre3" textKey="BUTTON_IMPORT" /></button>
            </div>
        </div>
    </div>
</layout:body>
