<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<i18n:setInitialLocale />

<layout:head>
    <link rel="stylesheet" type="text/css" href="css/cataloging.css" />
    <link rel="stylesheet" media="print" type="text/css" href="css/print.css" />

    <script type="text/javascript" src="scripts/json.js"></script>
    <script type="text/javascript" src="scripts/authorities.js"></script>
</layout:head>

<layout:body thisPage="search_auth">
    <div class="spacer"></div>

    <div class="tab tab_selected" rel="search" onclick="Core.changeTab('search', AuthoritiesSearch.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_SEARCH"/>
    </div>

    <div class="tab" rel="record" onclick="Core.changeTab('record', AuthoritiesSearch.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_FICHA"/>
    </div>

    <div class="tab" rel="marc" onclick="Core.changeTab('marc', AuthoritiesSearch.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_MARC"/>
    </div>
    <div class="tab_close"></div>

    <div class="tab_body" rel="search">
        <div id="search_box" class="search_box">
            <table onkeypress="Core.submitKeyPress(event, AuthoritiesSearch.searchSubmit);">
                <tr class="search_term">
                    <td class="label_1"><i18n:getText module="biblivre3" textKey="LABEL_SEARCH_CONTAINING_TERM" /></td>
                    <td class="label_2"><input type="text" class="input_text input_term" name="SEARCH_TERM"/></td>
                    <td class="label_3">&#160;</td>
                </tr>
                <tr class="form_buttons">
                    <td colspan="3">
                        <div class="search_list_all">
                            <button type="button" onclick="AuthoritiesSearch.searchSubmit(true);"><i18n:getText module="biblivre3" textKey="BUTTON_LIST_ALL" /></button>
                        </div>
                        <div class="search_submit">
                            <button type="button" onclick="AuthoritiesSearch.searchSubmit();"><i18n:getText module="biblivre3" textKey="BUTTON_SEARCH" /></button>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
    </div>

    <div class="tab_body hidden" rel="record">
        <div class="navigation_top">
            <button class="navigation_button_left" type="button" onclick="AuthoritiesSearch.searchNavigate('record', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="AuthoritiesSearch.searchNavigate('record', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
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
            <button class="navigation_button_left" type="button" onclick="AuthoritiesSearch.searchNavigate('record', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="AuthoritiesSearch.searchNavigate('record', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>
    </div>

    <div class="tab_body hidden" rel="marc">
        <div class="navigation_top">
            <button class="navigation_button_left" type="button" onclick="AuthoritiesSearch.searchNavigate('marc', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="AuthoritiesSearch.searchNavigate('marc', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
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
            <button class="navigation_button_left" type="button" onclick="AuthoritiesSearch.searchNavigate('marc', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="AuthoritiesSearch.searchNavigate('marc', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>
    </div>

    <div class="tab_footer" rel="search">
        <div class="paging search_paging"></div>

        <div class="search_print" style="text-align: center; margin-bottom: 10px;"><button type="button" onclick="window.print();"><i18n:getText module="biblivre3" textKey="BUTTON_PRINT" /></button></div>

        <div id="search_results" class="box_holder">
            <div class="template pointer">
                <div class="box" rel="{serial}" onclick="Core.changeTab('record', AuthoritiesSearch.tabHandler, '{serial}');">
                    <div class="box_content">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_NAME" />:</b> {name}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_SERIAL" />:</b> {serial}<br/>
                    </div>
                </div>
            </div>
        </div>

        <div class="paging search_paging"></div>
    </div>
</layout:body>