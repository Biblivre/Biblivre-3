<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<i18n:setInitialLocale />

<layout:head>
    <script type="text/javascript" src="scripts/bibliographic.js"></script>

    <script type="text/javascript">
        $(document).ready(function() {
            Bibliographic.updateDatabaseCount();
        });
    </script>
</layout:head>

<layout:body thisPage="cataloging_biblio">
    <div class="spacer"></div>

    <div class="biblio_database">
        <i18n:getText module="biblivre3" textKey="LABEL_BIBLIO_HOME_DATABASE"/>:
        <select id="biblio_database" onchange="Bibliographic.onDatabaseChange();">
            <option value="MAIN"><i18n:getText module="biblivre3" textKey="LABEL_MAIN_BASE"/></option>
            <option value="WORK"><i18n:getText module="biblivre3" textKey="LABEL_WORK_BASE"/></option>
        </select>
        &#160;&#160;&#160;
        <i18n:getText module="biblivre3" textKey="LABEL_TOTAL_RECORDS"/>:
        <span id="biblio_database_count"></span><br/>
    </div>

    <div class="spacer"></div>
    <div class="spacer"></div>

    <div class="tab tab_selected" rel="search" onclick="Core.changeTab('search', Bibliographic.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_SEARCH"/>
    </div>

    <div class="tab" rel="record" onclick="Core.changeTab('record', Bibliographic.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_RECORD"/>
    </div>

    <div class="tab_close"></div>

    <div class="tab_body" rel="search">
        <div id="biblio_search_box" class="search_box">
            <table onkeypress="Core.submitKeyPress(event, Bibliographic.searchSubmit);">

                <tr class="biblio_search_term">
                    <td class="label_1_biblio"><i18n:getText module="biblivre3" textKey="LABEL_MATERIAL" /></td>
                    <td class="label_2_biblio">
                        <select class="input_select input_material" name="ITEM_TYPE">
                            <%@ include file="/includes/material_types.jsp" %>
                        </select>
                    </td>
                    <td class="label_3_biblio">&#160;</td>
                </tr>

                <tr class="biblio_search_term dont_clear">
                    <td class="label_1_biblio"><i18n:getText module="biblivre3" textKey="LABEL_SEARCH_CONTAINING_TERM" /></td>
                    <td class="label_2_biblio"><input type="text" class="input_text biblio_input_term" name="SEARCH_TERM"/></td>
                    <td class="label_3_biblio">
                        <i18n:getText module="biblivre3" textKey="LABEL_SEARCH_ON_ATTRIBUTE" />
                        <select class="input_select input_atribute" name="SEARCH_ATTR">
                            <%@ include file="/includes/search_terms.jsp" %>
                        </select>
                        &#160;&#160;
                        <button type="button" style="width: 80px;" onclick="Bibliographic.searchAddTerm();"><i18n:getText module="biblivre3" textKey="BUTTON_REPEAT" /></button>
                    </td>
                </tr>

                <tr id="biblio_search_term_template" class="biblio_search_term template dont_clear">
                    <td class="label_1_biblio">
                        <select disabled="true" name="BOOL_OP" onchange="$(this).parents('tr').find('input.biblio_input_term').focus();">
                            <option value="AND"><i18n:getText module="biblivre3" textKey="VALUE_AND" /></option>
                            <option value="OR"><i18n:getText module="biblivre3" textKey="VALUE_OR" /></option>
                            <option value="AND_NOT"><i18n:getText module="biblivre3" textKey="VALUE_AND_NOT" /></option>
                        </select> <i18n:getText module="biblivre3" textKey="LABEL_SEARCH_CONTAINING_TERM" />
                    </td>
                    <td class="label_2_biblio"><input disabled="true" type="text" class="input_text biblio_input_term" name="SEARCH_TERM"/></td>
                    <td class="label_3_biblio">
                        <i18n:getText module="biblivre3" textKey="LABEL_SEARCH_ON_ATTRIBUTE" />
                        <select disabled="true" class="input_select input_atribute" name="SEARCH_ATTR">
                            <%@ include file="/includes/search_terms.jsp" %>
                        </select>
                        &#160;&#160;
                        <button type="button" style="width: 80px;" onclick="$(this).parents('tr.biblio_search_term').remove();"><i18n:getText module="biblivre3" textKey="BUTTON_REMOVE" /></button>
                    </td>
                </tr>

                <tr class="form_buttons">
                    <td colspan="3">
                        <div class="search_list_all">
                            <button type="button" onclick="Bibliographic.searchSubmit(true);"><i18n:getText module="biblivre3" textKey="BUTTON_LIST_ALL" /></button>
                        </div>
                        <div class="search_submit">
                            <button type="button" onclick="Bibliographic.searchSubmit();"><i18n:getText module="biblivre3" textKey="BUTTON_SEARCH" /></button>
                        </div>
                        <div class="search_add">
                            <button type="button" onclick="Bibliographic.moveAllRecords();" style="width: 200px;"><i18n:getText module="biblivre3" textKey="BUTTON_MOVE_ALL_RECORDS" /></button>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
    </div>






    <div class="tab_body hidden" rel="record">
        <div class="navigation_top">
            <button class="navigation_button_left" type="button" onclick="Bibliographic.searchNavigate('record', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="Bibliographic.searchNavigate('record', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>

        <h1 class="tab_inner_title"></h1>

        <table class="biblio_record_table">
            <tbody class="tab_inner_body" id="record_results">
                <tr class="template">
                    <td class="biblio_record_table_col1">{label}:</td>
                    <td class="biblio_record_table_col2">{value}</td>
                </tr>
            </tbody>
        </table>

        <div class="navigation_bottom">
            <button class="navigation_button_left" type="button" onclick="Bibliographic.searchNavigate('record', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="Bibliographic.searchNavigate('record', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>
    </div>






    <div class="tab_footer" rel="search">
        <div id="export_box" class="database_MAIN border lightbackground">
            <div id="export_list">
                <div class="export_item template">&bull; {title} <a href="javascript:void(0);" onclick="Bibliographic.removeFromExportList('{serial}');">[Remover]</a></div>
            </div>
            <div style="text-align: right; margin-top: 10px;">
                <button type="button" onclick="Bibliographic.clearExportList();"><i18n:getText module="biblivre3" textKey="BUTTON_CLEAR_MOVE_LIST" /></button>
                <button class="move_to_main" type="button" onclick="Core.tempDisable(this); Bibliographic.moveRecords();" style="margin-left: 10px"><i18n:getText module="biblivre3" textKey="BUTTON_MOVE_TO_MAIN_DATABASE" /></button>
                <button class="move_to_work" type="button" onclick="Core.tempDisable(this); Bibliographic.moveRecords();" style="margin-left: 10px"><i18n:getText module="biblivre3" textKey="BUTTON_MOVE_TO_WORK_DATABASE" /></button>
            </div>
            <input type="hidden" id="export_serial_list" name="serial_list" />
            <input type="hidden" id="export_base" name="base" />
        </div>

        <div class="paging search_paging"></div>

        <div class="search_print" style="text-align: center; margin-bottom: 10px;">
            <button type="button" onclick="Bibliographic.addAllToExportList();" style="margin-left: 10px"><i18n:getText module="biblivre3" textKey="BUTTON_MOVE_ALL" /></button>
        </div>

        <div id="search_results" class="box_holder">
            <div class="template pointer">
                <div class="box" rel="{serial}" onclick="Core.changeTab('record', Bibliographic.tabHandler, '{serial}');">
                    <div class="box_content_left">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_TITLE" />:</b> {title}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_AUTHOR" />:</b> {author}<br/>
                        {{date}<b><i18n:getText module="biblivre3" textKey="LABEL_DATE" />:</b> %%<br/>}
                        {{location}<b><i18n:getText module="biblivre3" textKey="LABEL_090" />:</b> %%<br/>}
                        {{ISBN}<b><i18n:getText module="biblivre3" textKey="LABEL_ISBN" />:</b> %%<br/>}
                        <b><i18n:getText module="biblivre3" textKey="LABEL_SERIAL" />:</b> {serial}<br/>
                        {{holdings_count}<b><i18n:getText module="biblivre3" textKey="LABEL_HOLDING_COUNT" />:</b> %% &#160;&#160;&#160;}
                        {{holdings_available}<b><i18n:getText module="biblivre3" textKey="LABEL_AVAILABLE_COUNT" />:</b> %% &#160;&#160;&#160;}
                        {{holdings_unavailable}<b><i18n:getText module="biblivre3" textKey="LABEL_UNAVAILABLE_COUNT" />:</b> %% &#160;&#160;&#160;}
                        {{holdings_lent}<b><i18n:getText module="biblivre3" textKey="LABEL_LENT_COUNT" />:</b> %% &#160;&#160;&#160;}
                        {{holdings_reserved}<b><i18n:getText module="biblivre3" textKey="LABEL_RESERVED_COUNT" />:</b> %%}
                    </div>
                    <div class="box_content_right">
                        <div><button type="button" class="export_button" onclick="Core.runStopingEvent(event, Bibliographic.addToExportList, [$(this).parents('.box').parent()]);"><i18n:getText module="biblivre3" textKey="BUTTON_SELECT_TO_MOVE" /></button></div>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </div>

        <div class="paging search_paging"></div>
    </div>
</layout:body>