<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<i18n:setInitialLocale />

<layout:head>
    <script type="text/javascript" src="scripts/usertypes.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            UserTypes.list();
        });
    </script>
</layout:head>

<layout:body thisPage="administration_usertypes">
    <div class="spacer"></div>

    <div class="tab tab_selected" rel="list" onclick="Core.changeTab('list', UserTypes.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_USERTYPES_LIST"/>
    </div>
    <div class="tab" rel="form" onclick="Core.changeTab('form', UserTypes.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_USERTYPES_FORM"/>
    </div>
    <div class="tab_close"></div>

    <div class="tab_body" rel="list">
        <h1></h1>
        <div class="submit_buttons">
            <button type="button" onclick="Core.changeTab('form', UserTypes.tabHandler, 0);"><i18n:getText module="biblivre3" textKey="BUTTON_NEW" /></button>
            <div class="clear"></div>
        </div>
        <div class="spacer"></div>
        <div class="spacer"></div>
        <div id="search_results" class="box_holder">
            <div class="template pointer">
                <div class="box" rel="{serial}" onclick="Core.changeTab('form', UserTypes.tabHandler, '{serial}');">
                    <div class="box_content_left">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_USER_TYPE" />:</b> {name}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_USER_TYPE_DESCRIPTION" />:</b> {description}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_MAX_ITEM_COUNT" />:</b> {maxLendingCount}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_DELIVERY_TIME" />:</b> {maxLendingDays}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_RESERVATION_DAYS" />:</b> {maxReservationDays}<br/>
                    </div>
                    <div class="box_content_right">
                        <div><button type="button" onclick="Core.runStopingEvent(event, UserTypes.deleteType, ['{serial}', this]);"><i18n:getText module="biblivre3" textKey="BUTTON_DELETE" /></button></div>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </div>
    </div>

    <div id="form_edit_box" class="tab_body hidden" rel="form" style="font-size: 12px;">
        <div class="spacer2"></div>
        <input type="hidden" name="serial"/>
        <div class="format_form">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_USER_TYPE"/></div>
            <div class="fcenter"><input type="text" name="name" maxlength="100" class="finput"/></div>
            <div class="clear"></div>
        </div>
        <div class="format_form">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_USER_TYPE_DESCRIPTION"/></div>
            <div class="fcenter"><input type="text" name="description" maxlength="100" class="finput"/></div>
            <div class="clear"></div>
        </div>
        <div class="format_form">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_MAX_ITEM_COUNT"/></div>
            <div class="fcenter"><input type="text" name="maxLendingCount" maxlength="10" class="finput"/></div>
            <div class="clear"></div>
        </div>
        <div class="format_form">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_DELIVERY_TIME"/></div>
            <div class="fcenter"><input type="text" name="maxLendingDays" maxlength="10" class="finput"/></div>
            <div class="clear"></div>
        </div>
        <div class="format_form">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_RESERVATION_DAYS"/></div>
            <div class="fcenter"><input type="text" name="maxReservationDays" maxlength="10" class="finput"/></div>
            <div class="clear"></div>
        </div>
        <div class="spacer2"></div>
        <div class="submit_buttons">
            <button type="button" onclick="Core.changeTab('list', UserTypes.tabHandler);"><i18n:getText module="biblivre3" textKey="BUTTON_CANCEL" /></button>
            <button type="button" onclick="Core.tempDisable(this); UserTypes.saveType();"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE" /></button>
            <div class="clear"></div>
        </div>
    </div>
</layout:body>