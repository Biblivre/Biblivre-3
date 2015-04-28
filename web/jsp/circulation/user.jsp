<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<i18n:setInitialLocale />

<layout:head>
    <script type="text/javascript" src="scripts/circulation.js"></script>
    <script type="text/javascript" src="scripts/lending.js"></script>
    <script type="text/javascript" src="scripts/ajaxfileupload.js"></script>

    <link rel="stylesheet" type="text/css" href="css/circulation.css" />
</layout:head>

<layout:body thisPage="circulation_user">
    <div class="spacer"></div>

    <div class="tab tab_selected" rel="search" onclick="Core.changeTab('search', Circulation.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_CIRCULATION_SEARCH"/>
    </div>
    <div class="tab" rel="record" onclick="Core.changeTab('record', Circulation.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_CIRCULATION_FICHA"/>
    </div>
    <div class="tab" rel="user_history" onclick="Core.changeTab('user_history', Circulation.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_CIRCULATION_HISTORY"/>
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
                    <td class="label_2"><input type="text" class="input_text input_user_enrol" name="SEARCH_USER_ID"/></td>
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
                        <div class="search_add">
                            <button type="button" onclick="Core.changeTab('record', Circulation.tabHandler, [null, {}]);"><i18n:getText module="biblivre3" textKey="BUTTON_NEW_USER" /></button>
                        </div>                        
                    </td>
                </tr>
            </table>
        </div>
    </div>



    <div class="tab_body hidden" rel="record">
        <div class="navigation_top">
            <button type="button" class="navigation_button_left" onclick="Circulation.searchNavigate('record', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button type="button" class="navigation_button_right" onclick="Circulation.searchNavigate('record', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>

        <h1 class="tab_inner_title"></h1>

        <div class="tab_inner_body" id="user_register_box">
            <table cellspacing="0" cellpadding="0" class="template registerTable">
                <tr>
                    <td colspan="2">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_NAME"/></b><br/>
                        <input type="text" maxlength="70" name="NAME" value="{name}"/>
                        <input type="hidden" name="user_id" value="{userid}"/>
                    </td>
                    <td width="25%">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_USER_TYPE"/></b><br/>
                        <select name="USER_TYPE">
                            <c:forEach var="userType" items="${sessionScope['LIST_USERS_TYPE']}" >
                                <option value="<c:out value='${userType.serial}'/>" {user_type|select|<c:out value='${userType.serial}'/>}><c:out value="${userType.name}"/></option>
                            </c:forEach>
                        </select>
                    </td>
                    <td width="25%" rowspan="5" style="text-align: center;">
                        <img class="userBigPhoto" width="150" height="200" src="DigitalMediaController?default=images%2Fphoto.jpg&id={photo_id}&rand={user_type|random}" alt=""/><br/>
                        <input type="hidden" name="NEW_USER_PHOTO" value="{photo_id}"/>
                        <button style="margin-top: 10px; width: 100%;" type="button" class="photo_upload_button"><i18n:getText module="biblivre3" textKey="BUTTON_SELECT_PICTURE"/></button><br/>
                    </td>
                </tr>
                <tr>
                    <td width="25%">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_ADDRESS"/></b><br/>
                        <input type="text" maxlength="100" name="ADDRESS" value="{address}"/>
                    </td>
                    <td width="25%">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_NUMBER"/></b><br/>
                        <input type="text" maxlength="10" name="NUMBER" value="{number}"/>
                    </td>
                    <td width="25%">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_COMPLEMENT"/></b><br/>
                        <input type="text" maxlength="20" name="COMPLETION" value="{completion}"/>
                    </td>
                </tr>
                <tr>
                    <td width="25%">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_ZIP_CODE"/></b><br/>
                        <input type="text" maxlength="8" name="ZIP_CODE" value="{zip_code}"/>
                    </td>
                    <td width="25%">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_CITY"/></b><br/>
                        <input type="text" maxlength="30" name="CITY" value="{city}"/>
                    </td>
                    <td width="25%">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_STATE"/></b><br/>
                        <input type="text" maxlength="10" name="STATE" value="{state}"/>
                    </td>
                </tr>
                <tr>
                    <td width="25%">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_DLICENSE"/></b><br/>
                        <input type="text" maxlength="15" name="DLICENSE" value="{dlicense}"/>
                    </td>
                    <td width="25%">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_SOCIAL_ID_NUMBER"/></b><br/>
                        <input type="text" maxlength="15" name="SOCIAL_ID_NUMBER" value="{social_id_number}"/>
                    </td>
                    <td width="25%">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_EMAIL"/></b><br/>
                        <input type="text" maxlength="50" name="EMAIL" value="{email}"/>
                    </td>
                </tr>
                <tr>
                    <td width="25%">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_TELEPHONE"/></b><br/>
                        <input type="text" maxlength="20" name="TEL_REF_1" value="{tel_ref_1}"/>
                    </td>
                    <td width="25%">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_CEL_PHONE"/></b><br/>
                        <input type="text" maxlength="20" name="CEL_PHONE" value="{cellphone}"/>
                    </td>
                    <td width="25%">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_BIRTHDAY"/></b><br/>
                        <input type="text" maxlength="10" name="BIRTHDAY" value="{birthday|Date}" onkeypress="return Core.dateMask(this, event);"/>
                    </td>
                </tr>
                <tr>
                    <td width="25%">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_TELEPHONE_COMERCIAL"/></b><br/>
                        <input type="text" maxlength="20" name="TEL_REF_2" value="{tel_ref_2}"/>
                    </td>
                    <td width="25%">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_TELEPHONE_EXTENSION_LINE"/></b><br/>
                        <input type="text" maxlength="5" name="RAMAL" value="{extension_line}"/>
                    </td>
                    <td width="25%">
                        &#160;
                    </td>
                </tr>
                <tr>
                    <td colspan="4">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_OBSERVATION"/></b><br/>
                        <textarea name="OBS">{obs}</textarea>
                    </td>
                </tr>
                <tr>
                    <td colspan="4" class="center">
                        <button type="button" style="width: 20%;" onclick="Core.tempDisable(this); Circulation.deleteUser();"><i18n:getText module="biblivre3" textKey="BUTTON_DELETE"/></button>
                        <button type="button" style="width: 20%; margin-left: 20px;" onclick="Core.tempDisable(this); Circulation.userSave();"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE"/></button>
                    </td>
                </tr>
            </table>
        </div>

        <div class="navigation_bottom">
            <button type="button" class="navigation_button_left" onclick="Circulation.searchNavigate('record', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button type="button" class="navigation_button_right" onclick="Circulation.searchNavigate('record', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>
    </div>
    
    <div class="tab_body hidden" rel="user_history">
        <div class="navigation_top">
            <button type="button" class="navigation_button_left" onclick="Circulation.searchNavigate('user_history', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button type="button" class="navigation_button_right" onclick="Circulation.searchNavigate('user_history', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>

        <h1 class="tab_inner_title"></h1>

        <div class="tab_inner_body">
            <h3><i18n:getText module="biblivre3" textKey="TITLE_CURRENT_FINES" /></h3>
            <div id="current_fines" class="box_holder">
                <div class="colored_box template_empty_message"><i18n:getText module="biblivre3" textKey="LABEL_NO_CURRENT_FINES" /></div>

                <div class="box template">
                    <div class="box_content_left">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_FINE_PAYMENT_DATE" />:</b> -<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_FINE_VALUE" />:</b> {value|Money}<br/>
                    </div>
                    <div class="box_content_right">
                        <div><button type="button" onclick="Core.tempDisable(this); Core.runStopingEvent(event, Lending.changeFineStatus, ['{serial}', 'pay', this]);"><i18n:getText module="biblivre3" textKey="BUTTON_FINE_PAY" /></button></div>
                        <div><button type="button" onclick="Core.tempDisable(this); Core.runStopingEvent(event, Lending.changeFineStatus, ['{serial}', 'accredit', this]);"><i18n:getText module="biblivre3" textKey="BUTTON_FINE_ACCREDIT" /></button></div>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>

            <div class="spacer2"></div>

            <h3><i18n:getText module="biblivre3" textKey="TITLE_CURRENT_LENDINGS" /></h3>
            <div id="current_lendings" class="box_holder">
                <div class="colored_box template_empty_message"><i18n:getText module="biblivre3" textKey="LABEL_NO_CURRENT_LENDINGS" /></div>

                <div class="box template">
                    <div class="box_content">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_LEND_DATE" />:</b> {lendDate|Date}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_TARGET_RETURN_DATE" />:</b> {returnDate|Date}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_TITLE" />:</b> {title}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_AUTHOR" />:</b> {author}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_ASSET_HOLDING" />:</b> {assetHolding}<br/>
                    </div>
                </div>
            </div>

            <div class="spacer2"></div>

            <h3><i18n:getText module="biblivre3" textKey="TITLE_PAST_FINES" /></h3>
            <div id="past_fines" class="box_holder">
                <div class="colored_box template_empty_message"><i18n:getText module="biblivre3" textKey="LABEL_NO_PAST_FINES" /></div>

                <div class="box template">
                    <div class="box_content">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_FINE_PAYMENT_DATE" />:</b> {payment|Date}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_FINE_VALUE" />:</b> {value|Money}<br/>
                    </div>
                </div>
            </div>

            <div class="spacer2"></div>

            <h3><i18n:getText module="biblivre3" textKey="TITLE_PAST_LENDINGS" /></h3>
            <div id="past_lendings" class="box_holder">
                <div class="colored_box template_empty_message"><i18n:getText module="biblivre3" textKey="LABEL_NO_PAST_LENDINGS" /></div>

                <div class="box template">
                    <div class="box_content">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_LEND_DATE" />:</b> {lendDate|Date}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_RETURN_DATE" />:</b> {returnDate|Date}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_TITLE" />:</b> {title}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_AUTHOR" />:</b> {author}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_ASSET_HOLDING" />:</b> {assetHolding}<br/>
                    </div>
                </div>
            </div>
        </div>

        <div class="navigation_bottom">
            <button type="button" class="navigation_button_left" onclick="Circulation.searchNavigate('user_history', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button type="button" class="navigation_button_right" onclick="Circulation.searchNavigate('user_history', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>
    </div>   
    
    <div class="tab_footer" rel="search">
        <div class="paging user_search_paging"></div>

        <div id="user_search_results" class="box_holder">
            <div class="template pointer">
                <div class="box user_status_{userStatus}" rel="{userid}" onclick="Core.changeTab('record', Circulation.tabHandler, ['{userid}']);">
                    <div class="box_content_left">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_NAME" /></b>: {name}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_ENROL" /></b>: {userid}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_SITUATION" /></b>: {userStatus|translation|USER_STATUS}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_USER_TYPE" /></b>: {user_type_text}<br/>
                    </div>
                    <div class="box_content_right">
                        <div><button type="button" onclick="Core.runStopingEvent(event, Circulation.createUserCard, ['{userid}', this]);"><i18n:getText module="biblivre3" textKey="BUTTON_CREATE_USER_CARD" /></button></div>
                        <div><button type="button" class="block_user" onclick="Core.runStopingEvent(event, Circulation.blockUser, ['{userid}', this]);"><i18n:getText module="biblivre3" textKey="BUTTON_BLOCK_USER" /></button></div>
                        <div><button type="button" class="unblock_user" onclick="Core.runStopingEvent(event, Circulation.unBlockUser, ['{userid}', this]);"><i18n:getText module="biblivre3" textKey="BUTTON_UNBLOCK_USER" /></button></div>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </div>

        <div class="paging user_search_paging"></div>
    </div>
</layout:body>
