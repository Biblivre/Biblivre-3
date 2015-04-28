<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<i18n:setInitialLocale />

<layout:head>
    <link rel="stylesheet" media="print" type="text/css" href="css/print.css">

    <script type="text/javascript" src="scripts/bibliographic.js"></script>
</layout:head>

<layout:body thisPage="search_biblio">
    <div class="spacer"></div>

    <div class="tab tab_selected" rel="search" onclick="Core.changeTab('search', BibliographicSearch.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_SEARCH"/>
    </div>
    <div class="tab" rel="record" onclick="Core.changeTab('record', BibliographicSearch.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_FICHA"/>
    </div>
    <div class="tab" rel="marc" onclick="Core.changeTab('marc', BibliographicSearch.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_MARC"/>
    </div>
    <div class="tab_close"></div>



    <div class="tab_body" rel="search">
        <div id="biblio_search_box" class="search_box">
            <table onkeypress="Core.submitKeyPress(event, BibliographicSearch.searchSubmit);">

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
                        <button type="button" style="width: 80px;" onclick="BibliographicSearch.searchAddTerm();"><i18n:getText module="biblivre3" textKey="BUTTON_REPEAT" /></button>
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
                            <button type="button" onclick="BibliographicSearch.searchSubmit(true);"><i18n:getText module="biblivre3" textKey="BUTTON_LIST_ALL" /></button>
                        </div>
                        <div class="search_submit">
                            <button type="button" onclick="BibliographicSearch.searchSubmit();"><i18n:getText module="biblivre3" textKey="BUTTON_SEARCH" /></button>
                        </div>
                        <div class="search_clear">
                            <button type="button" onclick="BibliographicSearch.searchClear();"><i18n:getText module="biblivre3" textKey="BUTTON_CLEAR" /></button>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
    </div>



    <div class="tab_body hidden" rel="record">
        <div class="navigation_top">
            <button class="navigation_button_left" type="button" onclick="BibliographicSearch.searchNavigate('record', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="BibliographicSearch.searchNavigate('record', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>

        <h1 class="tab_inner_title"></h1>

        <table class="biblio_record_table">
            <tbody class="tab_inner_body">
                <tr class="template">
                    <td class="biblio_record_table_col1">{label}:</td>
                    <td class="biblio_record_table_col2">{value}</td>
                </tr>
            </tbody>
        </table>

        <div class="spacer2"></div>

        <h1><i18n:getText module="biblivre3" textKey="TITLE_FILES" /></h1>
        <div class="spacer"></div>
        <ul class="tab_inner_links">
            <li class="template_empty_message"><i18n:getText module="biblivre3" textKey="LABEL_NO_FILES_FOR_THIS_BIBLIO_RECORD" /></li>
            <li class="template">
                <a {uri|href} target="_blank">{name}</a>
            </li>
        </ul>

        <div class="spacer2"></div>

        <h1><i18n:getText module="biblivre3" textKey="TITLE_HOLDINGS" /></h1>
        <div class="spacer"></div>
        <div class="tab_inner_availability">
            <div class="template">
                {{holdings_count}<b><i18n:getText module="biblivre3" textKey="LABEL_HOLDING_COUNT" />:</b> %% &#160;&#160;&#160;}
                {{holdings_available}<b><i18n:getText module="biblivre3" textKey="LABEL_AVAILABLE_COUNT" />:</b> %% &#160;&#160;&#160;}
                {{holdings_unavailable}<b><i18n:getText module="biblivre3" textKey="LABEL_UNAVAILABLE_COUNT" />:</b> %% &#160;&#160;&#160;}
                {{holdings_lent}<b><i18n:getText module="biblivre3" textKey="LABEL_LENT_COUNT" />:</b> %% &#160;&#160;&#160;}
                {{holdings_reserved}<b><i18n:getText module="biblivre3" textKey="LABEL_RESERVED_COUNT" />:</b> %%}
            </div>
        </div>

        <div class="spacer"></div>

        <div id="holding_results" class="box_holder">
            <div class="holding_result template">
                <div class="box" rel="{serial}">
                    <div class="box_content">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_ASSET_HOLDING" />:</b> {assetHolding}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_LENT_STATE" />:</b> {lent}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_AVAILABILITY" />:</b> {available_text}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_090" />:</b> {location}<br/>
                    </div>
                </div>
            </div>
        </div>


        <div class="navigation_bottom">
            <button class="navigation_button_left" type="button" onclick="BibliographicSearch.searchNavigate('record', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="BibliographicSearch.searchNavigate('record', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>
    </div>



    <div class="tab_body hidden" rel="marc">
        <div class="navigation_top">
            <button class="navigation_button_left" type="button" onclick="BibliographicSearch.searchNavigate('marc', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="BibliographicSearch.searchNavigate('marc', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>

        <h1 class="tab_inner_title"></h1>

        <table class="biblio_marc_table">
            <tbody class="tab_inner_body">
                <tr class="template">
                    <td class="biblio_marc_table_col1">{field}:</td>
                    <td class="biblio_marc_table_col2">{value}</td>
                </tr>
            </tbody>
        </table>

        <div class="spacer2"></div>

        <h1><i18n:getText module="biblivre3" textKey="TITLE_FILES" /></h1>
        <div class="spacer"></div>
        <ul class="tab_inner_links">
            <li class="template_empty_message"><i18n:getText module="biblivre3" textKey="LABEL_NO_FILES_FOR_THIS_BIBLIO_RECORD" /></li>
            <li class="template">
                <a {uri|href} target="_blank">{name}</a>
            </li>
        </ul>

        <div class="navigation_bottom">
            <button class="navigation_button_left" type="button" onclick="BibliographicSearch.searchNavigate('marc', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="BibliographicSearch.searchNavigate('marc', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>
    </div>

    <div class="tab_footer" rel="search">
        <div class="paging search_paging"></div>

        <div class="search_print" style="text-align: center; margin-bottom: 10px;"><button type="button" onclick="window.print();"><i18n:getText module="biblivre3" textKey="BUTTON_PRINT" /></button></div>

        <div id="search_results" class="box_holder">
            <div class="template pointer">
                <div class="box" rel="{serial}" onclick="Core.changeTab('record', BibliographicSearch.tabHandler, '{serial}');">
                    <div class="box_content">
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
                </div>
            </div>
        </div>

        <div class="paging search_paging"></div>
    </div>
</layout:body>

