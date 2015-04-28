<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<i18n:setInitialLocale />

<layout:head>
    <script type="text/javascript" src="scripts/distributed.js"></script>
</layout:head>

<layout:body thisPage="search_distributed">
    <div class="spacer"></div>

    <div class="tab tab_selected" rel="search" onclick="Core.changeTab('search', DistributedSearch.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_SEARCH"/>
    </div>
    <div class="tab" rel="record" onclick="Core.changeTab('record', DistributedSearch.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_FICHA"/>
    </div>
    <div class="tab" rel="marc" onclick="Core.changeTab('marc', DistributedSearch.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_MARC"/>
    </div>
    <div class="tab_close"></div>

    <div class="tab_body" rel="search">
        <div id="biblio_search_box" class="search_box">
            <table onkeypress="Core.submitKeyPress(event, DistributedSearch.searchSubmit);">
                <tr>
                   <td colspan="3">
                        <c:choose>
                            <c:when test="${empty z3950_server_list}">
                                <h1><i18n:getText module="biblivre3" textKey="Z3950_EMPTY_SERVER_LIST"/></h1>
                            </c:when>
                            <c:otherwise>
                                <fieldset class="z3950_server_list">
                                    <legend><i18n:getText module="biblivre3" textKey="Z3950_SERVERS"/></legend>
                                    <ul>
                                        <c:forEach items="${z3950_server_list}" var="server">
                                            <li>
                                                <input type="checkbox" name="serverIds" value="${server.serverId}" id="server_${server.serverId}" />

                                                <label for="server_${server.serverId}">${server.name}</label>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </fieldset>
                                <div class="spacer"></div>
                            </c:otherwise>
                        </c:choose>
                   </td>
                </tr>
                <c:if test="${not empty z3950_server_list}">
                    <tr class="biblio_search_term">
                        <td class="label_1_distributed"><i18n:getText module="biblivre3" textKey="LABEL_SEARCH_CONTAINING_TERM" /></td>
                        <td class="label_2_distributed"><input type="text" class="input_text biblio_input_term" name="SEARCH_TERM"/></td>
                        <td class="label_3_distributed">
                            <i18n:getText module="biblivre3" textKey="LABEL_SEARCH_ON_ATTRIBUTE" />
                            <select name="SEARCH_ATTR">
                                <option value="4"><i18n:getText module="biblivre3" textKey="VALUE_TITLE"/></option>
                                <option value="7"><i18n:getText module="biblivre3" textKey="VALUE_ISBN"/></option>
                                <option value="8"><i18n:getText module="biblivre3" textKey="VALUE_ISSN"/></option>
                                <option value="21"><i18n:getText module="biblivre3" textKey="VALUE_SUBJECT"/></option>
                                <option value="1003"><i18n:getText module="biblivre3" textKey="VALUE_AUTHOR"/></option>
                                <option value="1016"><i18n:getText module="biblivre3" textKey="VALUE_ANY"/></option>
                            </select>
                        </td>
                    </tr>

                    <tr class="form_buttons">
                        <td colspan="3">
                            <div class="search_list_all">
                                <button type="button" onclick="DistributedSearch.searchClear();"><i18n:getText module="biblivre3" textKey="BUTTON_CLEAR" /></button>
                            </div>
                            <div class="search_submit">
                                <button type="button" onclick="DistributedSearch.searchSubmit();"><i18n:getText module="biblivre3" textKey="BUTTON_SEARCH" /></button>
                            </div>
                        </td>
                    </tr>
                </c:if>
            </table>
        </div>
    </div>

    <div class="tab_body hidden" rel="record">
        <div class="navigation_top">
            <button class="navigation_button_left" type="button" onclick="DistributedSearch.searchNavigate('record', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="DistributedSearch.searchNavigate('record', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
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

        <div class="navigation_bottom">
            <button class="navigation_button_left" type="button" onclick="DistributedSearch.searchNavigate('record', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="DistributedSearch.searchNavigate('record', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>
    </div>


    <div class="tab_body hidden" rel="marc">
        <div class="navigation_top">
            <button class="navigation_button_left" type="button" onclick="DistributedSearch.searchNavigate('marc', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="DistributedSearch.searchNavigate('marc', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>

        <h1 class="tab_inner_title"></h1>

        <div style="text-align: center; margin-bottom: 10px;">
            <textarea style="width:700px; margin-top: 20px;" rows="17" name="freemarc" id="freemarc" ></textarea>
        </div>

        <c:if test="${LOGGED_USER != null}">
            <div class="spacer"></div>

            <div class="submit_buttons">
                <button type="button" onclick="Core.tempDisable(this); DistributedSearch.save();"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE" /></button>
                <div class="clear"></div>
            </div>
        </c:if>

        <div class="navigation_bottom">
            <button class="navigation_button_left" type="button" onclick="DistributedSearch.searchNavigate('marc', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="DistributedSearch.searchNavigate('marc', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>
    </div>


    <div class="tab_footer" rel="search">
        <div class="paging search_paging"></div>

        <div id="search_results" class="box_holder">
            <div class="template pointer">
                <div class="box" rel="{index}" onclick="Core.changeTab('record', DistributedSearch.tabHandler, '{index}');">
                    <div class="box_content">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_Z3950_SERVER" />:</b> {server_name}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_TITLE" />:</b> {title}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_AUTHOR" />:</b> {author}<br/>
                        {{publication}<b><i18n:getText module="biblivre3" textKey="LABEL_DATE" />:</b> %%<br/>}
                    </div>
                </div>
            </div>
        </div>

        <div class="paging search_paging"></div>
    </div>
</layout:body>

