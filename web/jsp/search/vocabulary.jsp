<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<i18n:setInitialLocale />

<layout:head>
    <link rel="stylesheet" type="text/css" href="css/cataloging.css" />
    <link rel="stylesheet" media="print" type="text/css" href="css/print.css" />

    <script type="text/javascript" src="scripts/json.js"></script>
    <script type="text/javascript" src="scripts/vocabulary.js"></script>
</layout:head>

<layout:body thisPage="search_vocabulary">
    <div class="spacer"></div>

    <div class="tab tab_selected" rel="search" onclick="Core.changeTab('search', VocabularySearch.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_SEARCH"/>
    </div>

    <div class="tab" rel="record" onclick="Core.changeTab('record', VocabularySearch.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_FICHA"/>
    </div>

    <div class="tab" rel="marc" onclick="Core.changeTab('marc', VocabularySearch.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_MARC"/>
    </div>
    <div class="tab_close"></div>

    <div class="tab_body" rel="search">
        <div id="search_box" class="search_box">
            <table onkeypress="Core.submitKeyPress(event, Vocabulary.searchSubmit);">
                <tr class="auth_search_term">
                    <td class="label_1_biblio"><i18n:getText module="biblivre3" textKey="LABEL_SEARCH_CONTAINING_TERM" /></td>
                    <td class="label_2_biblio"><input type="text" class="input_text input_term" name="search_term"/></td>
                    <td class="label_3_biblio">
                        <i18n:getText module="biblivre3" textKey="LABEL_SEARCH_ON_ATTRIBUTE" />
                        <select name="search_type">
                            <option value="150"><i18n:getText module="biblivre3" textKey="LABEL_TERM_TE"/></option>
                            <option value="450"><i18n:getText module="biblivre3" textKey="LABEL_TERM_UP"/></option>
                            <option value="550"><i18n:getText module="biblivre3" textKey="LABEL_TERM_TG"/></option>
                            <option value="360"><i18n:getText module="biblivre3" textKey="LABEL_TERM_VT_TA_TR"/></option>
                        </select>
                    </td>
                </tr>
                <tr class="form_buttons">
                    <td colspan="3">
                        <div class="search_list_all">
                            <button type="button" onclick="VocabularySearch.searchSubmit(true);"><i18n:getText module="biblivre3" textKey="BUTTON_LIST_ALL" /></button>
                        </div>
                        <div class="search_submit">
                            <button type="button" onclick="VocabularySearch.searchSubmit();"><i18n:getText module="biblivre3" textKey="BUTTON_SEARCH" /></button>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
    </div>


    <div class="tab_body hidden" rel="record">
        <div class="navigation_top">
            <button class="navigation_button_left" type="button" onclick="VocabularySearch.searchNavigate('record', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="VocabularySearch.searchNavigate('record', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <h1 class="tab_inner_title"></h1>
            <div class="clear"></div>
        </div>

        <table class="auth_record_table">
            <tbody class="tab_inner_body">
                <tr class="template">
                    <td class="auth_record_table_col1">{label}:</td>
                    <td class="auth_record_table_col2">{value}</td>
                </tr>
            </tbody>
        </table>

        <div class="navigation_bottom">
            <button class="navigation_button_left" type="button" onclick="VocabularySearch.searchNavigate('record', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="VocabularySearch.searchNavigate('record', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>
    </div>

    <div class="tab_body hidden" rel="marc">
        <div class="navigation_top">
            <button class="navigation_button_left" type="button" onclick="VocabularySearch.searchNavigate('marc', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="VocabularySearch.searchNavigate('marc', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <h1 class="tab_inner_title"></h1>
            <div class="clear"></div>
        </div>

        <table class="auth_marc_table">
            <tbody class="tab_inner_body">
                <tr class="template">
                    <td class="auth_marc_table_col1">{field}:</td>
                    <td class="auth_marc_table_col2">{value}</td>
                </tr>
            </tbody>
        </table>

        <div class="navigation_bottom">
            <button class="navigation_button_left" type="button" onclick="VocabularySearch.searchNavigate('marc', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="VocabularySearch.searchNavigate('marc', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>
    </div>

    <div class="tab_footer" rel="search">
        <div class="paging search_paging"></div>

        <div class="search_print" style="text-align: center; margin-bottom: 10px;"><button type="button" onclick="window.print();"><i18n:getText module="biblivre3" textKey="BUTTON_PRINT" /></button></div>

        <div id="search_results" class="box_holder">
            <div class="template pointer">
                <div class="box" rel="{serial}" onclick="Core.changeTab('record', VocabularySearch.tabHandler, '{serial}');">
                    <div class="box_content">
                        <b><i18n:getText module="biblivre3" textKey="v_150" />:</b> {term}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_SERIAL" />:</b> {serial}<br/>
                        {{created|Datetime}<b><i18n:getText module="biblivre3" textKey="LABEL_DATE" />:</b> %%<br/>}
                    </div>
                </div>
            </div>
        </div>
    </div>
</layout:body>