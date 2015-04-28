<%@page import="mercury.I18nUtils"%>
<%@ page import="biblivre3.authorization.AuthorizationPointTypes" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<i18n:setInitialLocale />

<layout:head>
    <link rel="stylesheet" type="text/css" href="css/permissions.css" />

    <script type="text/javascript" src="scripts/circulation.js"></script>
    <script type="text/javascript" src="scripts/permissions.js"></script>
</layout:head>

<layout:body thisPage="administration_permissions">
    <div class="spacer"></div>

    <div class="tab tab_selected" rel="search" onclick="Core.changeTab('search', Permissions.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_PERMISSIONS_SEARCH"/>
    </div>
    <div class="tab" rel="permissions" onclick="Core.changeTab('permissions', Permissions.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_PERMISSIONS_LOGIN"/>
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

    <div class="tab_body hidden" rel="permissions">
        <div class="user_selected_box">
            <h3><i18n:getText module="biblivre3" textKey="TITLE_SELECTED_USER" /></h3>

            <div id="user_selected">
                <div class="box template">
                    <div class="box_content_left">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_NAME" /></b>: {user.name}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_ENROL" /></b>: {user.userid}<br/>
                        {{user.login}<b><i18n:getText module="biblivre3" textKey="LABEL_USERNAME" /></b>: %%<br/>}
                    </div>
                    <div class="box_content_right">
                        <img class="userPhoto" width="75" height="100" src="DigitalMediaController?default=images%2Fphoto.jpg&id={user.photo_id}&rand={user.user_type|random}" alt=""/><br/>
                    </div>
                    <div class="box_content_bottom">
                        <button type="button" onclick="Core.changeTab('search');"><i18n:getText module="biblivre3" textKey="BUTTON_SELECT_OTHER_USER" /></button>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </div>
    </div>

    <div class="tab_footer hidden" rel="permissions">
        <div class="spacer2"></div>
        <h3><i18n:getText module="biblivre3" textKey="TITLE_ACCESS_DATA" /></h3>

        <div id="user_register_box">
            <div class="box template">
                <input type="hidden" name="userid" value="{user.userid}" />
                <table cellspacing="0" cellpadding="0" class="login_form">
                    <tr>
                        <td class="right"><b><i18n:getText module="biblivre3" textKey="LABEL_USERNAME"/></b>:</td>
                        <td style="width: 200px;">
                            {{user.login} %%}
                            {{!user.login}<input type="text" maxlength="30" name="login" value="%%"/>}
                        </td>
                        <td colspan="2"></td>
                    </tr>
                    <tr>
                        <td class="right">
                            {{user.login}<b><i18n:getText module="biblivre3" textKey="LABEL_NEW_PASSWORD"/></b>:}
                            {{!user.login}<b><i18n:getText module="biblivre3" textKey="LABEL_PASSWORD"/></b>:}
                        </td>
                        <td style="width: 200px;"><input type="password" maxlength="30" name="password" /></td>
                        <td class="right">&#160;&#160;&#160;<b><i18n:getText module="biblivre3" textKey="LABEL_REPEAT_PASSWORD"/></b>:</td>
                        <td style="width: 200px;"><input type="password" maxlength="30" name="password_2" /></td>

                    </tr>
                </table>
            </div>
        </div>

        <div class="spacer2"></div>
        <h3><i18n:getText module="biblivre3" textKey="TITLE_PERMISSIONS" /></h3>

        <div id="user_permissions_box">
            <div class="box">
                <div class="box_content">
                <%
                    String lastGroup = null;
                    for (AuthorizationPointTypes atp : AuthorizationPointTypes.values()) {
                        if (atp.isAlwaysAllowed()) {
                            continue;
                        }

                        if (!atp.getGroup().equals(lastGroup)) {
                            if (lastGroup != null) {
                                out.println("</fieldset>");
                            }

                            lastGroup = atp.getGroup();

                            out.println("<fieldset>");
                            out.println("<legend><input type=\"checkbox\" onclick=\"Permissions.checkAllPermissions(this);\" style=\"vertical-align: middle\"/>&#160;" + I18nUtils.getText(session, "biblivre3", lastGroup) + "</legend>");
                        }

                        out.println("<div><input type=\"checkbox\" value=\"" + atp + "\" name=\"permissions\" /> &#160; <span onclick=\"Permissions.check(this);\">" + I18nUtils.getText(session, "biblivre3", "AUTH_" + atp.toString()) + "</span></div>");
                    }

                    if (lastGroup != null) {
                        out.println("</fieldset>");
                    }
                %>
                </div>
            </div>
        </div>

        <div class="spacer2"></div>
        <div id="user_submit_buttons">
            <div class="template submit_buttons">
                <button type="button" style="width: 20%;" onclick="Core.tempDisable(this); Permissions.save();"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE"/></button>
                {{user.login}<button type="button" style="width: 20%; margin-left: 20px;" onclick="Core.tempDisable(this); Permissions.removeLogin();"><i18n:getText module="biblivre3" textKey="BUTTON_LOGIN_DELETE"/></button>}
            </div>
        </div>
    </div>

    <div class="tab_footer" rel="search">
        <div class="paging user_search_paging"></div>

        <div id="user_search_results" class="box_holder">
            <div class="template pointer">
                <div class="box user_status_{userStatus}" rel="{userid}" onclick="Core.changeTab('permissions', Permissions.tabHandler, ['{userid}']);">
                    <div class="box_content">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_NAME" /></b>: {name}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_ENROL" /></b>: {userid}<br/>
                        {{login}<b><i18n:getText module="biblivre3" textKey="LABEL_USERNAME" /></b>: %%<br/>}
                    </div>
                </div>
            </div>
        </div>

        <div class="paging user_search_paging"></div>
    </div>
</layout:body>
