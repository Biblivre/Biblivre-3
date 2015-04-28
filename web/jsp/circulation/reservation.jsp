<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<i18n:setInitialLocale />

<layout:head>
    <link rel="stylesheet" type="text/css" href="css/reservation.css" />

    <script type="text/javascript" src="scripts/circulation.js"></script>
    <script type="text/javascript" src="scripts/bibliographic.js"></script>
    <script type="text/javascript" src="scripts/reservation.js"></script>
</layout:head>

<layout:body thisPage="circulation_reservation">
    <div class="spacer"></div>

    <div class="tab tab_selected" rel="search" onclick="Core.changeTab('search', Reservation.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_RESERVATION_SELECT_USER"/>
    </div>
    <div class="tab" rel="records" onclick="Core.changeTab('records', Reservation.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_RESERVATION_RESERVE"/>
    </div>
    <div class="tab" rel="list" onclick="Core.changeTab('list', Reservation.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_RESERVATION_RESERVE_LIST"/>
    </div>
    <div class="tab_close"></div>

    <div class="tab_body" rel="search">
        <div id="user_search_box" class="search_box">
            <table onkeypress="Core.submitKeyPress(event, Circulation.search);">
                <tr class="search_term">
                    <td class="label_1_user"><i18n:getText module="biblivre3" textKey="LABEL_NAME" /></td>
                    <td class="label_2_user"><input type="text" class="input_text input_term" name="SEARCH_NAME"/></td>
                    <td class="label_3_user">&#160;</td>
                </tr>
                <tr class="search_term_b">
                    <td class="label_1_user"><i18n:getText module="biblivre3" textKey="LABEL_ENROL" /></td>
                    <td class="label_2_user"><input type="text" class="input_text input_user_enrol" name="SEARCH_USER_ID"/></td>
                    <td class="label_3_user">&#160;</td>
                </tr>
                <tr class="form_buttons">
                    <td colspan="3">
                        <div class="search_list_all">
                            <button type="button" onclick="Circulation.search(true);"><i18n:getText module="biblivre3" textKey="BUTTON_LIST_ALL" /></button>
                        </div>
                        <div class="search_submit">
                            <button type="button" onclick="Circulation.search();"><i18n:getText module="biblivre3" textKey="BUTTON_SEARCH" /></button>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
    </div>

    <div class="tab_body hidden" rel="records">
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

        <div class="paging search_paging" style="margin-top: 15px;"></div>

        <div id="search_results" class="box_holder">
            <div class="template">
                <div class="box">
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
                        <div class="current_circulations"></div>
                    </div>
                    <div class="box_content_right">
                        <div><button type="button" style="width: 240px;" onclick="Core.tempDisable(this); Reservation.reserve(this, '{serial}');"><i18n:getText module="biblivre3" textKey="BUTTON_RESERVE" /></button></div>
                        <div><button type="button" style="width: 240px;" onclick="Core.tempDisable(this); Reservation.listCirculations(this, '{serial}');"><i18n:getText module="biblivre3" textKey="BUTTON_LIST_CIRCULATIONS" /></button></div>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </div>

        <div class="paging search_paging"></div>
    </div>

    <div class="tab_body hidden" rel="list">
        <div id="reserve_results" class="box_holder">
            <div class="colored_box template_empty_message"><i18n:getText module="biblivre3" textKey="LABEL_NO_MATERIAL_RESERVED" /></div>

            <div class="template box">
                <div class="box_content_left">
                    <b><i18n:getText module="biblivre3" textKey="LABEL_NAME" />:</b> {userName}<br/>
                    <b><i18n:getText module="biblivre3" textKey="LABEL_TELEPHONE" />:</b> {userPhoneNumber}<br/>
                    <b><i18n:getText module="biblivre3" textKey="LABEL_EMAIL" />:</b> {userEmail}<br/><br/>
                    <b><i18n:getText module="biblivre3" textKey="LABEL_TITLE" />:</b> {title}<br/>
                    <b><i18n:getText module="biblivre3" textKey="LABEL_AUTHOR" />:</b> {author}<br/><br/>
                    <b><i18n:getText module="biblivre3" textKey="LABEL_RESERVATION_DATE" />:</b> {created|Datetime}<br/>
                    <b><i18n:getText module="biblivre3" textKey="LABEL_RESERVATION_LIMIT_DATE" />:</b> {expires|Datetime}<br/>
                    {{recordSerial}<br/><b><i18n:getText module="biblivre3" textKey="LABEL_RECORD_SERIAL" />:</b> %%<br/>}
                </div>
                <div class="box_content_right">
                    <div><button type="button" onclick="Reservation.deleteReservation('{reservationSerial}', this);"><i18n:getText module="biblivre3" textKey="BUTTON_DELETE" /></button></div>
                </div>
                <div class="clear"></div>
            </div>
        </div>
    </div>

    <div class="tab_footer hidden" rel="records">
        <div class="user_selected_box">
            <h1 class="hidden"></h1>

            <div class="spacer2"></div>
            <h3><i18n:getText module="biblivre3" textKey="TITLE_SELECTED_READER" /></h3>
            <div id="user_selected">
                <div class="colored_box template_empty_message"><i18n:getText module="biblivre3" textKey="LABEL_NO_SELECTED_READER" /></div>

                <div class="box template">
                    <div class="box_content_left">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_NAME" /></b>: {name}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_ENROL" /></b>: {userid}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_SITUATION" /></b>: {userStatus|translation|USER_STATUS}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_USER_TYPE" /></b>: {user_type_text}<br/>
                    </div>
                    <div class="box_content_right">
                        <img class="userPhoto" width="75" height="100" src="DigitalMediaController?default=images%2Fphoto.jpg&id={photo_id}&rand={user_type|random}" alt=""/><br/>
                    </div>
                    <div class="box_content_bottom">
                        <button type="button" onclick="Core.changeTab('search');"><i18n:getText module="biblivre3" textKey="BUTTON_SELECT_OTHER_READER" /></button>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>

            <div id="user_reservations">
                <h3><i18n:getText module="biblivre3" textKey="TITLE_RESERVED_MATERIAL" /></h3>
                <div id="user_reserved_books">
                    <div class="colored_box template_empty_message"><i18n:getText module="biblivre3" textKey="LABEL_NO_MATERIAL_RESERVED_TO_USER" /></div>

                    <div class="box template">
                        <div class="box_content_left">
                            <b><i18n:getText module="biblivre3" textKey="LABEL_TITLE" />:</b> {title}<br/>
                            <b><i18n:getText module="biblivre3" textKey="LABEL_AUTHOR" />:</b> {author}<br/><br/>
                            <b><i18n:getText module="biblivre3" textKey="LABEL_RESERVATION_DATE" />:</b> {created|Datetime}<br/>
                            <b><i18n:getText module="biblivre3" textKey="LABEL_RESERVATION_LIMIT_DATE" />:</b> {expires|Datetime}<br/>
                            {{recordSerial}<br/><b><i18n:getText module="biblivre3" textKey="LABEL_RECORD_SERIAL" />:</b> %%<br/>}
                        </div>
                        <div class="box_content_right">
                            <div><button type="button" onclick="Reservation.deleteReservation('{reservationSerial}', this);"><i18n:getText module="biblivre3" textKey="BUTTON_DELETE" /></button></div>
                        </div>
                        <div class="clear"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="tab_footer" rel="search">
        <div class="paging user_search_paging"></div>

        <div id="user_search_results" class="box_holder">
            <div class="template pointer">
                <div class="box user_status_{userStatus}" rel="{userid}" onclick="Core.changeTab('records', Reservation.tabHandler, ['{userid}']);">
                    <div class="box_content_left">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_NAME" /></b>: {name}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_ENROL" /></b>: {userid}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_SITUATION" /></b>: {userStatus|translation|USER_STATUS}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_USER_TYPE" /></b>: {user_type_text}<br/>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </div>

        <div class="paging user_search_paging"></div>
    </div>
</layout:body>
