<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<i18n:setInitialLocale />

<layout:head>
    <script type="text/javascript" src="scripts/json.js"></script>
    <script type="text/javascript" src="scripts/z3950.js"></script>
    <style type="text/css">
        .serverontext {
            color: #00AA00;
        }

        .serverofftext {
            color: #FF0000;
        }

        .serveron .serverofftext {
            display: none;
        }

        .serveroff .serverontext {
            display: none;
        }
    </style>
</layout:head>

<layout:body thisPage="administration_z3950">
    <div class="spacer"></div>

    <div class="tab tab_selected" rel="local_server" onclick="Core.changeTab('local_server', Z3950.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_Z3950_LOCAL"/>
    </div>
    <div class="tab" rel="remote_servers" onclick="Core.changeTab('remote_servers', Z3950.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_Z3950_REMOTE"/>
    </div>
    <div class="tab" rel="form" onclick="Core.changeTab('form', Z3950.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_Z3950_EDIT_SERVER"/>
    </div>
    <div class="tab_close"></div>

    <div class="tab_body" rel="local_server">
        <h1><i18n:getText module="biblivre3" textKey="LABEL_SERVER_Z3950_STATE"/></h1>
        <div class="spacer2"></div>
        <div id="server_status" class="<%= (Boolean) session.getAttribute("serverStatus") ? "serveron" : "serveroff" %>">
            <h1 class="serverontext"><i18n:getText module="biblivre3" textKey="Z3950_SERVER_STATUS_ON"/></h1>
            <h1 class="serverofftext"><i18n:getText module="biblivre3" textKey="Z3950_SERVER_STATUS_OFF"/></h1>
        </div>
        <div class="spacer2"></div>

        <div class="submit_buttons">
            <button type="button" onclick="Core.tempDisable(this); Z3950.changeServerStatus('activate');"><i18n:getText module="biblivre3" textKey="BUTTON_ACTIVATE" /></button>
            <button type="button" onclick="Core.tempDisable(this); Z3950.changeServerStatus('disable');"><i18n:getText module="biblivre3" textKey="BUTTON_DISABLE" /></button>
            <div class="clear"></div>
        </div>
    </div>

    <div class="tab_body hidden" rel="remote_servers">
        <h1></h1>
        <div class="submit_buttons">
            <button type="button" onclick="Core.changeTab('form', Z3950.tabHandler, 0);"><i18n:getText module="biblivre3" textKey="BUTTON_NEW" /></button>
            <div class="clear"></div>
        </div>
        <div class="spacer"></div>
        <div class="spacer"></div>
        <div id="search_results" class="box_holder">
            <div class="template pointer">
                <div class="box" rel="{serverId}" onclick="Core.changeTab('form', Z3950.tabHandler, '{serverId}');">
                    <div class="box_content_left">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_Z3950_SERVER_NAME" />:</b> {name}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_Z3950_SERVER_URL" />:</b> {url}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_Z3950_SERVER_PORT" />:</b> {port}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_Z3950_SERVER_COLLECTION" />:</b> {collection}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_Z3950_SERVER_CHARSET" />:</b> {charset}<br/>
                    </div>
                    <div class="box_content_right">
                        <div><button type="button" onclick="Core.runStopingEvent(event, Z3950.deleteServer, ['{serverId}', this]);"><i18n:getText module="biblivre3" textKey="BUTTON_DELETE" /></button></div>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </div>
    </div>

    <div id="form_edit_box" class="tab_body hidden" rel="form" style="font-size: 12px;">
        <div class="spacer2"></div>
        <input type="hidden" name="serverId"/>
        <div class="format_form">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_Z3950_SERVER_NAME"/></div>
            <div class="fcenter"><input type="text" name="name" maxlength="100" class="finput"/></div>
            <div class="clear"></div>
        </div>
        <div class="format_form">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_Z3950_SERVER_URL"/></div>
            <div class="fcenter"><input type="text" name="url" maxlength="100" class="finput"/></div>
            <div class="clear"></div>
        </div>
        <div class="format_form">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_Z3950_SERVER_PORT"/></div>
            <div class="fcenter"><input type="text" name="port" maxlength="10" class="finput"/></div>
            <div class="clear"></div>
        </div>
        <div class="format_form">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_Z3950_SERVER_COLLECTION"/></div>
            <div class="fcenter"><input type="text" name="collection" maxlength="30" class="finput use_default_value" defaultvalue="default"/></div>
            <div class="clear"></div>
        </div>
        <div class="format_form">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_Z3950_SERVER_CHARSET"/></div>
            <div class="fcenter">
                <select name="charset" class="finput use_default_value" defaultvalue="UTF-8">
                    <option value="UTF-8">UTF-8</option>
                    <option value="MARC-8">MARC-8</option>
                    <option value="LATIN1">LATIN1</option>
                </select>
            </div>
            <div class="clear"></div>
        </div>
        <div class="spacer2"></div>
        <div class="submit_buttons">
            <button type="button" onclick="Core.changeTab('remote_servers', Z3950.tabHandler);"><i18n:getText module="biblivre3" textKey="BUTTON_CANCEL" /></button>
            <button type="button" onclick="Core.tempDisable(this); Z3950.saveServer();"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE" /></button>
            <div class="clear"></div>
        </div>
    </div>
</layout:body>