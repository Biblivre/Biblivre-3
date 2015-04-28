<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<i18n:setInitialLocale />

<layout:head>
    <link rel="stylesheet" type="text/css" href="css/access.css" />

    <script type="text/javascript" src="scripts/circulation.js"></script>
    <script type="text/javascript" src="scripts/access.js"></script>
</layout:head>

<layout:body thisPage="circulation_access">
    <div class="spacer"></div>

    <div class="tab tab_selected" rel="search" onclick="Core.changeTab('search', Access.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_ACCESS_SELECT_USER"/>
    </div>
    <div class="tab" rel="cards" onclick="Core.changeTab('cards', Access.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_ACCESS_BIND"/>
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


    <div class="tab_body hidden" rel="cards">
        <div id="card_search_box" class="search_box">
            <table onkeypress="Core.submitKeyPress(event, Access.cardSearchSubmit);">
                <tr class="card_search_serial">
                    <td class="label_1_card"><i18n:getText module="biblivre3" textKey="LABEL_CARD_NUMBER" /></td>
                    <td class="label_2_card"><input type="text" class="input_text input_term" name="card_number" /></td>
                    <td class="label_3_card"><button type="button" style="width: 100%;" onclick="Access.cardSearchSubmit();"><i18n:getText module="biblivre3" textKey="BUTTON_SEARCH" /></button></td>
                </tr>
            </table>
        </div>

        <div id="card_search_results" class="box_holder">
            <div class="box template">
                <div class="box_content_left">
                    <div class="lent_user_info">
                        <h3><i18n:getText module="biblivre3" textKey="MESSAGE_CARD_ALREADY_LENT" /></h3>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_LENT_FOR" />:</b> {userName}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_USER_SERIAL" />:</b> {userSerial}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_LEND_DATE" />:</b> {entranceDatetime|Datetime}<br/>
                    </div>
                    <b><i18n:getText module="biblivre3" textKey="LABEL_NAME" />:</b> {cardNumber}<br/>
                    <b><i18n:getText module="biblivre3" textKey="LABEL_SERIAL" />:</b> {serialCard}<br/>
                    <b><i18n:getText module="biblivre3" textKey="LABEL_SITUATION" /></b>: {status|translation|CARD_STATUS}<br/>
                </div>
                <div class="box_content_right">
                    <div><button type="button" class="lend_button" onclick="Core.tempDisable(this); Access.lend(this, '{serialCard}');"><i18n:getText module="biblivre3" textKey="BUTTON_BIND" /></button></div>
                    <div><button type="button" class="lend_return_button" onclick="Core.tempDisable(this); Access.lendReturn(this, '{serialCard}', null);"><i18n:getText module="biblivre3" textKey="BUTTON_RETURN_BACK" /></button></div>
                </div>
                <div class="clear"></div>
            </div>
        </div>
    </div>
    
    <div class="tab_footer hidden" rel="cards">
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
        </div>
    </div>
    
    <div class="tab_footer" rel="search">
        <div class="paging user_search_paging"></div>

        <div id="user_search_results" class="box_holder">
            <div class="template pointer">
                <div class="box user_status_{userStatus} user_has_card_{has_card}" rel="{userid}" onclick="Core.changeTab('cards', Access.tabHandler, ['{userid}']);">
                    <div class="box_content_left">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_NAME" /></b>: {name}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_ENROL" /></b>: {userid}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_SITUATION" /></b>: {userStatus|translation|USER_STATUS}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_USER_TYPE" /></b>: {user_type_text}<br/>
                        {{card_number}<b><i18n:getText module="biblivre3" textKey="LABEL_CARD_NUMBER" /></b>: %%<br/>}
                    </div>
                    <div class="box_content_right">
                        <div><button type="button" class="lend_return_button" onclick="Core.runStopingEvent(event, Access.lendReturn, [this, null, '{userid}']);"><i18n:getText module="biblivre3" textKey="BUTTON_EXIT_WITHOUT_CARD" /></button></div>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </div>

        <div class="paging user_search_paging"></div>
    </div>

    <fieldset class="fines_form" id="fines_form" style="display: none;">
        <legend><i18n:getText module="biblivre3" textKey="LABEL_RETURN_HAS_FINES" /></legend>
        <div class="spacer2"></div>
        <table border="0">
            <tr>
                <td><i18n:getText module="biblivre3" textKey="LABEL_FINE_DAYS_LATE"/>:</td>
                <td style="text-align: center;"><span class="fine_days_late"></span></td>
            </tr>
            <tr>
                <td><i18n:getText module="biblivre3" textKey="LABEL_FINE_PRICE_PER_DAY"/>:</td>
                <td style="text-align: center;"><span class="fine_price_per_day"></span></td>
            </tr>
            <tr>
                <td><i18n:getText module="biblivre3" textKey="LABEL_FINE_TOTAL_VALUE"/>:</td>
                <td style="text-align: center;"><input type="text" name="fine_value" /></td>
            </tr>
        </table>
        <div class="spacer2"></div>
        <div class="dialog_buttons">
            <button type="button" onclick="Lending.saveFines(this, 'accredit');"><i18n:getText module="biblivre3" textKey="BUTTON_FINE_ACCREDIT" /></button>
            <button type="button" onclick="Lending.saveFines(this, 'pay');"><i18n:getText module="biblivre3" textKey="BUTTON_FINE_PAY" /></button>
            <button type="button" onclick="Lending.saveFines(this, 'create');"><i18n:getText module="biblivre3" textKey="BUTTON_FINE_CREATE" /></button>
        </div>
    </fieldset>
</layout:body>
