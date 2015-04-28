<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<i18n:setInitialLocale />

<layout:head>

    <script type="text/javascript">
        function reindexBase() {
            if (confirm('<i18n:getText module="biblivre3" textKey="CONFIRM_REINDEX_BIBLIO"/>')) {
                submitForm('FORM_1', 'REINDEX_BIBLIO_BASE');
            }
        }

        function reindexAuthoritiesBase() {
            if (confirm('<i18n:getText module="biblivre3" textKey="CONFIRM_REINDEX_AUTHORITIES"/>')) {
                submitForm('FORM_1', 'REINDEX_AUTHORITIES_BASE');
            }
        }

        function reindexThesaurusBase() {
            if (confirm('<i18n:getText module="biblivre3" textKey="CONFIRM_REINDEX_THESAURUS"/>')) {
                submitForm('FORM_1', 'REINDEX_THESAURUS_BASE');
            }
        }
        
        function exportBase(type) {
            $('input[name=type]').val(type);
            submitForm('FORM_1', 'EXPORT_ALL', undefined, '_blank');
        }
        
        $(document).ready(function() {
            $('#last_backups :first').addClass('last_backup');
        });
    </script>
</layout:head>

<layout:body thisPage="administration_maintenance">
    <fieldset>
        <legend><i18n:getText module="biblivre3" textKey="LABEL_BACKUP"/></legend>
        
        <div class="spacer"></div>
        <div class="submit_buttons">
            <button type="button" onclick="$(this).attr('disabled', true); submitForm('FORM_1','BACKUP');"><i18n:getText module="biblivre3" textKey="BUTTON_CREATE_BACKUP"/></button>
        </div>

        <div class="spacer2"></div>

        <h1><i18n:getText module="biblivre3" textKey="TITLE_LAST_BACKUPS"/></h1>
        <div class="help_message"><i18n:getText module="biblivre3" textKey="LABEL_BACKUP_HELP"/></div>

        <div class="center" id="last_backups" style="font-size: 12px; line-height: 18px;">
            <c:forEach items="${SYSTEM_LAST_FIVE_BACKUPS}" var="backuped">
                <a id="link" href="backup_${backuped[1]}.b3b" target="_blank">${backuped[0]}</a><br />
            </c:forEach>
        </div>

        <div class="spacer"></div>
    </fieldset>

    <div class="spacer2"></div>

    <fieldset>
        <legend><i18n:getText module="biblivre3" textKey="LABEL_REINDEX_BASE"/></legend>
        <div class="spacer"></div>

        <div class="submit_buttons">
            <button type="button" onclick="$(this).attr('disabled', true); reindexBase();"><i18n:getText module="biblivre3" textKey="BUTTON_REINDEX_BASE_BIBLIO"/></button>
            <button type="button" onclick="$(this).attr('disabled', true); reindexAuthoritiesBase();"><i18n:getText module="biblivre3" textKey="BUTTON_REINDEX_BASE_AUTHORITIES"/></button>
            <button type="button" onclick="$(this).attr('disabled', true); reindexThesaurusBase();"><i18n:getText module="biblivre3" textKey="BUTTON_REINDEX_BASE_THESAURUS"/></button>
        </div>
        
        <div class="spacer"></div>
    </fieldset>
            
    <div class="spacer2"></div>

    <fieldset>
        <legend><i18n:getText module="biblivre3" textKey="LABEL_EXPORT_BASE"/></legend>
        <div class="spacer"></div>

        <div class="biblio_database">
            <input type="hidden" name="type"/>
            <div class="fleft" style="width: 50%;">
                <i18n:getText module="biblivre3" textKey="LABEL_BIBLIO_DATABASE"/>:
            </div>
            <div class="fcenter">
                <select name="base">
                    <option value=""><i18n:getText module="biblivre3" textKey="LABEL_ALL_BASE"/></option>
                    <option value="MAIN"><i18n:getText module="biblivre3" textKey="LABEL_MAIN_BASE"/></option>
                    <option value="WORK"><i18n:getText module="biblivre3" textKey="LABEL_WORK_BASE"/></option>
                </select>
            </div>
            <div class="clear"></div>
            <div class="fleft" style="width: 50%;">
                <i18n:getText module="biblivre3" textKey="LABEL_FORMAT"/>:
            </div>
            <div class="fcenter">
                <select name="format" onchange="Bibliographic.onDatabaseChange();">
                    <option value="xml"><i18n:getText module="biblivre3" textKey="LABEL_XML"/></option>
                    <option value="iso2709"><i18n:getText module="biblivre3" textKey="LABEL_ISO"/></option>
                </select>
            </div>
            <div class="clear"></div>
            
        </div>
        
        <div class="spacer"></div>
        
        <div class="submit_buttons">
            <button type="button" onclick="$(this).attr('disabled', true); exportBase('biblio');"><i18n:getText module="biblivre3" textKey="BUTTON_EXPORT_BIBLIO"/></button>
            <button type="button" onclick="$(this).attr('disabled', true); exportBase('holding');"><i18n:getText module="biblivre3" textKey="BUTTON_EXPORT_HOLDINGS"/></button>
        </div>
        
        <div class="spacer"></div>
    </fieldset>
</layout:body>